package util.lang;

import java.util.ArrayList;
import java.util.HashMap;

import util.IntList;
import util.Lists;
/**Управление списком тегов-меток, которые могут использоваться в формировании и применении правил к анализатору.<br>
 * Умеет выводить значения по умолчанию*/
public class TokenLexicon {
	
	public static final String UNKNOWN= "UNKNOWN";public int code_UNKNOWN=-1;

	public static final String METHOD_CALL = "METHOD_CALL";public int code_METHOD_CALL=-1;
	public static final String IDENTIFIER = "IDENTIFIER";public int code_IDENTIFIER=-1;
	public static final String MODIFIER = "MODIFIER";public int code_MODIFIER=-1;
	public static final String CONSTRUCTION = "CONSTRUCTION";public int code_CONSTRUCTION=-1;
	public static final String COMMAND = "COMMAND";public int code_COMMAND=-1;
	public static final String TYPE = "TYPE";public int code_TYPE=-1;
	public static final String OPERAND = "OPERAND";public int code_OPERAND=-1;
	public static final String OPERAND_UNARY= "OPERAND_UNARY";public int code_OPERAND_UNARY=-1;
	public static final String OPERAND_BINARY = "OPERAND_BINARY";public int code_OPERAND_BINARY=-1;
	public static final String OPERAND_TERNARY = "OPERAND_TERNARY";public int code_OPERAND_TERNARY=-1;
	public static final String OPERAND_TERNARY_FIRST = "OPERAND_TERNARY_FIRST";public int code_OPERAND_TERNARY_FIRST=-1;
	public static final String OPERAND_TERNARY_SECOND = "OPERAND_TERNARY_SECOND";public int code_OPERAND_TERNARY_SECOND=-1;
	public static final String NULL = "NULL";public int code_NULL=-1;
	public static final String LOGIC = "LOGIC";public int code_LOGIC=-1;
	public static final String SYMBOL = "SYMBOL";public int code_SYMBOL=-1;
	public static final String CONSTANT = "CONSTANT";public int code_CONSTANT=-1;
	public static final String DIGIT = "DIGIT";public int code_DIGIT=-1;
	public static final String STRING = "STRING";public int code_STRING=-1;
	public static final String CHARACTER = "CHARACTER";public int code_CHARACTER=-1;
	public static final String COMMENTARY = "COMMENTARY";public int code_COMMENTARY=-1;
	public static final String TRUE = "TRUE";public int code_TRUE=-1;
	public static final String FALSE = "FALSE";public int code_FALSE=-1;
	public static final String EQUALIZATION = "EQUALIZATION";public int code_EQUALIZATION=-1;
	public static final String EQUALIZATIONMODIFIED = "EQUALIZATIONMODIFIED";public int code_EQUALIZATIONMODIFIED=-1;
	public static final String DOT = "DOT";public int code_DOT=-1;
	public static final String SEMICOLON = "SEMICOLON";public int code_SEMICOLON =-1;
	public static final String ZAPYATAYA = "ZAPYATAYA";public int code_ZAPYATAYA =-1;
	public static final String NEW = "NEW";public int code_NEW=-1;

	public static final String PLUS = "PLUS";public int code_PLUS =-1;
	public static final String MINUS = "MINUS";public int code_MINUS =-1;
	public static final String MULTIPLY = "MULTIPLY";public int code_MULTIPLY =-1;
	public static final String DIVIDE = "DIVIDE";public int code_DIVIDE =-1;
	public static final String MOD = "MOD";public int code_MOD=-1;
	public static final String POWER = "POWER";public int code_POWER=-1;
	public static final String CONJUNCTION = "CONJUNCTION";public int code_CONJUNCTION=-1;
	public static final String DISJUNCTION = "DISJUNCTION";public int code_DISJUNCTION=-1;

