package util.lang;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import common.Settings;
import util.Lists;
import util.Parameters;
import util.StringUtils;

public abstract class DilangInterpreterCore {
	public static final int Dialect_JAVA = 0;
	public static final int Dialect_Dilang = 1;
	protected TokenLexicon lexer;
	protected ASTLexicon lexerAST;
	protected Parameters localVariables;
	public Parameters globalVariables;
	protected ArrayList<AST> ASTStack;
	protected Object lastResult;
	protected boolean mayExternalVariablesBeForLocalUse=false;
	protected Parameters externalVariables=null;
	protected DilangInterpreterCore() {
		localVariables=new Parameters();
		ASTStack = new ArrayList<AST>();
		lastResult=null;
		globalVariables = new Parameters();
	}
	public Object process(AST e) {return process(e,null);}
	public Object process(AST e, Parameters externalVariables) {
		this.externalVariables=externalVariables;
		this.mayExternalVariablesBeForLocalUse=false;
		processAST(e);
		return lastResult;
	}
	public Object processInArguments(AST e, Parameters arguments) {
		this.externalVariables=arguments;
		this.mayExternalVariablesBeForLocalUse=true;
		processAST(e);
		return lastResult;
	}
	private Double _getDouble(Object arg0) {
		Double a=0.;
		if(arg0.getClass().equals(Integer.class))a=(Integer)arg0 +0.;else a=(Double)arg0;
		return a;
	}
	private void performComputation(String operationType, Object arg0, Object arg1, boolean isBinaryOperation) {
		do {
			if(isBinaryOperation)break;
			if(operationType.length()!=1)break;
			char symbol = operationType.charAt(0);
			switch(symbol) {
			case'-':lastResult=-(Double)arg0;return;
			case'!':lastResult=(1-(Double)arg0);return;
			}
		}while(false);
		do {
			if(operationType.length()!=1)break;
			char symbol = operationType.charAt(0);
			switch(symbol) {
			case'+':if(arg0.getClass().equals(String.class))lastResult=(String)(arg0)+(String)(arg1);else lastResult=_getDouble(arg0)+_getDouble(arg1); return;
			case'-':lastResult=_getDouble(arg0)-_getDouble(arg1);return;
			case'*':lastResult=_getDouble(arg0)*_getDouble(arg1);return;
			case'/':lastResult=_getDouble(arg0)/_getDouble(arg1);return;
			case'&':lastResult=(int)arg0&(int)arg1;return;
			case'|':lastResult=(int)arg0|(int)arg1;return;
			case'^':lastResult=Math.pow(_getDouble(arg0),_getDouble(arg1));return;
			case'<':lastResult=_getDouble(arg0)<_getDouble(arg1);return;
			case'>':lastResult=_getDouble(arg0)>_getDouble(arg1);return;
			}
		}while(false);
		do {
			if(operationType.length()!=2)break;
			if(operationType.equals("&&")) {lastResult=(Boolean)arg0&&(Boolean)arg1;return;}
			if(operationType.equals("||")) {lastResult=(Boolean)arg0||(Boolean)arg1;return;}
			if(operationType.equals("<=")) {lastResult=_getDouble(arg0)<=_getDouble(arg1);return;}
			if(operationType.equals(">=")) {lastResult=_getDouble(arg0)>=_getDouble(arg1);return;}
			if(operationType.equals("==")) {lastResult=arg0==arg1;return;}
		}while(false);
		lastResult=null;
	}
	
	private void fieldExtraction(Object selectedObject, String selectedField) {	try {lastResult=selectedObject.getClass().getField(selectedField);} catch (Exception e) {e.printStackTrace();}}

