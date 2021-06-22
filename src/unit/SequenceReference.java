package unit;

import java.awt.Point;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class SequenceReference extends ArrayList<Point> {
	public SequenceReference(Point firstElement) {
		super();
		this.add(firstElement);
	}
}
