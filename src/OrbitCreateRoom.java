// This file contains the code for the dialog that pops up when a user
// presses the "create room" button on the chat rooms dialog console.

import java.awt.*;
import java.awt.event.*;
import java.io.*;


public class OrbitCreateRoom
    extends Dialog
    implements ActionListener, ItemListener, KeyListener, WindowListener
{
    protected OrbitWindow parentWindow;
    protected Label roomNameLabel;
    protected TextField roomName;
    protected Checkbox priv;
    protected Label passwordLabel;
    protected TextField password;
    protected Label passwordWarningLabel1;
    protected Label passwordWarningLabel2;
    protected Button ok;
    protected Button cancel;

    protected GridBagLayout myLayout;
    protected GridBagConstraints myConstraints;


    OrbitCreateRoom(Frame parent)
    {
	super(parent, "Create a chat room", true);

	parentWindow = (OrbitWindow) parent;

	// Make all of the widgets

	myLayout = new GridBagLayout();
	myConstraints = new GridBagConstraints();
	setLayout(myLayout);
	
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 0;
	myConstraints.insets.right = 5; myConstraints.insets.left = 5;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.weightx = 1.0; myConstraints.weighty = 1.0;

	roomNameLabel = new Label("Room name:");
	roomNameLabel.setFont(parentWindow.smallFont);
	myConstraints.gridx = 0; myConstraints.gridy = 0;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.fill = myConstraints.NONE;
	myLayout.setConstraints(roomNameLabel, myConstraints);
	add(roomNameLabel);

	roomName = new TextField(30);
	roomName.setFont(parentWindow.smallFont);
	roomName.addKeyListener(this);
	roomName.setEditable(true);
	roomName.setEnabled(true);
	myConstraints.gridx = 0; myConstraints.gridy = 1;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.fill = myConstraints.BOTH;
	myLayout.setConstraints(roomName, myConstraints);
	add(roomName);

	priv = new Checkbox("room is private", false);
	priv.setFont(parentWindow.smallFont);
	priv.setEnabled(true);
	priv.addItemListener(this);
	myConstraints.gridx = 0; myConstraints.gridy = 2;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.fill = myConstraints.NONE;
	myConstraints.insets.top = 5; myConstraints.insets.bottom = 0;
	myLayout.setConstraints(priv, myConstraints);
	add(priv);

	passwordLabel = new Label("Password:");
	passwordLabel.setFont(parentWindow.smallFont);
	passwordLabel.setEnabled(priv.getState());
	myConstraints.gridx = 0; myConstraints.gridy = 3;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.fill = myConstraints.NONE;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 0;
	myLayout.setConstraints(passwordLabel, myConstraints);
	add(passwordLabel);

	password = new TextField();
	password.setFont(parentWindow.smallFont);
	password.addKeyListener(this);
	password.setEditable(true);
	password.setEnabled(priv.getState());
	password.setEchoChar(new String("*").charAt(0));
	myConstraints.gridx = 0; myConstraints.gridy = 4;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.fill = myConstraints.BOTH;
	myLayout.setConstraints(password, myConstraints);
	add(password);

	passwordWarningLabel1 =
	    new Label("Warning: Your Java client cannot encrypt your");
	passwordWarningLabel1
	    .setVisible(!parentWindow.passwordEncryptor.canEncrypt);
	passwordWarningLabel1.setFont(OrbitWindow.XsmallFont);
	passwordWarningLabel1.setEnabled(priv.getState());
	myConstraints.gridx = 0; myConstraints.gridy = 5;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.fill = myConstraints.NONE;
	myLayout.setConstraints(passwordWarningLabel1, myConstraints);
	add(passwordWarningLabel1);

	passwordWarningLabel2 =
	    new Label("passwords.  They will be sent as plain text.");
	passwordWarningLabel2
	    .setVisible(!parentWindow.passwordEncryptor.canEncrypt);
	passwordWarningLabel2.setFont(OrbitWindow.XsmallFont);
	passwordWarningLabel2.setEnabled(priv.getState());
	myConstraints.gridx = 0; myConstraints.gridy = 6;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myLayout.setConstraints(passwordWarningLabel2, myConstraints);
	add(passwordWarningLabel2);

	myConstraints.insets.top = 5; myConstraints.insets.bottom = 5;

	ok = new Button("Ok");
	ok.setFont(parentWindow.smallFont);
	ok.addActionListener(this);
	ok.addKeyListener(this);
	ok.setEnabled(false);
	myConstraints.gridx = 0; myConstraints.gridy = 7;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.fill = myConstraints.NONE;
	myConstraints.anchor = myConstraints.EAST;
	myConstraints.insets.right = 0; myConstraints.insets.left = 5;
	myLayout.setConstraints(ok, myConstraints);
	add(ok);

	cancel = new Button("Cancel");
	cancel.setFont(parentWindow.smallFont);
	cancel.addActionListener(this);
	cancel.addKeyListener(this);
	cancel.setEnabled(true);
	myConstraints.gridx = 1; myConstraints.gridy = 7;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.fill = myConstraints.NONE;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.insets.right = 5; myConstraints.insets.left = 0;
	myLayout.setConstraints(cancel, myConstraints);
	add(cancel);

	// register to receive the various events
	addKeyListener(this);
	addWindowListener(this);

	// show the window and get going
	setSize(200, 200);
	pack();
	setLocation((((((parentWindow.getBounds()).width)
	 	       - ((getSize()).width)) / 2)
	 	     + ((parentWindow.getLocation()).x)),
	 	    (((((parentWindow.getBounds()).height)
	 	       - ((getSize()).height)) / 2)
	 	     + ((parentWindow.getLocation()).y)));

	setVisible(true);
	roomName.requestFocus();
    }

    protected void goCreate()
    {
	if (!roomName.getText().equals(""))
	    {
		String pword = "";
		if (priv.getState())
		    pword = parentWindow.passwordEncryptor
			.encryptPassword(password.getText());

		parentWindow.canvas.clear();

		try {
		    parentWindow.theClient
			.sendEnterRoom(roomName.getText(), priv.getState(),
				       pword);
		}
		catch (IOException e) {
		    parentWindow.theClient.lostConnection();
		    return;
		}

		parentWindow.currentRoom.name = roomName.getText();
		parentWindow.roomOwner(true);
		return;
	    }
    }

    public void actionPerformed(ActionEvent E)
    {
	if (E.getSource() == ok)
	    {
		goCreate();
		dispose();
		return;
	    }

	if (E.getSource() == cancel)
	    {
		dispose();
		return;
	    }
    }

    public void itemStateChanged(ItemEvent E)
    {
	if (E.getSource() == priv)
	    {
		boolean state = priv.getState();
		passwordLabel.setEnabled(state);
		password.setEnabled(state);
		passwordWarningLabel1.setEnabled(state);
		passwordWarningLabel2.setEnabled(state);
		return;
	    }
    }

    public void keyPressed(KeyEvent E)
    {
    }

    public void keyReleased(KeyEvent E)
    {
	if (E.getKeyCode() == E.VK_ENTER)
	    {
		if (E.getSource() == cancel)
		    {
			dispose();
			return;
		    }

		else
		    {
			goCreate();
			dispose();
			return;
		    }
	    }

	else if (E.getSource() == roomName)
	    {
		if (roomName.getText().equals(""))
		    ok.setEnabled(false);
		else
		    ok.setEnabled(true);
		return;
	    }
    }

    public void keyTyped(KeyEvent E)
    {
    }   

    public void windowActivated(WindowEvent E)
    {
    }

    public void windowClosed(WindowEvent E)
    {
    }

    public void windowClosing(WindowEvent E)
    {
 	dispose();
	return;
    }

    public void windowDeactivated(WindowEvent E)
    {
    }

    public void windowDeiconified(WindowEvent E)
    {
    }

    public void windowIconified(WindowEvent E)
    {
    }

    public void windowOpened(WindowEvent E)
    {
    }
}
