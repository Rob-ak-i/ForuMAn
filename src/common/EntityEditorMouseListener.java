package common;


import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import gui.GUISchematicEditor_Toolbar;
import parts.GenericPhantom;
import parts.GenericPhantomRectangle;
import parts.PartBasic;
import parts.PartBinaryBasic;
import parts.PartNode;

public class EntityEditorMouseListener extends MouseInputAdapter{

	public static MouseWheelListener mouseWheelListener = (new MouseWheelListener() {
		@Override
		public void mouseWheelMoved(MouseWheelEvent arg0) {
			double value=arg0.getPreciseWheelRotation();
			moveAndScaleCamera(value, EntityEditorMouseListener.isLControlPressed, EntityEditorMouseListener.isLShiftPressed);
			if(CommonData.renderer!=null)CommonData.renderer.updateWindow(false);
		}
	});
	public static parts.PartNode nodeFrom=null, nodeTo=null;public static int partClassIndex=0;
	public static parts.PartBasic tempelement=null;
	public static JComboBox partsList;

	private static boolean middleKeyPressed=false;
	//private static boolean leftKeyPressed=false;
	private static boolean leftKeyPressed=false;

	public static final int SELECTORMODE_ALL = 0;
	public static final int SELECTORMODE_NODE = 1;
	public static final int SELECTORMODE_PARTBINARY = 2;
	//public static final int SELECTORMODE_REQUIREMENT = 3; WIF
	
	private static int selectorMode = SELECTORMODE_ALL;
	public static int getSelectorMode() {return selectorMode;}
	public static void setSelectorMode(int selectorModeNew) {
		selectorMode=selectorModeNew;
	}
	public static ArrayList<? extends PartBasic> selectedContainer() {
		if(selectorMode==SELECTORMODE_ALL)return CommonData.scheme.elementsContainer;
		if(selectorMode==SELECTORMODE_NODE)return CommonData.scheme.nodesContainer;
		if(selectorMode==SELECTORMODE_PARTBINARY)return CommonData.scheme.binaryPartsContainer;
		return CommonData.scheme.elementsContainer;
	}
	
	public static final int MODE_RESERVE=-1;
	public static final int MODE_OBSERVE=0;
	public static final int MODE_SELECT=1;
	public static final int MODE_SELECT_ZONE=2;
	public static final int MODE_MOVE=3;
	
	public static final int MODE_DRAW_LINE=4;
	public static final int MODE_SETUP_NODE=10;
	public static final int MODE_PUT_PHANTOM_MODE=11;
	
	public static final int MODE_SETUP_PART_STAGE1_selectNodeFrom=30;
	public static final int MODE_SETUP_PART_STAGE2_selectNodeTo=31;
	public static final int MODE_SETUP_PART_STAGE3_selectPosition=32;
	public static final int MODE_SELECT_WITH_CONTROL=40;
	
	
	private static int mode=MODE_OBSERVE;//public void setMode(int arg) {mode=arg;}
	private static int modeReserve=-1;
	public static int getMode() {return mode;}
	public static void setMode(int modeNew) {setMode(modeNew, -1);}
	public static void setMode(int modeNew, boolean successfullPreviousAction) {setMode(modeNew, successfullPreviousAction?1:0);}
	public static void setMode(int modeNew, int previousActionState) {
		if(previousActionState!=-1) {
			boolean successfullPreviousAction=previousActionState==1;
			if(mode==MODE_PUT_PHANTOM_MODE) {
				if(successfullPreviousAction)
					CommonData.genericPhantom.performAction(xPop, yPop);
				else
					CommonData.genericPhantom.exit();
			}
		}
		if(modeNew==MODE_RESERVE)modeNew=modeReserve;
		switch(modeNew) {
		case MODE_PUT_PHANTOM_MODE:
			modeReserve=mode;
			GUISchematicEditor_Toolbar.selectButton(null);
			break;
		case MODE_SELECT_ZONE:
			modeReserve=mode;
			GUISchematicEditor_Toolbar.selectButton(null);
			break;
		case MODE_OBSERVE:
			GUISchematicEditor_Toolbar.selectButton(GUISchematicEditor_Toolbar.arrowbutton);
			break;
		case MODE_SELECT:
			GUISchematicEditor_Toolbar.selectButton(GUISchematicEditor_Toolbar.selectbutton);
			break;
		case MODE_SETUP_NODE:
			GUISchematicEditor_Toolbar.selectButton(GUISchematicEditor_Toolbar.buttonBrushAddNode);
			break;
		case MODE_SETUP_PART_STAGE1_selectNodeFrom:
			GUISchematicEditor_Toolbar.selectButton(GUISchematicEditor_Toolbar.buttonBrushAddPart);
			break;
		case MODE_MOVE:
			GUISchematicEditor_Toolbar.selectButton(GUISchematicEditor_Toolbar.buttonMovePart);
			break;
		}
		mode=modeNew;
	}
	
