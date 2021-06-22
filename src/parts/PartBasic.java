package parts;

import util.Colors;
import util.GenericObject;
import util.Parameters;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import common.CommonData;
import common.EntityRenderer;
import common.MatrixHelper;

public abstract class PartBasic {
	public static int SIZE_WIDTH_STANDART;
	public static int SIZE_HEIGHT_STANDART;
	public Object boundedUnit = null;
	public int boundedUnitClass = -1;
	//@Deprecated at 10 of june in 2021
	//public int index;
	public int selected=0;//1 - selected//2 -sub//3-sub2 etc..
	public boolean isMainCaptionKnown=false;
	protected int texture=-1;protected static int texture_DefaultTextureIndex = -1;
	
	public static Graphics g = null;//CommonData.graphics;
	
	
	public Point pos;public void setPos(int newX, int newY) {pos.x=newX;pos.y=newY;}
	protected int xr,yr,width,height;
	protected double ax,ay,bx,by,cx,cy;
	//public int partIndex;
	protected String name;
	public Parameters properties;
	public static double screenWidthHalf=400;
	public static double screenHeightHalf=300;
	public PartBasic(int x, int y, int w, int h) {
		this.pos=new Point(x,y);
		width=w;xr=(w>>>1);
		height=h;yr=(h>>>1);
		//partIndex=0;
		name="";
		properties=null;//
	}
	public void free() {
		name=null;
		clearProperties();
		pos=null;
	}
	public void draw(double camposx,double camposy,double scalex, double scaley) {
		drawWithColor(Colors.getColor(selected),camposx,camposy,scalex,scaley);
		if(EntityRenderer.isShowPartNamesAlways==0)drawName(Colors.getTextColor(selected),camposx,camposy,scalex,scaley);
	}
	public void drawOnMap(int[][] map, int caption, double camposx,double camposy,double scalex, double scaley) {
		//Dot scrPos = EntityRenderer.getImageDotFromMathCoords(x, y); - not corectly work during to asynchronous updates of campos
		int x=(int)(((double)pos.x-camposx)*scalex+screenWidthHalf);
		int y=(int)(((double)pos.y-camposy)*scaley+screenHeightHalf);
		MatrixHelper.fillZone(map, (x-xr), (y-yr), (x+xr), (y+yr), caption);
	}
	public void drawWithColor(Color color, double camposx,double camposy,double scalex, double scaley) {
		g.setColor(color);
		int x=(int)(((double)pos.x-camposx)*scalex+screenWidthHalf);
		int y=(int)(((double)pos.y-camposy)*scaley+screenHeightHalf);
		if(texture!=texture_DefaultTextureIndex) {
			g.drawImage(CommonData.imageController.images.get(texture), x, y, null);
		}else
			g.fillOval(x-xr, y-yr, x+xr, y+yr);
	}
	public void drawName(Color colorText, double camposx,double camposy,double scalex, double scaley) {
		g.setColor(colorText);
		double sx=cx,sy=cy;if(sy>0) {sx=-sx;sy=-sy;}
		sx*=1.5;sy*=1.5;

		int x=(int)(((double)pos.x-camposx)*scalex+screenWidthHalf);
		int y=(int)(((double)pos.y-camposy)*scaley+screenHeightHalf);
		g.drawString(name, x+(int)4, y+(int)2);
	}
	public String getName() {return name;}
	public void setName(String value) {name=value;}
	public void setProperty(String fieldName, Object fieldCaption) {
		if(properties==null)properties=new Parameters();
		properties.put(fieldName, fieldCaption);
	}
	public Object getProperty(String fieldName) {
		return properties.get(fieldName);
	}
	public String getProperties() {
		if(properties==null)return "";
		return properties.toString();		
	}
	public void clearProperties() {
		if(properties!=null)
			properties.clear();		
	}
	/**value format:"key:value;key:value;...;key:value;"*/
	public void setProperties(String value) {
		if(properties==null)properties=new Parameters();
		if(value.charAt(value.length()-1)=='}')value=value.substring(0, value.length()-1);
		if(value.charAt(0)=='{')value=value.substring(1, value.length());
		if(value.charAt(value.length()-1)!=';')value=value.concat(";");
		int l=value.length();
		char c;
		StringBuilder sb=new StringBuilder();
		String k=null,v=null;boolean isFieldNameRead=true;
		for(int i=0;i<l;++i) {
			c=value.charAt(i);
			switch(c) {
			case ':':case '=':
				k=(sb.toString());
				sb=new StringBuilder();
				isFieldNameRead=false;
				break;
			case ';':case ',':
				v=sb.toString();
				properties.put(k, v);
				sb=new StringBuilder();
				isFieldNameRead=true;
				break;
			default:
				if(isFieldNameRead&&c==' ')break;//имя переменной не должно содержать пробелов
				sb.append(c);
			}
		}
	}
}
