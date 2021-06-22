package util.lang;

import java.util.ArrayList;

public class ASTObjectClone extends Object{
	public ASTObjectClone parent;
	public ArrayList<ASTObjectClone> fields;
	public ArrayList<ASTObjectClone> methods;
	public Object value;
	public ASTObjectClone (Object realObject) {
		super();
		fields= new ArrayList<ASTObjectClone>();
		methods= new ArrayList<ASTObjectClone>();
	}
}
