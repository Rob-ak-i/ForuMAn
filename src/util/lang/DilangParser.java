package util.lang;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.IntList;
import util.Lists;
import util.Properties;
import util.PropertiesInt;
import util.StringList;

//TODO DilangParser: ++ -- actions;
//TODO DilangParser: cast to other object;
public class DilangParser extends DilangExpressionParser {
	
	public DilangParser(TokenLexicon lexer, ASTLexicon lexerAST) {
		super(lexer, lexerAST);
	}
	
	private static final int error_notCaption=-3;
	private static final int error_notBody=-2;
	private static final int error_reservedWord=-1;
	private static final int error_missingSymbol=-99;
	
	private void error(List<Token> tokens, int index, int code, int opCode) {
		Token t=tokens.get(index);
		System.out.println("Error:"+this.getClass().getName()+": in token["+Integer.toString(index)+"]="+t.toString());
		System.out.println("--error code="+Integer.toString(code));
		do {
			if(code==error_missingSymbol) {
				System.out.println("----missing symbol"+(char)(opCode)+"\'"+" but now \""+t.toString()+"\" at "+t.getPosition());
				break;
			}
			if(code==error_notCaption)
				System.out.println("----not braces in toward");
			if(code==error_notBody)
				System.out.println("----not body in toward");
			if(code==error_reservedWord)
				System.out.println("----reserved word out of scope");
			if(opCode!=-1) {
				System.out.println("----at AST<"+lexerAST.getLabelName(opCode)+">");
			}
		}while(false);
		new Exception().printStackTrace();
		System.exit(-1);
	}