	public static boolean isLShiftPressed=false;
	public static boolean isLControlPressed=false;

	private static int xPush=0;
	private static int yPush=0;
	private static int xPop=0;
	private static int yPop=0;
	private static int xDelta=0;
	private static int yDelta=0;
	
	private static void moveAndScaleCamera(double value, boolean isScale, boolean isXAxis) {
		if(isScale) {
			EntityRenderer.scalex*=1-value*0.1;
			EntityRenderer.scaley*=1-value*0.1;
		}else {
			value=value*8.;
			if(isXAxis) {
				EntityRenderer.camposx+=value/EntityRenderer.scalex;
			}else
				EntityRenderer.camposy+=value/EntityRenderer.scaley;
		}
	}
	JFrame frame;//EntityEditorGlassPane glassPane;
	Container contentPane;
    JMenuBar menuBar;
    JToolBar toolbar;
	public EntityEditorMouseListener(
			JToolBar toolbar,
			JMenuBar menuBar,
			JFrame frame,//EntityEditorGlassPane glassPane,
			Container contentPane){
		this.toolbar=toolbar;
		this.menuBar=menuBar;
		this.frame=frame;
		this.contentPane=contentPane;
	}
	
    public void mouseMoved(MouseEvent e) {
		if(!redispatchMouseEvent(e))return;

		if(mode==MODE_PUT_PHANTOM_MODE){
			CommonData.genericPhantom.pos.x=e.getX();//
			CommonData.genericPhantom.pos.y=e.getY();//cur.y;
		}
    }
    
