package util.lang;

import java.util.ArrayList;
import java.util.HashMap;

import util.IntList;

public class AST {
	public ASTLexicon lexer;
	//public TokenLexicon lexerToken;
	//public Properties fields;
	public AST parent=null;
	//private ASTProperties properties=null;
	private ArrayList<AST> childs=new ArrayList<AST>(0);
	private int code=-1;public static final int code_NOTASSIGNED=-1;public int getCode(){return code;}public void changeCode(int newCode) {code=newCode;}
	private String identifier;
	public String position=null;
	
	public AST getChildWithCode(int commandCode) {
		AST child=null;
		for(int i=0;i<childs.size();++i) {
			child=childs.get(i);
			if(child.code==commandCode)return child;
		}
		return null;
	}
	public ArrayList<AST> getChildsWithCode(int code) {
		ArrayList<AST> result=null;
		AST child=null;
		for(int i=0;i<childs.size();++i) {
			child=childs.get(i);
			if(child.code==code) {
				if(result==null)result = new ArrayList<AST>();
				result.add(child);
			}
		}
		return result;
	}
	public AST(ASTLexicon lexer, int code) {
		this.lexer=lexer;
		this.code=code;
	}
	public AST prepareNewChild(int command) {
		AST child = new AST(lexer, command);
		addChild(child);
		return child;
	}
	public String getCodeName() {
		return lexer.getLabelName(code);
	}
	public int count() {if(childs!=null)return childs.size();return 0;}
	public AST getChild(int i) {if(childs!=null&&i<childs.size())return childs.get(i);return null;}
	/**удаление дерева впринципе, то есть выгрузка кода*/
	public void clear() {
		if(childs!=null) {for(int i=0;i<childs.size();++i)childs.get(i).clear();childs.clear();childs=null;}
	}
	public void fill(Token token, IntList tagLabels, TokenLexicon tokenLexer,int preferredAction) {
		fill(token,tagLabels, tokenLexer);
		if(preferredAction!=-1)code=preferredAction;
	}
	public void fill(Token token, IntList tagLabels, TokenLexicon tokenLexer) {
		//this.fields.put("position", token.getPosition());
		String id=token.toString();
		if(token.isSymbol()) {
			code=lexer.code_OPERATION;
		}
		int tokenStatement=token.getStatement();
		if(tagLabels.has(tokenLexer.code_TRUE))code=lexer.code_CONSTANT_LOGIC;//if(tokenStatement==Tokens._logicTrue) {code=lexer.code_CONSTANT_LOGIC;id="true";}
		if(tagLabels.has(tokenLexer.code_FALSE))code=lexer.code_CONSTANT_LOGIC;//if(tokenStatement==Tokens._logicFalse) {code=lexer.code_CONSTANT_LOGIC;id="false";}
		if(tagLabels.has(tokenLexer.code_NULL))code=lexer.code_CONSTANT_NULL;//if(tokenStatement==Tokens._null) {code=lexer.code_CONSTANT_NULL;id="null";}
		do {
			if(token.getSymbol()=='?') {code=lexer.code_EXPRESSION;id="?"; break;}
		}while(false);
		setIdentifier(id);
		//for debug only
		position = token.getPosition();
		
	}
	public String getIdentifier() {return identifier;}
	public void setIdentifier(String value) {
		identifier=value;
	}
	public void appendIdentifier(String value) {
		if(identifier!=null)
			identifier=identifier+value;
		else
			identifier=value;
	}
	public void addChild(AST child2) {
		if(child2==null)return;
		childs.add(child2);
		child2.parent=this;
	}
	/**used for expression parser*/
	public void replaceChild(AST oldElement, AST newElement) {
		int index=childs.indexOf(oldElement);
		childs.set(index, newElement);
		newElement.parent=this;
	}
	
}
