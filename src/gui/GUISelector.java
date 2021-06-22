package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import common.CommonData;
import common.EntityManager;
import common.EntityEditorMouseListener;
import common.Lang;
import common.Reports;
import parts.GenericPhantom;
import unit.DataTable;
import unit.ManagedObject;
import unit.MessageBank;
import unit.Sequence;
import util.AdditionalDataObjects;
import util.Parameters;
import util.ReportObject;
import util.WaitableObject;

public class GUISelector extends JFrame implements WaitableObject, ConcreteActionOwner{
	/**was set by random seed*/
	private static final long serialVersionUID = 2704133509628977253L;
	public static final int innerStateDefault=0;
	public static final int innerStateLoad=1;
	public static final int innerStateLoadTuple=11;
	public static final int innerStateSave=2;
	public static final int innerStatePrint=3;
	public static final int innerStateUseForScript=4;
	public static final int innerState_languageprocessing=5;
	public static final int innerState_MakeSequenceTree = 5;
	protected int innerState=innerStateDefault;
	protected ArrayList<String> labelStateNames;
	protected boolean waiting=false;
	protected boolean working=false;
	
	public JComboBoxExt selectorBox;
	public JLabel label;
	private JLabel processingStateLabel;
	private Class<? extends ManagedObject> classObject;
	protected EntityManager<? extends ManagedObject> manager;

	protected ArrayList<Integer> worksQueue = new ArrayList<Integer>();
	protected ArrayList<String> worksQueueProcesses_Code = new ArrayList<String>();
	protected ArrayList<Parameters> worksQueueProcesses_Args = new ArrayList<Parameters>();
	protected ArrayList<String> worksQueueProcesses_Name = new ArrayList<String>();
	public JCheckBox notDestroyLeavesCheck;

	public void addWork(int stateNumber) {
		addWork(stateNumber, null, null, null);
	}
	public void addWork(int stateNumber, String code, Parameters args, String name) {
		worksQueue.add(stateNumber);
		worksQueueProcesses_Code.add(code);
		worksQueueProcesses_Args.add(args);
		worksQueueProcesses_Name.add(name);
		if(worksQueue.size()>1) {return;}
		prepare();
	}
	/**prepare */
	private void prepare() {
		if(worksQueue.size()==0) return;
		working=true;
		int stateNumber = worksQueue.get(0);
		//parameter = worksQueueParameter.get(0);
		label.setText(labelStateNames.get(stateNumber));
		innerState=stateNumber;
		if(innerState==innerStateLoad || innerState==innerStateLoadTuple )
			this.selectorBox.setEditable(true);
		else 
			this.selectorBox.setEditable(false);
		if(innerState==innerStateLoad || innerState==innerStateLoadTuple ) {
			String fileName=null;
			if(worksQueueProcesses_Args.get(0)!=null) {
				fileName = (String) worksQueueProcesses_Args.get(0).get("fileName");
				if(fileName!=null)
					label.setText(labelStateNames.get(stateNumber)+"("+fileName+")");
			}
		}
		this.setVisible(true);
	}
	public GUISelector(Class classObject, String selectorName, EntityManager manager) {
		super(selectorName);
		this.classObject=classObject;
		AbstractAction buttonAcceptAction=new ConcreteAction(this);
		//components=new ArrayList<JComponent>();
		this.setSize(CommonData.WIDTH,CommonData.HEIGHT);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);//frame2.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setBounds(400,400,300,200);
		
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		Dimension preferredSize = new Dimension(300,50);
		processingStateLabel = new JLabel();
		processingStateLabel.setVisible(false);
		processingStateLabel.setText(Lang.InnerTable.GUI.AbstractSelectorLablePerformAction);
		processingStateLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
		//processingStateLabel.setBounds(0,0,200,50);
		processingStateLabel.setSize(preferredSize);
		this.getContentPane().add(processingStateLabel);
		selectorBox = new JComboBoxExt();
		//selectorBox.setBounds(0, 25, 80, 20);
		selectorBox.setAlignmentY(Component.CENTER_ALIGNMENT);
		//selectorBox.setBounds(0,0,200,50);
		selectorBox.setSize(preferredSize);
		selectorBox.setEditable(true);
		this.getContentPane().add(selectorBox);//addComponent(selectorBox,SpringLayout.NORTH);//this.add(selectorBox);
		
