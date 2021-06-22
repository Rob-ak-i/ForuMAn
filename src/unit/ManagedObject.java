package unit;

import java.util.List;

import common.EntityManager;

public interface ManagedObject {
	public void clear();
	public String getName();public void setName(String newName);
	public int getMeasurableParameter();
	public void saveToFile(String fileName);
	public void loadFromFile(String fileName, String loadCommand);
	public void append(ManagedObject object);
	public String getKey();
	public EntityManager<?> getManager();
	public ManagedObject produce(Class<?> resultObjectClass, String parameters);
}
