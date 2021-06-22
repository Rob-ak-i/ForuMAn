package gui;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;

import common.CommonData;
import common.Reports;
import parts.GenericPhantom;
import parts.PartBasic;
import unit.DataTable;
import unit.MessageBank;
import unit.Sequence;
import util.AdditionalDataObjects;
import util.Parameters;

@SuppressWarnings ("serial")
public class ContextMenuViewActions {
	

	public static final Action printForumReport = new AbstractAction("print report: forum") {
		public void actionPerformed(ActionEvent event) {
			Object boundedUnit = CommonData.partsSelected.get(0).boundedUnit;
			DataTable table = (DataTable)boundedUnit;
			String key = CommonData.tableManager.getManagedElementIdentifier(table);
			if(key==null)return;
			Reports.createForumReport(key);
		}
	};
	public static final Action printForumUserReport = new AbstractAction("print report: user") {
		public void actionPerformed(ActionEvent event) {
			PartBasic part =CommonData.partsSelected.get(0);
			
			Parameters pars=part.properties;
			String forumKey=(String) part.getProperty(Parameters.parameter_ForumKey);
			if(forumKey==null) {System.out.println("Could not find field '"+Parameters.parameter_ForumKey+"' or it is not user selected");}
			int userID=(int) part.getProperty(Parameters.parameter_ID);
			
			Reports.createUserReport(forumKey, userID);
		}
	};
	/*
	public static final Action printSequenceAction = new AbstractAction("print sequence") {
		public void actionPerformed(ActionEvent event) {
			Object boundedUnit = CommonData.partsSelected.get(0).boundedUnit;
			Sequence sequence = (Sequence)boundedUnit;
			int index = CommonData.sequenceManager.getManagedElementIndexByObject(sequence);
			if(index==-1)return;
			CommonData.genericPhantom.prepare(GenericPhantom.phantomType_SEQUENCE, index);
		}
	};
	public static final Action printMessagesTestActionWIP = new AbstractAction("!WIP!print TEST INFO ABOUT SYNTAX") {
		public void actionPerformed(ActionEvent event) {
			Object boundedUnit = CommonData.partsSelected.get(0).boundedUnit;
			MessageBank messages = (MessageBank)boundedUnit;
			int index = CommonData.textManager.getManagedElementIndexByObject(messages);
			if(index==-1)return;
			CommonData.genericPhantom.prepare(GenericPhantom.phantomType_TESTSYNTAX, index);
		}
	};
	*/
}
