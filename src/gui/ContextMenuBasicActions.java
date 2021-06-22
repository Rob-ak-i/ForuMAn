package gui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import common.CommonData;
import common.EntityEditorMouseListener;
import common.EntityManager;
import common.EntityRenderer;
import parts.GenericPhantom;
import unit.DataTable;
import unit.ManagedObject;
import unit.MessageBank;
import unit.Sequence;
import util.AdditionalDataObjects;
import util.Lists;
import util.Parameters;
import util.StringUtils;

public class ContextMenuBasicActions {
	private static final String txtFileExtension = ".txt";
	private static final String messageBankFileExtension = ".messageBank";
	private static final String sequenceFileExtension = ".sequence";
	private static final TextFileFilter txtFilter = new TextFileFilter(txtFileExtension);
	private static final TextFileFilter messageBankFilter = new TextFileFilter(messageBankFileExtension);
	private static final TextFileFilter sequenceFilter = new TextFileFilter(sequenceFileExtension);
	public static final Action concatAction = new AbstractAction("concat elements") {
		public void actionPerformed(ActionEvent event) {
			//CommonData.frame.contextMenu.setVisible(false);
			Object boundedUnit=null, nowBoundedUnit=null;
			boundedUnit = CommonData.partsSelected.get(0).boundedUnit;
			Class boundedUnitClass=null;//boundedUnit.getClass();
			ArrayList units = new ArrayList();boolean WIPLatch=false;
			for(int i=0;i<CommonData.partsSelected.size();++i) {
				nowBoundedUnit = CommonData.partsSelected.get(i).boundedUnit;
				
				if(!WIPLatch&&nowBoundedUnit==null) {WIPLatch=true;System.out.println("concatenation of nonbounded parts (i.e. by inner parameters) will be in future");continue;}//if(nowBoundedUnit==null) {new javax.swing.JDialog(new JFrame(), "Error: not all selected objects in similar group"); return;}
				//if(boundedUnitClass==null) {boundedUnitClass=nowBoundedUnit.getClass();}
				//else {if(boundedUnitClass!=nowBoundedUnit.getClass()){new javax.swing.JDialog(new JFrame(), "Error: not all selected objects in similar group"); return;}}
				units.add(nowBoundedUnit);
			}
			
			ArrayList<ArrayList<Object>> sortedElements = Lists.sortObjectsByClass(units);
			ArrayList<Object> nowSimilarGroup;
			EntityManager nowManager;EntityManager nowMgr=null;boolean tupleExists=false;
			for(int i=0;i<sortedElements.size();++i) {
				nowSimilarGroup=sortedElements.get(i);
				boundedUnitClass=nowSimilarGroup.get(0).getClass();
				//performing concatenation in matched manager
				if(Lists.getIndex(boundedUnitClass.getAnnotatedInterfaces(), ManagedObject.class.getClass())!=-1) {
					nowMgr = ((ManagedObject)nowSimilarGroup.get(0)).getManager();
					if(nowMgr==null) {System.out.print("Manager for object \'");System.out.print(nowSimilarGroup.get(0));System.out.print("\' not exists!");continue;}
					boundedUnit = nowMgr.concatElements(nowSimilarGroup);
					String scriptCode = "CommonData.get(\"scheme\").createBoundedElement(position, boundedUnit);";
					Parameters scriptArgs = AdditionalDataObjects.packParameters("boundedUnit", boundedUnit);
					String scriptName = "concat elements";
					CommonData.genericPhantom.tupleAddToPreparation(
							scriptCode, 
							scriptArgs,
							scriptName,
							null);
					tupleExists=true;
				}
			}
			if(tupleExists)
				CommonData.genericPhantom.tupleAttemptToStart();
			/*
			//TODO fixMakeAbstract - CHECK AND CLEAR!
			if(boundedUnitClass==DataTable.class) {
				DataTable table = CommonData.tableManager.concatElements(units);
				//CommonData.genericPhantom.prepare(GenericPhantom.phantomType_FORUM, CommonData.tableManager.getManagedElementIndex(table));
				PackedProcessData processData = AdditionalDataObjects.pack(
						CommonData.scheme, 
						AdditionalDataObjects.packParameters("linkedObject", table), 
						"process \'createBoundedElement\'; addResultToStack"
						);
				CommonData.genericPhantom.tupleAddToPreparation(processData, null);
				CommonData.genericPhantom.tupleAttemptToStart();
			}
			*/
		}
	};
	public static final Action saveAction = new  AbstractAction("save element as") {
		public void actionPerformed(ActionEvent event) {
			//CommonData.frame.contextMenu.setVisible(false);
			String fileName=null;
			Object boundedUnit=null;
			boundedUnit = CommonData.partsSelected.get(0).boundedUnit;
			if(boundedUnit==null)return;
			
			JFileChooser jf= new  JFileChooser(CommonData.appDir);
			TextFileFilter selectedFilter = null;
			String selectedExtension = null;
			int selectedClass=-1;
			
			while(true) {
				if(boundedUnit.getClass().getSimpleName().equals(DataTable.class.getClass().getSimpleName())){
					jf.addChoosableFileFilter(txtFilter);
					selectedClass=0;
					selectedFilter=txtFilter;
					selectedExtension = txtFileExtension;
					break;
				}
				if(boundedUnit.getClass().getSimpleName().equals(MessageBank.class.getClass().getSimpleName())) {
					jf.addChoosableFileFilter(messageBankFilter);
					selectedClass=1;
					selectedFilter=messageBankFilter;
					selectedExtension = messageBankFileExtension;
					break;
				}
				if(boundedUnit.getClass().getSimpleName().equals(Sequence.class.getClass().getSimpleName())) {
					jf.addChoosableFileFilter(sequenceFilter);
					selectedClass=2;
					selectedFilter=sequenceFilter;
					selectedExtension = sequenceFileExtension;
					break;
				}
				break;
			}
			int result = jf.showSaveDialog(null);
			if(result==JFileChooser.APPROVE_OPTION) {
				fileName = jf.getSelectedFile().getAbsolutePath();
			}else return;
			if(selectedFilter==null)return;
			
			if(fileName.indexOf(selectedExtension, fileName.length()-1-selectedExtension.length())<0)fileName+=selectedExtension;
			if(selectedClass>=0&&selectedClass<=2) {
				String scriptCode = "manager.saveElementToFile(fileName, boundedUnit);";
				Parameters scriptArgs = AdditionalDataObjects.packParameters("manager", ((ManagedObject)boundedUnit).getManager())
						.pack("fileName",fileName);
				String scriptName = "ContextMenu:saveToFile";
				CommonData.taskManager.addOperation(
						scriptCode,
						scriptArgs,
						scriptName
						);
			}
		}
	};

