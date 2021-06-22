package parts;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;

import common.CommonData;
import common.EntityRenderer;

public class PartNode extends PartBasic{
	public static int PART_DOT_SIZE=5;
	public static boolean drawRecurse=false;
	private int elementsCount=0;
	private static boolean recurseLock=false;
	//public ArrayList<PartBinaryBasic>parts=null;
	//public ArrayList<Integer> partsOrientations=null;
	public PartNode(int x, int y) {
		super(x, y, PartNode.PART_DOT_SIZE, PartNode.PART_DOT_SIZE);
	}
	/*
	public void destroy() {
		parts.clear();
	}
	*/
	public void drawWithColor(Color color,double camposx, double camposy, double scalex, double scaley) {
		g.setColor(color);

		int x=(int)(((double)pos.x-camposx)*scalex+screenWidthHalf);
		int y=(int)(((double)pos.y-camposy)*scaley+screenHeightHalf);
		if(texture!=texture_DefaultTextureIndex) {
			Image image = CommonData.imageController.images.get(texture);
			int w=image.getWidth(null);
			int h=image.getHeight(null);
			g.drawImage(image, x-w/2, y-h/2, null);
			//if(selected==1)
				g.drawRect(x-w/2-1, y-w/2-1, w+2, h+2);
		}else
			g.fillOval(x-xr, y-yr, width, height);
		//super.drawName(color, camposx, camposy, scalex, scaley);
		if(recurseLock)return;
		recurseLock=true;
		if(drawRecurse==true) {
			int x0,y0,x1,y1;boolean isElementOutOfScreen;
			PartBinaryBasic part;
			for(int i=0;i<CommonData.scheme.binaryPartsContainer.size();++i){
				part=CommonData.scheme.binaryPartsContainer.get(i);
				isElementOutOfScreen=false;
				if(part.nodeFrom==this){
					x1=part.nodeTo.pos.x;
					y1=part.nodeTo.pos.y;
					if(x1<EntityRenderer.windowLeft||x1>=EntityRenderer.windowRight||y1<EntityRenderer.windowDown||y1>=EntityRenderer.windowUp)isElementOutOfScreen=true;
				}else{
					if(part.nodeTo==this){
						x0=part.nodeFrom.pos.x;
						y0=part.nodeFrom.pos.y;
						if(x0<EntityRenderer.windowLeft||x0>=EntityRenderer.windowRight||y0<EntityRenderer.windowDown||y0>=EntityRenderer.windowUp)isElementOutOfScreen=true;		
					}else continue;
				}
				if(isElementOutOfScreen)continue;
				part.drawWithColor(
						Color.pink//(color==EntityEditor_Helper.colorMain) ? EntityEditor_Helper.colorMain : EntityEditor_Helper.colorRandom()
						, camposx, camposy,scalex, scaley
						);
			}
		}
		recurseLock=false;
	}
	public String getElementsCount() {
		return Integer.toString(elementsCount);		
	}
	public void addPart() {
		elementsCount+=1;
	}
	public void removePart() {
		elementsCount-=1;
	}

	public static int partsOrientations_FROMME=-1;
	public static int partsOrientations_TOME=1;
	public ArrayList<PartBinaryBasic> getPartsForNode(){
		ArrayList<PartBinaryBasic>parts=CommonData.scheme.binaryPartsContainer;
		ArrayList<Integer> partsOrientations = new ArrayList<Integer>();
		ArrayList<PartBinaryBasic> result = new ArrayList<PartBinaryBasic>();
		PartBinaryBasic part;
		for(int i=0;i<parts.size();++i) {
			part = parts.get(i);
			if(part.nodeFrom.equals(this)) {result.add(part);partsOrientations.add((partsOrientations_FROMME));}
			if(part.nodeTo.equals(this)) {result.add(part);partsOrientations.add((partsOrientations_TOME));}
		}
		return result;
	}

}
