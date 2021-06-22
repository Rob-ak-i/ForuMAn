package util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**Лексикон для каждой сохранённой строки или целого имеет список меток.<br>
 * очень хочется попробовать использовать его индивидуально для каждого интерпретатора.<br>
 * <font color="red">ВНИМАНИЕ </font>: все метки декларировать большими буквами и обязательно добавлять интовые "code_"+labelName нестатические нефинальные поля!
 * */
public abstract class Lexicon {
	public Object tags;
	public ArrayList<String> labelsList = null;
	public HashMap<String, IntList> labelsMap = null;
	public HashMap<Integer, IntList> labelsMapInt = null;
	private IntList label_DefaultInt = null;
	private IntList label_DefaultString = null;
	private String defaultValue="UNKNOWN_TAG";
	/**isAddLabels - индексировать ли финальные метки класса с записью в кодах*/
	public Lexicon(String[] defaultIntLabels, String[] defaultStringLabels) {
		labelsList = new ArrayList<String>();
		labelsMap = new HashMap<String, IntList>();
		labelsMapInt = new HashMap<Integer, IntList>();
		if(defaultIntLabels!=null) {
			label_DefaultInt = new IntList();
			int index=-1;
			for(int i=0;i<defaultIntLabels.length;++i) {
				index = getOrAdd(defaultIntLabels[i]);
				label_DefaultInt.add(index);
			}
		}
		if(defaultStringLabels!=null) {
			label_DefaultString = new IntList();
			int index=-1;
			for(int i=0;i<defaultStringLabels.length;++i) {
				index = getOrAdd(defaultStringLabels[i]);
				label_DefaultString.add(index);
			}
		}
	}
	public int getOrAdd(String labelName) {
		int index=labelsList.indexOf(labelName);
		if(index!=-1)return index;
		index=labelsList.size();
		labelsList.add(labelName);
		return index;
	}

	protected void addLabels(Object labelsToAddObject) {
		Class thisClass = labelsToAddObject.getClass();
		Field[] fields = thisClass.getFields();
		String fieldName=null, codeString=null;
		for(Field field:fields) {
			fieldName = field.getName();
			if(!fieldName.startsWith("code_"))continue;
			codeString = fieldName.substring(5);
			labelsList.add(codeString);
			try {
				//System.out.println(fieldName+":"+Integer.toString(thisClass.getField(fieldName).getInt(labelsToAddObject)));
				thisClass.getField(fieldName).setInt(labelsToAddObject, labelsList.size()-1);
				//System.out.println(fieldName+":"+Integer.toString(thisClass.getField(fieldName).getInt(labelsToAddObject)));
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		fields=null;
	}
	public String getLabelName(int code) {if(code<0||code>=labelsList.size())return defaultValue;return labelsList.get(code);}
	public String getLabelsName(IntList labels) {return labels.toText(labelsList);}
	/**Основная функция класса, выдаёт список меток для числового идентификатора LL(0)*/
	public IntList getTokenLabels(int code) {
		return labelsMapInt.getOrDefault(code, label_DefaultInt);
	}
	/**Основная функция класса, выдаёт список меток для строкового идентификатора LL(0)*/
	public IntList getTokenLabels(String identifier) {
		return labelsMap.getOrDefault(identifier, label_DefaultString);
	}
	
}
