// This file contains the code for the dialog that pops up when a user
// "owns" a chat room, so that they can control the room and the users in
// it.

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;


public class OrbitRoomControl
    extends Dialog
    implements ActionListener, ItemListener, KeyListener, WindowListener
{
    protected OrbitWindow parentWindow;

    // Widgets
    protected Label roomNameLabel;
    protected Label roomUsersLabel;
    protected java.awt.List roomUsersList;
    protected Label allUsersLabel;
    protected java.awt.List allUsersList;
    protected Button inviteUsers;
    protected Button bootUsers;
    protected Button banUsers;
    protected Button allowUsers;
    protected Button dismiss;

    protected GridBagLayout myLayout;
    protected GridBagConstraints myConstraints;


    OrbitRoomControl(Frame parent)
    {
	super(parent, "Chat room control", false);

	parentWindow = (OrbitWindow) parent;

	if (parentWindow.roomControlDialog != null)
	    {
		parentWindow.roomControlDialog.dispose();
		parentWindow.roomControlDialog = null;
	    }

	// Make all of the widgets

	myLayout = new GridBagLayout();
	myConstraints = new GridBagConstraints();
	setLayout(myLayout);
	
	// Layout stuff that's the same for all the widgets
	myConstraints.gridwidth = 1; myConstraints.gridheight = 1;
	myConstraints.insets.right = 5; myConstraints.insets.left = 5;
	myConstraints.anchor = myConstraints.CENTER;
	myConstraints.fill = myConstraints.BOTH;

	roomNameLabel = new Label(parentWindow.currentRoom.name);
	roomNameLabel.setFont(parentWindow.XlargeFont);
	myConstraints.gridx = 0; myConstraints.gridy = 0;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.top = 5; myConstraints.insets.bottom = 5;
	myLayout.setConstraints(roomNameLabel, myConstraints);
	add(roomNameLabel);

	roomUsersLabel = new Label("Users in this chat room:");
	roomUsersLabel.setFont(parentWindow.smallFont);
	myConstraints.gridx = 0; myConstraints.gridy = 1;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 5;
	myLayout.setConstraints(roomUsersLabel, myConstraints);
	add(roomUsersLabel);

	roomUsersList = new java.awt.List(5);
	roomUsersList.setFont(parentWindow.smallFont);
	roomUsersList.addItemListener(this);
	roomUsersList.addKeyListener(this);
	roomUsersList.setMultipleMode(true);
	myConstraints.gridx = 0; myConstraints.gridy = 2;
	myConstraints.weightx = 1.0; myConstraints.weighty = 1.0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 5;
	myLayout.setConstraints(roomUsersList, myConstraints);
	add(roomUsersList);

	bootUsers = new Button("Boot user(s)");
	bootUsers.setFont(parentWindow.smallFont);
	bootUsers.addActionListener(this);
	bootUsers.addKeyListener(this);
	bootUsers.setEnabled(false);
	myConstraints.gridx = 0; myConstraints.gridy = 3;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 5;
	myLayout.setConstraints(bootUsers, myConstraints);
	add(bootUsers);

	allUsersLabel = new Label("All other users online:");
	allUsersLabel.setFont(parentWindow.smallFont);
	myConstraints.gridx = 0; myConstraints.gridy = 4;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 5;
	myLayout.setConstraints(allUsersLabel, myConstraints);
	add(allUsersLabel);

	allUsersList = new java.awt.List(10);
	allUsersList.setFont(parentWindow.smallFont);
	allUsersList.addItemListener(this);
	allUsersList.addKeyListener(this);
	allUsersList.setMultipleMode(true);
	myConstraints.gridx = 0; myConstraints.gridy = 5;
	myConstraints.weightx = 1.0; myConstraints.weighty = 1.0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 5;
	myLayout.setConstraints(allUsersList, myConstraints);
	add(allUsersList);

	inviteUsers = new Button("Invite user(s)");
	inviteUsers.setFont(parentWindow.smallFont);
	inviteUsers.addActionListener(this);
	inviteUsers.addKeyListener(this);
	inviteUsers.setEnabled(false);
	myConstraints.gridx = 0; myConstraints.gridy = 6;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 0;
	myLayout.setConstraints(inviteUsers, myConstraints);
	add(inviteUsers);

	banUsers = new Button("Ban user(s)");
	banUsers.setFont(parentWindow.smallFont);
	banUsers.addActionListener(this);
	banUsers.addKeyListener(this);
	banUsers.setEnabled(false);
	myConstraints.gridx = 0; myConstraints.gridy = 7;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 0;
	myLayout.setConstraints(banUsers, myConstraints);
	add(banUsers);

	allowUsers = new Button("Allow user(s)");
	allowUsers.setFont(parentWindow.smallFont);
	allowUsers.addActionListener(this);
	allowUsers.addKeyListener(this);
	allowUsers.setEnabled(false);
	myConstraints.gridx = 0; myConstraints.gridy = 8;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 0;
	myLayout.setConstraints(allowUsers, myConstraints);
	add(allowUsers);

	dismiss = new Button("Dismiss");
	dismiss.setFont(parentWindow.smallFont);
	dismiss.addActionListener(this);
	dismiss.addKeyListener(this);
	dismiss.setEnabled(true);
	myConstraints.gridx = 0; myConstraints.gridy = 9;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 5;
	myLayout.setConstraints(dismiss, myConstraints);
	add(dismiss);

	// register to receive the various events
	addKeyListener(this);
	addWindowListener(this);

	// show the window and get going
	setSize(200, 200);
	pack();
	setLocation(parentWindow.getLocation().x,
		    parentWindow.getLocation().y);

	setVisible(true);
	inviteUsers.requestFocus();

	// Ask the server for information about the chat rooms, etc.  This
	// will cause our updateLists function, below, to fill up our lists.
	if (parentWindow.theClient != null)
	    try {
		parentWindow.theClient.requestRoomList();
	    }
	    catch (IOException e) {
		parentWindow.theClient.lostConnection();
		return;
	    }

	return;
    }

    private boolean userInRoom(String userName, OrbitRoomInfo room)
    {
	for (int count = 0; count < room.userNames.size(); count ++)
	    {
		if (((String) room.userNames.elementAt(count))
		    .equals(userName))
		    return (true);
	    }

	return (false);
    }

    public void updateLists()
    {
	// Update our lists.  This is called when something changes, such
	// as a user logs on/off, or a user leaves/joins our chat room
	
	if (roomUsersList.getItemCount() > 0)
	    roomUsersList.removeAll();
	if (allUsersList.getItemCount() > 0)
	    allUsersList.removeAll();

	OrbitRoomInfo currentRoom = parentWindow.currentRoom;

	// Add all of the users from this chat room to the 'chat room users'
	// list.
	for (int count = 0; count < currentRoom.userNames.size(); count ++)
	    roomUsersList.add((String)
			      currentRoom.userNames.elementAt(count));

	Vector usersVector = parentWindow.theClient.userList;

	// Add all of the connected users to the 'all users' list
	for (int count = 0; count < usersVector.size(); count ++)
	    {
		OrbitUser user = (OrbitUser) usersVector.elementAt(count);

		if (!userInRoom(user.name, currentRoom))
		    allUsersList.add(user.name);
	    }

	bootUsers.setEnabled(false);
	inviteUsers.setEnabled(false);
	banUsers.setEnabled(false);
	allowUsers.setEnabled(false);
	
	return;
    }

    protected void goBootUsers()
    {
	// This gets called when the room owner wants to boot another
	// user (or users) from the chat room.
	
	String[] selectedUsers;

	// Get the names of all selected users
	selectedUsers = roomUsersList.getSelectedItems();

	// For each selected user name, we will loop through our list of
	// connected users and send a BOOTUSER command to the server

	Vector usersVector = parentWindow.theClient.userList;

	for (int count1 = 0; count1 < selectedUsers.length; count1 ++)
	    {
		// We've got one of the selected users; now find the user
		// data structure so we can get the user id
		for (int count2 = 0; count2 < usersVector.size(); count2 ++)
		    {
			OrbitUser user = (OrbitUser)
			    usersVector.elementAt(count2);

			if (user.name.equals(selectedUsers[count1]))
			    try {
				// Send the command
				parentWindow.theClient.sendBootUser(user.id,
					    parentWindow.currentRoom.name);
			    }
			    catch (IOException e) {
				parentWindow.theClient.lostConnection();
				return;
			    }
		    }
	    }      

	// Refresh the user lists
	updateLists();
    }

    protected void goInviteUsers()
    {
	// This gets called when the room owner wants to invite another
	// user (or users) into a chat room.
	
	String[] selectedUsers;

	// Get the names of all selected users
	selectedUsers = allUsersList.getSelectedItems();

	// For each selected user name, we will loop through our list of
	// connected users and send a INVITE command to the server

	Vector usersVector = parentWindow.theClient.userList;

	for (int count1 = 0; count1 < selectedUsers.length; count1 ++)
	    {
		// We've got one of the selected users; now find the user
		// data structure so we can get the user id
		for (int count2 = 0; count2 < usersVector.size(); count2 ++)
		    {
			OrbitUser user = (OrbitUser)
			    usersVector.elementAt(count2);

			if (user.name.equals(selectedUsers[count1]))
			    try {
				// Send the command
				parentWindow.theClient.sendInvite(user.id,
					  parentWindow.currentRoom.name);
			    }
			    catch (IOException e) {
				parentWindow.theClient.lostConnection();
				return;
			    }
		    }
	    }      

	// Refresh the user lists
	updateLists();
    }

    protected void goBanUsers()
    {
	// This gets called when the room owner wants to ban another
	// user (or users) from the chat room.
	
	String[] selectedUsers;

	// Get the names of all selected users
	selectedUsers = allUsersList.getSelectedItems();

	// For each selected user name, we will loop through our list of
	// connected users and send a BANUSER command to the server

	Vector usersVector = parentWindow.theClient.userList;

	for (int count1 = 0; count1 < selectedUsers.length; count1 ++)
	    {
		// We've got one of the selected users; now find the user
		// data structure so we can get the user id
		for (int count2 = 0; count2 < usersVector.size(); count2 ++)
		    {
			OrbitUser user = (OrbitUser)
			    usersVector.elementAt(count2);

			if (user.name.equals(selectedUsers[count1]))
			    try {
				// Send the command
				parentWindow.theClient.sendBanUser(user.id,
					   parentWindow.currentRoom.name);
			    }
			    catch (IOException e) {
				parentWindow.theClient.lostConnection();
				return;
			    }
		    }
	    }      

	// Refresh the user lists
	updateLists();
    }

    protected void goAllowUsers()
    {
	// This gets called when the room owner wants to un-ban another
	// user (or users) from the chat room.
	
	String[] selectedUsers;

	// Get the names of all selected users
	selectedUsers = allUsersList.getSelectedItems();

	// For each selected user name, we will loop through our list of
	// connected users and send a ALLOWUSER command to the server

	Vector usersVector = parentWindow.theClient.userList;

	for (int count1 = 0; count1 < selectedUsers.length; count1 ++)
	    {
		// We've got one of the selected users; now find the user
		// data structure so we can get the user id
		for (int count2 = 0; count2 < usersVector.size(); count2 ++)
		    {
			OrbitUser user = (OrbitUser)
			    usersVector.elementAt(count2);

			if (user.name.equals(selectedUsers[count1]))
			    try {
				// Send the command
				parentWindow.theClient.sendAllowUser(user.id,
					     parentWindow.currentRoom.name);
			    }
			    catch (IOException e) {
				parentWindow.theClient.lostConnection();
				return;
			    }
		    }
	    }      

	// Refresh the user lists
	updateLists();
    }

    public void actionPerformed(ActionEvent E)
    {
	if (E.getSource() == bootUsers)
	    {
		// We need to send a command to the server to boot the
		// selected users
		goBootUsers();
		return;
	    }
	else if (E.getSource() == inviteUsers)
	    {
		// We need to send a command to the server to invite the
		// selected users
		goInviteUsers();
		return;
	    }
	else if (E.getSource() == banUsers)
	    {
		// We need to send a command to the server to ban the
		// selected users
		goBanUsers();
		return;
	    }
	else if (E.getSource() == allowUsers)
	    {
		// We need to send a command to the server to allow the
		// selected users
		goAllowUsers();
		return;
	    }
	else if (E.getSource() == dismiss)
	    {
		parentWindow.roomControlDialog = null;
		dispose();
		return;
	    }
    }

    public void itemStateChanged(ItemEvent E)
    {
	if (E.getSource() == roomUsersList)
	    {
		boolean somethingSelected =
		    (roomUsersList.getSelectedItems().length > 0);

		bootUsers.setEnabled(somethingSelected);
		return;
	    }
	
	else if (E.getSource() == allUsersList)
	    {
		boolean somethingSelected =
		    (allUsersList.getSelectedItems().length > 0);

		inviteUsers.setEnabled(somethingSelected);
		banUsers.setEnabled(somethingSelected);
		allowUsers.setEnabled(somethingSelected);
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
		if (E.getSource() == bootUsers)
		    {
			// We need to send a command to the server to boot
			// the selected users
			goBootUsers();
			return;
		    }
		else if (E.getSource() == inviteUsers)
		    {
			// We need to send a command to the server to invite
			// the selected users
			goInviteUsers();
			return;
		    }
		else if (E.getSource() == banUsers)
		    {
			// We need to send a command to the server to ban
			// the selected users
			goBanUsers();
			return;
		    }
		else if (E.getSource() == allowUsers)
		    {
			// We need to send a command to the server to ban
			// the selected users
			goAllowUsers();
			return;
		    }
		else if (E.getSource() == dismiss)
		    {
			parentWindow.roomControlDialog = null;
			dispose();
			return;
		    }
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
	parentWindow.roomControlDialog = null;
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
