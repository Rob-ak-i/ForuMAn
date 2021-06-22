package common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gui.GUISelector;
import parts.SchematicManager;
import unit.DataTable;
import unit.ManagedObject;
import util.Lists;
import util.Parameters;

public class Reports {
	public static void makeRGZ() {
		
		
	}
	/**marked for garbage*/
	public static void createReport() {
		System.out.println("maybe WIF (now state - garbage)");
		
	}
	public static void createForumReport(String dataTableKey) {
		ReportWriter.writeResult("Report"+" Forum "+ dataTableKey+".htm", dataTableKey, -1, true);
	}
	public static void createUserReport(String dataTableKey, int userID) {
		ReportWriter.writeResult("Report"+" Forum "+ dataTableKey +" User "+Integer.toString(userID)+".htm", dataTableKey, userID, false);
	}
	public static void makeResult1() {
		CommonData.frame_tableSelector.addWork(GUISelector.innerStateUseForScript);
	}public static boolean makeResult1_inner(DataTable selectedTable) {
		ArrayList<String> result=new ArrayList<String>();
		result.add("Список пользователей, отсортированный по числу сообщений");
		ArrayList<Integer> users = new ArrayList<Integer>();
		Lists.getUniqueArrayList(TextForum.getUserIDList(selectedTable), users);
		//DataTable table=new DataTable("userName/String;userMsgCount/Integer");
		DataTable table=selectedTable;
		//"userIDList/Integer;
		//userNameList/String;
		//postIDList/Integer;
		//parentPostIDList/Integer;
		//postTimeList/String;
		//messageList/String"
		table.removeColumn(5);
		table.removeColumn(4);
		table.removeColumn(3);
		ArrayList<Object> ones = new ArrayList<Object>();
		for(int i=0;i<table.nRows();++i) {ones.add(1);}
		table.fillColumn(2, ones, 0);
		int[] tableSortOrder={0};
		boolean[] sortOrderForward= {true};
		table.sortRows(tableSortOrder,sortOrderForward);
		int[] columnsChangeBehavior = {0,0,1};
		table.NarrowByUnique(0, columnsChangeBehavior);
		table.sortRowsByColumnIndexes("-2;-0");
		int[] columnsPrintOrder = {2,0,1};
		table.saveToFile(CommonData.reportsSubDir+"report1.txt", columnsPrintOrder, false);
		return true;
	}
	public static void makeResult2() {
		System.out.println("WIP");
	}
	public static void makeNLP() {
		CommonData.frame_tableSelector.addWork(GUISelector.innerState_languageprocessing);
	}
}