		this.manager=manager;
		if(manager!=null)manager.addControlledComboBox(selectorBox);
		
		label = new JLabel();
		//label.setBounds(0,0,80,20);
		label.setAlignmentY(Component.CENTER_ALIGNMENT);
		//label.setBounds(0,0,200,50);
		label.setSize(preferredSize);
		this.getContentPane().add(label);//addComponent(label, SpringLayout.WEST);
		
		JButton buttonAccept = new JButton();
		buttonAccept.setText("OK");
		buttonAccept.setAction(buttonAcceptAction);
		buttonAccept.setAlignmentY(Component.CENTER_ALIGNMENT);
		//buttonAccept.setBounds(0,0,200,50);
		buttonAccept.setSize(preferredSize);
		this.getContentPane().add(buttonAccept);//addComponent(buttonAccept,SpringLayout.WEST);//this.add(buttonAccept);
		labelStateNames = new ArrayList<String>();
		labelStateNames.add("Nothing to do");
		labelStateNames.add(common.Lang.InnerTable.GUI.TableSelectorLableLoad);
		labelStateNames.add(common.Lang.InnerTable.GUI.TableSelectorLableSave);
		labelStateNames.add(common.Lang.InnerTable.GUI.TableSelectorLablePrint);
		labelStateNames.add(common.Lang.InnerTable.GUI.TableSelectorLableUseForScript);
		labelStateNames.add(common.Lang.InnerTable.GUI.TableSelectorLableUpgradeToDataBank);
		labelStateNames.add("");
		labelStateNames.add("");
		labelStateNames.add("");
		labelStateNames.add("");
		labelStateNames.add("");
		labelStateNames.add(common.Lang.InnerTable.GUI.TableSelectorLableLoad);
		if(classObject.equals(MessageBank.class.getClass()))
				labelStateNames.set(innerState_MakeSequenceTree,common.Lang.InnerTable.GUI.TableSelectorLableUpgradeToSequence);
		if(classObject.equals(ReportObject.class.getClass())) {
			this.selectorBox.addItem(common.Lang.InnerTable.Action.computationsMenuAction0Name);
			this.selectorBox.addItem(common.Lang.InnerTable.Action.computationsMenuAction1Name);
			this.selectorBox.addItem(common.Lang.InnerTable.Action.computationsMenuAction2Name);
		}
		if(classObject.equals(MessageBank.class.getClass())) {
			notDestroyLeavesCheck = new JCheckBox();
			notDestroyLeavesCheck.setText("notDestroyLeaves");
	
			Dimension preferredSize1 = new Dimension(300,50);
			notDestroyLeavesCheck.setSize(preferredSize1);
			notDestroyLeavesCheck.setAlignmentX(Container.CENTER_ALIGNMENT);this.getContentPane().add(notDestroyLeavesCheck);
		}
		this.setResizable(true);
		this.setVisible(false);
		this.setFocusable(true);
	}
	
	//TODO fixMakeAbstract
	protected void performActionInner() {
		Sequence sequence=null;
		int sequenceIndex;
		if(classObject.equals(ReportObject.class.getClass())) {
			if(innerState!=innerStateSave)return;
			switch(this.selectorBox.getSelectedIndex()) {
			case 0:
				common.Reports.createReport();
				break;
			case 1:
				common.Reports.makeResult1();
				break;
			case 2:
				common.Reports.makeNLP();
				break;
			}
			innerState=innerStateDefault;
			this.setVisible(false);
		}
		if(classObject.equals(Sequence.class.getClass()))
		switch(innerState) {
		case innerStatePrint:
			sequenceIndex=CommonData.sequenceManager.getManagedElementIndex(selectorBox.getCaption());
			if(sequenceIndex==-1)break;
			CommonData.genericPhantom.prepare(GenericPhantom.phantomType_SEQUENCE, sequenceIndex);
			break;
		default:
			break;
		}
		if(classObject.equals(MessageBank.class.getClass())){
			boolean destroyLeaves=!notDestroyLeavesCheck.isSelected();
			MessageBank messageBank=null;
			int messageBankIndex=-1;
			switch(innerState) {
			case innerStatePrint:
				messageBankIndex=CommonData.textManager.getManagedElementIndex(selectorBox.getCaption());
				if(messageBankIndex==-1)break;
				EntityEditorMouseListener.setMode(EntityEditorMouseListener.MODE_PUT_PHANTOM_MODE);
				CommonData.genericPhantom.prepare(GenericPhantom.phantomType_TESTSYNTAX, messageBankIndex);
				break;
			case innerStateUseForScript:
				//TODO WIF put script actions for messagesBank
				System.out.println("MessageBank WIF");
				break;
			case innerState_MakeSequenceTree:
				messageBank=CommonData.textManager.getManagedElement(selectorBox.getCaption());
				int nonLittleLeaveStatement=2;
				if(messageBank==null)break;
				if(worksQueueProcesses_Args.get(0)==null) {
					worksQueueProcesses_Code.set(0, 
							  "seq = messageBank.produceSequenceTree(withLeaves,2);"
							+ "CommonData.get(\"sequenceManager\").addManagedElement(seq, key);"
							);
					worksQueueProcesses_Args.set(0, AdditionalDataObjects.packParameters("key", selectorBox.getCaption()).pack("messageBank", messageBank).pack("withLeaves",true));
					worksQueueProcesses_Name.set(0, "GUISelector:innerState_MakeSequenceTree");
				}
					
				//CommonData.sequenceManager.addManagedElement(selectorBox.getCaption(), destroyLeaves, 2);
				break;
			default:
				System.out.println("GUIBankSelector.performAction: no such action:"+innerState);
				//table=CommonData.textManager.//.getTable(selectorBox.getCaption());
				//if(innerState==innerState_makeResult1_inner)
				//	Reports.makeResult1_inner(table);
				//if(innerState==innerState_languageprocessing) {
				//	System.out.println("five/nine work");
				//	languageprocessing.LanguageProcessor.doAllWIP(null);
				//}
				break;
			}
		}

		DataTable table=null;
		int tableIndex;

		if(classObject.equals(DataTable.class.getClass()))
		switch(innerState) {
		case innerStateLoadTuple:
			worksQueueProcesses_Args.get(0).put("key", selectorBox.getCaption());
			/*
			CommonData.taskManager.addOperation(
					AdditionalDataObjects.pack(
							CommonData.tableManager, 
							"loadDataTable", 
							AdditionalDataObjects.packParameters("fileName", parameter).pack("key", selectorBox.getCaption()),
							null
							).pack(
									CommonData.genericPhantom, 
									"addToPreparationForWait", 
									AdditionalDataObjects.packParameters("processData", AdditionalDataObjects.pack(CommonData.scheme, "createBoundedElement", processParameters, null)),
									null
									)
					);*/
			
			break;
		case innerStateLoad:
			worksQueueProcesses_Args.get(0).put("key", selectorBox.getCaption());
			/*
			worksQueueProcesses.set(0,AdditionalDataObjects.pack(
					CommonData.tableManager,
					"loadDataTable",
					AdditionalDataObjects.packParameters("fileName", parameter).pack("key", selectorBox.getCaption()),
					null
					));
					*/
			
			//table = DataTable.readFromFile(CommonData.fileName);
			//if(table==null)break;
			//CommonData.tableManager.addManagedElement(table, selectorBox.getCaption());
			break;
		case innerStateSave:
			worksQueueProcesses_Args.get(0).put("key", selectorBox.getCaption());
			/*
			worksQueueProcesses.set(0,AdditionalDataObjects.pack(
					CommonData.tableManager,
					"saveDataTable",
					AdditionalDataObjects.packParameters("fileName", parameter).pack("key", selectorBox.getCaption()),
					null
					));
			*/
			//table=CommonData.tableManager.getManagedElement(selectorBox.getCaption());
			//if(table==null)break;
			//table.saveDataToFile(CommonData.fileName, true);
			break;
		case innerStatePrint://
			tableIndex=CommonData.tableManager.getManagedElementIndex(selectorBox.getCaption());
			if(tableIndex==-1)break;
			EntityEditorMouseListener.setMode(EntityEditorMouseListener.MODE_PUT_PHANTOM_MODE);
			waiting=true;
			CommonData.genericPhantom.prepare(GenericPhantom.phantomType_FORUM, tableIndex, this);
			break;
		case innerState_languageprocessing:
			table=CommonData.tableManager.getManagedElement(selectorBox.getCaption());
			if(table==null)break;
			worksQueueProcesses_Code.set(0, 
					  "messageBank=table.produceMessageBank();"
					+ "CommonData.get(\"textManager\").addManagedElement(messageBank, key);"
					+ "CommonData.get(\"languageProcessor\").doAll(messageBank);"//
					);
			worksQueueProcesses_Args.set(0, AdditionalDataObjects.packParameters("key", selectorBox.getCaption()).pack("table", table));
			worksQueueProcesses_Name.set(0, "GUISelector:innerState_languageprocessing");
			
			break;
		case innerStateUseForScript:
			table=CommonData.tableManager.getManagedElement(selectorBox.getCaption());
			if(table==null)break;
			System.out.println("Multiscripting maybe WIF...");

			worksQueueProcesses_Code.set(0, 
					  "CommonData.get(\"reports\").makeResult1_inner(table);"
					);
			worksQueueProcesses_Args.set(0, AdditionalDataObjects.packParameters("table", table));
			worksQueueProcesses_Name.set(0, "GUISelector:innerState_languageprocessing");
			
			//Reports.makeResult1_inner(table);
		default:
			break;
		}
		innerState=innerStateDefault;
		setVisible(false);
	}
	
	public void performAction(){
		//processProcessor = null;
		///processMethod = null;
		//processParameters = null;
		working=false;
		this.setVisible(false);

		waiting=false;
		
		performActionInner();
		
		if(worksQueueProcesses_Code.size()!=0) {
			if(worksQueueProcesses_Code.get(0)!=null)
				CommonData.taskManager.addOperation(
						worksQueueProcesses_Code.get(0)
						,worksQueueProcesses_Args.get(0)
						,worksQueueProcesses_Name.get(0)
						
						);//if(processProcessor!=null) {CommonData.taskManager.addOperation(AdditionalDataObjects.pack(processProcessor,processMethod,processParameters,null));}
			worksQueue.remove(0);
			worksQueueProcesses_Code.remove(0);
			worksQueueProcesses_Args.get(0).clear();
			worksQueueProcesses_Args.remove(0);
			worksQueueProcesses_Name.remove(0);
		}
		if(!waiting)
			prepare();
	}
	public void destroy() {
		
	}
	public void wakeUp() {
		waiting=false;
		prepare();
	}
	/*
	private static final AbstractAction buttonAcceptAction=new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			DataTable table=null;
			switch(innerState) {
			case innerStateLoad:
				table = DataTable.readFromFile(CommonData.fileName);
				if(table==null)return;
				CommonData.tableManager.addTable(table, selectorBox.getCaption());
				break;
			case innerStateSave:
				table=CommonData.tableManager.getTable(selectorBox.getCaption());
				if(table==null)return;
				table.saveDataToFile(CommonData.fileName, true);
				break;
			default:
				table=CommonData.tableManager.getTable(selectorBox.getCaption());
				if(innerState==3)
					Reports.makeResult1_inner(table);
				if(innerState==4)
					languageprocessing.LanguageProcessor.doAll(table);
				break;
			}
			innerState=innerStateDefault;
			CommonData.frame_tableSelector.setVisible(false);
		}
	};
	*/
	
	@Override
	public void setVisible(boolean state) {
		if(state==false) {
			if(working) {
				if(worksQueue.size()>0) {
					worksQueue.remove(0);
					if(worksQueueProcesses_Args.get(0)!=null) 
						worksQueueProcesses_Args.get(0).clear();
					worksQueueProcesses_Code.remove(0);
					worksQueueProcesses_Args.remove(0);
					worksQueueProcesses_Name.remove(0);
						
					working=false;
					prepare();
				}
			}
		}
		super.setVisible(state);
		
	}
}
