package common;

import util.Parameters;
import util.RunnableThread;
import util.lang.AST;
import util.lang.DilangExpressionParser;
import util.lang.DilangInterpreter;

public class BackendProcessor extends RunnableThread {


	protected boolean busy = false; public synchronized boolean isBusy() {return busy;} 
	protected Parameters arguments;
	protected String scriptCode;
	protected AST scriptAST;
	protected DilangInterpreter interpreter;

	public BackendProcessor(Object storageObject, String storageObjectIdentifier, int maxSleepTime) {
		super(maxSleepTime);
		interpreter = new DilangInterpreter();
		this.interpreter.globalVariables.put(storageObjectIdentifier, storageObject);
	}
	@Override
	public void runInner() {
		if(!busy)return;
		
		scriptAST = interpreter.parseDilang(scriptCode);
		//System.out.println(DilangExpressionParser.printAST(scriptAST));
		interpreter.processInArguments(scriptAST, arguments);
		arguments.clear();arguments=null;
		scriptCode=null;
		scriptAST.clear();
		busy=false;
	}
	
	public synchronized void addNewJob(String script, Parameters arguments) {
		if(busy) {System.out.println("Warning: scriptProcessor is still busy, try to call later.");return;}
		this.scriptCode=script;
		this.arguments=arguments;
		busy=true;
	}

}