	public static final String OPEN = "OPEN";public int code_OPEN=-1;
	public static final String CLOSE = "CLOSE";public int code_CLOSE=-1;
	public static final String BRACE = "BRACE";public int code_BRACE=-1;
	public static final String BRACE_INSTRUCTIONS = "BRACE_INSTRUCTIONS";public int code_BRACE_INSTRUCTIONS=-1;
	public static final String BRACE_VALUES = "BRACE_VALUES";public int code_BRACE_VALUES=-1;
	public static final String BRACE_INDEX = "BRACE_INDEX";public int code_BRACE_INDEX=-1;
	public static final String BRACE_PARAMETERS = "BRACE_PARAMETERS";public int code_BRACE_PARAMETERS=-1;
	
	public static final String ARRAYDEFINITION = "ARRAYDEFINITION";public int code_ARRAYDEFINITION =-1;
	
	public ArrayList<String> labelsList;
	private static final String[] __labels = {UNKNOWN, METHOD_CALL,
			IDENTIFIER,MODIFIER,CONSTRUCTION,COMMAND,TYPE,OPERAND,
			OPERAND_UNARY, OPERAND_BINARY, OPERAND_TERNARY, OPERAND_TERNARY_FIRST, OPERAND_TERNARY_SECOND,
			NULL,LOGIC,SYMBOL,CONSTANT,DIGIT,STRING,CHARACTER,COMMENTARY,TRUE,FALSE,NEW,
			EQUALIZATION, EQUALIZATIONMODIFIED, DOT, SEMICOLON, ZAPYATAYA,
			PLUS, MINUS,MULTIPLY, DIVIDE, MOD, POWER, CONJUNCTION, DISJUNCTION,
			OPEN, CLOSE, BRACE, BRACE_INSTRUCTIONS, BRACE_VALUES, BRACE_INDEX, BRACE_PARAMETERS
			
			, ARRAYDEFINITION
			};

	private IntList label_DIGIT_CONST = null;
	private IntList label_STRING_CONST = null;
	private IntList label_COMMENTARY = null;
	private IntList label_CHAR_CONST = null;
	private IntList label_LOGIC_TRUE = null;
	private IntList label_LOGIC_FALSE = null;
	private IntList label_NULL_CONST = null;
	private IntList label_METHOD_CALL = null;
	
