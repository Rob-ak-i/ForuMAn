import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import common.FileIO;
import common.Settings;
import util.Lists;
import util.Parameters;
import util.lang.AST;
import util.lang.DilangExpressionParser;
import util.lang.DilangInterpreter;
import util.lang.Token;
import util.lang.Tokenizer;

/**/public class TestSomeThings {

	private static String[] knownTags = {
			"se",
			"pm",
			"a",
			"n"
			};
	private static int tryToReadTag(String text, int pos) {
		char c;int j=0;boolean tagIsClosed=false;int firstIndex=-1,index=-1;int textLength=text.length();
		for(int i=pos+1;i<textLength;++i) {
			c=text.charAt(i);
			if(c=='/'&&j==0) {tagIsClosed=true;continue;}
			if(j>=knownTags.length) {if(c=='>')return j+1+(tagIsClosed?1:0); else return -1;}
			if(j==0)firstIndex=knownTags[j].indexOf(c);else index=knownTags[j].indexOf(c);
			if(firstIndex==-1&&j==0||(j>0&&firstIndex!=index&&c!='>'))return -1;
			if(c=='>')return j+1+(tagIsClosed?1:0);
			j++;
		}
		return j+(tagIsClosed?1:0);
	}
	
	public static void main(String[] args) throws Exception {
		Class a = HashMap.class;
		Class b = Map.class;
		printer("HashMapInterfaces=");printer(Lists.getArrayList( a.getInterfaces()));
		printer("MapInterfaces=");printer(Lists.getArrayList(b.getInterfaces()));
		printer("HashMapClasses=");printer(Lists.getArrayList(a.getClasses()));
		printer("MapClasses=");printer(Lists.getArrayList(b.getClasses()));
		if(true)return;
		autotestDilang();
		//testProcedure();
		String text=null,fileName=null, appPath=null;{try{appPath=new File( "." ).getCanonicalPath();}catch(Exception e) {}}if(appPath==null)return;
		fileName=appPath.concat("/src/TestSomeThings.java");
		//fileName=appPath.concat("/saves/Discuss2linear.html");
		text=FileIO.readTextFile(fileName);
		printAST(text,true);
		
	}
	public static void printer(Object value) {
		System.out.println(value.toString());
	}

	public static void printAST(String text, boolean isDocument) throws IOException {
		Tokenizer tokenizer = new Tokenizer();
		DilangInterpreter interpreter = new DilangInterpreter ();
		//printer(text);
		//printer("");
		ArrayList<Token> tokens = tokenizer.tokenizeAndFilter(text);
		AST result = interpreter.makeAST(tokens, isDocument);
		String resultAST=DilangExpressionParser.printAST(result);
		printer(resultAST);
	}
	public static void testPEG() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in) );
		String input=null;Settings.DEBUG=true;
		do {
			System.out.flush();
			input = reader.readLine();
			if(input==null||input.length()==0)return;
			printAST(input, false);
		}while(input!=null&&input.length()>0);
		if(true)System.exit(0);
		
	}

	public static void testDilang() throws IOException {
		DilangInterpreter interpreter = new DilangInterpreter ();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in) );
		String input=null;Settings.DEBUG=true;String resultAST;
		AST script;
		Parameters arguments = new Parameters();
		do {
			System.out.flush();
			input = reader.readLine();
			if(input==null||input.length()==0)return;
			Settings.DEBUG=false;
			script=interpreter.parseDilang(input);
			Settings.DEBUG=true;
			resultAST=DilangExpressionParser.printAST(script);
			printer(resultAST);
			interpreter.processInArguments(script, arguments);
			printer("arguments: ");
			printer(arguments);
		}while(input!=null&&input.length()>0);
		interpreter.clear();
		if(true)System.exit(0);
	}
	public static void autotestDilang() {
		String[][] tests={
				{"a=\"null\";b=\"false\";c=null;d=true;",
				""}
				,{"a=3;if(a==5)b=3;else{c=2;d=1;}",
				"a=3;c=2;d=1;"}
				,{"n=5;a=0;for(i=0;i<n;i=i+1)a=a+i^2;",
					"n=5;"}
				,{"n=5;a=0;i=0;while(i<n)a=a+i^2;",
					"n=5;"}
				,{"n=5;a=0;i=0;do a=a+n^2;while(a<n);i=a;",
					"n=5;"}
				//,{}
		};

		DilangInterpreter interpreter = new DilangInterpreter ();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in) );
		String input=null;;String resultAST;
		AST scriptAST=null;
		Parameters arguments = new Parameters(),argsTrue=null;
		for(int i=0;i<tests.length;++i) {
			printer("autotest["+Integer.toString(i)+"]:\n----("+tests[i][0]+")\n----("+tests[i][1]+")");
			arguments.clear();
			input=(String)tests[i][0];
			if(input.length()==0) {printer("Empty string found, terminating...");System.exit(0);}
			argsTrue=interpreter.processStringToArguments(tests[i][1]);
			scriptAST = interpreter.parseDilang(input);
			printer("Printing tree:");
			resultAST=DilangExpressionParser.printAST(scriptAST);
			printer(resultAST);
			printer("Printing execution tree:");
			interpreter.processInArguments(scriptAST, arguments);
			printer("checking:");
			printer("----output  : "+arguments.toString());
			printer("----argsTrue: "+argsTrue.toString());
			if(!argsTrue.equalsTo(arguments, false)) {
				printer("Test["+Integer.toString(i)+"] failed!");
				System.exit(-1);
			}
			
		}
		printer("All tests checked!");System.exit(0);
	}
	public static void testProcedure() throws IOException {
		DilangInterpreter interpreter = new DilangInterpreter ();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in) );
		String input=null;Settings.DEBUG=true;String resultAST;
		AST result;
		do {
			System.out.flush();
			input = reader.readLine();
			if(input==null||input.length()==0)return;
			result = interpreter.parseDilang(input);
			resultAST=DilangExpressionParser.printAST(result);
			printer(resultAST);
		}while(input!=null&&input.length()>0);
		interpreter.clear();
		if(true)System.exit(0);
	}
}
