package parts;

import java.util.ArrayList;
import java.util.List;

import common.CommonData;
import common.EntityManager;
import unit.ManagedObject;
import util.Parameters;


public class BoundedObjectController {
	public ArrayList<String> classes = new ArrayList<String>();
	public int getClassIdentifier(String className) {
		int index = classes.indexOf(className);
		if(index==-1) {
			classes.add(className);
			return classes.size()-1;
		}
		return index;
	}
	public void linkUnit (PartBasic part, Object linkedObject, boolean getTexture) {
		part.boundedUnit=linkedObject;
		part.boundedUnitClass =  getClassIdentifier (linkedObject.getClass().getSimpleName());
		if(getTexture) {
			int index=-1;
			index=CommonData.imageController.imageCodes.get(linkedObject.getClass().getSimpleName());
			if(index>=0)
				part.texture = index; 
		}
	}
}
