package util.lang;

import java.util.ArrayList;
import java.util.HashMap;

import util.Lexicon;

public class ASTLexicon extends Lexicon{
	public static final String UNKNOWN = "UNKNOWN";public int code_UNKNOWN=-1;
	//TODO for future releases
	//public static final String ROOT = "ROOT";public int code_ROOT=-1;
	//public static final String TERMINAL = "TERMINAL";public int code_TERMINAL=-1;
	//public static final String CONSOLE = "CONSOLE";public int code_CONSOLE=-1;
	public static final String DOCUMENT = "DOCUMENT";public int code_DOCUMENT=-1;
	public static final String IMPORT= "IMPORT";public int code_IMPORT=-1;
	public static final String PACKAGE= "PACKAGE";public int code_PACKAGE=-1;
	
	public static final String CLASS = "CLASS";public int code_CLASS=-1;
	
	/**объявление скрипта, подразумевает чтение одной единственной функции с передаваемыми ей параметрами типа Parameters*/
	public static final String SCRIPT = "SCRIPT";public int code_SCRIPT =-1;
	
	public static final String CONTINUE = "CONTINUE";public int code_CONTINUE=-1;
	public static final String BREAK = "BREAK";public int code_BREAK=-1;
	
	public static final String RETURN = "RETURN";public int code_RETURN=-1;
	public static final String BRANCH= "BRANCH";public int code_BRANCH=-1;
	public static final String BRANCH_FULL= "BRANCH_FULL";public int code_BRANCH_FULL=-1;
	public static final String BRANCH_SWITCH= "BRANCH_SWITCH";public int code_BRANCH_SWITCH=-1;
	public static final String BRANCH_SWITCH_CASE= "BRANCH_SWITCH_CASE";public int code_BRANCH_SWITCH_CASE=-1;
	public static final String BRANCH_SWITCH_DEFAULT= "BRANCH_SWITCH_DEFAULT";public int code_BRANCH_SWITCH_DEFAULT=-1;
	public static final String FOR = "FOR";public int code_FOR=-1;
	public static final String WHILE = "WHILE";public int code_WHILE=-1;
	public static final String DO = "DO";public int code_DO=-1;
	public static final String FUNCTION_INNER = "FUNCTION_INNER";public int code_FUNCTION_INNER=-1;
	public static final String INCLUSION = "INCLUSION";public int code_INCLUSION=-1;
	public static final String BODY = "BODY";public int code_BODY=-1;

	public static final String VARIABLE_ASSIGN = "VARIABLE_ASSIGN";public int code_VARIABLE_ASSIGN=-1;
	public static final String VARIABLE_DECLARATION = "VARIABLE_DECLARATION";public int code_VARIABLE_DECLARATION=-1;
	public static final String FUNCTION_DECLARATION = "FUNCTION_DECLARATION";public int code_FUNCTION_DECLARATION=-1;
	
	public static final String IDENTIFIER = "IDENTIFIER";public int code_IDENTIFIER=-1;
	public static final String MODIFIER= "MODIFIER";public int code_MODIFIER=-1;
	public static final String TYPE= "TYPE";public int code_TYPE=-1;
	
	
	//variables and functions - is similar, because we can send function as variable
	public static final String VARIABLE = "VARIABLE";public int code_VARIABLE=-1;
	public static final String METHOD = "METHOD";public int code_METHOD=-1;
	public static final String CLASS_FIELD = "CLASS_FIELD";public int code_CLASS_FIELD=-1;
	public static final String CLASS_METHOD = "CLASS_METHOD";public int code_CLASS_METHOD=-1;
	
	
	public static final String FUNCTION_CALL = "FUNCTION_CALL";public int code_FUNCTION_CALL=-1;
	//public static final String VARIABLE_DEFINITION = "VARIABLE_DEFINITION";public int code_VARIABLE_DEFINITION=-1;

	//public static final String CONSTANT = "CONSTANT";public int code_CONSTANT=-1;
	public static final String CONSTANT_NULL = "CONSTANT_NULL";public int code_CONSTANT_NULL=-1;
	public static final String CONSTANT_LOGIC = "CONSTANT_LOGIC";public int code_CONSTANT_LOGIC=-1;
	public static final String CONSTANT_DIGIT = "CONSTANT_DIGIT";public int code_CONSTANT_DIGIT=-1;
	public static final String CONSTANT_CHARACTER = "CONSTANT_CHARACTER";public int code_CONSTANT_CHARACTER=-1;
	public static final String CONSTANT_STRING = "CONSTANT_STRING";public int code_CONSTANT_STRING=-1;
	
	public static final String EXPRESSION = "EXPRESSION";public int code_EXPRESSION=-1;
	public static final String NEW_INSTANCE = "NEW_INSTANCE";public int code_NEW_INSTANCE=-1;
	public static final String NEW_INSTANCE_ARRAY = "NEW_INSTANCE_ARRAY";public int code_NEW_INSTANCE_ARRAY=-1;
	//public static final String OPERATOR = "OPERATOR";public int code_OPERATOR=-1;
	public static final String OPERATION = "OPERATION";public int code_OPERATION=-1;
	//public static final String OPERATION_UNARY = "OPERATION_UNARY";public int code_OPERATION_UNARY=-1;
	//public static final String OPERATION_BINARY = "OPERATION_BINARY";public int code_OPERATION_BINARY=-1;
	//public static final String OPERATION_TERNARY = "OPERATION_TERNARY";public int code_OPERATION_TERNARY=-1;
	//public static final String CONDITION = "CONDITION";public int code_CONDITION=-1;
	
	
	public ASTLexicon () {
		super(null, null);
		this.addLabels(this);
		
	}
	
	
}
