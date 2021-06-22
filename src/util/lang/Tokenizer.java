package util.lang;

import java.util.ArrayList;
import java.util.StringTokenizer;

import util.Lists;

public class Tokenizer extends Tokens {
	public static final int _UNKNOWN = -1;
	public static final int _CHAR = 0;
	public static final int _SYMBOL = 1;
	public static final int _NAME = 2;
	public static final int _VALUE = 3;

	public static final int _NAME_LITTLE = 20;
	public static final int _NAME_LITTLE_RANDOM = 21;
	public static final int _NAME_FIRST = 22;
	public static final int _NAME_CAPITALS = 23;
	public static final int _NAME_CAPITALS_RANDOM = 24;
	
	public static final int _VALUE_DECIMAL = 30;
	public static final int _VALUE_DECIMAL_REAL = 31;
	public static final int _VALUE_DECIMAL_FLOAT = 32;
	public static final int _VALUE_BINARY = 33;
	public static final int _VALUE_BINARY_REAL = 34;
	public static final int _VALUE_BINARY_FLOAT = 35;
	public static final int _VALUE_HEXIMAL = 36;
	public static final int _VALUE_HEXIMAL_REAL = 37;
	public static final int _VALUE_HEXIMAL_FLOAT = 38;

	public static final int _SYMBOL_DIGIT_0 = 48;
	public static final int _SYMBOL_DIGIT_1= 49;
	public static final int _SYMBOL_DIGIT_2 = 50;
	public static final int _SYMBOL_DIGIT_3 = 51;
	public static final int _SYMBOL_DIGIT_4 = 52;
	public static final int _SYMBOL_DIGIT_5 = 53;
	public static final int _SYMBOL_DIGIT_6 = 54;
	public static final int _SYMBOL_DIGIT_7 = 55;
	public static final int _SYMBOL_DIGIT_8 = 56;
	public static final int _SYMBOL_DIGIT_9 = 57;

	public static final int _SYMBOL_A_CAPITAL = 65;
	public static final int _SYMBOL_Z_CAPITAL = 90;
	public static final int _SYMBOL_A_SMALL = 97;
	public static final int _SYMBOL_Z_SMALL = 122;
	
	

	public static final int _SYMBOL_SPACE = 32;
	public static final int _SYMBOL_SPACE_2 = 160;

