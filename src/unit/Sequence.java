package unit;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import common.CommonData;
import common.EntityManager;
import languageprocessing.GraphNode;
import util.IntList;

public class Sequence extends IntList implements ManagedObject,GraphNode{
	/**was set by random seed*/
	private static final long serialVersionUID = -8370098379359406025L;
	//public static ArrayList<Sequence>epoch;
	public static boolean destroyLeaves=true;
	/**was set by random seed*/
	public Sequence parent=null;
	//public ArrayList<Integer> caption=null;
	public ArrayList<Sequence> childs=null;
	public SequenceReference references = null;
	private ArrayList<SequenceReference> pReferencesList = null;
	private ArrayList<Integer> pChilds=null;
	private ArrayList<Integer> pCounts=null;
	public int childsCount=0;
	public void addOccurrence(int nextElement, int messageID, int wordNumber) {
		if(pCounts==null) {pCounts = new ArrayList<Integer>();pChilds = new ArrayList<Integer>();pReferencesList = new ArrayList<SequenceReference>();}
		int index = pChilds.indexOf(nextElement);
		if(index==-1) {
			pChilds.add(nextElement);
			pCounts.add(1);
			pReferencesList.add(new SequenceReference(new Point(messageID, wordNumber)));
		}else {
			pCounts.set(index, pCounts.get(index)+1);
			pReferencesList.get(index).add(new Point(messageID, wordNumber));
		}
	}
	public Sequence(int firstElement, int secondElement, int count) {
		super();
		this.add(firstElement);
		this.add(secondElement);
		this.childsCount=count;
	}
	public Sequence(IntList componentsToAdd, int nextComponent) {
		super(componentsToAdd);
		this.add(nextComponent);
	}
	public Sequence(List<Integer> componentsToAdd, int nextComponent) {
		super(componentsToAdd);
		this.add(nextComponent);
	}
	public Sequence(List<Integer> componentsToAdd) {
		super(componentsToAdd);
	}
	public Sequence() {
		super();
	}
	/**gives control to nodes which has more than one occurrence*/
	public void newEpoch(ArrayList<Sequence> controlGrantedChilds, int minimumOccurrencesToSurvive) {
		if(childsCount<minimumOccurrencesToSurvive)return;
		if(pChilds==null)return;
		childs = new ArrayList<Sequence>();
		int nowCount;
		Sequence child;
		for(int i=0;i<pChilds.size();++i) {
			nowCount=pCounts.get(i);
			if(nowCount<minimumOccurrencesToSurvive&&destroyLeaves)continue;
			child=new Sequence(this, pChilds.get(i));
			child.parent=this;
			child.childsCount=nowCount;
			child.references = pReferencesList.get(i);
			childs.add(child);
			if(nowCount<minimumOccurrencesToSurvive)continue;
			controlGrantedChilds.add(child);
		}
		pReferencesList.clear();pReferencesList=null;
		pChilds.clear();pChilds=null;
		pCounts.clear();pCounts=null;
	}
	
	
	
	/**----------------------------------------------------------------------------------------*/

