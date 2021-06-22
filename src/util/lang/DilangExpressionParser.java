
package util.lang;

import java.util.ArrayList;
import java.util.List;

import common.Settings;
import util.IntList;
import util.Lists;

public class DilangExpressionParser {
	protected TokenLexicon lexer;
	protected ASTLexicon lexerAST;
	public DilangExpressionParser(TokenLexicon lexer, ASTLexicon lexerAST) {
		this.lexer=lexer;
		this.lexerAST=lexerAST;
	}
	/**in this we must to determine:<br>
	 * <b>constants</b> digits or strings ; <b>IDs</b> true, false or variable ; <b>arrayElement</b> var[...] <br>
	 * <b>functionCall</b> ; <b>unary</b> ! ; <b>binary</b> + - * / ^ % & | > < && || ++ -- == != <= >=<br>
	 * <b>ternary</b> ?: ; <b>array</b> {, , ,}<br>
	 * <b><font color="red">PRIORITIES</font>:</b>+ - * / ^ %
	 * */
	private void CaptionParser_UpdateParserStatement(IntList tagLabels, Token t) {
		int change=-3;
		isLastActionIsFunction=false;
		this.__nowPriority = this.operationsPriorities.length;
		nowOperationCode=lexerAST.code_EXPRESSION;
		if(tagLabels.has(lexer.code_NEW))	{change=CaptionParser_Statement_NEW_INSTANCE; nowOperationCode=lexerAST.code_NEW_INSTANCE;}
		if(tagLabels.has(lexer.code_ZAPYATAYA))		change=CaptionParser_Statement_ZAPYATAYA;
		if(tagLabels.has(lexer.code_CONSTANT))		{
			change=CaptionParser_Statement_IDENTIFIER;
			nowOperationCode=lexerAST.code_UNKNOWN;
			if(tagLabels.has(lexer.code_DIGIT))
				nowOperationCode=lexerAST.code_CONSTANT_DIGIT;
			if(tagLabels.has(lexer.code_CHARACTER))
				nowOperationCode=lexerAST.code_CONSTANT_CHARACTER;
			if(tagLabels.has(lexer.code_STRING))
				nowOperationCode=lexerAST.code_CONSTANT_STRING;
			if(tagLabels.has(lexer.code_NULL))
				nowOperationCode=lexerAST.code_CONSTANT_NULL;
			if(tagLabels.has(lexer.code_LOGIC))
				nowOperationCode=lexerAST.code_CONSTANT_LOGIC;
		}
		if(tagLabels.has(lexer.code_IDENTIFIER))		{change=CaptionParser_Statement_IDENTIFIER;nowOperationCode=lexerAST.code_VARIABLE;
			if(isLastActionIsDot)nowOperationCode=lexerAST.code_CLASS_FIELD;
		}
		if(tagLabels.has(lexer.code_METHOD_CALL)) {change=CaptionParser_Statement_IDENTIFIER;nowOperationCode=lexerAST.code_METHOD;
			if(isLastActionIsDot)nowOperationCode=lexerAST.code_CLASS_METHOD;
		
		}
		if(tagLabels.has(lexer.code_OPERAND))		{this.__nowPriority = this.__getOperationPriority(t.toString());nowOperationCode=lexerAST.code_OPERATION;}
		if(tagLabels.has(lexer.code_OPERAND_UNARY))		change=CaptionParser_Statement_OPERATION_UNARY;
		if(tagLabels.has(lexer.code_OPERAND_BINARY))	change=CaptionParser_Statement_OPERATION_BINARY;
		if(tagLabels.has(lexer.code_OPERAND_TERNARY))	change=CaptionParser_Statement_OPERATION_TERNARY;
		if(tagLabels.has(lexer.code_BRACE)) {
			if(tagLabels.has(lexer.code_BRACE_VALUES))		{
				change=tagLabels.has(lexer.code_OPEN)?CaptionParser_Statement_BRACE_OPEN:CaptionParser_Statement_BRACE_CLOSE;
				//if(isLastActionIsFunction)
				//if(__SelectedElement!=null&&__SelectedElement.getCommand()==lexerAST.code_OPERATION)
				//	__SelectedElement.changeCommand(lexerAST.code_FUNCTION);
			}
			//if(CaptionParser_Statement==CaptionParser_Statement_IDENTIFIER)
			//	isLastActionIsFunction=true;
			if(tagLabels.has(lexer.code_BRACE_INDEX))	change=tagLabels.has(lexer.code_OPEN)?CaptionParser_Statement_BRACE_LINEAR_OPEN:CaptionParser_Statement_BRACE_LINEAR_CLOSE;
			if(tagLabels.has(lexer.code_BRACE_INSTRUCTIONS))		change=tagLabels.has(lexer.code_OPEN)?CaptionParser_Statement_BRACE_FIGURAL_OPEN:CaptionParser_Statement_BRACE_FIGURAL_CLOSE;
			
			}
		isLastActionIsDot=false;
		if(change==-3)System.out.println("WARNING: Expression parser: UNKNOWN STATEMENT: "+tagLabels.toText(this.lexer.labelsList));
		else {
			int newStatement=CaptionParser_RulesMap[CaptionParser_Statement][change];
			CaptionParser_Work=CaptionParser_WorksMap[CaptionParser_Statement][change];
			if(newStatement==CaptionParser_Statement_FunctionCall) {
				newStatement=CaptionParser_Statement_DEFAULT;
				isLastActionIsFunction=true;
			}
			if(newStatement==CaptionParser_Statement_OPERATION_BINARY&&t.getSymbol()=='.') {
				isLastActionIsDot=true;
			}
		
			CaptionParser_Statement=newStatement;
		}
		if(change==-1)System.out.println("ERROR: FORBIDDEN STATEMENT");
	}
	private boolean isLastActionIsFunction=false;// - lexerAST.code_FUNCTION already exists
	private boolean isLastActionIsDot=false;// - lexerAST.code_FUNCTION already exists
	private int nowOperationCode=-1;
	public AST cookAST_CaptionComputation(List<Token>tokens, int beginIndex, int endIndex) {
		this.__SelectedElement = null;
		__locks.clear();
		__priorities.clear();
		__nowPriority=0;
		nowOperationCode=-1;
		CaptionParser_Statement = CaptionParser_Statement_DEFAULT;
		CaptionParser_Work = CaptionParser_Work_NOTHING;
		this.__instructionsStack.clear();
		int lastNewInstanceIndex=-2;
		Token token, nextToken;IntList tagLabels;
		for(int i=beginIndex;i<endIndex;++i) {
			token = tokens.get(i);if(i<endIndex-1)nextToken=tokens.get(i+1);else nextToken=null;
			tagLabels=lexer.getTokenLabels(token, nextToken);
			if(tagLabels==null||tagLabels.has(lexer.code_COMMENTARY))continue;
			CaptionParser_UpdateParserStatement(tagLabels, token);
			switch(CaptionParser_Work) {
			case CaptionParser_Work_RECORD_NEW_INSTANCE:
				if(i-lastNewInstanceIndex>1) {
					this.__prepareNewChild();lastNewInstanceIndex=i;break;}
				lastNewInstanceIndex=i;
				this.__SelectedElement.appendIdentifier(token.toString());
				this.__SelectedElement.fill(token, tagLabels, lexer);
				break;
			case CaptionParser_Work_OPERATION_BINARY_AND_UNARY:
				if(!tagLabels.has(lexer.code_OPERAND_UNARY)) {System.out.println("ERROR: Expression Parser:cookAST_CaptionComputation:case 22: tag["+Integer.toString(i)+"]");return getRoot();};
				nowOperationCode=lexerAST.code_OPERATION;
				this.__pushInstruction();
				this.__prepareNewChild();
				this.__SelectedElement.fill(token, tagLabels, lexer, nowOperationCode);
				this.__popInstruction();
				this.__SelectedElement.fill(token, tagLabels, lexer);
				break;
			case CaptionParser_Work_OPERATION_UNARY:
				if(!tagLabels.has(lexer.code_OPERAND_UNARY)) {System.out.println("ERROR: Expression Parser 2: tag["+Integer.toString(i)+"]");return getRoot();};
				this.__replace();
				this.__SelectedElement.fill(token, tagLabels, lexer);
				
				break;
			case CaptionParser_Work_UNKNOWN:
				System.out.println("ERROR: Expression Parser 3: tag["+Integer.toString(i)+"]");return getRoot();
				//break;
			case CaptionParser_Work_NOTHING:
				break;
				//break;
			case CaptionParser_Work_ADD_CHILDS:
				this.__prepareNewChild();
				this.__SelectedElement.fill(token, tagLabels, lexer);
				break;
			case CaptionParser_Work_ADD_PARAMETERS:
				this.__GoReturn();
				this.__prepareNewChild();
				this.__SelectedElement.fill(token, tagLabels, lexer);
				break;
			case CaptionParser_Work_REM_CLUTCH:
				this.__Unlock();
				break;
			case CaptionParser_Work_ADD_CLUTCH_OPERAND:
				this.__GoUp();this.__replace();//this.__prepareNewChild();
				this.__Lock();
				this.__SelectedElement.fill(token, tagLabels, lexer);
				break;
			case CaptionParser_Work_ADD_CLUTCH_FUNC:
				this.__GoUp();this.__replace();//this.__prepareNewChild();
				//add system operation call function
				this.__Lock();
				this.__SelectedElement.fill(token, tagLabels, lexer);
				if(isLastActionIsFunction)this.__SelectedElement.changeCode(lexerAST.code_FUNCTION_CALL);
				break;
			case CaptionParser_Work_RECORD_NEW_INSTANCE_ARRAY_DEFINITION:
				this.__SelectedElement.changeCode(lexerAST.code_NEW_INSTANCE_ARRAY);
			case CaptionParser_Work_ADD_CLUTCH_INDEX:
				this.__GoUp();this.__replace();//this.__prepareNewChild();
				//add system operation Get Element
				this.__Lock();
				this.__SelectedElement.fill(token, tagLabels, lexer);
				break;
			case CaptionParser_Work_ADD_CLUTCH_ARRAY:
				this.__GoUp();this.__replace();//this.__prepareNewChild();
				//add system operation create array
				this.__Lock();
				this.__SelectedElement.fill(token, tagLabels, lexer);
				break;
			case CaptionParser_Work_BINARY_FUNCTION_REPLACE:
				this.__GoUp();
				this.__replace();
				this.__SelectedElement.fill(token, tagLabels, lexer);
				break;
			case CaptionParser_Work_TERNARY_FUNCTION_ACTION:
				System.out.println("ERROR: TERNARY OPERATOR WIP!");//this.
				this.__SelectedElement.fill(token, tagLabels, lexer);
				break;
			case CaptionParser_Work_CREATE_AST:
				this.__prepareNewChild();
				this.__SelectedElement.fill(token, tagLabels, lexer);
				break;
			case CaptionParser_Work_NEW_CHILD_AT_CLUTCH:
				this.__GoReturn();
				break;
				//this.__prepareNewChild();
				//break;	
			}
			if(CaptionParser_Statement==CaptionParser_Statement_ERROR) {System.out.println("ERROR: Expression parser: tag["+Integer.toString(i)+"]: "+token.toString());break;}
		}
		return getRoot();
	}
	
