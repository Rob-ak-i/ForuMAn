package common;

import java.awt.Canvas;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

import gui.GUITableEditor;
import languageprocessing.LanguageProcessor;
import gui.GUIElementViewer;
import gui.GUISchematicEditor;
import gui.GUISelector;
import parts.BoundedObjectController;
import parts.GenericPhantom;
import parts.ImageController;
import parts.PartBasic;
import unit.DataTable;
import unit.Sequence;
import util.lang.Tokenizer;

public class CommonData {
//SETUP BLOCK
	/**shared memory*/
	private HashMap<String, Object> values;
	public static int WIDTH=800;
	public static int HEIGHT=600;
	public static int HEIGHTFRAMEDIFFERENCE=0;//=85;

	public static String dataSubDir = "data//";
	public static String savesSubDir = "saves//";
	public static String reportsSubDir = "reports//";
	public static String appDir;
	public static String getProgramName(String parameterName) {
		return "Форуман::2021";
	}
	public Object get(String parameterName) {
		return values.get(parameterName);
	}

//VARS BLOCK
	public static Tokenizer tokenizer = new Tokenizer();
	
	public static ImageController imageController = new ImageController();
	public static BoundedObjectController boundedObjectController = new BoundedObjectController();
	
	public static GenericPhantom genericPhantom=new GenericPhantom();
	
	
	
	public static GUISchematicEditor frame=null;
	

	public static Canvas canvas=null;
	public static EntityEditorMouseListener listener;
	public static EntityRenderer renderer=null;
	
	
	public static GUITableEditor frame2=null;

	public static GUISelector frame_reportSelector=null;
	public static GUISelector frame_tableSelector=null;
	public static GUISelector frame_bankSelector=null;
	public static GUISelector frame_sequenceSelector=null;
	
	public static TaskManager taskManager = null;
	
	public static EntityEditor scheme=null;


	public static ArrayList<PartBasic> partsSelected=new ArrayList<PartBasic>();
	public static javax.swing.JEditorPane nameedit=null;
	
	public static javax.swing.JEditorPane captionedit=null;
	
	public static EntityManager<DataTable> tableManager=null;
	public static EntityTextManager textManager=null;		
	public static EntityManager<Sequence> sequenceManager=null;
	
	public static EntityGUIElementViewerManager guiElementViewerManager=null;
	
	public LanguageProcessor languageProcessor ;
	public Reports reports ;
	
	public CommonData() {
		languageProcessor = new languageprocessing.LanguageProcessor();
		reports = new Reports();
		
		values = new HashMap<String, Object> ();
		Field[] fields = this.getClass().getFields();
		int n=fields.length;Field field;int modifiers;
		for(int i=0;i<n;++i) {
			field=fields[i];
			modifiers=field.getModifiers();
			if(Modifier.isPublic(modifiers))
				try {
					values.put(field.getName(), field.get(this));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					System.out.println("Error at:"+this.getClass().getName()+":"+e);
				}
		}
	}
}