	private static ArrayList<Sequence> collectBiwordPatterns(MessageBank messageBank, ArrayList<Integer> delimiters, int minimumOccurrencesToSurvive) {
		int count = CommonData.textManager.textLemmes.size();
		int[][] biwordMatrix = new int[count][count];Message message;int lemma=-1,lemmaNext=-1;
		for(int graphemeIndex=0;graphemeIndex<messageBank.textMessages.size();++graphemeIndex) {
			message=messageBank.textMessages.get(graphemeIndex);
			while(message!=null) {if(message.words.size()==0) {message=message.nextPartOfMessage;System.out.println("Detected empty message at:"+Integer.toString(graphemeIndex)+"in dataBank:"+messageBank.getName());continue;}
				lemmaNext=message.getLemmaIndex(0);
				for(int i=0;i<message.words.size()-2;++i) {
					lemma=lemmaNext;
					lemmaNext=message.getLemmaIndex(i);
					if(delimiters.indexOf(lemma)!=-1)continue;
					biwordMatrix[lemma][lemmaNext]++;
				}
				message=message.nextPartOfMessage;
			}
		}
		ArrayList<Sequence> sequences=new ArrayList<Sequence>();
		for(int i=0;i<biwordMatrix.length;++i) {
			if(delimiters.indexOf(i)!=-1)continue;
			for(int j=0;j<biwordMatrix[0].length;++j) {
				if(delimiters.indexOf(j)!=-1)continue;
				if(biwordMatrix[i][j]<minimumOccurrencesToSurvive)continue;
				sequences.add(new Sequence(i,j,biwordMatrix[i][j]));
			}
		}
		for(int i=0;i<biwordMatrix.length;++i)biwordMatrix[i]=null;biwordMatrix=null;
		return sequences;
	}
	private static ArrayList<Sequence> collectNextLengthWordPatterns(MessageBank messageBank, ArrayList<Sequence> epoch, int nowEpochLength, int minimumOccurrencesToSurvive, ArrayList<Integer>delimiters) {
		Message message;int lemmaNext=-1,index, messageWordIndex=-1, messageID=-1;
		IntList lemmaGroup;//List<Integer> lemmaGroup;
		
		ArrayList<Integer>nowLemmesSequence;
		for(int graphemeIndex=0;graphemeIndex<messageBank.textMessages.size();++graphemeIndex) {
			message=messageBank.textMessages.get(graphemeIndex);
			messageWordIndex = -1;
			messageID = message.postID;
			while(message!=null) {
				lemmaNext=message.getLemmaIndex(0);
				for(int i=0;i<message.words.size()-nowEpochLength;++i) {
					lemmaNext=message.getLemmaIndex(i+nowEpochLength);
					messageWordIndex+=1;
					if(delimiters.contains(lemmaNext))continue;
					lemmaGroup=message.lemmes.subIntList(i, i+nowEpochLength);
					index=lemmaGroup.findInArray(epoch);//index=epoch.indexOf(lemmaGroup);
					if(index==-1)continue;
					epoch.get(index).addOccurrence(lemmaNext, messageID, messageWordIndex);
				}
				message=message.nextPartOfMessage;
			}
		}
		ArrayList<Sequence> nextEpoch = new ArrayList<Sequence>();
		for(int i=0;i<epoch.size();++i) {
			epoch.get(i).newEpoch(nextEpoch,minimumOccurrencesToSurvive);
		}
		return nextEpoch;
	}
	public static Sequence createSequenceTreeFromMessages(MessageBank messageBank, boolean destroyLittleLeaves, int nonLittleLeaveStatement) {
		//prepare
		String delimitersChars=".,;:\'\"!?()";
		ArrayList<Integer> delimiters=  new ArrayList<Integer>();int index;
		for(int i=0;i<delimitersChars.length();++i) {index=CommonData.textManager.textLemmes.indexOf(delimitersChars.substring(i,i+1));if(index!=-1)delimiters.add(index);}
		
		//algorithm
		Sequence.destroyLeaves=destroyLittleLeaves;
		Sequence root=new Sequence();
		root.childs=collectBiwordPatterns(messageBank, delimiters, nonLittleLeaveStatement);
		for(int i=0;i<root.childs.size();++i)root.childsCount+=root.childs.get(i).childsCount;
		ArrayList<Sequence> nowEpoch=root.childs,nextEpoch;
		int epochNumber=2;
		System.out.print("Num of words:"+Integer.toString(CommonData.textManager.textMonomials.size())+"; ");
		System.out.print("Num of lemmes:"+Integer.toString(CommonData.textManager.textLemmes.size())+"; ");
		while(nowEpoch.size()>0) {
			System.out.print(epochNumber>2?", ":""+"size of "+Integer.toString(epochNumber)+" epoch is:"+Integer.toString(nowEpoch.size()));
			nextEpoch = collectNextLengthWordPatterns(messageBank, nowEpoch, epochNumber, nonLittleLeaveStatement, delimiters);
			if(epochNumber>2)nowEpoch.clear();
			epochNumber++;
			nowEpoch=nextEpoch;
		}System.out.println();
		root.add(epochNumber-1);
		
		//postscription
		delimiters.clear();delimiters=null;
		
		return root;
	}
	public void clear() {
		if(childs!=null) {for(int i=0;i<childs.size();++i)childs.get(i).clear();childs.clear();childs=null;}
		if(pChilds!=null) {pChilds.clear();pChilds=null;}
		if(pCounts!=null) {pCounts.clear();pCounts=null;}
		if(pReferencesList!=null) {for(int i=0;i<pReferencesList.size();++i)pReferencesList.get(i).clear(); pReferencesList.clear();pReferencesList=null;}
		super.clear();
		
	}
	@Override
	public GraphNode getParent() {
		return parent;
	}
	@Override
	public int getChildsCount() {
		if(childs==null)return 0;
		return childs.size();
	}
	@Override
	public GraphNode getChild(int childIndex) {
		if(childs==null)return null;
		return childs.get(childIndex);
	}
	@Override
	public int getTreeLength() {
		int maxCaption=0, nowCaption=0,count=getChildsCount();
		for(int i=0;i<count;++i) {
			nowCaption=getChild(i).getTreeLength();
			if(nowCaption>maxCaption)maxCaption=nowCaption;
		}
		if(maxCaption==0)maxCaption++;
		return maxCaption;
	}
	@Override
	public int getNumberOfLeaves() {
		int accumulator=0,count=getChildsCount();
		for(int i=0;i<count;++i)
			accumulator+=getChild(i).getNumberOfLeaves();
		if(accumulator==0)accumulator++;
		return accumulator;
	}
	@Override
	public String getName() {
		return IntList.toString(this.data);
	}
	@Override
	public int getMeasurableParameter() {
		return childsCount;
	}

