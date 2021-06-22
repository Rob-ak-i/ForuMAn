package util.lang;

import java.lang.reflect.Field;

class ConcreteField {
	public String fieldName=null;
	public Object owner=null;
	public ConcreteField(Object owner, String fieldName) {
		this.owner=owner;
		this.fieldName=fieldName;
	}
	public Object get() {
		try {
			return owner.getClass().getField(fieldName).get(owner);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void set(Object value) {
		try {
			owner.getClass().getField(fieldName).set(owner,value);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}
}