	public static final Action loadAction = new  AbstractAction("load files") {
		public void actionPerformed(ActionEvent event) {
			//CommonData.frame.contextMenu.setVisible(false);
			if(CommonData.genericPhantom.working)return;
			JFileChooser jf= new  JFileChooser(CommonData.appDir);
			jf.setMultiSelectionEnabled(true);
			jf.addChoosableFileFilter(new  TextFileFilter(".txt"));
			jf.addChoosableFileFilter(new  TextFileFilter(".htm,.html"));
			//jf.addChoosableFileFilter(new  TextFileFilter(".schematic"));
			int  result = jf.showOpenDialog(null);
			//CommonData.panel.requestFocus();
			if(result==JFileChooser.APPROVE_OPTION) {
				File[] files = null;//ArrayList<String> files = new ArrayList<String>();
				ArrayList<String> fileNames = new ArrayList<String>();
				try {
					files = jf.getSelectedFiles();
					for(int i=0;i<files.length;++i)
						fileNames.add(files[i].getCanonicalPath());
					files=null;
				} catch (IOException e) {
					System.out.println(common.Lang.InnerTable.Action.fileNotFoundName);
					System.out.println(jf.getSelectedFile().toString());
					return;
				}
				
				String fileName=null;boolean isGenericPhantomUsed=false;String fileExtension=null;
				for(int i=0;i<fileNames.size();++i) {
					fileName = fileNames.get(i);
					fileExtension = StringUtils.getFileExtension(fileName);
					/*
					if(fileName.indexOf(".schematic", fileName.length()-12)>=0)
						CommonData.taskManager.addOperation(
								AdditionalDataObjects.pack(
										CommonData.scheme,
										"loadResult",
										AdditionalDataObjects.packParameters("fileName",fileName),
										null
										)
								);
					*/
					if(fileExtension.equalsIgnoreCase("txt")||(fileExtension.indexOf("htm")>=0)) {
						CommonData.genericPhantom.tupleAddToPreparation(
								  "partBasic=CommonData.get(\"scheme\").prepareBoundedElement(position);" + '\n'
								+ "tableID=CommonData.get(\"tableManager\").loadManagedElement(fileName, \"\",fileExtension);" + '\n'
								+ "table=CommonData.get(\"tableManager\").getManagedElement(tableID);" + '\n'
								+ "CommonData.get(\"boundedObjectController\").linkUnit(partBasic, table,getTexture);" + '\n'
								+ "CommonData.get(\"scheme\").setPartName(partBasic,tableID);" + '\n'
								+ "" + '\n',
								AdditionalDataObjects.packParameters("fileName", fileName).pack("getTexture", true).pack("fileExtension", fileExtension)
								,"loading htm"
								,null
								);
						isGenericPhantomUsed=true;
						//CommonData.taskManager.addOperation(CommonData.tableManager,"loadDiscuss",AdditionalDataObjects.pack("fileName",CommonData.fileName));//CommonData.scheme.loadDuscuss(CommonData.fileName);
					}
				}
				if(isGenericPhantomUsed) {
					CommonData.genericPhantom.tupleAttemptToStart();
				}
			}
		}
	};
}
