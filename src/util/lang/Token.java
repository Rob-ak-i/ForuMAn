package util.lang;

import java.util.ArrayList;

public class Token extends Tokens {
	private int statement=-1;public int getStatement() {return statement;}
	private String caption;//public String getCaption() {if(caption==null)return "";return caption;}
	/*FOR DEBUGGING*/
	int lineNumber=-1;public int getLineNumber() {return lineNumber;}
	int linePosition=-1;public int getLinePosition() {return linePosition;}
	int textPosition=-1;public int getTextPosition() {return textPosition;}
	public void correctMultichar(Token nextToken) {
		if(statement<0||nextToken.isCaption())return;
		caption=(char)statement+""+(char)nextToken.getStatement();
		nextToken.caption=null;nextToken.statement=-1;
		statement=_multiCharacter;
	}
	public boolean equalsToReserveWord(String reserveWord) {return (statement==Tokens._name&&toString().equals(reserveWord));}
	public String getPosition() {return Integer.toString(lineNumber)+":"+Integer.toString(linePosition)+":"+Integer.toString(textPosition);}
	public boolean isSymbol() {return statement>=0;}
	public boolean isCaption() {return statement<-1;}
	public String toString() {
		if(statement>=0)return ""+((char)statement);
		if(caption==null)return "";
		return caption;
	}
	private int identifier;public int getIdentifier() {return identifier;} public boolean setIdentifier(ArrayList<String> dictionary) {int index = dictionary.indexOf(caption); if(index==-1)return false; identifier = index; caption=null; return true;} public void setIdentifierManually(int identifier) {this.identifier = identifier;caption=null;}public boolean translate(ArrayList<String> oldDictionary, ArrayList<String> newDictionary) {caption = oldDictionary.get(identifier);int index = newDictionary.indexOf(caption);caption=null; if(index==-1)return false; identifier=index;return true;} 
	public char getSymbol() {if(statement>=0)return (char) statement;return 0;}
	public Token(String caption, int statement, int lineNumber, int linePosition, int textPosition) {
		this.caption=caption;
		this.statement=statement;
		this.lineNumber=lineNumber;
		this.linePosition=linePosition;
		this.textPosition=textPosition;
		identifier = -1;
	}
	public Token(int identifier, int statement, int lineNumber, int linePosition, int textPosition) {
		this.caption=null;
		this.statement=statement;
		this.lineNumber=lineNumber;
		this.linePosition=linePosition;
		this.textPosition=textPosition;
		this.identifier=identifier;
	}
}
