package gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import common.CommonData;
import common.Reports;
import parts.PartBasic;
import unit.DataTable;
import unit.MessageBank;
import unit.Sequence;
import util.Parameters;

@SuppressWarnings("serial")
/**https://pro-java.ru/java-dlya-nachinayushhix/kak-sozdat-vsplyvayushhee-menyu-v-java/*/
public class GUISchematicEditorPopupMenu extends JPopupMenu {
	private Container contentPane;
	public int buttonWidth = 100;
	public int buttonHeight = 30;

	public GUISchematicEditorPopupMenu() {
		super("popupMenu");
		this.setBounds(600,300,400,400);
		//this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		//this.setUndecorated(true);
		//this.setShape(null);
		//contentPane = new JPanel();
		//this.setContentPane(contentPane);
		
		//JMenu menu;
		//JMenuBar menuBar = new JMenuBar();
		//menu = new JMenu();
		//menuBar.add(menu);
		//this.setJMenuBar(menuBar);
		
		contentPane=this;//contentPane=this.getJMenuBar();//contentPane=this.getContentPane();
		
		//listener = new InnerListener();
		//contentPane.addMouseMotionListener(listener);
		//contentPane.addMouseListener(listener);
		
		
		//contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		this.setVisible(false);
		
	}
	public void addMenuItem(Action action) {
		addMenuItem(action, (String) action.getValue(action.NAME));
	}
	public void addMenuItem(Action action, String preferredName) {
		//JToggleButton menuItem=new JToggleButton (action); 
		//JButton menuItem=new JButton(action);
		JMenuItem menuItem=new JMenuItem(action);
		//menuItem.setUI("javax.swing.plaf.metal.MetalLookAndFeel");
		menuItem.setName(preferredName);
		menuItem.setAlignmentX(LEFT_ALIGNMENT);
		menuItem.setSize(buttonWidth, buttonHeight);
		//menuItem.setBorder(javax.swing.border.LineBorder.createBlackLineBorder());
		menuItem.setVerifyInputWhenFocusTarget(true);
		menuItem.setBorderPainted(true);
		menuItem.setContentAreaFilled(true);
		menuItem.setFocusable(true);
		menuItem.setFocusPainted(true);
		menuItem.setForeground(new Color(0xa7a7ff));
		//menuItem.setIcon();
		//L&F UI info^
		// https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		// https://yandexwebcache.net/yandbtm?lang=ru&fmode=inject&tm=1619587805&tld=ru&la=1618143744&text=beautiful%20gui%20swing%20java&url=https%3A%2F%2Faskdev.ru%2Fq%2Fkak-uluchshit-vneshniy-vid-java-swing-gui-134876%2F&l10n=ru&mime=html&sign=105f6cc19ed6d133d9040b47c4a3d864&keyno=0&mode=text
		//menuItem.setUI();
		
		
		//components.add(menuItem);
		contentPane.add(menuItem);
	}

	public void compileMenu(int xScreen, int yScreen) {
		//if(components.size()>0)for(int i=0;i<components.size();++i)components.get(i).remove(getMenuBar());
		//components.clear();
		contentPane.removeAll();
		if(CommonData.partsSelected.size()==0) {
			addMenuItem(ContextMenuBasicActions.loadAction);
		}
		if(CommonData.partsSelected.size()>=1) {
			boolean latch1=false,latch2=false,latch3=false;
			Object boundedUnit=null;
			PartBasic part=null;
			for(int i=0;i<CommonData.partsSelected.size();++i) {
				part=CommonData.partsSelected.get(i);
				boundedUnit=part.boundedUnit;
				if(boundedUnit==null)continue;
				Class boundedUnitClass = boundedUnit.getClass();String boundedUnitClassName = boundedUnitClass.getSimpleName();
				if(boundedUnitClassName.equals(DataTable.class.getSimpleName())) {
					//TODO add colNames for forumContainedInfo check
					if(!latch1) {
						latch1=true;
						addMenuItem(ContextMenuScriptActions.makeMessagesBanksFromDataTablesAction);
					}
				}
				if(boundedUnitClassName.equals(MessageBank.class.getSimpleName())) {
					if(!latch2) {
						latch2=true;
						addMenuItem(ContextMenuScriptActions.makeSequencesFromMessageBanksAction);
					}
				}
				/*
				if(boundedUnitClassName.equals(Sequence.class.getSimpleName())) {
					if(!latch3) {
						latch3=true;
						addMenuItem(ContextMenuPrintActions.printSequenceAction);
					}
				}
				*/
			}
				
		}
		if(CommonData.partsSelected.size()==1) while(true) { 
			Object boundedUnit = CommonData.partsSelected.get(0).boundedUnit;
			if(boundedUnit==null) {
				//TODO обозреваем элементы по свойствам
				PartBasic part = CommonData.partsSelected.get(0);
				Parameters pars = part.properties;
				String unitClass = (String)pars.get(Parameters.parameter_UnitClass);
				if(unitClass==null)break;
				if(unitClass==Parameters.parameter_UnitClass_user) {
					int userID = (int)pars.get(Parameters.parameter_ID);
					String forumKey = (String)pars.get(Parameters.parameter_ForumKey);
					Reports.createUserReport(forumKey, userID);
				}
				break;
			}
			//if(boundedUnit.getClass()!=DataTable.class&&boundedUnit.getClass()!=MessageBank.class)break;
			addMenuItem(ContextMenuBasicActions.saveAction);
			
			Class boundedUnitClass = boundedUnit.getClass();String boundedUnitClassName = boundedUnitClass.getSimpleName();
			if(boundedUnitClassName.equals(DataTable.class.getSimpleName())) {
				//TODO add colNames for forumContainedInfo check
				addMenuItem(ContextMenuPrintActions.printForumSocialNetAction);
				addMenuItem(ContextMenuViewActions.printForumReport);
			}
			if(boundedUnitClassName.equals(MessageBank.class.getSimpleName())) {
				addMenuItem(ContextMenuPrintActions.printMessagesTestActionWIP);
			}
			if(boundedUnitClassName.equals(Sequence.class.getSimpleName())) {
				addMenuItem(ContextMenuPrintActions.printSequenceAction);
			}
			
			break;
		}
		if(CommonData.partsSelected.size()>1) while(true){
			Object boundedUnit = CommonData.partsSelected.get(0).boundedUnit;
			if(boundedUnit==null)break;
			addMenuItem(ContextMenuBasicActions.concatAction, "concat elements"+" ("+boundedUnit.getClass().getSimpleName()+")");
			break;
		}
		addMenuItem(basicAction);
		int w=buttonWidth;
		int h=buttonHeight*contentPane.getComponents().length;
		contentPane.setSize(w, h);
		this.setBounds(xScreen, yScreen, w, h);
	}
	Action basicAction = new  AbstractAction("null") {
		public void actionPerformed(ActionEvent event) {
			
		}
	};
}
