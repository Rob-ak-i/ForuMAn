package parts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import common.CommonData;
import common.EntityManager;
import common.EntityEditorMouseListener;
import common.EntityRenderer;
import common.Lang;
import languageprocessing.SyntaxProcessor;
import unit.DataTable;
import unit.ManagedObject;
import unit.Message;
import unit.MessageBank;
import unit.Sequence;
import util.AdditionalDataObjects;
import util.Parameters;
import util.WaitableObject;

@SuppressWarnings("rawtypes")
public class GenericPhantom {
	public int phantomType=0;
	public static final int phantomType_ELEMENTS=5;
	public static final int phantomType_ELEMENT=4;
	public static final int phantomType_TESTSYNTAX=3;
	public static final int phantomType_SEQUENCE=2;
	public static final int phantomType_FORUM=1;
	public static final int phantomType_DISABLED=0;
	public boolean isMultiSelectionMode() {return phantomType==phantomType_ELEMENTS;}
	int sizeScreenRadiusX=10;
	int sizeScreenRadiusY=10;
	/**own fields*/
	public boolean prepared = false;
	private double radiusScale = 1.;
	public Point pos=new Point(0,0);
	public String name="";
	public String caption="";
	/**monoSelection fields*/
	public int selectedElementIndex=-1;
	public EntityManager selectedManager=null;
	/**multiSelection fields*/
	public ArrayList<String> selectedItemsProcessData_ScriptCode = new ArrayList<String>();
	public ArrayList<Parameters> selectedItemsProcessData_Parameters = new ArrayList<Parameters>();
	public ArrayList<String> selectedItemsProcessData_ScriptName = new ArrayList<String>();
	public ArrayList<Point> selectedItemsPosition = new ArrayList<Point>();
	
	private WaitableObject waitableObject=null;
	private ArrayList<String>waitQueue=new ArrayList<String>();
	private String nowPortName=null;
	
	public boolean isRingPointSet = false;
	public boolean working = false;