	public HashMap<String, IntList> labelsMap;private IntList label_IDENTIFIER = null;
	public HashMap<Integer, IntList> labelsMapSYMBOL;private IntList label_SYMBOL = null;
	/**for symbols*/
	private void addLabel(int labelCode, int tokenName) {
		if(!labelsMapSYMBOL.containsKey(tokenName))labelsMapSYMBOL.put(tokenName, new IntList()); 
		IntList tokenLabels =labelsMapSYMBOL.get(tokenName); if(!tokenLabels.has(labelCode))tokenLabels.add(labelCode);
	}
	/**for strings*/
	private void addLabel(int labelCode, String tokenName) {
		if(!labelsMap.containsKey(tokenName))labelsMap.put(tokenName, new IntList()); 
		IntList tokenLabels =labelsMap.get(tokenName); if(!tokenLabels.has(labelCode))tokenLabels.add(labelCode);
	}
	private void addLabelsForCharInner(int labelCode, int charTokenName){if(!labelsMapSYMBOL.containsKey(charTokenName))labelsMapSYMBOL.put(charTokenName, new IntList()); IntList tokenLabels =labelsMapSYMBOL.get(charTokenName); if(!tokenLabels.has(labelCode))tokenLabels.add(labelCode);}
	private void addLabelsForChar(int labelCode, String charTokenNameString){int charTokenName=charTokenNameString.charAt(0);addLabelsForCharInner(labelCode, charTokenName);}
	//private void addLabelsForMultichar(int labelCode, String multiCharTokenName) {int charTokenName = multiCharTokenName.charAt(0)+0x100*multiCharTokenName.charAt(1);addLabelsForCharInner(labelCode, charTokenName);}
	public IntList getLabels(String stroka) {return labelsMap.getOrDefault(stroka, label_IDENTIFIER);}
	public IntList getLabels(int symbol) {return labelsMapSYMBOL.getOrDefault(symbol, label_SYMBOL);}
	private void addIdentifierToArray(String identifierName, String[] tokens, boolean isCharactersTokens) {
		int nowIdentifierCode = labelsList.indexOf(identifierName);
		if(nowIdentifierCode==-1) {System.out.println("Inner Error! (2342345) Wrong identifier - \'"+identifierName+"\'"); return;}
		for(String token:tokens) {
			if(!isCharactersTokens)
				addLabel(nowIdentifierCode, token);
			else {
				//if(token.length()==2)
				//	addLabelsForMultichar(nowIdentifierCode, token);
				//else
					addLabelsForChar(nowIdentifierCode, token);
			}
		}
		
	}
	private void addLabels() {
		String nowLabel=null, nowFieldName=null;
		Class thisClass = this.getClass();
		for(int i=0;i<__labels.length;++i) {
			nowLabel=__labels[i];
			labelsList.add(nowLabel);
			nowFieldName="code_"+nowLabel;
			try {
				thisClass.getField(nowFieldName).setInt(this, i);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}
	private void addConstantLabels() {

		label_METHOD_CALL = new IntList();label_METHOD_CALL.add(code_METHOD_CALL);
		label_IDENTIFIER = new IntList();label_IDENTIFIER.add(code_IDENTIFIER);
		label_SYMBOL = new IntList();label_SYMBOL.add(code_SYMBOL);
		label_DIGIT_CONST = new IntList();label_DIGIT_CONST.add(code_DIGIT);label_DIGIT_CONST.add(code_CONSTANT);
		label_STRING_CONST = new IntList();label_STRING_CONST.add(code_STRING);label_STRING_CONST.add(code_CONSTANT);
		label_COMMENTARY = new IntList();label_COMMENTARY.add(code_COMMENTARY);
		label_CHAR_CONST = new IntList();label_CHAR_CONST.add(code_CHARACTER);label_CHAR_CONST.add(code_CONSTANT);
		label_LOGIC_TRUE = new IntList();label_LOGIC_TRUE.add(code_LOGIC);label_LOGIC_TRUE.add(code_TRUE);label_LOGIC_TRUE.add(code_CONSTANT);
		label_LOGIC_FALSE= new IntList();label_LOGIC_FALSE.add(code_LOGIC);label_LOGIC_FALSE.add(code_FALSE);label_LOGIC_FALSE.add(code_CONSTANT);
		label_NULL_CONST = new IntList();label_NULL_CONST.add(code_NULL);label_NULL_CONST.add(code_CONSTANT);
	}
	public TokenLexicon() {
		labelsList = new ArrayList<String>();
		labelsMap = new HashMap<String, IntList>();
		labelsMapSYMBOL = new HashMap<Integer, IntList>();
		//constructing indexes for each label
		addLabels();
		//constructing label indexes to each token
		addConstantLabels();

		addIdentifierToArray(OPEN, __OPEN ,true);
		for(int i=0;i<__OPEN.length;++i) {String eqC=__OPEN[i];int lblCode=labelsList.indexOf(__BRACES[i]);addLabelsForChar(lblCode,eqC);}
		addIdentifierToArray(CLOSE, __CLOSE ,true);
		for(int i=0;i<__CLOSE.length;++i) {String eqC=__CLOSE[i];int lblCode=labelsList.indexOf(__BRACES[i]);addLabelsForChar(lblCode,eqC);}
		addIdentifierToArray(BRACE,__OPEN,true);
		addIdentifierToArray(BRACE,__CLOSE,true);
		
		addIdentifierToArray(OPERAND,__binaryOperands, true);
		for(int i=0;i<__binaryOperands.length;++i) {String eqC=__binaryOperands[i];int lblCode=labelsList.indexOf(__binaryOperandsLabels[i]);addLabelsForChar(lblCode,eqC);}

		addIdentifierToArray(OPERAND,__ternaryOperands, true);
		addIdentifierToArray(OPERAND_TERNARY,__ternaryOperands, true);
		addLabelsForCharInner(labelsList.indexOf(OPERAND_TERNARY_FIRST), '?');
		addLabelsForCharInner(labelsList.indexOf(OPERAND_TERNARY_SECOND), ':');

		addIdentifierToArray(IDENTIFIER,__nullaryLogicOperands, false);
		addIdentifierToArray(LOGIC,__nullaryLogicOperands, false);
		addIdentifierToArray(LOGIC,__unaryLogicOperands, true);
		addIdentifierToArray(LOGIC,__binaryLogicOperands, true);
		addIdentifierToArray(LOGIC,__binaryLogicOperandsMULTICHAR, false);
		addIdentifierToArray(OPERAND,__nullaryLogicOperands, false);
		addIdentifierToArray(OPERAND,__unaryLogicOperands, true);
		addIdentifierToArray(OPERAND,__binaryLogicOperands, true);
		addIdentifierToArray(OPERAND,__binaryLogicOperandsMULTICHAR, false);
		
		addLabel(code_NULL,"null");
		
		
		addIdentifierToArray(OPERAND_UNARY,__unaryOperands, true);
		addIdentifierToArray(OPERAND_BINARY,__binaryOperands, true);
		addIdentifierToArray(OPERAND_BINARY,__binaryLogicOperands, true);
		addIdentifierToArray(OPERAND_BINARY,__binaryLogicOperandsMULTICHAR, false);
		
		
		addIdentifierToArray(MODIFIER,__modifiers, false);
		addIdentifierToArray(CONSTRUCTION,__constructions, false);
		addIdentifierToArray(COMMAND,__commands, false);
		addIdentifierToArray(TYPE,__basicTypes, false);

		
		addLabelsForCharInner(code_TYPE,'[');
		addLabelsForCharInner(code_TYPE,']');
		addLabelsForCharInner(code_TYPE,'<');
		addLabelsForCharInner(code_TYPE,'>');
		addLabel(code_TYPE,"[]");
		
		addLabel(code_NEW,"new");
		
		//addLabelsForMultichar(code_TYPE,"[]");
		addLabelsForCharInner(code_EQUALIZATION, '=');
		for(int i=0;i<__binaryOperands.length;++i) {String eqMC=__binaryOperands[i]+"=";int lblCode=labelsList.indexOf(__binaryOperandsLabels[i]); addLabel(code_EQUALIZATION, eqMC);addLabel(code_EQUALIZATIONMODIFIED, eqMC);addLabel(lblCode,eqMC);}
		addLabelsForCharInner(code_DOT, '.');
		addLabelsForCharInner(code_OPERAND, '.');
		addLabelsForCharInner(code_OPERAND_BINARY, '.');
		addLabelsForCharInner(code_SEMICOLON, ';');
		addLabelsForCharInner(code_ZAPYATAYA, ',');
	}
	
	/**has OPEN*/
	protected static final String[] __OPEN = {"{","(","[","<"};
	protected static final String[] __CLOSE = {"}",")","]",">"};
	protected static final String[] __BRACES = {BRACE_INSTRUCTIONS,BRACE_VALUES,BRACE_INDEX, BRACE_PARAMETERS};
	///**has EQUALIZATION*/ - 
	//protected static final String[] __equalizationsMULTICHAR = {"-=","+=","*=","/=","%=","^=","&=","|="};
	
	/**has OPERAND*/
		protected static final String[] __unaryOperands = {"+","-","!"};
		protected static final String[] __binaryOperands = {"+","-","*","/","%","^","&","|"};
		protected static final String[] __binaryOperandsLabels = {PLUS, MINUS,MULTIPLY, DIVIDE, MOD, POWER, CONJUNCTION, DISJUNCTION};
		protected static final String[] __ternaryOperands = {"?",":"};
		/**has LOGIC*/
			protected static final String[] __nullaryLogicOperands = {"true", "false"};
			protected static final String[] __unaryLogicOperands = {"!"};
			protected static final String[] __binaryLogicOperands = {"<",">","&","|"};
			protected static final String[] __binaryLogicOperandsMULTICHAR = {"!=","==","<=",">=","&&","||"};
	/**has MODIFIER*/
	protected static final String[] __modifiers = {"public","private","protected","static","final", "abstract", "synchronized", "transient","native"};
	/**has CONSTRUCTION*/
	protected static final String[] __constructions = {"if","else","for","do","while"};
	/**has COMMAND*/
	protected static final String[] __commands = {"continue","break","return","import","package"};
	/**has TYPE*/
	protected static final String[] __basicTypes = {"void","int","boolean","char","String","procedure","Object", "Class"};
	
	public String printLabelNames(IntList labels) {
		return labels.toText(labelsList);
	}
	
	private boolean skipNextToken=false;
	/**if next token will be escaped*/
	public boolean isLastInstructionMultitoken() {return skipNextToken;}
	private static final String[] multichars = {
			"!=","==","<=",">=","&&","||",
			"+=","-=","*=","/=","%=","^=","&=","|=",
			"++","--"
	
	};
	public static void correctIfMultichar(Token token, Token nextToken) {
		if(!token.isSymbol()||nextToken==null||!nextToken.isSymbol()) return;
		int index=-1;
		String multicharRepresentation = ""+token.getSymbol() +""+nextToken.getSymbol();
		index = Lists.getIndex(__binaryLogicOperandsMULTICHAR, multicharRepresentation);
		if(index!=-1) {token.correctMultichar(nextToken);return;}
	}
	/**Основная функция класса, выдаёт список меток для входного токена, <br> если же результат нулевой - следует проигнорировать обработку токена, т.к. в предыдущий раз встречен мультисимвол.
	 * LL(1)
	 * */
	public IntList getTokenLabels(Token token, Token nextToken){
		IntList result = null;
		if(skipNextToken) {skipNextToken=false; return null;}
		//loading multisymbol
		if(token.isSymbol()) {
			if(nextToken!=null&&nextToken.isSymbol()) { 
				//int character = token.getSymbol();
				String multicharRepresentation = ""+token.getSymbol() +""+nextToken.getSymbol();
				//character+=0x100*nextToken.getSymbol();
				//result = labelsMapSYMBOL.getOrDefault(character, null);
				result = labelsMap.getOrDefault(multicharRepresentation,null);
				if(result!=null) {
					//skipNextToken=true; 
					//if(isLastInstructionMultichar())token.correctMultichar(nextToken);
					//skipNextToken=false;
					
					token.correctMultichar(nextToken);
					return result;}
			}
			result = labelsMapSYMBOL.getOrDefault((int)token.getSymbol(), label_SYMBOL);
			return result;
		}else {
			if((nextToken!=null)&&(nextToken.isSymbol())&&(nextToken.getSymbol()=='(')) {
				return label_METHOD_CALL;
			}
		}
		switch(token.getStatement()) {
		case Tokens._name:{
			String tag =token.toString();
			if(tag.equals("null"))return label_NULL_CONST;
			if(tag.equals("false"))return label_LOGIC_FALSE;
			if(tag.equals("true"))return label_LOGIC_TRUE;
			return labelsMap.getOrDefault(token.toString(), label_IDENTIFIER);
		}
		case Tokens._multiCharacter:
			return labelsMap.getOrDefault(token.toString(), label_SYMBOL);
		case Tokens._digit:
			return label_DIGIT_CONST;
		case Tokens._stringConstant:
			return label_STRING_CONST;
		case Tokens._stringComment:
			return label_COMMENTARY;
		case Tokens._character:
			return label_CHAR_CONST;
		//case Tokens._logicTrue:
		//	return label_LOGIC_TRUE;
		//case Tokens._logicFalse:
		//	return label_LOGIC_FALSE;
		//case Tokens._null:
		//	return label_NULL_CONST;
		}
		System.out.println("Error 43534523 - token statement = "+Integer.toString(token.getStatement()));
		return labelsMap.getOrDefault(token.toString(), label_IDENTIFIER);
	}
}