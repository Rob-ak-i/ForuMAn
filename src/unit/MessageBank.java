package unit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import common.CommonData;
import common.EntityManager;
import languageprocessing.SyntaxProcessor;
import util.Lists;

public class MessageBank implements ManagedObject{
	/** if 0 then stores vanilla string, if 1 then stores OpenCorporaTag, if 2 then stores one grapheme and link to nextGrapheme*/
	public static final int storageLevel_Vanilla=0;
	public static final int storageLevel_Lexemes=1;
	public static final int storageLevel_Graphemes=2;
	
	public static final int storageLevel_Syntax=3;
	/**ниже приведены уровни упрощения сообщения на пути к извлечению необходимых признаков*/
	/**уровень, при котором разрешаются анафоры*/
	//public static final int storageLevel_DePersonized=3;
	public int storageLevel=0;
	public String name;
	public ArrayList<Message> textMessages;
	public MessageBank() {
		textMessages = new ArrayList<Message>();
		name="";
	}
	public void free() {
		clear();
		textMessages=null;
		name=null;
	}
	@Override
	public void clear() {
		if(textMessages!=null)for(int i=0;i<textMessages.size();++i)textMessages.get(i).clear();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getMeasurableParameter() {
		//if(textMessages==null)return 0;
		return textMessages.size();
	}
	
	public void upgradeToLevel3_Syntax() {
		if(storageLevel!=storageLevel_Graphemes)return;
		Message nowMessage;
		for(int i=0;i<textMessages.size();++i) {
			nowMessage=textMessages.get(i);
			while(nowMessage!=null) {
				SyntaxProcessor.processMessage(nowMessage);
				nowMessage=nowMessage.nextPartOfMessage;
			}
		}
		storageLevel=storageLevel_Syntax;
	}
	public void saveToFile(String fileName) {
		File file = new File(fileName);try {if(!file.exists())file.createNewFile();} catch(java.io.IOException e) {return; }//throw new RuntimeException(e);}
		PrintWriter out=null; try {out = new PrintWriter(file);} catch (FileNotFoundException e) {e.printStackTrace(); return;}
		StringBuilder line;
		Message nowMessage;
		int j=0;
		for(int i=0;i<textMessages.size();++i) {
			j=0;
			nowMessage=textMessages.get(i);
			out.println("userID = "+Integer.toString(nowMessage.userID));
			out.println("postID = "+Integer.toString(nowMessage.postID));
			out.println("parentPostID = "+Integer.toString(nowMessage.parentPostID));
			out.println("postTime = "+Integer.toString(nowMessage.postTime));
			while(nowMessage!=null) {
				out.println("sentense №"+Integer.toString(j)+": ");
				++j;
				printMessageToFile(nowMessage, out, j);
				nowMessage=nowMessage.nextPartOfMessage;
			}
		}
		//TODO WIP
		out.close();
	}
	private static void printMessageToFile(Message message,PrintWriter out, int msgNumber) {
		StringBuilder line=new StringBuilder();
		for(int i=0;i<msgNumber;++i)line.append("	");
		for(int i=0;i<message.words.size();++i) {
			line.append(message.words.get(i)).append(' ');
		}out.println(line.toString());
		line=null;
	}
	private static void printDictionaryToFile(MessageBank messageBank,PrintWriter out) {
		
	}

	public void loadFromFile(String fileName) {
		System.out.println("public void MessageBank.loadFromFile(String fileName)  !WIF!");
	}
	@Override
	public void setName(String newName) {
		
	}
	@Override
	public void loadFromFile(String fileName, String parameters) {
		
	}

	public Sequence produceSequenceTree (boolean withLeaves, int nonLittleLeaveStatement) {
		Sequence rootTree = Sequence.createSequenceTreeFromMessages(this, !withLeaves, nonLittleLeaveStatement);
		return rootTree;
	}
	@Override
	public ManagedObject produce(Class<?> resultObjectClass, String parameters) {
		if(!resultObjectClass.equals(Sequence.class.getClass()))return null;
		ArrayList<String> args = Lists.splitInBraces(parameters);
		boolean destroyLittleLeaves=false;
		if(args.get(0).equals("no leaves"))destroyLittleLeaves=true;
		if(args.get(0).equals("with leaves"))destroyLittleLeaves=false;
		int nonLittleLeaveStatement = Integer.valueOf(args.get(1));
		Sequence rootTree = Sequence.createSequenceTreeFromMessages(this, destroyLittleLeaves, nonLittleLeaveStatement);
		if(rootTree==null)return null;
		return rootTree;
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