	public void saveToFile(String fileName) {
		File file = new File(fileName);try {if(!file.exists())file.createNewFile();} catch(java.io.IOException e) {return; }//throw new RuntimeException(e);}
		PrintWriter out=null; try {out = new PrintWriter(file);} catch (FileNotFoundException e) {e.printStackTrace(); return;}
		StringBuilder line;
		//Message nowMessage;
		/*
		int j=0;
		for(int i=0;i<childs.size();++i) {
			j=0;
			nowMessage=textMessages.get(i);
			out.println("userID = "+Integer.toString(nowMessage.userID));
			out.println("postID = "+Integer.toString(nowMessage.postID));
			out.println("parentPostID = "+Integer.toString(nowMessage.parentPostID));
			out.println("postTime = "+Integer.toString(nowMessage.postTime));
			while(nowMessage!=null) {
				out.println("sentense ???"+Integer.toString(j)+": ");
				++j;
				printMessageToFile(nowMessage, out, j);
				nowMessage=nowMessage.nextPartOfMessage;
			}
		}
		*/
		//TODO WIP
		out.print("WIP");
		out.close();
	}
	public void loadFromFile(String fileName) {
		System.out.println("public void Sequence.loadFromFile(String fileName)  !WIF!");
	}
	@Override
	public void setName(String newName) {
		System.out.println("Error at 0xa7b58a71");
	}
	@Override
	public void loadFromFile(String fileName, String parameters) {
		loadFromFile(fileName);
	}
	@Override
	public ManagedObject produce(Class<?> resultObjectClass, String parameters) {
		return null;
	}
	@Override
	public void append(ManagedObject object) {
	}
	@Override
	public String getKey() {
		return CommonData.textManager.getManagedElementIdentifier(this);
	}
	@Override
	public EntityManager<?> getManager() {
		return CommonData.textManager;
	}
}
