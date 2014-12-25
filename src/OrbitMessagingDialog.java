import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;


public class OrbitMessagingDialog
    extends Dialog
    implements ActionListener, ItemListener, KeyListener, WindowListener
{
    protected OrbitWindow parentWindow;

    protected Button readMessages;
    protected Label instantForLabel;
    protected java.awt.List allUsersList;
    protected Label saveForLabel;
    protected TextField saveFor;
    protected Label messageTextLabel;
    protected TextArea messageText;
    protected Button ok;
    protected Button cancel;
    protected Panel p1;
    protected Panel p2;
    protected GridBagLayout myLayout;
    protected GridBagConstraints myConstraints;


    public OrbitMessagingDialog(OrbitWindow parent)
    {
	super(parent, "Messaging", false);

	parentWindow = parent;
	myLayout = new GridBagLayout();
	myConstraints = new GridBagConstraints();

	setLayout(myLayout);

	myConstraints.insets = new Insets(0, 5, 0, 5);

	p1 = new Panel();
	p1.setLayout(myLayout);

	readMessages = new Button("Read saved messages");
	readMessages.addActionListener(this);
	readMessages.addKeyListener(this);
	myConstraints.gridx = 0; myConstraints.gridy = 0;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.insets.top = 5; myConstraints.insets.bottom = 0; 
	myLayout.setConstraints(readMessages, myConstraints);
	p1.add(readMessages);

	instantForLabel = new Label("Send instant message to:");
	myConstraints.gridx = 0; myConstraints.gridy = 1;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.fill = myConstraints.NONE;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 0; 
	myLayout.setConstraints(instantForLabel, myConstraints);
	p1.add(instantForLabel);

	allUsersList = new java.awt.List(5);
	allUsersList.setFont(parentWindow.smallFont);
	allUsersList.addItemListener(this);
	allUsersList.addKeyListener(this);
	allUsersList.setMultipleMode(false);
	myConstraints.gridx = 0; myConstraints.gridy = 2;
	myConstraints.weightx = 1.0; myConstraints.weighty = 1.0;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.anchor = myConstraints.WEST;
	myLayout.setConstraints(allUsersList, myConstraints);
	p1.add(allUsersList);

	saveForLabel = new Label("- OR - Save message for user name:");
	myConstraints.gridx = 0; myConstraints.gridy = 3;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.fill = myConstraints.NONE;
	myConstraints.anchor = myConstraints.WEST;
	myLayout.setConstraints(saveForLabel, myConstraints);
	p1.add(saveForLabel);

	saveFor = new TextField(20);
	saveFor.addKeyListener(this);
	myConstraints.gridx = 0; myConstraints.gridy = 4;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.anchor = myConstraints.WEST;
	myLayout.setConstraints(saveFor, myConstraints);
	p1.add(saveFor);

	messageTextLabel = new Label("Message to send:");
	myConstraints.gridx = 0; myConstraints.gridy = 5;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.fill = myConstraints.NONE;
	myConstraints.anchor = myConstraints.WEST;
	myLayout.setConstraints(messageTextLabel, myConstraints);
	p1.add(messageTextLabel);

	messageText =
	    new TextArea("", 2, 20, TextArea.SCROLLBARS_VERTICAL_ONLY);
	messageText.addKeyListener(this);
	myConstraints.gridx = 0; myConstraints.gridy = 6;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.anchor = myConstraints.WEST;
	myLayout.setConstraints(messageText, myConstraints);
	p1.add(messageText);

	myConstraints.gridx = 0; myConstraints.gridy = 0;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.anchor = myConstraints.CENTER;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 0; 
	myConstraints.insets.left = 0; myConstraints.insets.right = 0; 
	myLayout.setConstraints(p1, myConstraints);
	add(p1);

	p2 = new Panel();
	p2.setLayout(myLayout);

	ok = new Button("Ok");
	ok.addActionListener(this);
	ok.addKeyListener(this);
	myConstraints.gridx = 0; myConstraints.gridy = 0;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.fill = myConstraints.NONE;
	myConstraints.anchor = myConstraints.EAST;
	myConstraints.insets.top = 5; myConstraints.insets.bottom = 5; 
	myConstraints.insets.left = 5; myConstraints.insets.right = 0; 
	myLayout.setConstraints(ok, myConstraints);
	p2.add(ok);

	cancel = new Button("Cancel");
	cancel.addActionListener(this);
	cancel.addKeyListener(this);
	myConstraints.gridx = 1; myConstraints.gridy = 0;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.fill = myConstraints.NONE;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.insets.top = 5; myConstraints.insets.bottom = 5; 
	myConstraints.insets.left = 0; myConstraints.insets.right = 5; 
	myLayout.setConstraints(cancel, myConstraints);
	p2.add(cancel);

	myConstraints.gridx = 0; myConstraints.gridy = 1;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.anchor = myConstraints.CENTER;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 0; 
	myConstraints.insets.left = 0; myConstraints.insets.right = 0; 
	myLayout.setConstraints(p2, myConstraints);
	add(p2);

	setSize(600,400);
	pack();
	setResizable(false);
	setLocation((((((parentWindow.getBounds()).width) - 
		       ((getSize()).width)) / 2)
		     + ((parentWindow.getLocation()).x)),
		    (((((parentWindow.getBounds()).height) - 
		       ((getSize()).height)) / 2)
		     + ((parentWindow.getLocation()).y)));

	addKeyListener(this);
	addWindowListener(this);
	setVisible(true);
	updateLists();
	allUsersList.requestFocus();
    }

    public void updateLists()
    {
	// Update our lists.  This is called when something changes, such
	// as a user logs on/off
	
	if (allUsersList.getItemCount() > 0)
	    allUsersList.removeAll();

	Vector usersVector = parentWindow.theClient.userList;

	// Add all of the connected users to the 'all users' list
	for (int count = 0; count < usersVector.size(); count ++)
	    {
		OrbitUser user = (OrbitUser) usersVector.elementAt(count);
		allUsersList.add(user.name);
	    }

	return;
    }

    private void sendMessage()
    {
	String instantUser = allUsersList.getSelectedItem();
	String saveUser = saveFor.getText();

	if ((instantUser == null) && saveUser.equals(""))
	    {
		new OrbitInfoDialog(parentWindow, "Need recipient", true,
				      "You must specify a recipient for "
				      + "the message!");
		return;
	    }

	if (parentWindow.connected != true)
	    {
		new OrbitInfoDialog(parentWindow, "Not connected", true,
				      "Must be connected first!");
		return;
	    }

	// If the user has typed a name, leave the message on the server
	// for that user
	if (!saveUser.equals(""))
	    try {
		// Send the message to the server
		parentWindow.theClient.sendLeaveMess(saveUser,
						     messageText.getText());
	    }
	    catch (IOException e) {
		parentWindow.theClient.lostConnection();
		return;
	    }
	else
	    {
		// Send an instant message.  First we need to find the user
		// id that matches the name that's selected

		Vector usersVector = parentWindow.theClient.userList;

		for (int count = 0; count < usersVector.size(); count ++)
		    {
			OrbitUser user =
			    (OrbitUser) usersVector.elementAt(count);

			if (user.name.equals(instantUser))
			    {
				try {
				    parentWindow.theClient
					.sendInstantMess(user.id,
						 messageText.getText());
				}
				catch (IOException e) {
				    parentWindow.theClient.lostConnection();
				    return;
				}
				break;
			    }
		    }
	    }

	return;
    }


    public void actionPerformed(ActionEvent E)
    {
	if (E.getSource() == readMessages)
	    {
		try {
		    parentWindow.theClient.sendReadMess();
		}
		catch (IOException e) {
		    parentWindow.theClient.lostConnection();
		}
		dispose();
		return;
	    }

	else if (E.getSource() == ok)
	    {
		sendMessage();
		dispose();
		return;
	    }
	
	else if (E.getSource() == cancel)
	    {
		dispose();
		return;
	    }
    }

    public void itemStateChanged(ItemEvent E)
    {
	if (E.getSource() == allUsersList)
	    {
		// If a user name has been selected, empty out the saveFor
		// field
		if (allUsersList.getSelectedItem() != null)
		    saveFor.setText("");
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
		if (E.getSource() == readMessages)
		    {
			try {
			    parentWindow.theClient.sendReadMess();
			}
			catch (IOException e) {
			    parentWindow.theClient.lostConnection();
			}
			dispose();
			return;
		    }

		else if ((E.getSource() == ok) ||
		    (E.getSource() == saveFor) ||
		    (E.getSource() == messageText))
		    {
			sendMessage();
			dispose();
			return;
		    }
		
		else if (E.getSource() == cancel)
		    {
			dispose();
			return;
		    }
	    }

	else if (E.getSource() == saveFor)
	    {
		// The user is typing a name, so make sure no names are
		// selected in the allUsersList
		int items = allUsersList.getRows();
		for (int count = 0; count < items; count ++)
		    allUsersList.deselect(count);
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