	public static final int ZONE_SPACE=-1;
	public static final int ZONE_DIGIT=-2;
	public static final int ZONE_ALPHABETIC=-3;
	int[] zones;
	public Tokenizer () {
		zones = new int[256];
		String characterSymbols = ";(){}[]/*\\|#?!.\'\"`~&%$:,<>=_+-^@\n ";
		int nowCaption=-1;
		for(int i=0;i<characterSymbols.length();++i) {nowCaption=(int)characterSymbols.charAt(i);zones[nowCaption]=nowCaption;}
		zones[_SYMBOL_SPACE]=ZONE_SPACE;zones[_SYMBOL_SPACE_2]=ZONE_SPACE;zones[_SYMBOL_TAB]=ZONE_SPACE;zones[_SYMBOL_ENTER]=ZONE_SPACE;
		for(int i=_SYMBOL_DIGIT_0;i<=_SYMBOL_DIGIT_9;++i)
			zones[i]=ZONE_DIGIT;
		for(int i=_SYMBOL_A_CAPITAL;i<=_SYMBOL_Z_CAPITAL;++i)
			zones[i]=ZONE_ALPHABETIC;
		for(int i=_SYMBOL_A_SMALL;i<=_SYMBOL_Z_SMALL;++i)
			zones[i]=ZONE_ALPHABETIC;
		String lineSeparator = System.lineSeparator();
		int code;
		for(int i=0;i<lineSeparator.length();++i) {
			code=lineSeparator.charAt(i);
			if(code>255)
				isLineSeparatorUnicode=true;
			else
				zones[code]=ZONE_SPACE;
		}
		if(isLineSeparatorUnicode)System.out.println("Warning: System.lineSeparator() is unicode character.");
	}
	private void tryToAddToken() {
		if(newToken.length()==0)return;
		addToken(newToken.toString(), nowTokenStatement);
		newToken.delete(0, newToken.length());
	}
	private void addToken(String caption, int tokenStatement) {
		abstractStatement=stateNothing;
		int captionLength=0;if(caption!=null)captionLength=caption.length();
		int index = Lists.getIndex(inclusionTypeTokens, tokenStatement);
		if(index!=-1)captionLength+=inclusionTypeInitSequences[index].length();
		tokens.add(new Token(caption, tokenStatement, lineCounter, linePositionCounter-captionLength, textCharIndex-captionLength));
	}
	private static final int stateNothing = 0;
	private static final int stateCaption = 1;
	private static final int stateSymbol = 2;
	private int abstractStatement=stateNothing;
	public boolean isLineSeparatorUnicode=false;
	private int textCharIndex;
	private int lineCounter=0, linePositionCounter=0;
	private char character;
	private int charCode;
	private String text;
	private StringBuilder newToken = null;
	private ArrayList<Token> tokens;
	private int nowTokenStatement = -1;
	private static final String[] inclusionTypeInitSequences = {
			"\"",
			"\'",
			"/*",
			"//"
	};
	private static final String[] inclusionTypeExitSequences = {
			"\"",
			"\'",
			"*/",
			"\n"
	};
	private static final int[] inclusionTypeTokens= {
			Tokens._stringConstant,
			Tokens._character,
			Tokens._stringComment,
			Tokens._stringComment
	};
	private static final int inclusionTypeString=0;
	private static final int inclusionTypeCharacter=1;
	private static final int inclusionTypeCommentary=2;
	private static final int inclusionTypeCommentaryLine=3;
	private void analyzeInclusion(int inclusionType) {
		tryToAddToken();
		StringBuilder record= new StringBuilder();
		String exitSequence = inclusionTypeExitSequences[inclusionType];
		textCharIndex+=inclusionTypeInitSequences[inclusionType].length();
		char c=0;boolean trig=false;boolean backSlashChecked=false; int backSlashCount=0;
		for(;textCharIndex<text.length();++textCharIndex) {
			trig=true;
			for(int i=0;i<exitSequence.length();++i)
				if(text.charAt(i+textCharIndex)!=exitSequence.charAt(i)) {trig=false;break;}
			c = text.charAt(textCharIndex);
			/**escaping special characters input*/
			if(c=='\\') {
				if(backSlashChecked)
					backSlashCount++;
				else {
					backSlashCount=1;
					backSlashChecked=true;
				}
			}else {backSlashChecked=false;}
			if(trig) {
				if(inclusionType==inclusionTypeCommentary||inclusionType==inclusionTypeCommentaryLine)break;
				if(!backSlashChecked||(backSlashCount%2==0))
					break;
			}
			record.append(c);
			if(c==Tokens._SYMBOL_ENTER) {lineCounter++;linePositionCounter=0;}else {linePositionCounter++;}
		}
		linePositionCounter+=inclusionTypeInitSequences[inclusionType].length();
		if(c!=Tokens._SYMBOL_ENTER) {linePositionCounter+=inclusionTypeExitSequences[inclusionType].length()-1;}
		textCharIndex+=inclusionTypeExitSequences[inclusionType].length()-1;
		addToken(record.toString(), inclusionTypeTokens[inclusionType]);
		if(c==Tokens._SYMBOL_ENTER) {lineCounter++;linePositionCounter=0;}
	}
	public ArrayList<Token> tokenizeAndFilter(String text){
		ArrayList<Token> tokens = (tokenize(text,true)), tokens2=null;
		tokens2=filterTokens(tokens);
		tokens.clear();
		return tokens2;
	}
	public ArrayList<Token> tokenize(String text){
		return (tokenize(text,true));
	}
	public ArrayList<Token> filterTokens( ArrayList<Token> tokens){
		ArrayList<Token> result=new ArrayList<Token>();
		String tag=null;Token token,nextToken;
		for(int i=0;i<tokens.size();++i) {
			token = tokens.get(i);
			if(token==null)continue;
			if(token.getStatement()==Tokens._unknown)continue;
			if(token.getStatement()==Tokens._stringComment)continue;
			tag=token.toString();
			//if(tag.length()==0)continue;
			nextToken = (i<tokens.size()-1)?tokens.get(i+1):null;
			TokenLexicon.correctIfMultichar(token, nextToken);
			result.add(token);
		}
		return result;
	}
	public ArrayList<Token> tokenize(String text, boolean saveTokenPositions) {
		this.text=text;tokens = new ArrayList<Token>();newToken = new StringBuilder();lineCounter=1;
		int l=text.length();
		//char c;
		int code;int zone=-1; int abstractStatementPrev=stateNothing;char nextCharacter;boolean inclusionCheck=false;
		for(textCharIndex = 0; textCharIndex<text.length();textCharIndex++) {
			character = text.charAt(textCharIndex);
			charCode = (int)character;
			if(charCode==Tokens._SYMBOL_ENTER) {lineCounter++;linePositionCounter=0;}else {linePositionCounter++;}
			/**determining zone*/
			if(charCode>255)
				zone=ZONE_ALPHABETIC;
			else 
				zone=zones[charCode];
			/**determining abstract statement*/
			while(true) {
				abstractStatementPrev=abstractStatement;
				if(zone==ZONE_DIGIT || zone==ZONE_ALPHABETIC) {abstractStatement=stateCaption;break;}
				if(zone==ZONE_SPACE) {abstractStatement=stateNothing;break;}
				abstractStatement=stateSymbol;
				break;
			}
			/**checking for special sequences*/
			while(true) {
				if(textCharIndex==text.length()-1)break;
				inclusionCheck=true;
				if(character == '\'') {analyzeInclusion(inclusionTypeCharacter);break;}
				if(character == '\"') {analyzeInclusion(inclusionTypeString);break;}
				nextCharacter = text.charAt(textCharIndex+1);
				if(character == '/') {
					if(nextCharacter=='/') {analyzeInclusion(inclusionTypeCommentaryLine);break;}
					if(nextCharacter=='*') {analyzeInclusion(inclusionTypeCommentary);break;}
				}
				inclusionCheck=false;
				break;
			}
			if(inclusionCheck) {abstractStatement=stateNothing;inclusionCheck=false;continue;}
			/**perform action in automata - prevZone nowZone*/
			do{
				if(abstractStatementPrev==stateNothing || abstractStatementPrev==stateSymbol) {
					if(abstractStatement==stateCaption) {
						nowTokenStatement=(zone==ZONE_DIGIT)?Tokens._digit:Tokens._name;
						newToken.append(character); 
						break;}
					if(abstractStatement==stateSymbol) {
						addToken(null,zone); 
						break;}
					break;
				}
				if(abstractStatementPrev==stateCaption) {
					if(abstractStatement==stateCaption) {
						newToken.append(character); 
						break;}
					if(abstractStatement==stateSymbol) {
						if(nowTokenStatement==Tokens._digit && character == '.') {
							newToken.append(character);
							abstractStatement=stateCaption;
							break;}//dot in digit
						tryToAddToken(); 
						addToken(null,zone); 
						break;}
					tryToAddToken();
					break;}
			}while(false);
			
		}
		tryToAddToken();
		ArrayList<Token> result = tokens;tokens=null;newToken=null;this.text=null;return result;
	}
}
