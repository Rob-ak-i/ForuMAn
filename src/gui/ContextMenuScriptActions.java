package gui;

import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import common.CommonData;
import parts.GenericPhantom;
import parts.PartBasic;
import unit.DataTable;
import unit.ManagedObject;
import unit.MessageBank;
import util.AdditionalDataObjects;

public class ContextMenuScriptActions {

	public static final Action makeMessagesBanksFromDataTablesAction = new AbstractAction("make MessageBank(-s)") {
		public void actionPerformed(ActionEvent event) {
			//CommonData.frame.contextMenu.setVisible(false);
			boolean isGenericPhantomUsed=false;
			for(int i=0;i<CommonData.partsSelected.size();++i) {
				PartBasic part = CommonData.partsSelected.get(i);
				ManagedObject boundedUnit = (ManagedObject)part.boundedUnit;
				if(boundedUnit==null)continue;if(!boundedUnit.getClass().equals(DataTable.class))continue;
				isGenericPhantomUsed=true;Point position = new Point(part.pos.x,part.pos.y);
				CommonData.genericPhantom.tupleAddToPreparation(
						  "partBasic=CommonData.get(\"scheme\").prepareBoundedElement(position);"+'\n'
						+ "messageBank=table.produceMessageBank();"
						+ "CommonData.get(\"textManager\").addManagedElement(messageBank, key);"+'\n'
						+ "CommonData.get(\"languageProcessor\").doAll(messageBank);"+'\n'
						+ "CommonData.get(\"boundedObjectController\").linkUnit(partBasic, messageBank, isGetTexture);"+'\n'
						+ "CommonData.get(\"scheme\").setPartName(partBasic,key);"+'\n'
						+ ""+'\n'
						+ ""+'\n'
						
						,AdditionalDataObjects.packParameters("key", boundedUnit.getKey())
						.pack("table", boundedUnit).pack("isGetTexture", true).pack("position", position)
						, "ContextMenu:make MessageBank(-s)"
						, position
						);
			}
			if(isGenericPhantomUsed)
				CommonData.genericPhantom.tupleAttemptToStart();
		}
	};
	

	public static final Action makeSequencesFromMessageBanksAction = new AbstractAction("make Sequence(-s)") {
		public void actionPerformed(ActionEvent event) {
			boolean destroyLeaves = true;
			//CommonData.frame.contextMenu.setVisible(false);
			boolean isGenericPhantomUsed=false;
			//TODO добавить метод ввода для поиска словосочетаний (хотя бы через настройки)
			int nonLittleLeaveStatement=3; boolean withLeaves=false;
			
			for(int i=0;i<CommonData.partsSelected.size();++i) {
				PartBasic part = CommonData.partsSelected.get(i);
				ManagedObject boundedUnit = (ManagedObject)part.boundedUnit;
				if(boundedUnit==null)continue;if(!boundedUnit.getClass().equals(MessageBank.class))continue;
				isGenericPhantomUsed=true;
				Point position = new Point(part.pos.x,part.pos.y);
				CommonData.genericPhantom.tupleAddToPreparation(
						  "partBasic=CommonData.get(\"scheme\").prepareBoundedElement(position);"+'\n'
						+ "sequenceTree=messageBank.produceSequenceTree(withLeaves, nonLittleLeaveStatement);"
						+ "CommonData.get(\"sequenceManager\").addManagedElement(sequenceTree, key);"+'\n'
						+ "CommonData.get(\"boundedObjectController\").linkUnit(partBasic, sequenceTree, isGetTexture);"+'\n'
						+ "CommonData.get(\"scheme\").setPartName(partBasic,key);"+'\n'
						+ ""+'\n'
						+ ""+'\n'
						
						,AdditionalDataObjects.packParameters("key", boundedUnit.getKey())
						.pack("messageBank", boundedUnit).pack("isGetTexture", true).pack("position", position)
						.pack("withLeaves", withLeaves).pack("nonLittleLeaveStatement",nonLittleLeaveStatement)
						, "ContextMenu:make Sequence(-s)"
						, position
						);
			}
			if(isGenericPhantomUsed)
				CommonData.genericPhantom.tupleAttemptToStart();
		}
	};
}
