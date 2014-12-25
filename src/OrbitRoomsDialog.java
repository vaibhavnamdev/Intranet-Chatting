// This file contains the code for the dialog that pops up when a user
// presses the "chat rooms" button on the main console.  It contains a list
// of all the currently active chat rooms, along with associated information,
// and provides an interface for joining a new chat room

import java.awt.*;
import java.awt.event.*;
import java.io.*;


public class OrbitRoomsDialog
    extends Dialog
    implements ActionListener, ItemListener, KeyListener, WindowListener
{
    protected OrbitWindow parentWindow;
    protected java.awt.List roomList;
    protected Button enterRoom;
    protected Button roomInfo;
    protected Button createRoom;
    protected Button updateList;
    protected Button dismiss;

    protected GridBagLayout myLayout;
    protected GridBagConstraints myConstraints;

    protected boolean haveRoomList = false;

    OrbitRoomsDialog(Frame parent)
    {
	super(parent, "Chat rooms", false);

	parentWindow = (OrbitWindow) parent;

	// Make all of the widgets

	myLayout = new GridBagLayout();
	myConstraints = new GridBagConstraints();
	setLayout(myLayout);

	// Layout stuff that's the same for all the widgets
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.BOTH;
	
	roomList = new java.awt.List(10);
	roomList.setFont(parentWindow.smallFont);
	roomList.addItemListener(this);
	roomList.addKeyListener(this);
	roomList.setMultipleMode(false);
	myConstraints.gridx = 0; myConstraints.gridy = 0;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 5;
	myConstraints.weightx = 1.0; myConstraints.weighty = 1.0;
	myConstraints.insets.right = 5; myConstraints.insets.left = 5;
	myConstraints.insets.top = 5; myConstraints.insets.bottom = 0;
	myLayout.setConstraints(roomList, myConstraints);
	add(roomList);

	enterRoom = new Button("Enter room");
	enterRoom.setFont(parentWindow.smallFont);
	enterRoom.addActionListener(this);
	enterRoom.addKeyListener(this);
	enterRoom.setEnabled(false);
	myConstraints.gridx = 0; myConstraints.gridy = 3;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 0; myConstraints.insets.left = 5;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 5;
	myLayout.setConstraints(enterRoom, myConstraints);
	add(enterRoom);

	roomInfo = new Button("Room info");
	roomInfo.setFont(parentWindow.smallFont);
	roomInfo.addActionListener(this);
	roomInfo.addKeyListener(this);
	roomInfo.setEnabled(false);
	myConstraints.gridx = 1; myConstraints.gridy = 3;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 0; myConstraints.insets.left = 0;
	myLayout.setConstraints(roomInfo, myConstraints);
	add(roomInfo);

	createRoom = new Button("Create room");
	createRoom.setFont(parentWindow.smallFont);
	createRoom.addActionListener(this);
	createRoom.addKeyListener(this);
	createRoom.setEnabled(true);
	myConstraints.gridx = 2; myConstraints.gridy = 3;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myLayout.setConstraints(createRoom, myConstraints);
	add(createRoom);

	updateList = new Button("Update list");
	updateList.setFont(parentWindow.smallFont);
	updateList.addActionListener(this);
	updateList.addKeyListener(this);
	updateList.setEnabled(true);
	myConstraints.gridx = 3; myConstraints.gridy = 3;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myLayout.setConstraints(updateList, myConstraints);
	add(updateList);


	dismiss = new Button("Dismiss");
	dismiss.setFont(parentWindow.smallFont);
	dismiss.addActionListener(this);
	dismiss.addKeyListener(this);
	dismiss.setEnabled(true);
	myConstraints.gridx = 4; myConstraints.gridy = 3;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 5; myConstraints.insets.left = 0;
	myLayout.setConstraints(dismiss, myConstraints);
	add(dismiss);

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
	enterRoom.requestFocus();

	getRoomList();
    }

    protected void getRoomList()
    {
	// Ask the server for a list of chat rooms
	
	synchronized (roomList) {
	    haveRoomList = false;

	    // Clear out the room list
	    if (roomList.getItemCount() > 0)
		roomList.removeAll();

	    // Disable various things while we wait for the room list.
	    enterRoom.setEnabled(false);
	    roomInfo.setEnabled(false);
	    
	    // Show a message just in case it takes a moment
	    roomList.add("Requesting room list from the server...");
	    
	    // Make the request
	    try {
		parentWindow.theClient.requestRoomList();
	    }
	    catch (IOException e) {
		parentWindow.theClient.lostConnection();
		return;
	    }

	    // ... and now we wait for the client to fill up our list.
	}
    }

    public void receivedList()
    {
	// This routine will be called by the client when it has acquired
	// a list of the current chat rooms from the server
	
	synchronized (roomList) {
	    
	    // Clear out the room list
	    if (roomList.getItemCount() > 0)
		roomList.removeAll();

	    // Now add the names of the chat rooms to the list widget
	    for (int count = 0; count < parentWindow.roomInfoArray.length;
		 count ++)
		roomList.add(parentWindow.roomInfoArray[count].name);

	    haveRoomList = true;
	}
    }

    protected void goEnter()
    {
	// The user wants to join the selected chat room.  Send
	// a message to the server
	int index = 0;
	OrbitRoomInfo selectedRoom;

	index = roomList.getSelectedIndex();

	if (index >= 0)
	    {
		selectedRoom = parentWindow.roomInfoArray[index];

		if (parentWindow.currentRoom == selectedRoom)
		    {
			new OrbitInfoDialog(parentWindow,
					      "Change unnecessary", true,
					      "You are already in the room \""
					      + selectedRoom.name + "\"");
			return;
		    }

		String password = "";
		// If this room is private, and we have not been previously
		// invited, prompt for a password
		if (selectedRoom.priv && !selectedRoom.invited)
		    {
			OrbitPasswordDialog passDialog = 
			    new OrbitPasswordDialog(parentWindow,
			      "Enter password for private room \"" +
			      selectedRoom.name+ "\"", true);
			password = passDialog.getPassword();
		    }
		
		try {
		    parentWindow.theClient.sendEnterRoom(selectedRoom.name,
							 selectedRoom.priv,
							 password);
		}
		catch (IOException e) {
		    parentWindow.theClient.lostConnection();
		    return;
		}
	    }
	
	parentWindow.roomsDialog = null;

	// DON'T do any other "active" stuff to change chat rooms here.
	// The move between chat rooms only becomes "official" when the
	// server sends out an "enter" message.  There are a couple of
	// reasons for this; one is that it's possible to move between
	// rooms without choosing to do so from this dialog box (users
	// can be booted by room owners).  Another is that the server
	// must authorize the move before it happens.
    }

    protected void showInfo()
    {
	// Gather all of the information we have about this room,
	// construct a big String to contain it, and display it
	// in a dialog box
	int index = 0;
	OrbitRoomInfo selectedRoom;
	String infoString = "";

	index = roomList.getSelectedIndex();

	if (index >= 0)
	    {
		selectedRoom = parentWindow.roomInfoArray[index];

		infoString += "Room name: " + selectedRoom.name
		    + "\nCreator: " + selectedRoom.creatorName
		    +"\nPrivate: ";

		if (selectedRoom.priv)
		    {
			infoString += "yes\nInvited: ";
			if (selectedRoom.invited)
			    infoString += "yes";
			else
			    infoString += "no";
		    }
		else
		    infoString += "no";

		infoString += "\nUsers: " + selectedRoom.userNames.size()
		    + "\n\n";

		for (int count = 0; count < selectedRoom.userNames.size();
		     count ++)
		    {
			infoString +=
			    (String) selectedRoom.userNames.elementAt(count);
			if (count < (selectedRoom.userNames.size() - 1))
			    infoString += ", ";
		    }

		// Now create the dialog box
		new OrbitTextDialog(parentWindow, "Chat room information",
				      infoString, 30, 10,
				      TextArea.SCROLLBARS_VERTICAL_ONLY,
				      false);
	    }
    }

    public void actionPerformed(ActionEvent E)
    {
	if (E.getSource() == enterRoom)
	    {
		goEnter();
		parentWindow.roomOwner(false);
		dispose();
		return;
	    }

	if (E.getSource() == roomInfo)
	    {
		showInfo();
		return;
	    }

	if (E.getSource() == createRoom)
	    {
		new OrbitCreateRoom(parentWindow);
		dispose();
		return;
	    }

	if (E.getSource() == updateList)
	    {
		getRoomList();
		return;
	    }

	if (E.getSource() == dismiss)
	    {
		parentWindow.roomsDialog = null;
		dispose();
		return;
	    }
    }

    public void itemStateChanged(ItemEvent E)
    {
	if (E.getSource() == roomList)
	    {
		// If we don't have the room list yet, then the user has
		// selected the 'hold on a minute' message thing we place
		// in the window while we're waiting.  Ignore it
		if (!haveRoomList)
		    return;
		
		boolean somethingSelected =
		    (roomList.getSelectedItems().length > 0);

		enterRoom.setEnabled(somethingSelected);
		roomInfo.setEnabled(somethingSelected);

		// If the selected room is private, enable the password
		// bits.
		OrbitRoomInfo selectedRoom =
		    parentWindow.roomInfoArray[roomList.getSelectedIndex()];
	    }
    }

    public void keyPressed(KeyEvent E)
    {
    }

    public void keyReleased(KeyEvent E)
    {
	if (E.getKeyCode() == E.VK_ENTER)
	    {
		if (E.getSource() == enterRoom)
		    {
			goEnter();
			dispose();
			return;
		    }
		else if (E.getSource() == roomInfo)
		    {
			showInfo();
			return;
		    }
		else if (E.getSource() == createRoom)
		    {
			new OrbitCreateRoom(parentWindow);
			dispose();
			return;
		    }
		else if (E.getSource() == updateList)
		    {
			getRoomList();
			return;
		    }
		else if (E.getSource() == dismiss)
		    {
			parentWindow.roomsDialog = null;
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
	parentWindow.roomsDialog = null;
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