    public void mouseDragged(MouseEvent e) {
    	if(leftKeyPressed&&isLControlPressed) {
    		xDelta=e.getX()-xDelta;
    		yDelta=e.getY()-yDelta;
			double scale=-1.;
			EntityRenderer.camposx+=xDelta*scale/EntityRenderer.scalex;
			EntityRenderer.camposy+=yDelta*scale/EntityRenderer.scaley;
			if(CommonData.renderer!=null)CommonData.renderer.updateWindow(false);
    		xDelta=e.getX();
    		yDelta=e.getY();
    		return;
    	}
		if(mode==MODE_PUT_PHANTOM_MODE){
			CommonData.genericPhantom.pos.x=e.getX();//
			CommonData.genericPhantom.pos.y=e.getY();//cur.y;
			return;
		}
		if(mode==MODE_SELECT_ZONE) {
			GenericPhantomRectangle.x0=xPush;
			GenericPhantomRectangle.y0=yPush;
			GenericPhantomRectangle.x1=e.getX();
			GenericPhantomRectangle.y1=e.getY();
			return;
		}
		setMode(MODE_SELECT_ZONE, true);
		if(5<7)return;
		if((mode==MODE_SELECT&&leftKeyPressed==true)||mode==MODE_SELECT_ZONE||(mode==MODE_MOVE&&leftKeyPressed==true)) {
			if(leftKeyPressed&&mode!=MODE_SELECT_ZONE)
				modeReserve = mode;
			mode=MODE_SELECT_ZONE;
			GenericPhantomRectangle.x0=xPush;
			GenericPhantomRectangle.y0=yPush;
			GenericPhantomRectangle.x1=e.getX();
			GenericPhantomRectangle.y1=e.getY();
			
		}
		if (leftKeyPressed==true) {
			switch (mode) {
			
			case MODE_SELECT:
				break;
				
			}
		}
	}
    /*
	public void mouseClicked(MouseEvent e) {
	}
	*/
 	/*
    public void mouseEntered(MouseEvent e) {
		if(!redispatchMouseEvent(e))return;
    }
 
    public void mouseExited(MouseEvent e) {
		if(!redispatchMouseEvent(e))return;
    }*/
    public void mousePressed(MouseEvent e) {
    	
		xPush=e.getX();
		yPush=e.getY();
		
		xDelta=xPush;
		yDelta=yPush;
		
		if(e.getButton()==2){
			middleKeyPressed=true;
			return;
		}
		if(e.getButton()==1){
			leftKeyPressed=true;
			return;
		}
    }
    /**if RMB released*/
	private void openContextMenu(MouseEvent e) {
		//GUISchematicEditorContextMenu contextMenu = CommonData.frame.contextMenu;
		int xCanvas=e.getX();//cur.x;
		int yCanvas=e.getY();//cur.y;
		int xScreen=CommonData.frame.getBounds().x+CommonData.frame.getMousePosition().x;//CommonData.frame.getMousePosition().x;//cur.x;
		int yScreen=CommonData.frame.getBounds().y+CommonData.frame.getMousePosition().y;//CommonData.frame.getMousePosition().y;//cur.y;
		CommonData.frame.popupMenu.compileMenu(xScreen, yScreen);//contextMenu.compileMenu(xScreen, yScreen);
		CommonData.frame.popupMenu.show(e.getComponent(), e.getX(), e.getY());//contextMenu.setVisible(true);
	}
    private void setTextEditCaptions() {
		if(CommonData.partsSelected.size()>1) {
			CommonData.nameedit.setText("there are "+CommonData.partsSelected.size()+" elements");
			CommonData.captionedit.setText(common.Lang.InnerTable.Item.itemCaptionEditFantomMultipleCaptionName);
		}else {
			if(CommonData.partsSelected.size()==1) {
				CommonData.nameedit.setText(CommonData.partsSelected.get(0).getName());
				CommonData.captionedit.setText(CommonData.partsSelected.get(0).getProperties());
			}else {
				CommonData.nameedit.setText(common.Lang.InnerTable.Item.itemNameEditFantomCaptionName);
				CommonData.captionedit.setText(common.Lang.InnerTable.Item.itemCaptionEditFantomCaptionName);
			}
		}
    }
    private void changeElementInSelected(PartBasic element, boolean add) {
    	if(add) {
	    	CommonData.partsSelected.add(element);
	    	element.selected=1;
    	}else {
	    	CommonData.partsSelected.remove(element);
	    	element.selected=0;
    	}
    }
    private void clearPartsSelected() {
    	for(int i=0;i<CommonData.partsSelected.size();++i)
    		CommonData.partsSelected.get(i).selected=0;
    	CommonData.partsSelected.clear();
    }
    public void mouseReleased(MouseEvent e) {
		int xCanvas=e.getX();//cur.x;
		int yCanvas=e.getY();//cur.y;
		xPop=xCanvas;
		yPop=yCanvas;
		if(e.getButton()==3) {
			if(mode==MODE_PUT_PHANTOM_MODE) {
				setMode(MODE_OBSERVE, false);
				return;
			}
			openContextMenu(e);return;
		}
		if(e.getButton()==2){
			middleKeyPressed=false;
			return;
		}
		Point  MousePosOnRealMap= EntityRenderer.getRealDotFromImageCoords(xCanvas, yCanvas);
		int xMath=MousePosOnRealMap.x;//cur.x;
		int yMath=MousePosOnRealMap.y;//cur.y;
		if((Math.abs(xPush-xPop)<4)&&(Math.abs(yPush-yPop)<4)) {
			if(mode==MODE_SELECT_ZONE)mode=MODE_SELECT;
		}
		//Dot MousePosOnRealMap=EntityRenderer.getRealDotFromImageCoords(xScr, yScr);
		//common computations for oval and rectangle
		switch(mode) {
		case MODE_SELECT_ZONE:
			setMode(modeReserve, false);
			{
				if(!isLShiftPressed) {
					clearPartsSelected();
				}
				Point  p0 = EntityRenderer.getRealDotFromImageCoords(xPush, yPush);
				Point  p1 = EntityRenderer.getRealDotFromImageCoords(xPop, yPop);
				Rectangle zoneReal = EntityRenderer.normalizeZone(p0.x,p0.y,p1.x,p1.y);
				//CommonData.scheme.addNode(zoneReal.x, zoneReal.y);
				//CommonData.scheme.addNode(zoneReal.x+zoneReal.width, zoneReal.y+zoneReal.height);
				ArrayList<PartBasic> partsInZone = new ArrayList<PartBasic>();//ArrayList<PartBasic> partsInZone=CommonData.scheme.whatElementsInZone(xPush,yPush,xPop,yPop);
				PartBasic part=null;
				for(int i=0;i<selectedContainer().size();++i){
					part = selectedContainer().get(i);
					if(zoneReal.contains(part.pos))
						partsInZone.add(part);
				}
				
				//removing which already added
				{
					int i=0;
					while(i<partsInZone.size()) {
						if(partsInZone.get(i).selected!=1)
							i++;
						else
							partsInZone.remove(i);
					}
				}
				for(int i=0;i<partsInZone.size();++i)partsInZone.get(i).selected=1;
				CommonData.partsSelected.addAll(partsInZone);
				partsInZone.clear();
				partsInZone=null;
				setTextEditCaptions();
			}
			break;
		case MODE_PUT_PHANTOM_MODE:
			if(CommonData.genericPhantom.prepared)
				setMode(MODE_OBSERVE, true);
			break;
		// line
		case MODE_DRAW_LINE:
			System.out.println("Attempting to make line with EntityEditor_Helper");
			//g.drawLine(xf, yf, e.getX(), e.getY());
			break;
		case MODE_MOVE:
			tempelement=CommonData.scheme.whatElementInZone(xCanvas, yCanvas, 10);
			if(tempelement!=null) {
				if(isLShiftPressed) {
					this.changeElementInSelected(tempelement, tempelement.selected==0);
				}else {
					if(CommonData.partsSelected.contains(tempelement))
						//unselect
						changeElementInSelected(tempelement, false);
					else {
						//reselect
						clearPartsSelected();
						changeElementInSelected(tempelement, true);
					}
				}
				setTextEditCaptions();
				break;
			}
			if(CommonData.partsSelected.size()!=1) {
				if(CommonData.partsSelected.size()==0)break;
				double centerX=0,centerY=0;
				PartBasic part=null;
				for(int i=0;i<CommonData.partsSelected.size();++i) {
					part = CommonData.partsSelected.get(i);
					centerX+=part.pos.x;
					centerY+=part.pos.y;
				}
				centerX/=CommonData.partsSelected.size();
				centerY/=CommonData.partsSelected.size();
				int deltaX=(int) (MousePosOnRealMap.x-centerX);
				int deltaY=(int) (MousePosOnRealMap.y-centerY);
				int newX=0,newY=0;
				for(int i=0;i<CommonData.partsSelected.size();++i) {
					part = CommonData.partsSelected.get(i);
					newX=part.pos.x+deltaX;
					newY=part.pos.y+deltaY;
					part.setPos(newX, newY);
				}
				break;
			}
			if(CommonData.scheme.whatElementIndexInZone(xCanvas,yCanvas,PartBinaryBasic.SIZE_WIDTH_STANDART)!=-1)break;
			CommonData.partsSelected.get(0).setPos(MousePosOnRealMap.x,MousePosOnRealMap.y);
			break;
		case MODE_SELECT:
			if(!isLShiftPressed)this.clearPartsSelected();
			tempelement=CommonData.scheme.whatElementInZone(xCanvas, yCanvas, 10);
			if(tempelement!=null)
				this.changeElementInSelected(tempelement, tempelement.selected!=1);
			setTextEditCaptions();
			break;
			
		case MODE_SETUP_NODE:
			tempelement=CommonData.scheme.whatElementInZone(xCanvas, yCanvas, 10);
			if(tempelement!=null)break;
			CommonData.scheme.addNode(xMath, yMath);
			break;
		case MODE_SELECT_WITH_CONTROL:
			System.out.println("Attempting to select with VKControl key pressed.");
			//((PartBinaryControlledBasic)CommonData.partSelected).getNextElement(tempelement);
			break;
			
			
			
			
		case MODE_SETUP_PART_STAGE1_selectNodeFrom:
			/**setting up double port
			 * part 1 - basic node selection*/
			tempelement=CommonData.scheme.whatElementInZone(xCanvas, yCanvas, 10);
			if(tempelement==null)break;
			if(tempelement.getClass().getSimpleName().compareTo(PartNode.class.getSimpleName())!=0)break;
			nodeFrom=(PartNode) tempelement;
			nodeFrom.selected=2;
			mode=MODE_SETUP_PART_STAGE2_selectNodeTo;
			break;
		case MODE_SETUP_PART_STAGE2_selectNodeTo:
			/**setting up double port
			 * part 2 - second node selection*/
			tempelement=CommonData.scheme.whatElementInZone(xCanvas, yCanvas, 10);
			if(tempelement==null)break;
			if(tempelement.getClass().getSimpleName().compareTo(PartNode.class.getSimpleName())!=0)break;
			nodeTo=(PartNode) tempelement;
			if(nodeTo==nodeFrom)break;
			nodeTo.selected=3;
			mode=MODE_SETUP_PART_STAGE3_selectPosition;
			break;
		case MODE_SETUP_PART_STAGE3_selectPosition:
			/**setting up double port
			 * part 3 - positioning and setting*/
			CommonData.scheme.addPart(xMath, yMath, nodeFrom, nodeTo, partsList.getSelectedIndex());
			nodeFrom.selected=0;
			nodeTo.selected=0;
			nodeFrom=null;
			nodeTo=null;
			
			mode=MODE_SETUP_PART_STAGE1_selectNodeFrom;
			break;
			
			
			
			
		}
		leftKeyPressed=false;
    }