	public void exit(){
		flush(true);
	}//TODO fixMakeAbstract
	private void flush(boolean disable){
		EntityEditorMouseListener.setMode(EntityEditorMouseListener.MODE_RESERVE);
		selectedElementIndex=-1;
		name=Lang.InnerTable.Misc.table_tutorial;
		caption="DataTables:"+Integer.toString(CommonData.tableManager.getManagedElementsCount())+";DataBanks:"+Integer.toString(CommonData.textManager.getManagedElementsCount())+"Sequences:"+Integer.toString(CommonData.sequenceManager.getManagedElementsCount());
		if(disable) {
			phantomType=phantomType_DISABLED;
			selectedManager=null;

			selectedItemsProcessData_ScriptCode.clear();
			selectedItemsProcessData_ScriptName.clear();
			
			
			//for(int i=0;i<selectedItemsProcessData_Parameters.size();++i)selectedItemsProcessData_Parameters.get(i).clear(); - DO NOT UNCOMMENT THIS! PARAMETERS CLEARING WILL BE IN BACKENDPROCESSOR!!! 
			selectedItemsProcessData_Parameters.clear();
			selectedItemsPosition.clear();//selectedItemsPosition.clear();
			
			working = false;
			prepared = false;
		}
	}
	public void tupleAddToPreparation(String scriptCode, Parameters scriptParameters, String scriptName, Point position) {
		selectedItemsProcessData_ScriptCode.add(scriptCode);
		selectedItemsProcessData_Parameters.add(scriptParameters);
		selectedItemsProcessData_ScriptName.add(scriptName);
		selectedItemsPosition.add(position);
		//working = true;
	}
	public void tupleWaitFor(String waitCode) {
		waitQueue.add(waitCode);
		//working = true;
	}
	public void tupleAttemptToStart() {
		working = true;
		if(waitQueue.size()==0)prepare(phantomType_ELEMENTS, -1);
	}
	public void tupleAddToPreparationForWait(String scriptCode, Parameters scriptParameters, String scriptName, Point position, String waitCode) {
		selectedItemsProcessData_ScriptCode.add(scriptCode);
		selectedItemsProcessData_Parameters.add(scriptParameters);
		selectedItemsProcessData_ScriptName.add(scriptName);
		if(position==null)position=new Point(0,0);
		selectedItemsPosition.add(position);
		int index=-1;
		index=waitQueue.indexOf(waitCode);
		if(index!=-1)waitQueue.remove(index);
		if(!working)return;
		if(waitQueue.size()==0)prepare(phantomType_ELEMENTS, -1);
	}
	public void prepare(int chosenPhantomType, int selectedItem) {
		prepare(chosenPhantomType, selectedItem, null);
	}
	//TODO fixMakeAbstract
	public void prepare(int chosenPhantomType, int selectedItem, WaitableObject waitableObject) {
		EntityEditorMouseListener.setMode(EntityEditorMouseListener.MODE_PUT_PHANTOM_MODE);
		this.waitableObject=waitableObject;
		//flush(false);
		phantomType=chosenPhantomType;
		switch(phantomType) {
		case phantomType_FORUM:
			selectedManager=CommonData.tableManager;
			setElementIndex(selectedItem);
			
			DataTable table = CommonData.tableManager.getManagedElement(selectedElementIndex);
			if(table==null){sizeScreenRadiusX=30;sizeScreenRadiusY=30;}else {
				sizeScreenRadiusX = common.TextForum.getUniqueUsersCount(table);
				sizeScreenRadiusY = sizeScreenRadiusX;
			}
			
			break;
		case phantomType_SEQUENCE:
			selectedManager=CommonData.sequenceManager;
			setElementIndex(selectedItem);
			
			Sequence sequence = CommonData.sequenceManager.getManagedElement(selectedElementIndex);
			if(sequence==null){sizeScreenRadiusX=30;sizeScreenRadiusY=30;}else {
				sizeScreenRadiusX = sequence.getNumberOfLeaves();
				sizeScreenRadiusY = sequence.getTreeLength();
			}
			
			break;
		case phantomType_TESTSYNTAX:
			selectedManager=CommonData.textManager;
			setElementIndex(selectedItem);

			MessageBank messages = CommonData.textManager.getManagedElement(selectedElementIndex);
			if(messages==null){sizeScreenRadiusX=30;sizeScreenRadiusY=30;}else {
				sizeScreenRadiusX = messages.textMessages.size();
				int height=30, nowMessageSize=0, maxMessageSize=0;
				{
					Message nowMessage;
					for(int i=0;i<messages.textMessages.size();++i) {
						if(nowMessageSize>maxMessageSize)maxMessageSize=nowMessageSize;
						nowMessageSize=0;
						nowMessage=messages.textMessages.get(i);
						while(nowMessage!=null) {
							nowMessageSize+=nowMessage.words.size();
							SyntaxProcessor.processMessage(nowMessage);
							nowMessage=nowMessage.nextPartOfMessage;
						}
					}
					if(maxMessageSize>0)height = maxMessageSize;
				}
				sizeScreenRadiusY = height;
			}
			break;
		case phantomType_ELEMENTS:
			selectedManager=null;
			isRingPointSet=(selectedItemsPosition.indexOf(null)!=-1);
			int n=selectedItemsPosition.size();
			double x,y, l=10.*n*2./EntityRenderer.scalex,a=0,cx=0,cy=0,r=l/(2.*Math.PI);
			ArrayList<Integer> positionsToSuppose=null;
			for(int i=0;i<n;++i) {
				x=Math.cos(a)*r;
				y=Math.sin(a)*r;
				a+=Math.PI*2./n;
				if(selectedItemsPosition.get(i)==null) {
					selectedItemsPosition.set(i, new Point((int)x,(int)y));
				}else {
					cx+=selectedItemsPosition.get(i).x;
					cy+=selectedItemsPosition.get(i).y;
					if(positionsToSuppose==null)positionsToSuppose=new ArrayList<Integer> ();
					positionsToSuppose.add(i);
				}
			}
			if(positionsToSuppose!=null) {
				cx/=positionsToSuppose.size();
				cy/=positionsToSuppose.size();
				for(int i=0;i<positionsToSuppose.size();++i) {
					selectedItemsPosition.get(positionsToSuppose.get(i)).x-=cx;
					selectedItemsPosition.get(positionsToSuppose.get(i)).y-=cy;
				}
				positionsToSuppose.clear();
				positionsToSuppose=null;
			}
			break;
		}
		prepared=true;
	}
	private void setElementIndex(int elementIndex) {
		selectedElementIndex=elementIndex;
		ManagedObject element=selectedManager.getManagedElement(elementIndex);
		name=selectedManager.getManagedElementIdentifier(elementIndex);
		caption=selectedManager.getManagedObjectClass().getSimpleName()+" name = "+element.getName()+"; size = "+Integer.toString(element.getMeasurableParameter());
	}
	private void changeElementIndex(int direction) {
		if(selectedManager==null)return;
		int elementsCount=selectedManager.getManagedElementsCount();
		if(elementsCount==0) return;
		selectedElementIndex=selectedElementIndex+direction;
		if(selectedElementIndex<0)selectedElementIndex=elementsCount-1;
		if(selectedElementIndex>=elementsCount)selectedElementIndex=0;
		setElementIndex(selectedElementIndex);
	}
	//TODO fixMakeAbstract
	public void performAction(int x, int y) {
		pos.x=x;pos.y=y;
		//if(selectedElementIndex<0||selectedElementIndex>=selectedManager.getManagedElementsCount())return;
		Point mathCoords = EntityRenderer.getRealDotFromImageCoords(pos.x, pos.y);
		switch(phantomType) {
		case phantomType_FORUM:
			CommonData.taskManager.addOperation(
					"CommonData.get(\"scheme\").printDiscuss(position,radiusScale, discuss,isAppendMode);"
					,AdditionalDataObjects
							.packParameters("position", mathCoords)
							.pack("discuss", CommonData.tableManager.getManagedElement(selectedElementIndex))
							.pack("isAppendMode", EntityEditorMouseListener.isLShiftPressed)
							.pack("radiusScale", radiusScale)
					,"printing forum"
			);
			//CommonData.scheme.printDiscuss(mathCoords.x, mathCoords.y, CommonData.tableManager.getManagedElement(selectedElementIndex),EntityEditorMouseListener.isLShiftPressed);
			break;
		case phantomType_SEQUENCE:
			CommonData.taskManager.addOperation(
					"CommonData.get(\"scheme\").printSequenceTree(position,rootSequence);"
					,AdditionalDataObjects
							.packParameters("position", mathCoords)
							.pack("rootSequence", CommonData.sequenceManager.getManagedElement(selectedElementIndex))
					,"printSequenceTree"
			);
			//CommonData.scheme.printSequenceTree(mathCoords.x, mathCoords.y, CommonData.sequenceManager.getManagedElement(selectedElementIndex));
			break;
		case phantomType_TESTSYNTAX:
			CommonData.taskManager.addOperation(
					"CommonData.get(\"scheme\").printMessagesSyntaxes(position,messages);"
					,AdditionalDataObjects
							.packParameters("position", mathCoords)
							.pack("messages", CommonData.textManager.getManagedElement(selectedElementIndex))
					,"printMessagesSyntaxes"
			);
			//CommonData.scheme.printMessagesSyntaxes(mathCoords.x, mathCoords.y, CommonData.textManager.getManagedElement(selectedElementIndex));
			break;
		case phantomType_ELEMENTS:
			int mx=0,my=0;
			for(int i=0;i<selectedItemsPosition.size();++i) {
				mx+=selectedItemsPosition.get(i).x;
				my+=selectedItemsPosition.get(i).y;
			}
			if(mx!=0)mx/=(selectedItemsPosition.size());
			if(my!=0)my/=(selectedItemsPosition.size());
			for(int i=0;i<selectedItemsPosition.size();++i) {
				selectedItemsPosition.get(i).x=selectedItemsPosition.get(i).x+pos.x;
				selectedItemsPosition.get(i).y=selectedItemsPosition.get(i).y+pos.y;
				selectedItemsPosition.set(i,EntityRenderer.getRealDotFromImageCoords(selectedItemsPosition.get(i)));
				selectedItemsProcessData_Parameters.get(i).put("position", selectedItemsPosition.get(i));
				CommonData.taskManager.addOperation(
						selectedItemsProcessData_ScriptCode.get(i),
						selectedItemsProcessData_Parameters.get(i),
						selectedItemsProcessData_ScriptName.get(i)
				);
			}
			//CommonData.scheme.printMessagesSyntaxes(mathCoords.x, mathCoords.y, CommonData.textManager.getManagedElement(selectedElementIndex));
			break;
		}
		flush(true);
		if(waitableObject!=null)waitableObject.wakeUp();
	}
	public void draw(Graphics g){
		int sizeScreenRadiusXReal = (int) (sizeScreenRadiusX*EntityRenderer.scalex);
		int sizeScreenRadiusYReal = (int) (sizeScreenRadiusY*EntityRenderer.scaley);
		g.setColor(Color.gray);
		switch(phantomType){
		case phantomType_FORUM:
			g.drawLine(pos.x-30, pos.y-30, pos.x+30, pos.y+30);
			g.setColor(Color.green);
			g.drawOval(pos.x-sizeScreenRadiusXReal, pos.y-sizeScreenRadiusYReal, sizeScreenRadiusXReal*2, sizeScreenRadiusYReal*2);
			g.setColor(Color.black);
			g.drawString(name, pos.x+3, pos.y-6);
			g.drawString(caption, pos.x+3, pos.y+6);
			if(EntityEditorMouseListener.isLShiftPressed) {
				g.setColor(Color.red);
				g.drawString(Lang.InnerTable.Misc.table_appendMode, pos.x+3, pos.y+12);
			}
			break;
		case phantomType_SEQUENCE:
			g.drawOval(pos.x-30, pos.y-30, 60, 60);
			g.drawLine(pos.x-15, pos.y-15, pos.x, pos.y);
			g.drawLine(pos.x, pos.y-15, pos.x, pos.y+15);
			g.drawLine(pos.x+15, pos.y-15, pos.x, pos.y);
			g.setColor(Color.green);
			g.drawRect(pos.x-sizeScreenRadiusXReal/2, pos.y-sizeScreenRadiusYReal/2, sizeScreenRadiusXReal, sizeScreenRadiusYReal);
			g.setColor(Color.black);
			g.drawString(name, pos.x+3, pos.y-6);
			g.drawString(caption, pos.x+3, pos.y+6);
			
			break;
		case phantomType_TESTSYNTAX:
			g.drawRect(pos.x-30, pos.y-30, 60, 60);
			g.drawLine(pos.x-15, pos.y-15, pos.x, pos.y);
			g.drawLine(pos.x, pos.y-15, pos.x, pos.y+15);
			g.drawLine(pos.x+15, pos.y-15, pos.x, pos.y);
			g.setColor(Color.green);
			g.drawRect(pos.x-sizeScreenRadiusXReal/2, pos.y-sizeScreenRadiusYReal/2, sizeScreenRadiusXReal, sizeScreenRadiusYReal);
			g.setColor(Color.black);
			g.drawString(name, pos.x+3, pos.y-6);
			g.drawString(caption, pos.x+3, pos.y+6);
			
			break;
		case phantomType_ELEMENTS:
			int x=0,y=0;
			for(int i=0;i<selectedItemsPosition.size();++i) {
				x=selectedItemsPosition.get(i).x+pos.x;
				y=selectedItemsPosition.get(i).y+pos.y;
				g.drawRect(x-1, y-1, 3, 3);
			}
			g.setColor(Color.black);
			g.drawLine(pos.x-20, pos.y, pos.x+20, pos.y);
			g.drawLine(pos.x, pos.y-20, pos.x, pos.y+20);
			break;
		}
		
	}
}
