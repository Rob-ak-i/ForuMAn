package util;

import java.util.HashMap;

public class Parameters extends HashMap<String, Object> {
	public static final String parameter_UnitClass = "unitClass";
	public static final String parameter_UnitClass_user = "user";
	public static final String parameter_ID = "ID";
	public static final String parameter_ForumKey = "ForumKey";
	public static final String parameter_UnitClass_sequence = "sequence";
	public static final String parameter_UnitClass_attached = "attached";
	/**was set by random seed*/
	private static final long serialVersionUID = -1589099301835372898L;
	public boolean HALT=false;
	public Parameters pack(String key, Object value) {
		this.put(key, value);
		return this;
	}
	public boolean equalsTo(Parameters otherParameters) {return equalsTo(otherParameters,true);}
	public boolean equalsTo(Parameters otherParameters, boolean checkLength) {
		if(checkLength&&keySet().size()!=otherParameters.keySet().size())return false;
		Object value1;
		Object value2;
		for(String key:keySet()) {
			value1=this.get(key);
			value2=otherParameters.get(key);
			if(!value1.equals(value2))return false;
		}return true;
	}
	public String toString() {
		String result = super.toString();
		if(result.charAt(0)=='{')result=result.substring(1);
		if(result.charAt(result.length()-1)=='}')result=result.substring(0, result.length()-1);
		return result;
	}
	public Parameters append(Parameters otherParameters, boolean clearAppendix) {
		if(otherParameters == null)return this;
		for(String key:otherParameters.keySet())
			this.put(key, otherParameters.get(key));
		if(clearAppendix)
			otherParameters.clear();
		return this;
	}
	public void reassign(String keyOld, String keyNew) {
		Object object = this.get(keyOld);
		if(object==null)System.out.print("key \'"+keyOld+"\' not exist");
		this.remove(keyOld);
		this.put(keyNew, object);
	}
	public Object find(Class unitClass) {
		Object result=null;
		for(String key: keySet()) {
			result = this.get(key);
			if(result.getClass()==unitClass)
				return result;
		}
		return null;
	}
	public String searchKeyByObjectClassName(String objectClassName) {
		Object object=null;
		for(String key:keySet()) {
			object = get(key);
			if(object.getClass().getSimpleName().equals(objectClassName))return key;
		}
		return null;
	}
}