    //A basic implementation of redispatching events.
    /**marked for garbage. in this feature we doesn't need because only Canvas component has this listener*/
    private boolean redispatchMouseEvent(MouseEvent e) {
    	boolean result =true;
    	if(result==true)return true;
        Point glassPanePoint = e.getPoint();
        Container container = contentPane;
        Point canvasPoint = SwingUtilities.convertPoint(
                                        frame,
                                        glassPanePoint,
                                        CommonData.canvas);
        //cur=canvasPoint;
        if (canvasPoint.y < 0) { //we're not in the content pane
            if (canvasPoint.y + menuBar.getHeight() >= 0) { 
                //The mouse event is over the menu bar.
                //Could handle specially.
            } else { 
                //The mouse event is over non-system window 
                //decorations, such as the ones provided by
                //the Java look and feel.
                //Could handle specially.
            }
            result=false;
        } else {
            //The mouse event is probably over the content pane.
            //Find out exactly which component it's over.  
            Component component = 
                SwingUtilities.getDeepestComponentAt(
                                        container,
                                        canvasPoint.x,
                                        canvasPoint.y);
                             
            if ((component != null) 
                && (component.equals(toolbar))) {
                //Forward events over the check box.
                Point componentPoint = SwingUtilities.convertPoint(
                                            frame,
                                            glassPanePoint,
                                            component);
                component.dispatchEvent(new MouseEvent(component,
                                                     e.getID(),
                                                     e.getWhen(),
                                                     e.getModifiers(),
                                                     componentPoint.x,
                                                     componentPoint.y,
                                                     e.getClickCount(),
                                                     e.isPopupTrigger()));
                result=false;
            }
        }
        return result;
    }
    public static KeyAdapter keyAdapter = (new  KeyAdapter() {
		public void keyReleased(KeyEvent e) {
			int keyCode=e.getExtendedKeyCode();
			switch(keyCode) {
			case KeyEvent.VK_CONTROL:
				isLControlPressed=false;
				if(mode==MODE_SELECT_WITH_CONTROL)
					mode=MODE_SELECT;
				break;
			case KeyEvent.VK_SHIFT:
				isLShiftPressed=false;
				break;
			case 37:case 39://left-right
			case 38:case 40://up-down
				if(CommonData.renderer!=null)
					CommonData.renderer.updateWindow(false);
				break;
			default:
				System.out.println(keyCode);
				break;
			}
			//System.out.println(keyCode);
			// setting focus on panel,
			// for writing text on it
			//panel.requestFocus();
		}
		public void keyPressed(KeyEvent e) {
			double value=0;
			int keyCode=e.getExtendedKeyCode();
			switch(keyCode) {
			case KeyEvent.VK_ESCAPE:
				if(mode==MODE_PUT_PHANTOM_MODE){
					setMode(MODE_OBSERVE, false);
				}
				//if(CommonData.frame.contextMenu.isVisible())CommonData.frame.contextMenu.setVisible(false);
				break;
			case KeyEvent.VK_CONTROL:
				isLControlPressed=true;
				if(mode==MODE_SELECT)
					mode=MODE_SELECT_WITH_CONTROL;
				break;
			case KeyEvent.VK_SHIFT:
				isLShiftPressed=true;
				break;
			case 45:case 61:
				value=1;
				if(keyCode==45)value=-1;
				//if(mode==MODE_PUT_PHANTOM_MODE) {GenericPhantom.changeElementIndex((int)value);} - deprecated
				if(!isLControlPressed)break;
				moveAndScaleCamera(value, true, false);
				if(CommonData.renderer!=null)
					CommonData.renderer.updateWindow(false);
				break;
			case 37:case 39://left-right
				value=1;
				if(keyCode==37)value=-1;
				moveAndScaleCamera(value, false, true);
				break;
			case 38:case 40://up-down
				value=1;
				if(keyCode==38)value=-1;
				moveAndScaleCamera(value, false, false);
				break;
			default:
				System.out.println(keyCode);
				break;	
			}
			//panel.requestFocus();
		}
		public void keyTyped(KeyEvent e) {
			int keyCode=e.getExtendedKeyCode();
			double value=1;
			//System.out.println(keyCode);
			switch(keyCode){

			case 37:case 39://left-right
				value=1;
				if(keyCode==37)value=-1;
				moveAndScaleCamera(value,false,true);
				break;
			case 38:case 40://up-down
				value=1;
				if(keyCode==38)value=-1;
				moveAndScaleCamera(value,false,false);
				break;
			}
			/* useless, marked as junk
			if(mode==MODE_SELECT && CommonData.partSelected!=null){
				if(CommonData.partSelected.getClass().getSimpleName().compareTo(PartNode.class.getSimpleName())!=0) {
					if(e.getKeyChar()=='=') {
						//keyCharAction
					}						
				} 
			}
			*/
			//panel.requestFocus();
		}
	});
}