	private static final int CaptionParser_Statement_ERROR=-1;
	private static final int CaptionParser_Statement_IDENTIFIER=0;
	private static final int CaptionParser_Statement_OPERATION_UNARY=1;
	private static final int CaptionParser_Statement_OPERATION_BINARY=2;
	private static final int CaptionParser_Statement_OPERATION_TERNARY=3;
	private static final int CaptionParser_Statement_BRACE_OPEN=4;
	private static final int CaptionParser_Statement_BRACE_CLOSE=5;
	private static final int CaptionParser_Statement_DEFAULT=6;
	private static final int CaptionParser_Statement_BRACE_LINEAR_OPEN=7;
	private static final int CaptionParser_Statement_BRACE_LINEAR_CLOSE=8;
	private static final int CaptionParser_Statement_BRACE_FIGURAL_OPEN=9;
	private static final int CaptionParser_Statement_BRACE_FIGURAL_CLOSE=10;
	private static final int CaptionParser_Statement_ZAPYATAYA=11;
	private static final int CaptionParser_Statement_NEW_INSTANCE=12;
	private static final int CaptionParser_Statement_FunctionCall=99;
	
	//private static final int CaptionParser_Statement_FUNCTION=6;
	private int CaptionParser_Statement = CaptionParser_Statement_DEFAULT;
	private int CaptionParser_Work = CaptionParser_Work_NOTHING;
	private int[][]CaptionParser_RulesMap= {//3,4,5 - forbidden states
			//ID,UN,BI,TR,B(,B),DF,B[,B],B{,B},ZP
			 {-1, 2, 2, 6,99, 0, 6, 6, 0, 6, 0, 6}//ID
			,{ 0,-1,-1,-1, 6,-1,-1,-1,-1, 0,-1,-1}//UN
			,{ 0,-1, 2, 6, 6,-1,-1,-1,-1, 6,-1,-1}//BIN
			,{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}//TER
			,{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}//BRC_OPEN
			,{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}//BRC_CLOSE
			,{ 0, 1, 1, 6, 6, 0, 6, 6, 0, 6, 0, 6}//DFLT
			,{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}//BRC_[_OPEN
			,{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}//BRC_]_CLOSE
			,{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}//BRC_{_OPEN
			,{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}//BRC_}_CLOSE
			,{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}//ZAPYATAYA
			,{12,-1,-1,-1, 6,-1,-1, 6,-1,-1,-1, 6}//NEW INSTANCE
	};
	private int[][] CaptionParser_WorksMap= {
			//ID,UN,BI,TR,B(,B),DF,B[,B],B{,B},ZP
			 {-1,-1, 8, 9, 5, 3, 0, 6, 3,-1, 3,11}//ID
			,{ 1,-1,-1,-1, 4,-1,-1,-1,-1, 1,-1,-1}//UN
			,{ 1,22,22,-1, 4,-1,-1,-1,-1, 7,-1,-1}//BIN
			,{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}//TER
			,{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}//BRC_(_OPEN
			,{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}//BRC_)_CLOSE
			,{10, 0,23, 9, 4, 3, 0,-1, 3, 7, 3,11}//DFLT
			,{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}//BRC_[_OPEN
			,{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}//BRC_]_CLOSE
			,{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}//BRC_{_OPEN
			,{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}//BRC_}_CLOSE
			,{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}//ZAPYATAYA
			,{99,-1,-1,-1, 6,-1,-1,98,-1,-1,-1,-1}//NEW INSTANCE
			
	};
	private static final int CaptionParser_Work_UNKNOWN=-1;
	private static final int CaptionParser_Work_NOTHING=0;
	private static final int CaptionParser_Work_ADD_CHILDS=1;
	private static final int CaptionParser_Work_ADD_PARAMETERS=2;
	private static final int CaptionParser_Work_REM_CLUTCH=3;
	private static final int CaptionParser_Work_ADD_CLUTCH_OPERAND=4;
	private static final int CaptionParser_Work_ADD_CLUTCH_FUNC=5;
	private static final int CaptionParser_Work_ADD_CLUTCH_INDEX=6;
	private static final int CaptionParser_Work_ADD_CLUTCH_ARRAY=7;
	private static final int CaptionParser_Work_BINARY_FUNCTION_REPLACE=8;
	private static final int CaptionParser_Work_TERNARY_FUNCTION_ACTION=9;
	private static final int CaptionParser_Work_CREATE_AST=10;
	private static final int CaptionParser_Work_NEW_CHILD_AT_CLUTCH=11;
	private static final int CaptionParser_Work_OPERATION_BINARY_AND_UNARY=22;
	private static final int CaptionParser_Work_OPERATION_UNARY=23;
	private static final int CaptionParser_Work_RECORD_NEW_INSTANCE_ARRAY_DEFINITION=98;
	private static final int CaptionParser_Work_RECORD_NEW_INSTANCE=99;
	private static final String[] operationsPriorities = {
			"?",
			":",
			">=",
			"<=",
			"!=",
			"==",
			"||",
			"|",
			"&&",
			"&",
			"%",
			"+",
			"-",
			"*",
			"/",
			"^"
			
	};
	private int __getOperationPriority(String operation) {
		int index=Lists.getIndex(operationsPriorities, operation);
		if(index!=-1)return index;

		char symbol=(operation.charAt(0));
		if(symbol=='.'||symbol=='['||symbol=='(')return operationsPriorities.length;
		//for identifiers, functions and variables
		return operationsPriorities.length;
	}
	private ArrayList<AST> __instructionsStack = new ArrayList<AST>();
	private void __pushInstruction() {
		__instructionsStack.add(__SelectedElement);
	}
	private void __popInstruction() {
		if(__instructionsStack.size()==0)return;
		int index=__instructionsStack.size()-1;
		__SelectedElement=__instructionsStack.get(index);
		__instructionsStack.remove(index);
	}
	private ArrayList<AST> __locks = new ArrayList<AST>();
	private ArrayList<Integer> __priorities = new ArrayList<Integer>();
	private AST __locks_GetLast() {if(__locks.size()==0)return null;return __locks.get(__locks .size()-1);}
	//private Integer CaptionParser_Priorities_GetLast() {if(CaptionParser_Priorities.size()==0)return null;return CaptionParser_Priorities.get(CaptionParser_Priorities .size()-1);}
	/**чем меньше, тем больше всплываем наверх*/
	private int __nowPriority=0;
	private AST __SelectedElement = null;
	private void __prepareNewChild() {
		if(__SelectedElement==null)
			__SelectedElement = new AST(lexerAST, nowOperationCode);
		else
			__SelectedElement = __SelectedElement.prepareNewChild(nowOperationCode);
	}
	/**calling if now token is binary operator*/
	private void __replace() {__replace(new AST(lexerAST, nowOperationCode));}
	/**calling if now token is binary operator*/
	private void __replace(AST nowStatement) {
		//removing from old parent on selected
		if(__SelectedElement==null) {__SelectedElement=nowStatement;return;}
		if(__SelectedElement.parent!=null) {
			AST parent = __SelectedElement.parent;
			parent.replaceChild(__SelectedElement, nowStatement);
			//int index = parent.indexOf(__SelectedElement);
			//parent.childs.set(index, nowStatement);
			//nowStatement.parent=parent;
		}
		//became to new parent on selected
		nowStatement.addChild(__SelectedElement);
		__SelectedElement.parent=nowStatement;
		//reselecting
		__SelectedElement=nowStatement;
	}
	private void __Lock() {
		__locks.add(__SelectedElement);
		__priorities.add(__nowPriority);
		__nowPriority=0;
	}
	private boolean __Unlock() {
		int lastIndex;
		lastIndex=__locks.size()-1;
		if(lastIndex<0)return false;
		AST lockedNode=__locks.get(lastIndex);
		__locks.remove(lockedNode);
		__SelectedElement=lockedNode;
		return true;
	}
	/**идёт вверх ограниченное число раз*/
	private void __GoUp(int count) {
		for(int i=0;i<count;++i){
			if(__SelectedElement==null)return;
			if(__SelectedElement.parent==null)return;
			if(__SelectedElement.equals(__locks_GetLast()))return;
			__SelectedElement = __SelectedElement.parent;
		}
	}
	/**идёт вверх, пока не встретит менее приоритетную операцию*/
	private void __GoUp() {
		AST parent;
		while(true){
			if(__SelectedElement==null)return;
			parent=__SelectedElement.parent;
			if(parent==null)return;
			if(__SelectedElement.equals(__locks_GetLast()))return;
			if(this.__nowPriority>getOperationPriority(parent))return;
			__SelectedElement = parent;
		}
	}
	public int getOperationPriority(AST node) {
		if(node.getCode()!=lexerAST.code_OPERATION)return -1;
		String id = node.getIdentifier();
		return this.__getOperationPriority(id);
	}
	/**идёт вверх по дереву как можно выше, пока не встретит замка*/
	private void __GoReturn() {
		while(true) {
			if(__SelectedElement==null)return;
			if(__SelectedElement.parent==null)return;
			if(__SelectedElement.equals(__locks_GetLast()))return;
			__SelectedElement = __SelectedElement.parent;
		}
	}
	private AST getRoot() {
		AST root=__SelectedElement;
		while(true) {
			if(root==null)break;
			if(root.parent==null)break;
			root= root.parent;
		}
		return root;
	}

