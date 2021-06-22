package util;

import java.util.HashMap;

public class PropertiesInt extends HashMap<Integer, String>{
	public static final String stringSeparator = ";";  
	private static final long serialVersionUID = -2474893772874179842L;
	public void append(Integer key, String appendableValue) {
		if(containsKey(key))
			put(key, get(key)+stringSeparator+appendableValue);
		else
			put(key, appendableValue);
	}
	
}
