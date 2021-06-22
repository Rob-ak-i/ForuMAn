package common;
//MY RENDERING SYSTEM EXPLAINED IN JAVA AND WITH ONLY ONE CLASS.
//DOES NOT INCLUDE TICKING JUST RENDERING, TICKING IS A DIFFERENT THREAD.
//(so they can run on different frames per second)
//main() method is at the very bottom.

/*
 * @Author
 *  CodyOrr4
 */
import javax.swing.*;

import parts.GenericPhantomRectangle;
import parts.PartBasic;
import util.Colors;

import java.awt.*;
import java.awt.image.*;
import java.util.concurrent.TimeUnit;

public class EntityRenderer implements Runnable{
	public static int isShowPartNamesAlways=0;
	
	private static JPanel panel;
	//public static JViewport viewport;
	public static Canvas canvas;
	public static BufferedImage bi;
	public static BufferStrategy bs;
	public static Graphics g;
	public static Thread renderingThread;
	public static boolean renderingSwitch;
	public static int renderingMethod=0;
	public static int maxSleepTime;
	private static int tick=0;
	private static int ticksCount=100;

	private static int width=800;
	private static int height=600;
	public static double scalex=1.;
	public static double scaley=1.;
	public static int windowLeft=0;
	public static int windowRight=0;
	public static int windowUp=0;
	public static int windowDown=0;
	private static boolean updateLogicMapNow = false;
	private static boolean updateLogicMapInProgress = false;
	public static void updateLogicMapNow() {if(updateLogicMapInProgress)return;updateLogicMapNow=true;};
	

	public static Rectangle normalizeZone(int x0, int y0, int x1, int y1) {
		int index=0;
		if(x1<x0) {index=x0;x0=x1;x1=index;}
		if(y1<y0) {index=y0;y0=y1;y1=index;}
		if(x0<windowLeft)x0=windowLeft;
		if(y0<windowUp)y0=windowUp;
		if(x1>windowRight)x1=windowRight;
		if(y1>windowDown)y1=windowDown;
		Rectangle rect = new Rectangle(x0,y0,x1-x0,y1-y0);
		return rect;
	}
	public static double camposx=350;
	public static double camposy=250;
    //turns this runnable into an object.
	public static Point getImageDotFromMathCoords1(double x,double y){
		int xi=(int)(((double)x-camposx)*scalex+((double)(width>>>1)));
		int yi=(int)(((double)y-camposy)*scaley+((double)(height>>>1)));
		return new Point(xi,yi);
	}
	public static Point getRealDotFromImageCoords(double xi,double yi){
		int x=(int)((xi-((double)(width>>>1)))/scalex+camposx);
		int y=(int)((yi-((double)(height>>>1)))/scaley+camposy);
		return new Point(x,y);
	}
	public static Point getRealDotFromImageCoords(Point pos){
		int x=(int)((pos.x-((double)(width>>>1)))/scalex+camposx);
		int y=(int)((pos.y-((double)(height>>>1)))/scaley+camposy);
		return new Point(x,y);
	}
	public static Rectangle getRealRectFromImageRect(Rectangle rect){
		Rectangle result = new Rectangle ();
		int x=(int)((rect.x-((double)(width>>>1)))/scalex+camposx);
		int y=(int)((rect.y-((double)(height>>>1)))/scaley+camposy);
		result.x=x;
		result.y=y;
		result.width=(int) (rect.width*scalex);
		result.height=(int) (rect.height*scaley);
		return result;
	}
	public void updateWindow(boolean resized){
		if(resized){
			width=canvas.getWidth();
			height=canvas.getHeight();
			PartBasic.screenWidthHalf=width>>>1;
			PartBasic.screenHeightHalf=height>>>1;
		}
		int wl=windowLeft,wr=windowRight,wd=windowDown,wu=windowUp;
		windowLeft=(int)(camposx-((double)(width>>>1))/scalex);
		windowRight=(int)(camposx+((double)(width>>>1))/scalex);
		windowUp=(int)(camposy-((double)(height>>>1))/scaley);
		windowDown=(int)(camposy+((double)(height>>>1))/scaley);
		if((wl!=windowLeft||wr!=windowRight||wd!=windowDown||wu!=windowUp))//&&(CommonData.renderer!=null))
			renderMap();//CommonData.renderer.renderMap();//updateLogicMapNow();
	}
	private static void computeWindow(){
		width=canvas.getWidth();
		height=canvas.getHeight();
		PartBasic.screenWidthHalf=width>>>1;
		PartBasic.screenHeightHalf=height>>>1;
		//int wl=windowLeft,wr=windowRight,wd=windowDown,wu=windowUp;
		windowLeft=(int)(camposx-((double)(width>>>1))/scalex);
		windowRight=(int)(camposx+((double)(width>>>1))/scalex);
		windowUp=(int)(camposy-((double)(height>>>1))/scaley);
		windowDown=(int)(camposy+((double)(height>>>1))/scaley);
	}
    public EntityRenderer(JPanel panelIn, Canvas canvasIn, int renderMethodIn) {
    	panel=panelIn;
    	canvas=canvasIn;
        renderingMethod= renderMethodIn;
		updateWindow(true);
        maxSleepTime=100;
    }

