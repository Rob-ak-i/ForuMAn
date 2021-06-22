package unit;

import java.util.ArrayList;

import util.GenericObject;

public class DataTableColumn<E> extends ArrayList<E>{
	public static DataTableColumn<?> create(Class c){
		if(c.equals(String.class))
			return new DataTableColumn<String>();
		if(c.equals(Integer.class))
			return new DataTableColumn<Integer>();
		if(c.equals(Double.class))
			return new DataTableColumn<Double>();
		//if(c.equals(GenericObject.class))
			return new DataTableColumn<GenericObject>();
		//return null;
	}
}

