package common;

import java.util.ArrayList;
import java.util.List;

import gui.JComboBoxExt;
import unit.DataTable;
import unit.ManagedObject;
import unit.Sequence;
import util.ItemSorter;
import util.Lists;
import util.Parameters;

@SuppressWarnings("rawtypes")
public class EntityManager<E extends ManagedObject> {
	protected ArrayList <E> managedElements;
	protected ArrayList <String> identifiers;
	protected ArrayList <JComboBoxExt> controlledComboBoxes;
	protected String objectClassName;
	protected Class objectClass;
	public EntityManager(Class<? extends ManagedObject> objectClass) {
		managedElements=(ArrayList) new ArrayList<E>();
		this.objectClass= objectClass;
		identifiers= new ArrayList<String>();
		controlledComboBoxes=new ArrayList<JComboBoxExt>();
		this.objectClassName=objectClass.getSimpleName();
	}
	public Class getManagedObjectClass() {return objectClass;}
	public String getManagedObjectClassName() {//public Class<?> getManagedObjectClassName() {
		return objectClassName;//return objectClass.getClass().getSimpleName();
	}
	public void addControlledComboBox(JComboBoxExt controlledComboBox) {this.controlledComboBoxes.add(controlledComboBox);}
	public int getManagedElementsCount() {return managedElements.size();}//=new ArrayList<DataTable>();
	/**try to add table with identifier==table.name*/

	public void addManagedElement(E element, String identifier) {
		int index = identifiers.indexOf(identifier);
		if(index!=-1) {System.out.println("Element with identifier '"+identifier+"' already exists; try to add this element with another identifier.");return;}
		managedElements.add(element);
		identifiers.add(identifier);
		System.out.println("Element \'"+element.toString()+"\' with identifier \'"+identifier+"\'was successfully added");
		JComboBoxExt controlledComboBox=null;for(int i=0;i<controlledComboBoxes.size();++i) {controlledComboBox=controlledComboBoxes.get(i);if(controlledComboBox==null)controlledComboBoxes.remove(i); else controlledComboBox.addItem(identifier);}
	}
	public void removeManagedElement(String identifier) {
		int index=identifiers.indexOf(identifier);
		if(index==-1) {System.out.println("Element with identifier '"+identifier+"' not exists, nothing to remove.");return;}
		managedElements.get(index).clear();
		managedElements.remove(index);
		identifiers.remove(index);
		JComboBoxExt controlledComboBox=null;for(int i=0;i<controlledComboBoxes.size();++i) {controlledComboBox=controlledComboBoxes.get(i);if(controlledComboBox==null)controlledComboBoxes.remove(i); else controlledComboBox.remove(index);}
	}
	public E getManagedElement(String identifier) {
		int index=identifiers.indexOf(identifier);
		if(index==-1) {System.out.println("Element with identifier '"+identifier+"' not exists, nothing to get.");return null;}
		return managedElements.get(index);
	}
	public int getManagedElementIndex(String identifier) {
		int index=identifiers.indexOf(identifier);
		if(index==-1) {System.out.println("Element with identifier '"+identifier+"' not exists, nothing to get.");return -1;}
		return index;
	}
	public int getManagedElementIndexByObject(E object) {
		int index = managedElements.indexOf(object);
		//int index=identifiers.indexOf(identifier);
		if(index==-1) {
			System.out.println("Error: element '"+object+"' not indexed");
			return -1;
			}
		return index;
	}
	public String getManagedElementIdentifier(int index) {
		return identifiers.get(index);
	}
	public String getManagedElementIdentifier(Object object) {
		int index=managedElements.indexOf(object);
		if(index<0)return null;
		return identifiers.get(index);
	}
	public E getManagedElement(int index) {
		if(index<0||index>=managedElements.size())return null;
		return managedElements.get(index);
	}
	
	public void clear() {
		for(int i = 0; i<managedElements.size();++i)
			managedElements.get(i).clear();
		managedElements.clear();
		identifiers.clear();
		JComboBoxExt controlledComboBox=null;
		for(int i=0;i<controlledComboBoxes.size();++i) {
			controlledComboBox=controlledComboBoxes.get(i);
			if(controlledComboBox==null)
				controlledComboBoxes.remove(i);
			else
				controlledComboBox.removeAll();
		}
	}
	public E getNewInstance() {
		E element=null;
		try {
			element = (E) objectClass.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return element;
	}
	public String importObject(ManagedObject producer, String newKey, String methodName) {
		E newObject = (E)producer.produce(objectClass, methodName);
		newKey = fixKey(newKey);
		addManagedElement(newObject, newKey);
		return newKey;
	}
	public void saveManagedElement(String fileName, String identifier) {
		int index= identifiers.indexOf(identifier);
		managedElements.get(index).saveToFile(fileName);
	}
	public void saveElementToFile(String fileName, ManagedObject element) {
		element.saveToFile(fileName);
	}
	public String loadManagedElement(String fileName, String identifier, String loadCommand) {
		E element = getNewInstance();
		element.loadFromFile(fileName, loadCommand);
		String name = fixKey((identifier==null||identifier.length()==0)?element.getName():identifier);
		addManagedElement(element, name);
		return name;
	}
	public E concatElements(ArrayList<E> elements) {
		if(elements.size()==0)return null;if(elements.size()==1)return elements.get(0);
		E element = getNewInstance(),nowElement=null;//new DataTable(elements.get(0),"concat_name", "concat_title"), 
		String elementName="{";
		String key="{";int index;
		for(int i =0;i<elements.size();++i) {
			if(i>0)key+="+";
			nowElement=elements.get(i);
			element.append(nowElement);
			index=this.managedElements.indexOf(nowElement);
			key+=this.identifiers.get(index)+", ";
			elementName+=nowElement.getName()+", ";
		}
		elementName=elementName.substring(0, elementName.length()-2)+"}";
		key=key.substring(0, key.length()-2)+"}";
		addManagedElement(element, key);
		return element;
	}
	public String generateNewKey(String prefix) {
		int intResult=-1;
		int index=0;//=(int) (Math.random()*0xffffffff);
		String indexRepresentation=null;
		while(index!=-1) {
			indexRepresentation = (prefix!=null)?prefix:""+ Integer.toString(index);
			index++;
			if(identifiers.contains(indexRepresentation))continue;else break;
		}if(index==-1)return null;
		return indexRepresentation;
	}
	protected String fixKey(String key) {
		if(key==null||identifiers.contains(key))key=generateNewKey(key);return key;
	}
	public int size() {
		return identifiers.size();
	}


}
