package gui;

import common.EntityEditor;
import common.EntityGUIElementViewerManager;
import common.CommonData;
import common.EntityManager;
import common.EntityTextManager;
import common.TaskManager;
import unit.DataTable;
import unit.MessageBank;
import unit.Sequence;
import util.ReportObject;

public class GUI {
	public GUI() {
		//preLoad
		CommonData.tableManager= new EntityManager<DataTable>(DataTable.class);
		CommonData.textManager = new EntityTextManager();
		CommonData.sequenceManager = new EntityManager<Sequence>(Sequence.class);
		//load
		CommonData.frame = new GUISchematicEditor();
		CommonData.frame2 = new GUITableEditor();
		//load - may be lazyloadmode
		
		CommonData.frame_reportSelector = new GUISelector(ReportObject.class.getClass(),common.Lang.InnerTable.Item.windowReportSelectorName, null);
		CommonData.frame_tableSelector=new GUISelector(DataTable.class.getClass(),common.Lang.InnerTable.Item.windowTableSelectorName, CommonData.tableManager);
		CommonData.frame_bankSelector=new GUISelector(MessageBank.class.getClass(),common.Lang.InnerTable.Item.windowBankSelectorName, CommonData.textManager);
		CommonData.frame_sequenceSelector=new GUISelector(Sequence.class.getClass(),common.Lang.InnerTable.Item.windowSequenceSelectorName, CommonData.sequenceManager);
		
		
		CommonData.guiElementViewerManager = new EntityGUIElementViewerManager();
		
		
		
		//postLoad
		CommonData.scheme = new EntityEditor(CommonData.WIDTH, CommonData.HEIGHT);
		
		Object commonData=new CommonData();
		CommonData.taskManager = new TaskManager(commonData, commonData.getClass().getSimpleName());
		CommonData.taskManager.start();
		
		
	}

}