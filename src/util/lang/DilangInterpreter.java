package util.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import common.Settings;
import util.IntList;
import util.Parameters;

/**sounds like Dilovar's language - inventor of Silver Numbers (every number which able to sum with two is Silver Number)*/
public class DilangInterpreter extends DilangInterpreterCore{
	protected Tokenizer tokenizer;
	protected DilangParser parser=null;
	ArrayList<Token> tokens;int mainTokenIndex;
	ArrayList<Integer> gotoStack;
	ArrayList<String> gotoFunctionIdentifierStack;
	public DilangInterpreter () {
		super();
		tokenizer = new Tokenizer();
		lexer = new TokenLexicon();
		lexerAST = new ASTLexicon();
		
		parser = new DilangParser (lexer, lexerAST);
	}
	int performComputation(List<Token> tokens) {
		return Tokens._dataType_int;
	}

	public Parameters processStringToArguments(String str) {
		Parameters result = new Parameters();
		this.externalVariables=result;
		this.mayExternalVariablesBeForLocalUse=true;
		AST e = this.parseDilang(str);
		processAST(e);
		return result;
	}
	public void processFunction(String functionCode) {
		tokens = tokenizer.tokenize(functionCode, true);
		IntList labels=null;Token token=null, nextToken=null;
		for(int i=0;i<tokens.size();++i) {
			if(i<tokens.size()-1)nextToken=tokens.get(i+1);
			token=tokens.get(i);
			labels=lexer.getTokenLabels(token, nextToken);
			if(labels==null)System.out.println(token.toString()+" "+"Previous is Multichar");else
			System.out.println(token.toString()+" "+labels.toText(lexer.labelsList));
		}
		for(mainTokenIndex = 0;mainTokenIndex < tokens.size();++mainTokenIndex) {
			
		}
		tokens.clear();tokens = null;
	}
	public AST parseDilang(String scriptCode) {
		ArrayList<Token> tokens = tokenizer.tokenizeAndFilter(scriptCode);
		AST result = parser.cookAST_DilangScript(tokens);
		tokens.clear();
		return result;
	}
	/**TODO parser - ++ -- actions;*/
	public AST makeAST(List<Token> tokens, boolean isDocument) {
		if(isDocument)
		return parser.cookAST_Document(tokens);
		return parser.cookAST_CaptionComputation(tokens, 0, tokens.size());
	}
	public void clear() {
		//TODO WIP
	}
}
class DilangCheatSheet extends DilangInterpreter{

	public AST interpretScript(String scriptCode) {
		ArrayList<Token> tokens = tokenizer.tokenize(scriptCode, true);
		{int spellCheckResult = parser.spellCheck(tokens);if(spellCheckResult!=-1) {if(spellCheckResult>=0)System.out.println("SpellCheckError at"+tokens.get(spellCheckResult));tokens.clear();return null;}}
		Token token=null, nextToken=null;IntList tokenLabels=null;
		ArrayList<IntList> tokenTags = new ArrayList<IntList>();
		for(int i=0;i<tokens.size();++i) {
			token = tokens.get(i);
			if(i<tokens.size()-1)nextToken=tokens.get(i+1);else nextToken=null;
			tokenLabels = lexer.getTokenLabels(token, nextToken);
			if(tokenLabels==null)continue;
		}
		
		return null;
	}
}