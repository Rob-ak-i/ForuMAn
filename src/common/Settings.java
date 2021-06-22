package common;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import parts.ImageController;
import unit.DataTable;
import unit.MessageBank;
import unit.Sequence;

public class Settings {
	public static boolean DEBUG = true;
	/** in UNIX is '/', but in Windows is '\\'
	 * */
	public static char linkDelimiter='\\';
	/** application directory path, like this:
	 * "/home/user1/Documents/Zen/projects/schematicSolver"
	 * */
	public static String appPath="";
	/** full application directory path, like this:
	 * "/home/user1/Documents/Zen/projects/schematicSolver/"
	 * */
	public static int language=1;
	public static String[] languages = {"EN", "RU"};
	public static boolean isWindows=false;
	public static String reportSubDirectory="report";
	public static String appPathFull="";
	private static void getSymbolicLinks() {
		File file=null;
		try {
			file=new File( "." );
			appPath=file.getCanonicalPath();
		} catch (IOException e) {e.printStackTrace();}
		appPathFull=file.getAbsoluteFile().toString();
		appPathFull=appPathFull.substring(0,appPathFull.length()-1);
		linkDelimiter=appPathFull.charAt(appPathFull.length()-1);
		return;
	}
	private static void setCommonData() {
		CommonData.dataSubDir = appPathFull+"data"+linkDelimiter;
		CommonData.savesSubDir = appPathFull+"saves"+linkDelimiter;
		CommonData.reportsSubDir = appPathFull+"reports"+linkDelimiter;
		CommonData.appDir = appPath;
	}
	private static void registerTextures() {
		CommonData.imageController.registerImage("file", CommonData.dataSubDir+"part_file.png");
		CommonData.imageController.registerImage(DataTable.class.getSimpleName(), CommonData.dataSubDir+"part_database.png");
		CommonData.imageController.registerImage(MessageBank.class.getSimpleName(), CommonData.dataSubDir+"part_messagebank.png");
		CommonData.imageController.registerImage(MessageBank.class.getSimpleName()+"enhanced", CommonData.dataSubDir+"part_messagebank_enhanced.png");
		CommonData.imageController.registerImage(Sequence.class.getSimpleName(), CommonData.dataSubDir+"part_sequence.png");
	}
	public static void initialize() {
		getSymbolicLinks();
		setCommonData();
		registerTextures();
		//try {Lang.SaveInnerTable(CommonData.dataSubDir+"LangOutDBG.txt");} catch (Exception e) {}
		isWindows = System.getProperty("os.name").contains("Windows");
		String languageFileName="Lang";
		languageFileName=languageFileName.concat(languages[language]);
		if(isWindows)
			languageFileName=languageFileName.concat("_Windows.txt");
		else
			languageFileName=languageFileName.concat("_UNIX.txt");
		Lang.LoadInnerTable(CommonData.dataSubDir+languageFileName);
		//stupidly saves to same file
		System.out.println("LanguageFile: "+languageFileName);
		try {Lang.SaveInnerTable(CommonData.dataSubDir+languageFileName);} catch (Exception e) {}
		//try {Lang.SaveInnerTable(CommonData.dataSubDir+"Lang"+languages[language]+"_debug.txt");} catch (Exception e) {}
	}

	public static String returnFullFileName(String fileName, String fileDirectory) {
		return appPathFull+fileDirectory+linkDelimiter+fileName;
	}
	public static String returnFullFileName(String fileName, String fileDirectory, String fileSubDirectory) {
		return appPathFull+fileDirectory+linkDelimiter+fileSubDirectory+linkDelimiter+fileName;
	}
}
