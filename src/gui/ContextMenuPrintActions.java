package gui;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;

import common.CommonData;
import parts.GenericPhantom;
import unit.DataTable;
import unit.MessageBank;
import unit.Sequence;
import util.AdditionalDataObjects;

@SuppressWarnings ("serial")
public class ContextMenuPrintActions {
	
	//TODO fixMakeAbstract
	public static final Action printForumSocialNetAction = new AbstractAction("print forum social network") {
		public void actionPerformed(ActionEvent event) {
			Object boundedUnit = CommonData.partsSelected.get(0).boundedUnit;
			DataTable table = (DataTable)boundedUnit;
			int index = CommonData.tableManager.getManagedElementIndexByObject(table);
			if(index==-1)return;
			CommonData.genericPhantom.prepare(GenericPhantom.phantomType_FORUM, index);
		}
	};
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
}
