package common;

import gui.GUIElementViewer;
import util.AdditionalDataObjects;

public class EntityGUIElementViewerManager extends EntityManager<GUIElementViewer>{

	public EntityGUIElementViewerManager() {
		super(GUIElementViewer.class);
	}

	
	public void prepareViewer(String identifier, int x, int y) {
		if(this.identifiers.contains(identifier))return;
		GUIElementViewer viewer = new GUIElementViewer(x, y);
		this.addManagedElement(viewer, identifier);
		viewer.prepare(identifier, AdditionalDataObjects.packParameters("null", null));
	}
}