	public AST cookAST_DilangScript(List<Token> tokens) {
		AST result = cookAST_body(tokens, 0, tokens.size(), false);
		result.changeCode(lexerAST.code_SCRIPT);
		return result;
	}
	public AST cookAST_Document(List<Token> tokens) {
		int l=tokens.size();
		AST resultDocument = new AST(lexerAST, lexerAST.code_DOCUMENT);
		Token token;String tag;IntList tagLabels;
		String annotation=null;PropertiesInt newProperties=new PropertiesInt();
		for(int i=0;i<l;++i) {
			token=tokens.get(i);
			tag = token.toString();
			if(token.getIdentifier()==Tokens._stringComment)continue;
			if(token.isSymbol()&&token.getSymbol()=='@'){++i;annotation=tokens.get(i).toString();continue;}
			if(token.isSymbol())continue;
			if(tag.length()==0)continue;
			if(tag.equals(";"))continue;
			if(tag.equals("import")) {
				StringBuilder _imports = new StringBuilder();
				AST imports=resultDocument.getChildWithCode(lexerAST.code_IMPORT);
				if(imports!=null){
					_imports.append(imports.getIdentifier());_imports.append(';');
				}else {
					imports=resultDocument.prepareNewChild(lexerAST.code_IMPORT);
				}
				int nextSemicolonPosition = getNextSymbolIndex(tokens, i, l,';');
				if(nextSemicolonPosition==-1) {System.out.println("Error: cookAST_Document: at"+token.getPosition()); return null;}
				for(int j=i+1;j<nextSemicolonPosition;++j)_imports.append(tokens.get(j).toString());
				if(_imports!=null&&_imports.length()>0)
					imports.setIdentifier(_imports.toString());
				_imports=null;
				i=nextSemicolonPosition;
				continue;
			}
			if(tag.equals("package")) {
				StringBuilder _package = new StringBuilder();
				int nextSemicolonPosition=getNextSymbolIndex(tokens, i, l,';');
				if(nextSemicolonPosition==-1) {System.out.println("Error: cookAST_Document: at"+token.getPosition()); return null;}
				for(int j=i+1;j<nextSemicolonPosition;++j)_package.append(tokens.get(j).toString());

				AST packageAST=resultDocument.getChildWithCode(lexerAST.code_PACKAGE);
				if(packageAST==null) {packageAST = new AST(lexerAST, lexerAST.code_PACKAGE);resultDocument.addChild(packageAST);}
				packageAST.setIdentifier(_package.toString());
				_package=null;
				continue;
			}
			if(tag.equals("class")) {
				Point area=getNextBody(tokens, i, l);
				AST child=cookAST_Class(tokens, area.x,area.y, newProperties);
				resultDocument.addChild(child);
				child.setIdentifier(tokens.get(area.x-1).toString());
				newProperties = new PropertiesInt();
				i=area.y;
			}
			tagLabels = lexer.getLabels(tag);
			if(tagLabels.has(lexer.code_MODIFIER))newProperties.append(lexerAST.code_MODIFIER,tag);
			//if(tagLabels.has(lexer.code_IDENTIFIER))newProperties.append(lexerAST.code_IDENTIFIER,tag);
		}
		newProperties.clear();newProperties=null;
		return resultDocument;
	}
	private AST cookAST_Class(List<Token> tokens, int beginIndex, int endIndex, PropertiesInt initialProperties) {
		if(initialProperties==null)initialProperties = new PropertiesInt();
		Token token, nextToken;String tag;int tagCode;IntList tagLabels;
		int a=beginIndex,b=endIndex;
		Point area = getNextBody(tokens, beginIndex, endIndex);
		a=area.x;b=area.y;
		if(a>beginIndex) {
			for(int i=beginIndex;i<a-1;++i)initialProperties.append(lexerAST.code_MODIFIER, tokens.get(i).toString());
			initialProperties.append(lexerAST.code_IDENTIFIER, tokens.get(a-1).toString());
		}
		AST result = new AST(lexerAST, lexerAST.code_CLASS);AST child=null;
		for(int code:initialProperties.keySet()) {
			tag=initialProperties.get(code);
			if(code==lexerAST.code_IDENTIFIER) {
				result.appendIdentifier(tag);
				continue;
			}
			child=result.prepareNewChild(code);
			child.setIdentifier(tag);
		}
		//getting classID for finding constructor
		boolean isVariableDeclaration=false
				,isVariableDeclarationDefinition=false 
				,isFunctionDeclaration=false 
				,isFunctionDeclarationDefinition=false 
				//,isConstructorDefinition=false - will be determined in function interpreter
				;
		int constructionType=-1;
		String classIdentifier = result.getIdentifier();
		Properties newProperties = new Properties();
		int nearestOpenVanillaBraceIndex=-1,nearestSemicolonIndex=-1,nearestEqualIndex=-1, nearestBodyOpenIndex=-1, nearestBodyCloseIndex=-1;Point nearestBodyArea;
		for(int i=a+1;i<b;++i) {
			token=tokens.get(i);
			tag=token.toString();
			if(token.getStatement()==Tokens._stringComment)continue;
			if(tag.length()==0)continue;
			//if(i<b-1)nextToken=tokens.get(i+1);else nextToken=null;
			
			constructionType=-1;
			nearestSemicolonIndex=getNextSymbolIndex(tokens, i, b,';');
			//if==-1 then lastInstruction and functionDefinition or nothing
			nearestBodyArea=getNextBody(tokens, i,b);
			if(nearestBodyArea==null) {
				if(nearestSemicolonIndex==-1)return result;
				nearestBodyOpenIndex=b;nearestBodyCloseIndex=b;
			}else {
				nearestBodyOpenIndex=nearestBodyArea.x;nearestBodyCloseIndex=nearestBodyArea.y;}
			if(nearestSemicolonIndex==-1)
				isFunctionDeclarationDefinition=true;
			else {
				nearestOpenVanillaBraceIndex=-1;
				for(int j=i;j<nearestSemicolonIndex&&j<nearestBodyOpenIndex;++j)if(tokens.get(j).isSymbol()&&tokens.get(j).getSymbol()=='(') {nearestOpenVanillaBraceIndex=j;break;}
				nearestEqualIndex=-1;
				for(int j=i;j<nearestSemicolonIndex&&j<nearestBodyOpenIndex;++j)if(tokens.get(j).isSymbol()&&tokens.get(j).getSymbol()=='=') {nearestEqualIndex=j;break;}
				isVariableDeclaration=nearestEqualIndex==-1&&nearestOpenVanillaBraceIndex==-1&&nearestSemicolonIndex<nearestBodyOpenIndex;
				isVariableDeclarationDefinition=nearestEqualIndex!=-1&&nearestEqualIndex<nearestSemicolonIndex;
				isFunctionDeclaration=nearestOpenVanillaBraceIndex!=-1&&nearestEqualIndex==-1;
				isFunctionDeclarationDefinition=nearestOpenVanillaBraceIndex!=-1&&nearestEqualIndex==-1&&nearestBodyOpenIndex<nearestSemicolonIndex;
				//isConstructorDefinition=false;
			}
			
			if(isVariableDeclaration)constructionType=0;
			if(isVariableDeclarationDefinition)constructionType=1;
			if(isFunctionDeclaration)constructionType=2;
			if(isFunctionDeclarationDefinition)constructionType=3;
			//if(isConstructorDefinition)constructionType=4;
			
			if(constructionType==2) {
				result.addChild(cookAST_Function(tokens, classIdentifier,i, nearestSemicolonIndex));
				i=nearestSemicolonIndex;
			}
			if(constructionType==3) {
				result.addChild(cookAST_Function(tokens, classIdentifier,i, nearestBodyCloseIndex));
				i=nearestBodyCloseIndex;
			}
			if(constructionType==-1)break;
			if(constructionType<2) {
				cookAST_VariableDeclaration(result,tokens, i, nearestSemicolonIndex);
				i=nearestSemicolonIndex;
			}
			//tagLabels = lexer.getTokenLabels(token, nextToken);
			//if(tagLabels==null)continue;
			
		}
		return result;
	}
	private int getNearestSensitiveSymbol(List<Token> tokens, int beginIndex, int endIndex) {

		int nearestDelimiterIndex=-1,nearestEqualIndex=-1;
		nearestDelimiterIndex = getNextSymbolIndex(tokens, beginIndex, endIndex, ',');
		if(nearestDelimiterIndex==-1)nearestDelimiterIndex=endIndex;
		nearestEqualIndex = getNextSymbolIndex(tokens, beginIndex, endIndex, '=');
		if(nearestEqualIndex==-1)nearestEqualIndex=endIndex;
		int nearestSensitiveSymbol=Math.min(nearestEqualIndex,nearestDelimiterIndex);
		return nearestSensitiveSymbol;
	}
	private void cookAST_VariableDeclaration(AST parent, List<Token> tokens, int beginIndex, int endIndex) {
		StringList initialProperties = new StringList ();Token t;AST child=null;char c;
		int nearestSensitiveSymbol=getNearestSensitiveSymbol(tokens, beginIndex, endIndex);
		for(int i=beginIndex;i<nearestSensitiveSymbol-1;++i) {//считаем объявление типа данных до чувствительного символа или равенства (вне вложений)
			initialProperties.add(tokens.get(i).toString());
		}
		boolean isDeclaration=initialProperties.size()>0;
		for(int i=nearestSensitiveSymbol-1;i<endIndex;++i) {//считаем массив 
			t=tokens.get(i);
			if(t.isSymbol()) {
				c=t.getSymbol();
				if(c==',')continue;
				if(c!='=')break;//встречен неизвестный символ или разделитель инструкций
				//ввод значения для очередной переменной
				int nextSensitiveSymbol = getNearestSensitiveSymbol(tokens, i+1, endIndex);
				child.addChild(this.cookAST_CaptionComputation(tokens, i+1, nextSensitiveSymbol));
				i=nextSensitiveSymbol;continue;
			}
			//встречен индентификатор переменной, объявление переменной
			if(isDeclaration) {
				child=parent.prepareNewChild(lexerAST.code_VARIABLE_DECLARATION);
				child.addChild(cookAST_Type(initialProperties));
			}else
				child=parent.prepareNewChild(lexerAST.code_VARIABLE_ASSIGN);
			child.setIdentifier(t.toString());
		}
		initialProperties.clear();
	}
	private AST cookAST_Type(StringList initialProperties) {
		AST result= new AST(lexerAST, lexerAST.code_TYPE);AST child;
		for(String tag:initialProperties) {
			//TODO определиться 
//			child = result.getChildWithCode(lexerAST.code_CONSTANT_STRING);
//			if(child==null)
//				child=result.prepareNewChild(lexerAST.code_CONSTANT_STRING);
//			child.appendIdentifier(tag);
			child=result.prepareNewChild(lexerAST.code_CONSTANT_STRING);
			child.setIdentifier(tag);
		}
		return result;
	}
	private AST cookAST_Function(List<Token> tokens, String classIdentifier, int beginIndex, int endIndex) {
		HashMap<Integer, String> initialProperties = new HashMap<Integer, String>();
		//Token token, nextToken;String tag;int tagCode;IntList tagLabels;
		int a=beginIndex,b=endIndex;
		Point area = getNextBody(tokens, beginIndex, endIndex);
		if(area==null) {a=b;}
		a=area.x;b=area.y;Token t;IntList lbls;
		Point parametersArea = getNextInBraces(tokens, beginIndex, a);
		if(parametersArea==null) {System.out.println("Error: DilangParser: not function parameters in function definition at tokens"+Integer.toString(beginIndex)+":"+Integer.toString(endIndex)); return null;}
		boolean isConstructor=false;
		if(initialProperties.getOrDefault(lexerAST.code_IDENTIFIER, null)==classIdentifier&&classIdentifier!=null)isConstructor=true;
		if(a>beginIndex) {
			boolean isModifiersDefinition=true;
			for(int i=beginIndex;i<parametersArea.x-1;++i) {
				t=tokens.get(i);
				lbls=lexer.getTokenLabels(t, null);
				if(!lbls.has(lexer.code_MODIFIER))isModifiersDefinition=false;
				if(isModifiersDefinition)
					initialProperties.put(lexerAST.code_MODIFIER, t.toString());
				else
					initialProperties.put(lexerAST.code_TYPE, t.toString());
			}
			String identifier=tokens.get(parametersArea.x-1).toString();
			initialProperties.put(lexerAST.code_IDENTIFIER, identifier);
			if(classIdentifier!=null&&classIdentifier.equals(identifier)) {
				isConstructor=true;
			//	initialProperties.put(lexerAST.code_TYPE, tokens.get(a-2).toString());
			}//else
			//	initialProperties.put(lexerAST.code_MODIFIER, tokens.get(a-2).toString());
		}
		AST result = new AST(lexerAST, lexerAST.code_FUNCTION_DECLARATION);
		AST child=null;
		for(int code:initialProperties.keySet()) {
			child=result.prepareNewChild(code);
			child.setIdentifier(initialProperties.get(code));
		}result.addChild(cookAST_body(tokens, a, b, true));
		return result;
	}
	private int cookAST_body_getNextBodyOrInstruction(AST parent, List<Token> tokens, int nowIndex, int endIndex) {
		int i=nowIndex;Point area;AST child2;
		if(tokens.get(i).getSymbol()=='{') {
			area=getNextBody(tokens,i,endIndex);if(area==null)return i+1;
			child2=cookAST_body(tokens,area.x+1,area.y, false);
			parent.addChild(child2);
			i=area.y+1;
		}else {if(tokens.get(i).getSymbol()==';')return i+1;
			int index2=getNextSymbolIndex(tokens, i, endIndex,';');
			if(index2==-1)this.error(tokens, i, -1, -1);
			cookAST_instruction(parent,tokens,i,index2);
			i=index2+1;
		}
		return i;
	}
	private AST cookAST_body(List<Token> tokens, int beginIndex, int endIndex, boolean computeBorders) {
		Token token, nextToken;String tag;int tagCode;
		int a=beginIndex,b=endIndex;//int a=beginIndex+1,b=endIndex;
		Point area=null;if(computeBorders) {area=getNextBody(tokens,a,b);if(area!=null) {if(area.x==a) {a+=1;if(area.y==b)b-=1;}}}
		AST result = new AST(lexerAST, lexerAST.code_BODY);
		AST child=null,child2=null;
		AST lastIfElement=null;
		AST lastBodyConsumer=result;
		for(int i=a;i<b;++i) {
			token = tokens.get(i);
			if(token.getStatement()==Tokens._stringConstant||token.getStatement()==Tokens._stringComment)continue;
			tag=token.toString();
			if(tag.equals(";")||tag.length()==0)continue;
			if(tag.equals("if")) {
				area=getNextInBraces(tokens,i,b-1);
				child=result.prepareNewChild(lexerAST.code_BRANCH);
				child2=cookAST_CaptionComputation(tokens, area.x+1, area.y);
				child.addChild(child2);
				i=area.y+1;
				for(int branchchilds=0;branchchilds<2;branchchilds++) {
					i=cookAST_body_getNextBodyOrInstruction(child, tokens, i, b);
					if(branchchilds==0&&i<tokens.size()&&tokens.get(i).equalsToReserveWord("else")) {
						i+=1;
						child.changeCode(lexerAST.code_BRANCH_FULL);
						continue;
					}i=i-1;break;
				}continue;
			}
			//TODO Create and Debug at 20.06
			if(tag.equals("while")) {
				area=getNextInBraces(tokens,i,b);
				if(area==null)error(tokens, i, error_notBody, lexerAST.code_WHILE);
				child=result.prepareNewChild(lexerAST.code_WHILE);
				child2=cookAST_CaptionComputation(tokens, area.x+1, area.y);
				child.addChild(child2);
				i=area.y+1;
				//baking body
				i=cookAST_body_getNextBodyOrInstruction(child, tokens, i, b);
				i-=1;
				continue;
			}
			//TODO Create and Debug at 20.06
			if(tag.equals("do")) {
				child=result.prepareNewChild(lexerAST.code_DO);
				i=cookAST_body_getNextBodyOrInstruction(child, tokens, i, b);
				area=getNextInBraces(tokens,i,b);
				if(area==null)error(tokens, i, error_notCaption, lexerAST.code_DO);
				child2=cookAST_CaptionComputation(tokens, area.x+1, area.y);
				child.addChild(child2);
				i=area.y+1;//поглощаем разделитель инструкций
				continue;
			}
			//TODO Create and Debug at 20.06
			if(tag.equals("{")) {
				area=getNextBody(tokens,i,b);
				lastBodyConsumer.addChild(cookAST_body(tokens,area.x+1,area.y, false));
				lastBodyConsumer=result;
				i=area.y+1;
				continue;
			}
			//TODO Create and Debug at 20.06
			if(tag.equals("for")) {
				area=getNextInBraces(tokens,i,b-1);if(area==null)error(tokens, i, error_notCaption, lexerAST.code_FOR);
				child=result.prepareNewChild(lexerAST.code_FOR);
				
				int indexStart=area.x+1;
				int indexEnd=getNextSymbolIndex(tokens, area.x+1, area.y, ';');
				cookAST_instruction(child,tokens, indexStart,indexEnd);
				indexStart=indexEnd+1;
				indexEnd=getNextSymbolIndex(tokens, indexStart, area.y, ';');
				child.addChild(cookAST_CaptionComputation(tokens, indexStart,indexEnd));
				indexStart=indexEnd+1;
				indexEnd=getNextSymbolIndex(tokens, indexStart, area.y, ';');
				cookAST_instruction(child,tokens, indexStart,indexEnd);
				
				i=indexEnd+1;
				i=cookAST_body_getNextBodyOrInstruction(child, tokens, i, b);
				continue;
			}
			if(tag.equals("switch")) {
				//TODO WIP
				/**switch syntax: "switch brace_open caption brace_close body"*/
				area=getNextInBraces(tokens,i,b-1);
				if(area==null||area.x!=i+1) {this.error(tokens, i, error_notCaption,lexerAST.code_BRANCH_SWITCH);break;}
				child=result.prepareNewChild(lexerAST.code_BRANCH_SWITCH);
				child.addChild(cookAST_CaptionComputation(tokens,area.x,area.y));
				int index2=area.y+1;
				area=getNextBody(tokens,index2,b-1);
				if(area==null||area.x!=index2){this.error(tokens, i, error_notBody,lexerAST.code_BRANCH_SWITCH);break;}
				child.addChild(cookAST_body(tokens, area.x, area.y, false));
				i=area.y;
				continue;
			}
			if(tag.equals("case")) {
				//syntax: case caption :
				child=result.prepareNewChild(lexerAST.code_BRANCH_SWITCH_CASE);
				int index2=this.getNextSymbolIndex(tokens, i, endIndex, ':');
				if(index2==-1)this.error(tokens,i,error_missingSymbol,':');
				child.addChild(cookAST_CaptionComputation(tokens,i,index2));
				//this.error(tokens,i,error_reservedWord,lexerAST.code_BODY);
				i=index2;
				continue;
			}
			if(tag.equals("default")) {
				//syntax: default :
				child=result.prepareNewChild(lexerAST.code_BRANCH_SWITCH_DEFAULT);
				i+=1;
				Token t=tokens.get(i);
				if(!(t.isSymbol()&&t.getSymbol()==':'))
					this.error(tokens,i,error_missingSymbol,':');
				continue;
			}
			if(tag.equals("return")) {
				child=result.prepareNewChild(lexerAST.code_RETURN);
				Token t=tokens.get(i+1);
				if(t.toString()==";")continue;
				int index2 = getNextSymbolIndex(tokens, i+2, b,';');
				child2 = cookAST_CaptionComputation(tokens, i+2, index2);
				child.addChild(child2);
				i=index2;
				continue;
			}
			if(tag.equals("continue")) {
				result.prepareNewChild(lexerAST.code_CONTINUE);
				continue;
			}
			if(tag.equals("break")) {
				result.prepareNewChild(lexerAST.code_BREAK);
				continue;
			}
			int index2 = getNextSymbolIndex(tokens, i, b,';');
			if(index2==-1)continue;
			cookAST_instruction(result,tokens,i,index2);
			i=index2; 
		}
		return result;
	}
	/**variable declaration or equalization or function_call*/
	private void cookAST_instruction(AST parent, List<Token> tokens, int beginIndex, int endIndex) {
		Token t;IntList lbls;
		int a=beginIndex,b=endIndex;t=tokens.get(a);
		/**is body*/
		if(t.getSymbol()=='{') {parent.addChild(cookAST_body(tokens,beginIndex,endIndex, true));return;}
		/**is nothing*/
		if(t.getSymbol()==';') {
			if((b-a)<=1)
				return;
			else
				a++;
		}
		int index = getNextOccurenceIndex(tokens, a, b,"=;");
		if(index!=-1&&tokens.get(index).getSymbol()=='='){cookAST_VariableDeclaration(parent, tokens, beginIndex, endIndex);return;}//это присваивание
		//надо определить, это объявление новой переменной или условие с вычисленим значения
		boolean isVariableDeclaration=true;
		for(int i=a;i<b;++i) {
			t=tokens.get(i);
			lbls=lexer.getTokenLabels(t, null);
			if(t.getSymbol()==';')break;//пора заканчивать
			if(t.getSymbol()==',')continue;//этот символ ни о чём не говорит
			if(lbls.has(lexer.code_TYPE)||lbls.has(lexer.code_IDENTIFIER)||lbls.has(lexer.code_MODIFIER));else {isVariableDeclaration=false;break;}
		}
		if(isVariableDeclaration) {}
		if(isVariableDeclaration) {this.cookAST_VariableDeclaration(parent, tokens, beginIndex, endIndex);return;}
		//если это всё же вычисление значения
		parent.addChild(cookAST_CaptionComputation(tokens, beginIndex, endIndex));
	}
	/**Возвращает индекс первого попавшегося символа из группы "=;{"<br>
	 * хорошо работает везде, кроме секции вычисления, т.к. легко может спутать мультисимвол с присваиванием*/
	private int getNextOccurenceIndex(List<Token> tokens, int beginIndex, int endIndex, String catches){
		Token token;int inheritancesLevel=0;
		int tokenSymbol, brakeIndex;
		for(int i=beginIndex;i<endIndex;++i) {
			token = tokens.get(i);
			if(!token.isSymbol())continue;
			tokenSymbol=token.getSymbol();
			if((catches.indexOf(tokenSymbol)!=-1)&&inheritancesLevel==0)return i;
			brakeIndex=Lists.getIndex(brakes, tokenSymbol);
			if(brakeIndex==-1)continue;
			inheritancesLevel+=brakeIndex<3?1:-1;
			if(inheritancesLevel==-1) {System.out.println("Warning: missing semicolon before token["+Integer.toString(i)+"]; token pos "+token.getPosition() );return -1;}
		}
		return -1;
	}
	/**returns next symbol token position or -1 if end of body or out of endIndex*/
	private static int getNextSymbolIndex(List<Token> tokens, int beginIndex, int endIndex, char symbol){
		Token token;int inheritancesLevel=0;
		int tokenSymbol, brakeIndex;boolean isNormalInstructionsExists=false;
		for(int i=beginIndex;i<endIndex;++i) {
			token = tokens.get(i);
			if(!token.isSymbol())continue;
			tokenSymbol=token.getSymbol();
			if(tokenSymbol==symbol&&inheritancesLevel==0)return i;
			brakeIndex=Lists.getIndex(brakes, tokenSymbol);
			if(brakeIndex==-1)continue;
			inheritancesLevel+=brakeIndex<3?1:-1;
			if(inheritancesLevel==-1) {
				//System.out.println("Warning: missing semicolon before token["+Integer.toString(i)+"]; token pos "+token.getPosition() );
				return endIndex;
			}
		}
		return endIndex;
	}
	/**finds next body in area from beginIndex to endIndex-1*/
	private static Point getNextInBraces(List<Token> istream, int beginIndex,int endIndex){
		return getNextRegion(istream, beginIndex, endIndex, '(', ')');
	}
	/**finds next body in area from beginIndex to endIndex*/
	private static Point getNextBody(List<Token> istream, int beginIndex,int endIndex){
		return getNextRegion(istream, beginIndex, endIndex, '{', '}');
	}
	/**finds next body in area from beginIndex to endIndex-1*/
	private static Point getNextRegion(List<Token> istream, int beginIndex,int endIndex, char symbolOpen, char symbolClose){
		Token token;int inheritancesLevel=0;boolean isRecordStarted=false;
		int tokenSymbol, brakeIndex;int x=-1,y=-1;
		for(int i=beginIndex;i<endIndex+1;++i) {if(i==istream.size())return new Point(beginIndex, endIndex);
			token = istream.get(i);
			if(!token.isSymbol())continue;
			tokenSymbol=token.getSymbol();
			brakeIndex=Lists.getIndex(brakes, tokenSymbol);
			if(brakeIndex==-1)continue;
			inheritancesLevel+=brakeIndex<3?1:-1;
			if(inheritancesLevel==1&&!isRecordStarted&&tokenSymbol==symbolOpen) {
				isRecordStarted=true;
				x=i;
			}
			if(inheritancesLevel==0&&isRecordStarted&&tokenSymbol==symbolClose) {
				y=i;
				return new Point(x,y);
			}
		}
		return null;
	}
	private static int[] brakes = {'(','[','{',')',']','}'};
	/**now contains only brakes check<br>
	 * returns index>=0 - in error; -1 - all Ok; -2 - not all brakes closed*/
	public static int spellCheck(List<Token> tokens) {
		ArrayList<Integer> brakesStack = new ArrayList<Integer>();
		int nowSymbol;Token token;int index;
		for(int i=0;i<tokens.size();++i) {
			token=tokens.get(i);
			if(!token.isSymbol())continue;
			index=Lists.getIndex(brakes, (int)token.getSymbol());
			if(index==-1)continue;
			if(index<3) {brakesStack.add(index);continue;}
			index%=3;
			if(brakesStack.size()==0) {brakesStack.clear(); return i;}
			if(index!=brakesStack.get(brakesStack.size()-1)) {brakesStack.clear(); return i;}
			brakesStack.remove(brakesStack.size()-1);
		}
		if(brakesStack.size()>0) {brakesStack.clear();System.out.println("Error: spellCheck: not all closed brakes.");return -2;}
		brakesStack.clear();
		return -1;
	}

}
