package common;

import java.awt.Color;
import java.util.ArrayList;

import util.Parameters;
import util.RunnableThread;

public class TaskManager extends RunnableThread{
	protected boolean updated;
	
	public static int WORKDONESUCCESSTIMEOUT = 1000;
	public static int WORKDONENOTSUCCESSTIMEOUT = 5000;
	
	String scriptName;

	ArrayList<String> namesStack;
	ArrayList<Parameters> argumentsStack;
	ArrayList<String> operationsStack;
	private BackendProcessor backendProcessor;
	
	private boolean working = false;
	
	ArrayList<String> workDoneList;
	ArrayList<Integer> workDoneTimeList;
	private void reloadBackendProcessor() {
		backendProcessor.stop();
		backendProcessor.start();
	}
	public TaskManager(Object storageObject, String storageObjectIdentifier) {
		super(100);
		updated=false;
		
		scriptName="";
		
		backendProcessor =  new BackendProcessor(storageObject, storageObjectIdentifier, 100);
		reloadBackendProcessor();
		operationsStack = new ArrayList<String>();
		argumentsStack = new ArrayList<Parameters>();
		namesStack = new ArrayList<String>();
		
		workDoneList = new ArrayList<String>();
		workDoneTimeList = new ArrayList<Integer>();
	}
	public boolean addOperation(String scriptCode, Parameters arguments, String scriptName) {
		if(backendProcessor.isBusy()) {
			operationsStack.add(scriptCode);
			namesStack.add(scriptName);
			argumentsStack.add(arguments);
		}else {
			backendProcessor.addNewJob(scriptCode, arguments);
			this.scriptName=scriptName;
		}
		updated=true;
		working=true;
		return true;
	}
	private static Color[] colors = {Color.black, Color.gray, Color.orange, Color.green, Color.red, Color.blue, new Color(0x00ff7f)};
	
	public void runInner() {
		//printing works that done
		if(workDoneList.size()>0) {
			CommonData.frame.clearColoredText();
			for(int i=0;i<workDoneList.size();) {
				workDoneTimeList.set(i,workDoneTimeList.get(i)-maxSleepTime);
				if(workDoneTimeList.get(i)<=0) {
					workDoneTimeList.remove(i);
					workDoneList.remove(i);
					updated=true;
					continue;
				}
				CommonData.frame.addColoredText(workDoneList.get(i)+"done!",colors[6]);
				++i;
			}
			if(workDoneList.size()==0)CommonData.frame.clearColoredText();
		}
		//printing works that work
		if(updated) {
			if(backendProcessor.isBusy()) {
				CommonData.frame.addColoredText("baking: "+scriptName+" ", new Color(3));
			}
			CommonData.frame.addColoredText("||", Color.black);
			for(int i=0;i<operationsStack.size();++i) {
				CommonData.frame.addColoredText(namesStack.get(i)+"["+Integer.toString(i)+"]"+";", colors[1]);
			}
			updated=false;
		}
		if(!backendProcessor.isBusy()&&working){
			updated=true;
			workDoneList.add(scriptName+"["+Integer.toString(operationsStack.size())+"]");//workDoneList.add(processes.get(key).getClass().getSimpleName());
			
			workDoneTimeList.add(WORKDONESUCCESSTIMEOUT);
			if(operationsStack.size()>0){
				backendProcessor.addNewJob(operationsStack.get(0), argumentsStack.get(0));
				scriptName=namesStack.get(0);
				namesStack.remove(0);
				operationsStack.remove(0);
				argumentsStack.remove(0);
				working=true;
			}else
				working=false;
		}
	}
	
}

