package gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

@SuppressWarnings("serial")
public class ConcreteAction extends AbstractAction {
	ConcreteActionOwner owner;
	public ConcreteAction (ConcreteActionOwner owner) {
		this.owner=owner;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		owner.performAction();
	}

}