    /**used to run things that are not meant to be run in a loop;*/
    private void init() {
        //cache.initCache(); //can now grab sprites (including names/ids) and other types within cache.
    }
    public static BufferedImage getCanvasImage() {
    	if(renderingMethod==1)
    		return bi;
    	return (BufferedImage) canvas.createImage(width, height);
    }
    //runs the runnable
    public void createCanvasComponents(boolean withoutNullCheck) {
        System.out.println("creating canvas components: renderingMethod = "+Integer.toString(renderingMethod));
    	if(renderingMethod==1 && bi == null
    			|| renderingMethod==1 && withoutNullCheck ) {
    		width=panel.getWidth();
    		height=panel.getHeight();
        	bi=new  BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			g = bi.createGraphics();
			PartBasic.g=g;
            
			Graphics2D d2 = (Graphics2D) g;
			d2.setColor(Color.white);
			d2.fillRect(0, 0, width, height);
        }
        if(renderingMethod==0 && bs == null
    			|| renderingMethod==1 && withoutNullCheck ) {
    		width=panel.getWidth();
    		height=panel.getHeight();
    		canvas.setSize(width, height);
            canvas.createBufferStrategy(3);//should only need a max of 3.
            bs = canvas.getBufferStrategy();
            g = bs.getDrawGraphics();

			Graphics2D d2 = (Graphics2D) g;
			d2.setColor(Color.white);
			d2.fillRect(0, 0, width, height);
            
			PartBasic.g=g;
			


        }
    }
    public void run() {
        init();
        while(renderingSwitch) {
        	
            if(bi == null && bs==null) {
            	this.createCanvasComponents(false);
            }

            long startRendering=System.nanoTime();
            g=bs.getDrawGraphics();
            g.clearRect(0, 0, width, height);
            renderGUI();
            tick=(tick+1)%ticksCount;
    		animationPhase=animationPhase+1;if(animationPhase==animationPhasesCount)animationPhase=0;
    		if(animationPhase==0||updateLogicMapNow) {
                computeWindow();
    			renderMap();
    		}
    		
            render(g);//drawing with methods
            bs.show();
            //g.dispose();
            
            //duration of the frame rendering in ms :
            long durationMs=TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-startRendering);
            // now waits 
            if (durationMs < maxSleepTime) 
            {
            	try {
					Thread.sleep(maxSleepTime - durationMs);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }     
        }
    }
    
    private static int animationPhase=0;
    private static int animationPhasesCount=16;
	private void renderGUI() {
		if(EntityEditorMouseListener.getMode()==EntityEditorMouseListener.MODE_PUT_PHANTOM_MODE)
			CommonData.genericPhantom.draw(g);
		if(EntityEditorMouseListener.getMode()==EntityEditorMouseListener.MODE_SELECT_ZONE)
			GenericPhantomRectangle.draw(g);
	}
    
    private void renderMap(){
    	updateLogicMapNow=false;
    	if(updateLogicMapInProgress)return;
    	updateLogicMapInProgress=true;
    	parts.SchematicManager.drawLogicMap(windowLeft, windowRight, windowUp, windowDown, camposx, camposy, scalex, scaley);
    	updateLogicMapInProgress=false;
    }
    /**renders everything (this method is used in a while() loop based on a boolean, within the run() method);*/
    private void render(Graphics g) {
    	g.setColor(Colors.colorMain);
    	parts.SchematicManager.drawScene(g, windowLeft, windowRight, windowUp, windowDown, camposx, camposy, scalex, scaley);
    	
    	//panel.requestFocusInWindow();
        //g.drawImage(cache.getSprite(0), 400, 300, 25, 25, null);
    	g.setColor(Colors.colorBackground);
    }

    //starts the run method and creates a thread for this 
    public synchronized void start() {
        renderingThread = new Thread(this);
        renderingThread.setName("Graphic editor rendering thread");
        renderingThread.start();
        renderingSwitch = true;
    }

    //stops the while loop by setting the boolean to false and the thread is now null
    public synchronized void stop() {
        renderingThread = null;
        renderingSwitch = false;
    }


   ///public static void main(String[] args) {        Renderer gameExample = new Renderer();        gameExample.start();    }

}