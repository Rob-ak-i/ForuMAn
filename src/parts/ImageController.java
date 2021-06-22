package parts;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;

import common.FileIO;

public class ImageController {
	public HashMap<String, Integer> imageCodes = new HashMap<String, Integer> ();
	public ArrayList<Image> images = new ArrayList<Image>();
	public void registerImage(String imageName, Image image) {
		int index=-1;
		index=images.size();
		images.add(image);
		imageCodes.put(imageName, index);
	}
	public void registerImage(String imageName, String imageFileName) {
		registerImage(imageName, FileIO.readImage(imageFileName));
	}
}