	public static String printAST(AST terminalStatement) {
		return printAST(terminalStatement, 0);
	}
	private static String printAST(AST nowNode, int nowHeight) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<nowHeight;++i)
			sb.append("----");
		sb.append(nowNode.getCodeName());
		sb.append('<');
		sb.append(nowNode.getIdentifier());
		sb.append('>');
		sb.append('\t');
		sb.append(nowNode.position);
		for(int i=0;i<nowNode.count();++i) {
			sb.append('\n');
			sb.append('|');
			sb.append(printAST(nowNode.getChild(i),nowHeight+1));
		}
		return sb.toString();
	}
}

/**!!!EXCLUSIONS!!! : new INSTANTIATE("TYPE,<,>" "[,(" CAPTION "],)")<br> 
 * 2: [] - <br> 
 * 3: {} - array definition <br>
 * */
/* - заготовка 
private ArrayList<Token> translateCaption(List<Token> tokens){
	int l=tokens.size();
	ArrayList<Token> result = new ArrayList<Token>();
	boolean isMultiChar=false;
	boolean isFunctionCall=false;
	Token token,nextToken, newToken;IntList tokenLabels;
	for(int i=0;i<l;++i) {
		token=tokens.get(i);
		if(i<l-1)nextToken=tokens.get(i+1);else nextToken=null;
		tokenLabels=lexer.getTokenLabels(token, nextToken);
		if(tokenLabels==null)continue;
		if(tokenLabels.has(lexer.WIP));
		//IF(TOKENLABELS.HAS(LEXER.code_IDENTIFIER)&&nextToken!=null&&nextToken.isSymbol()&&nextToken.getSymbol()=='=')
			
		
	}
}
*/
