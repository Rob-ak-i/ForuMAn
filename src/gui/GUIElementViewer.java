package gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import common.CommonData;
import common.EntityManager;
import parts.PartBasic;
import unit.ManagedObject;
import unit.Sequence;
import util.Lists;
import util.Parameters;
import util.lang.Token;

/**language:
 * */
public class GUIElementViewer extends JFrame implements ManagedObject{
	/**was set by random seed*/
	private static final long serialVersionUID = -3713450632567079274L;
	public GUIElementViewer(int x, int y) {
		super(common.Lang.InnerTable.GUI.GUIElementViewerName);
		this.setSize(300, 500);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setResizable(true);
		this.setVisible(false);
		this.setFocusable(true);
		this.setBounds(x,y,800,600);
	}
	private void identify(String command) {
		ArrayList<Token> tokens = CommonData.tokenizer.tokenize(command);//Lists.tokenizeString(command, " \n",".,;:\'\"\\/!@#$%^&*(){}[]-=_+|№?`~");
		//command = fromSchematic
		
		//command = fromManager \'manager_name\'
	}
	public void prepare(String identifier, Parameters parameters) {
		PartBasic part = null;
		String unitClass = null;
		if(unitClass.equals(Parameters.parameter_UnitClass_sequence)) {
			Sequence sequence = (Sequence) part.boundedUnit;
			;;;
			return;
		}
		if(unitClass.equals(Parameters.parameter_UnitClass_user)){
			int userID = (int) part.getProperty(Parameters.parameter_ID);
			;;;
			return;
		}
		
	}
	public void setVisible (boolean arg) {
		if(!arg)clear();
	}
	@Override
	public void clear() {
		this.dispose();
	}
	
	@Override
	public int getMeasurableParameter() {
		return 0;
	}
	@Override
	public void saveToFile(String fileName) {}
	public ManagedObject produce(Class<?> resultObjectClass, String parameters) {return null;}
	public void append(ManagedObject object) {}
	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public EntityManager<?> getManager() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void loadFromFile(String fileName, String loadCommand) {
		// TODO Auto-generated method stub
		
	}
	
	
}