	private void methodCall(Object selectedObject, Method method, List<Object> parameters) {try {
			if(parameters==null||parameters.size()==0)
				lastResult=method.invoke(selectedObject);
			else {
				lastResult=method.invoke(selectedObject, Lists.getArray(parameters));
			}
		}catch (Exception e) {System.out.println(this.getClass().getCanonicalName()+":methodCall():Exception:"+e.getMessage());}
	}
	private void methodCall(Object selectedObject, String methodName, List<Object> parameters) {try {
			Method method=null;int parv=0;if(parameters!=null)parv=parameters.size();
			method = ConcreteMethod.getMethod(selectedObject, methodName, parameters);
			if(parv==0)
				lastResult=method.invoke(selectedObject);
			else {
				lastResult=method.invoke(selectedObject, Lists.getArray(parameters));
			}
		}catch (Exception e) {System.out.println(this.getClass().getCanonicalName()+":methodCall():Exception:"+e.getMessage());}
	}
	private void getField(Object selectedObject, String fieldName) {try {
		Class c=selectedObject.getClass();
		//Field f= c.getField(fieldName);
		lastResult=new ConcreteField(selectedObject,fieldName);
	}catch (Exception e) {System.out.println(this.getClass().getCanonicalName()+":getField():Exception:"+e.getMessage());}}
	private void getMethod(Object selectedObject, String methodName, List<Object> parameters) {try {
		Class c=selectedObject.getClass();
		//Field f= c.getField(methodName);
		lastResult=new ConcreteMethod(selectedObject,methodName, parameters);
	}catch (Exception e) {System.out.println(this.getClass().getCanonicalName()+":getField():Exception:"+e.getMessage());}}
	protected void processAST(AST e ) {
		ASTStack.add(e);
		lastResult = null;
		int code = e.getCode();String str;
		int entriesCount=0;
		
		
		if(code==lexerAST.code_FUNCTION_CALL) {
			//собираем вычисленные аргументы
			int n=e.count();
			ArrayList<Object> args=null;
			if(n>1)args=new ArrayList<Object>();
			for(int i=1;i<n;++i) {
				processAST(e.getChild(i));
				args.add(getLastResult());
			}
			//вызываем
			this.processAST_FunctionCall(e.getChild(0), args);
			if(args!=null)args.clear();
			entriesCount++;
		}
		if(code==lexerAST.code_OPERATION) {
			if(e.getIdentifier().contains(".")) {
				processAST(e.getChild(0));
				Object owner = getLastResult();
				AST ownerChild =e.getChild(1);
				String methodName = ownerChild.getIdentifier();
				if(ownerChild.getCode()==lexerAST.code_CLASS_FIELD)
					lastResult = new ConcreteField(owner,methodName);
				if(ownerChild.getCode()==lexerAST.code_CLASS_METHOD)try {
					System.out.println("UNREACHABLE CODE AT"+this.getClass().getCanonicalName()+":line 146");
					lastResult = owner.getClass().getMethod(methodName).invoke(owner);//new ConcreteMethod(owner,methodName);
				}catch(Exception e1) {}
			}else {//  PLUS MINUS MULTIPLY DIVIDE
				processAST(e.getChild(0));
				Object temp0=getLastResult(); 
				Object temp1=null; 
				if(e.count()>1) {processAST(e.getChild(1));temp1=getLastResult();}
				performComputation(e.getIdentifier(),temp0, temp1, e.count()>1);
			}
			entriesCount++;
		}
		if(code==lexerAST.code_VARIABLE) {
			processAST__VARIABLE(e);
			entriesCount++;
		}
		if(code==lexerAST.code_CONSTANT_NULL) {
			lastResult=null;
			entriesCount++;
		}
		if(code==lexerAST.code_CONSTANT_DIGIT) {
			str=e.getIdentifier();
			if(str.indexOf(".")!=-1)
				lastResult = Double.valueOf(str);
			lastResult = Integer.valueOf(str);
			entriesCount++;
		}
		if(code==lexerAST.code_CONSTANT_LOGIC) {
			str=e.getIdentifier();
			lastResult = (str.indexOf("true")>=0);
			entriesCount++;
		}
		if(code==lexerAST.code_CONSTANT_CHARACTER) {
			str=e.getIdentifier();
			lastResult = str.charAt(str.length()-1);
			entriesCount++;
		}
		if(code==lexerAST.code_CONSTANT_STRING) {
			lastResult = e.getIdentifier();
			entriesCount++;
		}  
		if(code==lexerAST.code_VARIABLE_DECLARATION || code==lexerAST.code_VARIABLE_ASSIGN) {
			processAST_VariableAssignment(e);
			entriesCount++;	
		}
		if(code==lexerAST.code_BODY||code==lexerAST.code_SCRIPT) {
			for(int i=0;i<e.count();++i)
				processAST(e.getChild(i));
			entriesCount++;
		}
		if(code==lexerAST.code_DO) {
			do {
				processAST(e.getChild(0));
				processAST(e.getChild(1));
			}while(getLastResult().getClass().equals(boolean.class)&&((boolean)getLastResult())==true);
			entriesCount++;
		}
		if(code==lexerAST.code_WHILE) {
			processAST(e.getChild(0));
			while(getLastResult().getClass().equals(boolean.class)&&((boolean)getLastResult())==true) {
				processAST(e.getChild(1));
				processAST(e.getChild(0));
			}
			entriesCount++;
		}
		if(code==lexerAST.code_FOR) {//init expr iter cycle
			//initialization
			processAST(e.getChild(0));
			//expression
			processAST(e.getChild(1));
			while(getLastResult().getClass().getSimpleName().equals(Boolean.class.getSimpleName())&&((boolean)getLastResult())==true) {
				//cycle
				processAST(e.getChild(3));
				//iter
				processAST(e.getChild(2));
				//expression
				processAST(e.getChild(1));
			}
			entriesCount++;
		}
		if(code==lexerAST.code_BRANCH||code==lexerAST.code_BRANCH_FULL) {
			processAST(e.getChild(0));
			Object caption=getLastResult();
			boolean result=false;
			if(caption.getClass().equals(Boolean.class)&&((boolean)caption))result=true;
			if(caption.getClass().equals(Integer.class)&&((int)caption)!=0)result=true;
			if(caption.getClass().equals(Double.class)&&((double)caption)!=0)result=true;
			if(result==true&&e.count()>1)processAST(e.getChild(1));
			if(result==false&&e.count()>2)processAST(e.getChild(2));
			entriesCount++;
		}
		
		if(entriesCount==0)System.out.println(StringUtils.power("----", ASTStack.size()-1)+"Error in "+this.getClass().getName()+": not entry for "+lexerAST.getLabelName(code));
		ASTStack.remove(ASTStack.size()-1);
	}
	private void processAST_VariableAssignment(AST e) {
		//TODO if var_declaration then first node is type|||if var_definition then first node value
		AST node;
		int n=e.count();
		if(n==0)return;
		node=e.getChild(n-1);
		processAST(node);//вычисляем выражение 
		String identifierName = e.getIdentifier();
		if(mayExternalVariablesBeForLocalUse)
			externalVariables.put(identifierName, getLastResult());
		else
			localVariables.put(identifierName, getLastResult());
	}
	private void processAST_FunctionCall(AST e, List<Object> parameters) {
		
		if(e.getCode()==lexerAST.code_VARIABLE) {
			System.out.println("ERROR! UNREACHABLE CODE AT"+this.getClass().getCanonicalName()+":line 258");
			processAST__VARIABLE(e);
			Object owner = getLastResult();
			
		}
		
		if(e.getIdentifier().contains(".")) {
			processAST(e.getChild(0));
			Object owner = getLastResult();
			AST ownerChild =e.getChild(1);
			String methodName = ownerChild.getIdentifier();Method method=null;
			if(ownerChild.getCode()==lexerAST.code_CLASS_FIELD)
				lastResult = new ConcreteField(owner,methodName);
			if(ownerChild.getCode()==lexerAST.code_CLASS_METHOD)try {
				if(parameters==null||parameters.size()==0) {
					method=ConcreteMethod.getMethod(owner, methodName, null);
					lastResult = method.invoke(owner);
				}else {
					method = ConcreteMethod.getMethod(owner, methodName, parameters);
					lastResult = method.invoke(owner, Lists.getArray(parameters));
				}
			}catch(Exception e1) {
				if(owner==null)
					System.out.println("Error:"+this.getClass().getCanonicalName()+":processAST_FunctionCall(): null object owner of method: "+methodName+"("+parameters.toString()+")");
				else				
					System.out.println("Error:"+this.getClass().getCanonicalName()+":processAST_FunctionCall(): not such method: "+owner.getClass().getCanonicalName()+"."+methodName+"("+(parameters!=null?parameters.toString():"")+")");
				
				Method m;Method[] methods;
				methods=owner.getClass().getMethods();
				System.out.println("ownerMethods:");
				for(int i=0;i<methods.length;++i) {
					m=methods[i];
					System.out.println("----"+m.getName()+"():["+m.getParameterCount()+"]");
					for(int j=0;j<m.getParameterCount();++j)
						System.out.println("--------"+m.getParameters()[j].getType().toString());
				}
				e1.printStackTrace();}
		}
	}
	private void processAST__VARIABLE(AST e) {
		if(externalVariables!=null) {
			lastResult = externalVariables.get(e.getIdentifier());
			if(lastResult==null)lastResult=this.findObject(e.getIdentifier());
		}else
			lastResult = this.findObject(e.getIdentifier());
	}
	private Object getLastResult() {
		if(lastResult==null)return null;
		if(lastResult.getClass().equals(ConcreteField.class))return ((ConcreteField)(lastResult)).get();
		if(lastResult.getClass().equals(ConcreteMethod.class))return ((ConcreteMethod)(lastResult)).get();
		return lastResult;
	}
	private Object findObject(String identifier) {
		Object result = null;
		result = localVariables.get(identifier);
		if(result==null)result = globalVariables.get(identifier);
		if(result==null)System.out.println("Error in "+this.getClass().getCanonicalName()+" at findObject: not found variable with identifier='"+identifier+"'");
		return result;
	}
}
