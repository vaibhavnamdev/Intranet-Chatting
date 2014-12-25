import java.applet.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;


public class OrbitClient
    extends Thread
{
    protected Socket socket;
    protected DataInputStream istream;
    protected DataOutputStream ostream;
    protected boolean stop = false;

    protected double protocolVersion = 2.0; // by default
    protected OrbitWindow parentWindow;
    protected Vector userList = new Vector();
    protected Vector roomList = new Vector();
    protected Vector ignoredUsers = new Vector();


    public OrbitClient(String host, String name, int portnumber,
			 OrbitWindow mainWindow)
	throws UnknownHostException, IOException, Exception 
    {
	super("Orbit Chat client thread");

	parentWindow = mainWindow;

	// set up the client socket
	socket = new Socket(host, portnumber);

	// Get the output stream
	ostream = new DataOutputStream(socket.getOutputStream());

	// Get an input stream to correspond to the client socket
	istream = new DataInputStream(socket.getInputStream());

	synchronized (istream) {
	    synchronized (ostream) {
		// We will send our preferred protocol version to the server.
		// The server will respond with whatever version it wants us
		// to use (which will never be greater than the version
		// we've asked for)
		sendProtocol(protocolVersion);

		if (istream.readShort() == OrbitCommand.SETPROTO)
		    receiveProtocol();
		else
		    {
			// Arhg.  Netscape 4.x for Windows seems to lose it
			// here after disconnecting/connecting a couple of
			// times.  This is a workaround.  Just drain the
			// stream without negotiating the protocol.  Put up
			// a dialog so that the user knows that their client
			// sucks.
			istream.readFully(new byte[istream.available()]);

			new OrbitTextDialog(mainWindow, "Warning",
			      "Your Java client lost some data while " +
			      "initiating the connection.  Attempting to " +
			      "continue anyway.  Most often this happens " +
			      "while using Netscape Navigator 4.x for " +
			      "Windows.  Please consider upgrading your " +
			      "client or browser!", 60, 15,
			      TextArea.SCROLLBARS_VERTICAL_ONLY, false);
		    }
	    }
	}

	// Start listening for stuff from the server
	start();

	// Now send the server some information about this user
	sendUserInfo();
    }

    public void run()
    {
	while (!stop)
	    try {
		parseCommand();
		ostream.flush();
	    }
	    catch (IOException e) {
		lostConnection();
		return;
	    }

	return;
    }

    void parseCommand()
	throws IOException
    {
	// This routine figures out which command we are being sent, and 
	// dispatches it to the appropriate subroutine, below.

	short commandType = 0;

	synchronized (istream) {

	    // We need to do all input processing from within this
	    // 'synchronized' block, since we read the first part of the
	    // command here, followed by the rest in subroutines.

	    // Try to read a short from the stream.  This indicates the
	    // command type
	    commandType = istream.readShort();

	    // Make sure that we didn't get stopped while we were blocking,
	    // waiting for that data
	    if (stop)
		return;

	    // Now that we have a command to read, we will call the
	    // appropriate client routine to interpret it.
		
	    if (protocolVersion >= 2.0)
		{
		    // The following commands are supported by protocol
		    // version 2.0

		    // The commands LEAVEMESS, READMESS, and EXIT are never
		    // sent from the server to the client (only vice-versa),
		    // so we don't need to worry about them here
			
		    switch (commandType) {
		    
		    case OrbitCommand.NOOP:
			{
			    // Do nothing
			    break;
			}

		    case OrbitCommand.PING:
			{
			    // The server is sending us a ping request.  Just
			    // send one back
			    sendPing();
			    break;
			}

		    case OrbitCommand.CONNECT:
			{
			    // The server is telling us that a new user
			    // connected
			    receiveConnect();
			    break;
			}

		    case OrbitCommand.USERINFO:
			{
			    // The server is sending info about a user
			    // (maybe us)
			    receiveUserInfo();
			    break;
			}

		    case OrbitCommand.SERVERMESS:
			{
			    // The server is sending us a 'dialog box' message
			    receiveServerMess();
			    break;
			}

		    case OrbitCommand.DISCONNECT:
			{
			    // Somebody is disconnecting.  Maybe us :)
			    receiveDisconnect();
			    break;
			}

		    case OrbitCommand.ROOMLIST:
			{
			    // The server is supplying us with a list of
			    // current chat rooms
			    receiveRoomList();
			    break;
			}

		    case OrbitCommand.INVITE:
			{
			    // Someone is inviting us to join a chat room
			    receiveInvite();
			    break;
			}

		    case OrbitCommand.ENTERROOM:
			{
			    // Someone is entering a chat room
			    receiveEnterRoom();
			    break;		   
			}

		    case OrbitCommand.BOOTUSER:
			{
			    // We have been booted from our chat room
			    receiveBootUser();
			    break;		   
			}

		    case OrbitCommand.BANUSER:
			{
			    // We have been banned from our requested chat
			    // room
			    receiveBanUser();
			    break;		   
			}

		    case OrbitCommand.ALLOWUSER:
			{
			    // Someone has allowed us into a chat room
			    receiveAllowUser();
			    break;		   
			}

		    case OrbitCommand.ACTIVITY:
			{
			    // Someone is typing, drawing, etc.
			    receiveActivity();
			    break;
			}

		    case OrbitCommand.CHATTEXT:
			{
			    // There's incoming chat text from another user
			    receiveChatText();
			    break;
			}

		    case OrbitCommand.LINE:
			{
			    // Someone drew a line on the canvas
			    receiveLine();
			    break;
			}

		    case OrbitCommand.RECT:
			{
			    // Someone drew a rectangle on the canvas
			    receivePoly(OrbitCommand.RECT);
			    break;
			}

		    case OrbitCommand.OVAL:
			{
			    // Someone drew an oval on the canvas
			    receivePoly(OrbitCommand.OVAL);
			    break;
			}

		    case OrbitCommand.DRAWTEXT:
			{
			    // Someone drew some text on the canvas
			    receiveDrawText();
			    break;
			}

		    case OrbitCommand.DRAWPICTURE:
			{
			    // Someone pasted an image on the canvas
			    receiveDrawPicture();
			    break;
			}

		    case OrbitCommand.CLEARCANV:
			{
			    // Someone cleared the canvas
			    receiveClearCanv();
			    break;
			}

		    case OrbitCommand.PAGEUSER:
			{
			    // Someone is paging us
			    receivePageUser();
			    break;
			}

		    case OrbitCommand.INSTANTMESS:
			{
			    // The server is sending us an instant message
			    receiveInstantMess();
			    break;
			}

		    case OrbitCommand.STOREDMESS:
			{
			    // The server is sending one of our messgaes to us
			    receiveStoredMess();
			    break;
			}

		    case OrbitCommand.ERROR:
			{
			    // Someone is refusing a page from us
			    receiveError();
			    break;
			}

		    default:
			{
			    // Eek.  We don't understand this command

			    byte[] foo = new byte[istream.available()];
			    istream.readFully(foo);

			    // Since we don't understand whatever the server
			    // is sending to us, is it possible that we've
			    // tried to connect to a Orbit Chat v1.x server
			    // that doesn't speak our language?
			    String stringFoo = new String(foo);
			    if (stringFoo.startsWith("lcome to Orbit"))
				{
				    notifyObsoleteV1();
				    shutdown(false);
				    parentWindow.offline();
				    return;
				}

			    System.out.println("client: unknown command "
					       + commandType);
			    break;
			}
		    }
		}
	}
    }

    protected void notifyObsoleteV1()
    {
	// This function is used to notify the client that this server is
	// a Orbit Chat v1.x server, and can't accept a connection from
	// this client.
	new OrbitInfoDialog(parentWindow, "Obsolete server", true,
			      "Cannot communicate with a version 1.x "
			      + "server.  Please ask the server operator to "
			      + "upgrade.");
    }

    protected OrbitUser findUser(int userId)
    {
	// Find a user in the list

	OrbitUser tmpUser = null;
	OrbitUser returnUser = null;

	// Is it "nobody"?
	if (userId == 0)
	    return (null);

	// Go through the list of connected users, looking for one with the
	// correct id
	for (int count = 0; count < userList.size(); count ++)
	    {
		tmpUser = (OrbitUser) userList.elementAt(count);

		if (tmpUser.id == userId)
		    {
			returnUser = tmpUser;
			break;
		    }
	    }

	return (returnUser);
    }

    protected OrbitUser readUser()
	throws IOException
    {
	// This will read a user id from the input stream and return the
	// corresponding user object.  It will return null if the user id
	// is zero ("nobody") or if the user is not found in the list
	
	int userId = 0;

	// What's the user ID of the user?
	userId = istream.readInt();

	return (findUser(userId));
    }

    protected void sendRecipients()
	throws IOException
    {
	// This function will construct a recipient list and send it
	// down the pipe based on which users are selected in the 'send to'
	// list of the parent window

	String[] selectedUsers;
	int numberUsers = 0;

	// Is "send to all" selected?
	if (parentWindow.sendToAll.getState())
	    {
		// Send an empty user list
		ostream.writeInt(0);
	    }
	else
	    {
		// Get the list of selected user names
		selectedUsers = parentWindow.sendTo.getSelectedItems();
	
		// How many are there?
		numberUsers = selectedUsers.length;

		// Write out how many
		ostream.writeInt(numberUsers);
			
		// Loop for each 
		for (int count1 = 0; count1 < numberUsers; count1 ++)
		    {
			// Find this user in our user list
			for (int count2 = 0; count2 < userList.size();
			     count2++)
			    {
				OrbitUser tmp = (OrbitUser)
				    userList.elementAt(count2);
					
				if (selectedUsers[count1].equals(tmp.name))
				    ostream.writeInt(tmp.id);
			    }
		    }
	    }
    }

    protected void addToIgnored()
    {
	// This routine will add the selected users to our list of ignored
	// users

	String[] selectedUsers;
	int numberUsers = 0;

	// Get the list of selected user names
	selectedUsers = parentWindow.sendTo.getSelectedItems();
	
	// How many are there?
	numberUsers = selectedUsers.length;

	// Loop for each 
	for (int count1 = 0; count1 < numberUsers; count1 ++)
	    {
		// Find this user in our user list
		for (int count2 = 0; count2 < userList.size(); count2++)
		    {
			OrbitUser tmp = (OrbitUser)
			    userList.elementAt(count2);

			// Is the user already being ignored?
			if (isIgnoredUser(tmp.name))
			    continue;

			if (selectedUsers[count1].equals(tmp.name))
			    ignoredUsers.addElement(tmp.name);
		    }
	    }

	// Output a message
	parentWindow.messages.append("<<Ignoring the users: ");
	for (int count = 0; count < ignoredUsers.size(); count ++)
	    {
		parentWindow.messages
		    .append((String) ignoredUsers.elementAt(count));
		if (count < (ignoredUsers.size() - 1))
		    parentWindow.messages.append(", ");
	    }
	parentWindow.messages.append(">>\n");
    }

    protected boolean isIgnoredUser(String who)
    {
	// This will return true if the supplied user name is in our list of
	// ignored users
	for (int count = 0; count < ignoredUsers.size(); count ++)
	    if (((String) ignoredUsers.elementAt(count)).equals(who))
		return (true);
	return (false);
    }


    // The following routines will send and receive the various command
    // types

    protected void sendProtocol(double version)
	throws IOException
    {
	// Send a message to the server about our desired protocol
	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.SETPROTO);
		ostream.writeDouble(version);
	    }
    }

    protected void receiveProtocol()
	throws IOException
    {
	// The server is telling us which protocol to use

	// The client thread has already absorbed the 'command id'
	// from the stream
	protocolVersion = istream.readDouble();
    }

    protected void sendPing()
	throws IOException
    {
	// Send a ping reply back to the server
	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.PING);
	    }
    }

    protected void receiveConnect()
	throws IOException
    {
	// A new user has connected.  We only use this to output a message
	// saying that the user has connected; we get a USERINFO command
	// that will actually tell us about the user later
	
	String userName = istream.readUTF();

	parentWindow.messages
	    .append("<<New user \"" + userName + "\" connected>>\n");
    }

    protected void sendUserInfo()
	throws IOException
    {
	// Send the server some information about this user
	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.USERINFO);
		ostream.writeInt(0); // We don't know our id
		ostream.writeUTF(parentWindow.name);
		ostream.writeUTF(parentWindow.encryptedPassword);
		ostream.writeBoolean(parentWindow.passwordEncryptor
				     .canEncrypt);
		ostream.writeUTF(parentWindow.additional);
	    }
    }

    protected void receiveUserInfo()
	throws IOException
    {
	int tmpId = 0;
	String tmpName = "";
	String tmpAdditional = "";
	String tmpChatroomName = "";
	OrbitUser newUser;

	// The server is sending new information about some user.
	tmpId = istream.readInt();
	tmpName = istream.readUTF();
	// Password field will be empty
	istream.readUTF();
	istream.readBoolean();
	tmpAdditional = istream.readUTF();

	// Is the user name ours?  If so, the server is telling us our
	// own user id number.
	if (tmpName.equals(parentWindow.name))
	    {
		parentWindow.id = tmpId;
	    }
	else
	    {
		// Some new user has connected.  Create a new user object
		newUser = new OrbitUser(tmpId, tmpName, "", tmpAdditional);

		// Add the user to our collection of users
		userList.addElement(newUser);
	    }
    }

    protected void receiveServerMess()
	throws IOException
    {
	String message = "";

	// The server is sending us a message.  Get the message.
	message = istream.readUTF();

	// Make a dialog box with the message
	
	new OrbitInfoDialog(parentWindow, "Server message", true,
			      message);
    }

    protected void sendDisconnect()
	throws IOException
    {
	// Tell the server that we're disconnecting
	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.DISCONNECT);
		ostream.writeInt(parentWindow.id);
		ostream.writeUTF("");
	    }
    }

    protected void receiveDisconnect()
	throws IOException
    {
	int tmpId = 0;
	OrbitUser tmpUser;
	String disconnectMess = "";
	java.awt.List list = parentWindow.sendTo;

	tmpId = istream.readInt();
	disconnectMess = istream.readUTF();
	istream.readFully(new byte[istream.available()]);

	// Who is it?  Is it us?
	if ((tmpId == parentWindow.id) || (tmpId == 0))
	    {
		// If it's us, make a dialog box with the disconnection
		// message
	
		if (disconnectMess.equals(""))
		    disconnectMess = "(no reason given)";

		new OrbitInfoDialog(parentWindow, "Disconnected", true,
				      disconnectMess);

		shutdown(false);
		parentWindow.offline();
	    }
	else
	    {
		// Some other user disconnected.  Output a message that this
		// user has left the chat.

		tmpUser = findUser(tmpId);

		if (tmpUser == null)
		    // Don't know who this is.  Ignore.
		    return;

		parentWindow.messages.append("<<" + tmpUser.name
					     + " is disconnecting>>\n");

		// Remove this name from our 'currently sending to' list
		synchronized (list)
		    {
			for (int count2 = 0; count2 < list.getItemCount();
			     count2 ++)
			    {
				if (list.getItem(count2).equals(tmpUser.name))
				    {
					if (list.isIndexSelected(count2))
					    list.select(0);
					list.remove(count2);
					break;
				    }
			    }
			
			// If there's nothing left in the list, make sure
			// the 'send to all' checkbox is checked
			if (list.getSelectedItems().length == 0)
			    parentWindow.sendToAll.setState(true);
		    }

		// Remove this name from the user list of its chat room
		// info
		OrbitRoomInfo roomInfo = null;

		if (parentWindow.roomInfoArray != null)
		    {
			for (int count2 = 0;
			     count2 < parentWindow.roomInfoArray.length;
			     count2 ++)
			    // Is this the user's chat room?
			    if (parentWindow.roomInfoArray[count2]
				.name.equals(tmpUser.getChatRoomName()))
				{
				    roomInfo = parentWindow
					.roomInfoArray[count2];
				    break;
				}
			if (roomInfo != null)
			    {
				// Remove the user from the room's list
				// of users
				roomInfo.userNames
				    .removeElement(tmpUser.name);
				roomInfo.userNames.trimToSize();
			    }
		    }

		// Remove this name from our user list also
		userList.removeElement((Object) tmpUser);
		userList.trimToSize();

		// If there's a OrbitRoomControl dialog, tell it to update
		// itself
		if (parentWindow.roomControlDialog != null)
		    parentWindow.roomControlDialog.updateLists();
	    }
    }

    protected void requestRoomList()
	throws IOException
    {
	// This will ask the server to send us a list of the current
	// chat rooms
	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.ROOMLIST);
		ostream.writeShort(0); // We're asking for a list,
		// not supplying one
	    }
    }

    protected void receiveRoomList()
	throws IOException
    {
	int howManyRooms = 0;
	OrbitRoomInfo[] roomList;
	OrbitRoomInfo tmp = null;

	// This will receive the list of chat rooms from the server,
	// place them into an array (and supply the array to the
	// OrbitroomsDialog, if one exists).

	// How many chat rooms are there?
	howManyRooms = istream.readShort();

	// Now that we know the number, we can create the array
	roomList = new OrbitRoomInfo[howManyRooms];

	// Now loop and fill out the information in each OrbitRoomInfo
	// structure
	for (int count1 = 0; count1 < howManyRooms; count1 ++)
	    {
		tmp = new OrbitRoomInfo();

		tmp.name = istream.readUTF();
		tmp.creatorName = istream.readUTF();
		tmp.priv = istream.readBoolean();
		tmp.invited = istream.readBoolean();
		int numUsers = istream.readInt();

		// Fill out the user names array
		for (int count2 = 0; count2 < numUsers; count2 ++)
		    tmp.userNames.addElement(istream.readUTF());

		roomList[count1] = tmp;

		if (tmp.name.equals(parentWindow.currentRoom.name))
		    parentWindow.currentRoom = tmp;
	    }

	// Save the list of chat room info
	parentWindow.roomInfoArray = roomList;

	// Now notify the chat rooms dialog box, if it exists, so that
	// changes will be shown there.
	if (parentWindow.roomsDialog != null)
	    parentWindow.roomsDialog.receivedList();

	// If there's a OrbitRoomControl dialog, tell it to update
	// itself
	if (parentWindow.roomControlDialog != null)
	    parentWindow.roomControlDialog.updateLists();
    }

    protected void sendInvite(int userId, String roomName)
	throws IOException
    {
	// Tell the server that we want to invite a user into our chat room
	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.INVITE);
		ostream.writeInt(parentWindow.id);
		ostream.writeUTF(roomName);
		ostream.writeInt(userId);
	    }
    }

    protected void receiveInvite()
	throws IOException
    {
	OrbitUser fromUser = null;
	String roomName = null;

	// This user has been invited to join a chat room

	// Who did it?
	fromUser = readUser();

	roomName = istream.readUTF();
	istream.readInt(); // Discard our user id

	if (fromUser == null)
	    // Ack.  No such user.  It can happen if someone logs out at
	    // just the right moment
	    return;

	if (isIgnoredUser(fromUser.name))
	    return;

	// Make a dialog box with a message so that the user knows that
	// they're invited to join
	new OrbitInfoDialog(parentWindow, "Invitation", false,
			      "You have been invited to join the room \""
			      + roomName + "\" by user " + fromUser.name);
    }

    protected void sendEnterRoom(String roomName, boolean priv,
				 String password)
	throws IOException
    {
	if (roomName.equals(""))
	    // Skip it, no such thing as a room with an empty name
	    return;

	// Tell the server that we want to join a different chat room
	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.ENTERROOM);
		ostream.writeInt(parentWindow.id);
		ostream.writeUTF(roomName);
		ostream.writeBoolean(priv);
		ostream.writeUTF(password);
		ostream.writeBoolean(parentWindow.passwordEncryptor
				     .canEncrypt);
	    }
    }

    protected void receiveEnterRoom()
	throws IOException
    {
	int userId = 0;
	String newRoomName = "";
	OrbitUser tmpUser;
	java.awt.List list = parentWindow.sendTo;

	// Some user (maybe us) has just entered a chat room.
	userId = istream.readInt();
	newRoomName = istream.readUTF();
	istream.readBoolean();
	istream.readUTF(); // Ignore empty password
	istream.readBoolean(); // Not encrypted
	
	// Who is the user?  If it's us, we need to set the name of our
	// chat room.  If it's another user, we need to set the name of
	// their chat room, and add them to our 'send to' list if they're
	// in the same room as us.

	if (userId == parentWindow.id)
	    {
		// It's us.

		if (!parentWindow.currentRoom.name.equals(""))
		    parentWindow.messages.append("<<entering chat room \""
						 + newRoomName + "\">>\n");
		
		// Clear out the 'send to' list
		if (parentWindow.sendTo.getItemCount() > 0)
		    parentWindow.sendTo.removeAll();
		parentWindow.sendToAll.setState(true);

		// Clear the canvas
		parentWindow.canvas.clear();

		// Who else is currently in our new room?  Add them to our
		// 'send to' list
		for (int count1 = 0; count1 < userList.size(); count1 ++)
		    {
			tmpUser = (OrbitUser) userList.elementAt(count1);

			if (tmpUser.getChatRoomName().equals(newRoomName))
			    parentWindow.sendTo.add(tmpUser.name);
		    }

		// Get the room info for the room we're entering.

		for (int count1 = 0;
		     count1 < parentWindow.roomInfoArray.length; count1 ++)
		    {
			OrbitRoomInfo roomInfo =
			    parentWindow.roomInfoArray[count1];

			if (roomInfo.name.equals(newRoomName))
			    {
				// Found it
				parentWindow.currentRoom = roomInfo;
				break;
			    }
		    }

		// If we had a room-control dialog box open, we should
		// discard it.  However, we should also load another one
		// if we own the new chat room as well
		if (parentWindow.roomControlDialog != null)
		    {
			parentWindow.roomControlDialog.dispose();
			parentWindow.roomControlDialog = null;
		    }
		if (parentWindow.currentRoom.creatorName
		    .equals(parentWindow.name))
		    parentWindow.roomControlDialog =
			new OrbitRoomControl(parentWindow);

		return;
	    }

	// Loop through our user list and find the one with this id
	for (int count1 = 0; count1 < userList.size(); count1 ++)
	    {
		tmpUser = (OrbitUser) userList.elementAt(count1);

		if (tmpUser.id != userId)
		    continue;

		// This is the one.  Was the user previously in our chat
		// room?  If so, we need to remove them from our 'currently
		// sending to' list
		String oldRoomName = tmpUser.getChatRoomName();
		if (oldRoomName.equals(parentWindow.currentRoom.name))
		    {
			if (!oldRoomName.equals(""))
			    parentWindow.messages
				.append("<<" + tmpUser.name +
					" moved to chat room \"" +
					newRoomName + "\">>\n");
			
			// Remove this name from our 'currently sending to'
			// list
			synchronized (list) {
			    for (int count2 = 0; count2 < list.getItemCount();
				 count2 ++)
				{
				    if (list.getItem(count2)
					.equals(tmpUser.name))
					{
					    if (list.isIndexSelected(count2))
						list.select(0);
					    list.remove(count2);
					    break;
					}
				}
			}
		    }
		// Is the user entering our chat room?
		else if (newRoomName.equals(parentWindow.currentRoom.name))
		    {
			if (!oldRoomName.equals(""))
			    parentWindow.messages
				.append("<<" + tmpUser.name +
					" entering chat room>>\n");

			// Add this name to our 'currently sending to' list
			synchronized (list) {
			    list.add(tmpUser.name);
			}
		    }

		// Set the user's new room name.
		tmpUser.setChatRoomName(newRoomName);
		
		break;
	    }

	// If there's a OrbitRoomControl dialog, get a room list update
	if (parentWindow.roomControlDialog != null)
	    requestRoomList();
    }

    protected void sendBootUser(int userId, String roomName)
	throws IOException
    {
	// Tell the server that we want to boot the user from the
	// current chat room
	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.BOOTUSER);
		ostream.writeInt(parentWindow.id);
		ostream.writeUTF(roomName);
		ostream.writeInt(userId);
	    }
    }

    protected void receiveBootUser()
	throws IOException
    {
	OrbitUser fromUser = null;
	String roomName = null;

	// This user is being booted from the current chat room

	// Who did it?
	fromUser = readUser();

	roomName = istream.readUTF();
	istream.readInt(); // Discard our user id

	// Dont't ignore this, even if the user that booted us is an
	// ignored user; an ignored user still has the right to boot us
	// from their chat room, and we should be notified if we get booted.

	if (fromUser == null)
	    // Ack.  No such user.  It can happen if someone logs out at
	    // just the right moment
	    return;

	// Make a dialog box with a message so that the user notices
	// they're a goner.
	new OrbitInfoDialog(parentWindow, "Booted", true,
			      "You have been booted from the room \""
			      + roomName + "\" by user "
			      + fromUser.name);
    }

    protected void sendBanUser(int userId, String roomName)
	throws IOException
    {
	// Tell the server that we want to ban the user from the
	// current chat room
	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.BANUSER);
		ostream.writeInt(parentWindow.id);
		ostream.writeUTF(roomName);
		ostream.writeInt(userId);
	    }
    }

    protected void receiveBanUser()
	throws IOException
    {
	OrbitUser fromUser = null;
	String roomName = null;

	// This user has been banned from the requested chat room

	// Who did it?
	fromUser = readUser();

	roomName = istream.readUTF();
	istream.readInt(); // Discard our user id

	// Dont't ignore this, even if the user that banned us is an
	// ignored user; an ignored user still has the right to ban us
	// from their chat room, and we should be notified if we get banned.

	// Make a dialog box with a message so that the user notices
	// they're a goner.
	new OrbitInfoDialog(parentWindow, "Banned", true,
			      "You have been banned from the room \""
			      + roomName + "\"");
    }

    protected void sendAllowUser(int userId, String roomName)
	throws IOException
    {
	// Tell the server that we want to un-ban the user from the
	// current chat room
	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.ALLOWUSER);
		ostream.writeInt(parentWindow.id);
		ostream.writeUTF(roomName);
		ostream.writeInt(userId);
	    }
    }

    protected void receiveAllowUser()
	throws IOException
    {
	OrbitUser fromUser = null;
	String roomName = null;

	// This user has been un-banned from a chat room

	// Who did it?
	fromUser = readUser();

	roomName = istream.readUTF();
	istream.readInt(); // Discard our user id

	if (fromUser == null)
	    // Ack.  No such user.  It can happen if someone logs out at
	    // just the right moment
	    return;

	if (isIgnoredUser(fromUser.name))
	    return;

	// Make a dialog box with a message so that the user knows that
	// they're allowed back in
	new OrbitInfoDialog(parentWindow, "Allowed", true,
			      "You are now allowed to join the room \""
			      + roomName + "\"");
    }

    protected void sendActivity(short activity)
	throws IOException
    {
	// This tells our selected recipients that we're in the middle
	// of typing something.
	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.ACTIVITY);
		ostream.writeInt(parentWindow.id);
		ostream.writeShort(activity);

		// Who's it for?  Send the recipient list
		sendRecipients();
	    }
    }

    protected void receiveActivity()
	throws IOException
    {
	// Some user is doing some activity, such as typing or drawing.

	OrbitUser fromUser = null;
	short activity = 0;
	int numForUsers = 0;

	// Who is it?
	fromUser = readUser();

	// Read the activity and discard the recipient list, if there is one
	activity = istream.readShort();
	numForUsers = istream.readInt();
	for (int count = 0; count < numForUsers; count ++)
	    istream.readInt();
	
	if (fromUser == null)
	    // Ack.  No such user.  It can happen if someone logs out at
	    // just the right moment
	    return;

	// Are we ignoring this user?
	if (isIgnoredUser(fromUser.name))
	    return;

	// Set the appropriate message in the activity window
	String tmpString = "";
	if (activity == OrbitCommand.ACTIVITY_DRAWING)
	    tmpString = "drawing: " + fromUser.name;
	else if (activity == OrbitCommand.ACTIVITY_TYPING)
	    tmpString = "typing: " + fromUser.name;
	// Else what?  Ignore it.
	if (!parentWindow.activity.getText().equals(tmpString))
	    parentWindow.activity.setText(tmpString);
    }

    protected void sendChatText(String data)
	throws IOException
    {
	// This sends a line of chat text to the selected recipients
	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.CHATTEXT);
		ostream.writeInt(parentWindow.id);
		if (parentWindow.sendToAll.getState())
		    // Public
		    ostream.writeBoolean(false);
		else
		    // Private
		    ostream.writeBoolean(true);
		ostream.writeShort(0); // No colour
		ostream.writeUTF(data);
		
		// Who's it for?  Send the recipient list
		sendRecipients();
	    }
    }

    protected void receiveChatText()
	throws IOException
    {
	OrbitUser fromUser = null;
	boolean priv = false;
	short colour = 0;
	String data = "";
	String output = "";
	int numForUsers = 0;

	// There is incoming chat text.

	// From whom is this message?
	fromUser = readUser();

	// Is this message private?  Also grab the colour value (unused),
	// the data itself, and the recipient list
	priv = istream.readBoolean();
	colour = istream.readShort();
	data = istream.readUTF();

	// Discard the recipient list, if there is one
	numForUsers = istream.readInt();
	for (int count = 0; count < numForUsers; count ++)
	    istream.readInt();
	
	if (fromUser != null)
	    {
		if (isIgnoredUser(fromUser.name))
		    return;

		// Precede the message with the name of the user that sent it
		// Is the message public or private?
		if (fromUser != null)
		    {
			if (priv)
			    output += "*private from " + fromUser.name +
				"*> ";
			else
			    output += fromUser.name + "> ";
		    }
	    }

	// Append the actual message
	output += data;

	// Clear the 'activity' window
	parentWindow.activity.setText("");
	
	// Put all the text in the chat window
	parentWindow.messages.append(output);
    }

    protected void sendLine(short colour, short startx, short starty,
			    short endx, short endy, short thick)
	throws IOException
    {
	// This sends a graphic line to the selected recipients

	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.LINE);
		ostream.writeInt(parentWindow.id);
		ostream.writeShort(colour);
		ostream.writeShort(startx);
		ostream.writeShort(starty);
		ostream.writeShort(endx);
		ostream.writeShort(endy);
		ostream.writeShort(thick);

		// Who's it for?  Send the recipient list
		sendRecipients();
	    }
    }

    protected void receiveLine()
	throws IOException
    {
	// There is an incoming graphic line.

	OrbitUser fromUser = null;
	short colournum = 0;
	short startx = 0;
	short starty = 0;
	short endx = 0;
	short endy = 0;
	short thick = 0;
	int numForUsers = 0;
	Color colour;

	// From whom is this message?
	fromUser = readUser();

	// Grab the colour value, x, y, and thickness, and the recipient list
	colournum = istream.readShort();
	startx = istream.readShort();
	starty = istream.readShort();
	endx = istream.readShort();
	endy = istream.readShort();
	thick = istream.readShort();

	// Discard the recipient list, if there is one
	numForUsers = istream.readInt();
	for (int count = 0; count < numForUsers; count ++)
	    istream.readInt();

	if (fromUser != null)
	    if (isIgnoredUser(fromUser.name))
		return;

	// Now draw the line
	colour = parentWindow.canvas.colourArray[colournum];
	parentWindow.canvas.drawLine(colour, startx, starty, endx, endy,
				     thick, OrbitCanvas.MODE_PAINT);
    }

    protected void sendPoly(short colour, short x, short y, short width,
			    short height, short thick, boolean fill, int kind)
	throws IOException
    {
	// This sends a graphic polygon to the selected recipients.

	synchronized (ostream)
	    {
		ostream.writeShort(kind);
		ostream.writeInt(parentWindow.id);
		ostream.writeShort(colour);
		ostream.writeShort(x);
		ostream.writeShort(y);
		ostream.writeShort(width);
		ostream.writeShort(height);
		ostream.writeShort(thick);
		ostream.writeBoolean(fill);

		// Who's it for?  Send the recipient list
		sendRecipients();
	    }
    }

    protected void receivePoly(short kind)
	throws IOException
    {
	// There is an incoming graphic polygon.

	OrbitUser fromUser = null;
	short colournum = 0;
	short x = 0;
	short y = 0;
	short height = 0;
	short width = 0;
	short thick = 0;
	boolean fill = false;
	int numForUsers = 0;
	Color colour;

	// From whom is this message?
	fromUser = readUser();

	// Grab the colour value, x, y, width, height, thickness and fill,
	// and the recipient list
	colournum = istream.readShort();
	x = istream.readShort();
	y = istream.readShort();
	width = istream.readShort();
	height = istream.readShort();
	thick = istream.readShort();
	fill = istream.readBoolean();

	// Discard the recipient list, if there is one
	numForUsers = istream.readInt();
	for (int count = 0; count < numForUsers; count ++)
	    istream.readInt();

	if (fromUser != null)
	    if (isIgnoredUser(fromUser.name))
		return;

	// Get the colour
	colour = parentWindow.canvas.colourArray[colournum];

	// Now draw the shape
	if (kind == OrbitCommand.RECT)
	    parentWindow.canvas.drawRect(colour, x, y, width, height, fill,
					 thick, OrbitCanvas.MODE_PAINT);
	else if (kind == OrbitCommand.OVAL)
	    parentWindow.canvas.drawOval(colour, x, y, width, height, fill,
					 thick, OrbitCanvas.MODE_PAINT);
    }

    protected void sendDrawText(short colour, short x, short y, short type,
				short attribs, short size, String text)
	throws IOException
    {
	// This sends graphic text to the selected recipients.
	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.DRAWTEXT);
		ostream.writeInt(parentWindow.id);
		ostream.writeShort(colour);
		ostream.writeShort(x);
		ostream.writeShort(y);
		ostream.writeShort(type);
		ostream.writeShort(attribs);
		ostream.writeShort(size);
		ostream.writeUTF(text);

		// Who's it for?  Send the recipient list
		sendRecipients();
	    }
    }

    protected void receiveDrawText()
	throws IOException
    {
	// There is an incoming graphic polygon.

	OrbitUser fromUser = null;
	short colournum = 0;
	short x = 0;
	short y = 0;
	short type = 0;
	short size = 0;
	short attribs = 0;
	String text = "";
	int numForUsers = 0;
	Color colour;

	// From whom is this message?
	fromUser = readUser();

	// Grab the colour value, x, y, type, size, and attributes,
	// and the recipient list
	colournum = istream.readShort();
	x = istream.readShort();
	y = istream.readShort();
	type = istream.readShort();
	attribs = istream.readShort();
	size = istream.readShort();
	text = istream.readUTF();

	// Discard the recipient list, if there is one
	numForUsers = istream.readInt();
	for (int count = 0; count < numForUsers; count ++)
	    istream.readInt();

	if (fromUser != null)
	    if (isIgnoredUser(fromUser.name))
		return;

	// Get the colour
	colour = parentWindow.canvas.colourArray[colournum];

	// Now draw the text
	parentWindow.canvas.drawText(colour, x, y, type, attribs, size, text, 
				     parentWindow.canvas.MODE_PAINT);
    }

    protected void sendDrawPicture(short x, short y, File pictureFile)
	throws IOException
    {
	// This sends graphical images to the selected recipients.

	// How many bytes is the picture file?
	int fileLength = (int) pictureFile.length();;

	// Read the picture file into a byte array
	byte[] byteArray = new byte[fileLength];
	try {
	    FileInputStream fileStream = new FileInputStream(pictureFile);

	    if (fileStream.read(byteArray) < fileLength)
		{
		    new OrbitInfoDialog(parentWindow, "Error", true,
					  "Couldn't read whole picture file");
		    return;
		}
	}
	catch (Exception e) {
	    e.printStackTrace();
	    return;
	}

	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.DRAWPICTURE);
		ostream.writeInt(parentWindow.id);
		ostream.writeShort(x);
		ostream.writeShort(y);
		ostream.writeInt(fileLength);
		ostream.write(byteArray, 0, fileLength);

		// Who's it for?  Send the recipient list
		sendRecipients();
	    }
    }

    protected void receiveDrawPicture()
	throws IOException
    {
	// There is an incoming picture.

	OrbitUser fromUser = null;
	short x = 0;
	short y = 0;
	int length = 0;
	byte[] data = null;
 	int numForUsers = 0;

	// From whom is this thing?
	fromUser = readUser();

	// Grab the x, y coordinates, the length, the image data,
	// and the recipient list
	x = istream.readShort();
	y = istream.readShort();
	length = istream.readInt();

	// We know how long the data is.  Make an array to hold
	// it, and read the data
	data = new byte[length];
	int read = 0;
	while (read < length)
	    read += istream.read(data, read, (length - read));

	// Discard the recipient list, if there is one
	numForUsers = istream.readInt();
	for (int count = 0; count < numForUsers; count ++)
	    istream.readInt();

	if (fromUser != null)
	    if (isIgnoredUser(fromUser.name))
		return;

	Image theImage = null;

	// Turn the raw image data into an image and draw it
	try {
	    theImage = parentWindow.getToolkit().createImage(data);
	    parentWindow.getToolkit().prepareImage(theImage, -1, -1,
						   parentWindow.canvas);
	    // We have to wait until the image is ready
	    while ((parentWindow.getToolkit().checkImage(theImage, -1, -1,
		 parentWindow.canvas) & parentWindow.canvas.ALLBITS) == 0)
		{}
	}
	catch (Exception e) {
	    e.printStackTrace();
	    return;
	}
	
	parentWindow.canvas.drawPicture(x, y, theImage);
    }

    protected void sendClearCanv()
	throws IOException
    {
	// Sends the 'clear canvas' signal to the specified users
	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.CLEARCANV);
		ostream.writeInt(parentWindow.id);

		// Who's it for?  Send the recipient list
		sendRecipients();
	    }
    }

    protected void receiveClearCanv()
	throws IOException
    {
	OrbitUser fromUser = null;
	int numForUsers = 0;

	// Someone has cleared the canvas

	// Who was it?
	fromUser = readUser();

	// Discard the recipient list, if there is one
	numForUsers = istream.readInt();
	for (int count = 0; count < numForUsers; count ++)
	    istream.readInt();
	
	if (fromUser == null)
	    // Ack.  No such user.  It can happen if someone logs out at
	    // just the right moment
	    return;

	if (isIgnoredUser(fromUser.name))
	    return;

	// Clear the canvas
	parentWindow.canvas.clear();	
	parentWindow.messages.append("<<" + fromUser.name 
				     + " cleared the canvas>>\n");
    }

    protected void sendPageUser()
	throws IOException
    {
	// Sends the paging signal to the specified users

	String[] selectedUsers;
	int numberUsers = 0;

	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.PAGEUSER);
		ostream.writeInt(parentWindow.id);

		// Who's it for?  Send the recipient list
		sendRecipients();
	    }

	// Print a message to our own screen stating that the page has
	// been issued.

	// Is the first item selected?  If so, this means we're sending
	// to everybody
	if (parentWindow.sendToAll.getState())
	    parentWindow.messages.append("<<paging all conference "
					 + "participants>>\n");
	
	// Otherwise, list the users that have been paged
	else
	    {
		// Get the list of selected user names
		selectedUsers = parentWindow.sendTo.getSelectedItems();
	
		// How many are there?
		numberUsers = selectedUsers.length;

		if (numberUsers == 1)
		    parentWindow.messages.append("<<paging "
						 + selectedUsers[0] + ">>\n");
		else
		    {
			parentWindow.messages.append("<<paging the "
						     + "following users:");
			// Loop for each 
			for (int count = 0; count < numberUsers; count ++)
			    parentWindow.messages.append(" "
						 + selectedUsers[count]);
			parentWindow.messages.append(">>\n");
		    }
	    }
    }

    protected void receivePageUser()
	throws IOException
    {
	OrbitUser fromUser = null;
	int numForUsers = 0;

	// Someone is paging us

	// Who was it?
	fromUser = readUser();

	// Discard the recipient list, if there is one
	numForUsers = istream.readInt();
	for (int count = 0; count < numForUsers; count ++)
	    istream.readInt();
	
	if (fromUser == null)
	    // Ack.  No such user.  It can happen if someone logs out at
	    // just the right moment
	    return;

	if (isIgnoredUser(fromUser.name))
	    return;

	// Write a message to the conference area so that the user can
	// see he's been paged
	parentWindow.messages.append("<<" + fromUser.name
				     + " is paging you>>\n");
		
	// If the user has not turned the paging feature off, play
	// the paging sound.  Otherwise, return a message stating that
	// the page has been refused.
	if (parentWindow.menuPlaySound.getState())
	    {
		// If this Java version is less than 1.2, we can't play
		// this sound
		float javaVersion = Float
		    .parseFloat(System.getProperty("java.version")
				.substring(0, 3));
		if (javaVersion < 1.2)
		    sendError(fromUser.id, OrbitCommand.ERROR_NOSOUND);
		
		else
		    {
			// Try to play the paging sound
			try {
			    URL soundURL =
				new URL(parentWindow.OrbitURL.getProtocol(),
					parentWindow.OrbitURL.getHost(),
					parentWindow.OrbitURL.getFile() +
					"OrbitPage.au");
			    Applet.newAudioClip(soundURL).play();
			}
			catch (Exception argh) {
			    // This client can't play the paging sound.

			    // Try beeps instead
			    for (int count = 0; count < 3; count ++)
				parentWindow.getToolkit().beep();

			    // Send a message
			    sendError(fromUser.id,
				      OrbitCommand.ERROR_NOSOUND);
			}
		    }
	    }
	else
	    // The user is not accepting pages.  Send a message.
	    sendError(fromUser.id, OrbitCommand.ERROR_NOPAGE);
    }

    protected void sendError(int toWhom, short code)
	throws IOException
    {
	// Sends the requested advisory to the specified user
	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.ERROR);
		ostream.writeInt(parentWindow.id);
		ostream.writeShort(code);
		ostream.writeInt(1); // 1 recipient
		ostream.writeInt(toWhom);
	    }
    }

    public void sendInstantMess(int whoFor, String message)
	throws IOException
    {
	// Sends an instant message to the server for the specified user
	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.INSTANTMESS);
		ostream.writeInt(parentWindow.id);
		ostream.writeInt(whoFor);
		ostream.writeUTF(message);
	    }
    }

    public void receiveInstantMess()
	throws IOException
    {
	OrbitUser fromUser = null;
	String message = "";
			
	// From whom is this message?
	fromUser = readUser();

	// What's the message?
	istream.readInt(); // Our id
	message = istream.readUTF();

	if (fromUser == null)
	    // Ack.  No such user.  It can happen if someone logs out at
	    // just the right moment
	    return;

	if (isIgnoredUser(fromUser.name))
	    return;

	// Place it on the screen as a dialog box
	new OrbitTextDialog(parentWindow, "Instant message from "
			      + fromUser.name, message, 40, 5,
			      TextArea.SCROLLBARS_VERTICAL_ONLY, false);
	return;
    }

    public void sendLeaveMess(String whofor, String message)
	throws IOException
    {
	// Sends our message to the server for the specified user
	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.LEAVEMESS);
		ostream.writeInt(parentWindow.id);
		ostream.writeUTF(whofor);
		ostream.writeUTF(message);
	    }
    }

    public void sendReadMess()
	throws IOException
    {
	// Sends our message to the server for the specified user
	synchronized (ostream)
	    {
		ostream.writeShort(OrbitCommand.READMESS);
		ostream.writeInt(parentWindow.id);
	    }
    }

    public void receiveStoredMess()
	throws IOException
    {
	short numberMessages = 0;
	String from = "";
	String message = "";
			
	// Read the command header
	numberMessages = istream.readShort();

	if (numberMessages == 0)
	    {
		new OrbitInfoDialog(parentWindow, "None", true,
				      "No unread messages."); 
		return;
	    }

	// Loop for each message
	for (int count = 0; count < numberMessages; count ++)
	    {
		// What's the message?
		from = istream.readUTF();
		message = istream.readUTF();

		// Place it on the screen as a dialog box
		new OrbitTextDialog(parentWindow, "Message from " + from,
			      message, 40, 10,
			      TextArea.SCROLLBARS_VERTICAL_ONLY, true);
	    }

	// Tell the user that that's all
	new OrbitInfoDialog(parentWindow, "Done", true,
			      "End of saved messages."); 
	return;
    }

    protected void receiveError()
	throws IOException
    {
	// Another user has sent an error code to us

	OrbitUser fromUser = null;
	short errorCode = 0;
	int numForUsers = 0;

	// Who was it?
	fromUser = readUser();

	// What was the error code?
	errorCode = istream.readShort();

	// Discard the recipient list, if there is one
	numForUsers = istream.readInt();
	for (int count = 0; count < numForUsers; count ++)
	    istream.readInt();
	
	if (fromUser == null)
	    // Ack.  No such user.  It can happen if someone logs out at
	    // just the right moment
	    return;

	if (isIgnoredUser(fromUser.name))
	    return;

	// Now do different things depending on the advisory type
	switch(errorCode)
	    {
	    case OrbitCommand.ERROR_NOPAGE:
		{
		    // The user is not accepting our page
		    parentWindow.messages.append("<<" + fromUser.name +
					 " is not accepting pages>>\n");
		    break;
		}
	    case OrbitCommand.ERROR_NOSOUND:
		{
		    // This client can't play sounds
		    parentWindow.messages.append("<<" + fromUser.name +
					 " has no sound capability>>\n");
		    break;
		}
	    default:
		{
		    // Or else what?
		    parentWindow.messages.append("<<Unknown error from " +
						 fromUser.name + ">>\n");
		    break;
		}
	    }
    }

    public void lostConnection()
    {
	if (!stop)
	    {
		shutdown(true);
		parentWindow.offline();
	    }
	return;
    }

    public synchronized void shutdown(boolean notifyUser)
    {
	// Shut down the reader thread
	stop = true;

	// Close my input and output data streams.  Don't do this
	// synchronized, since the client reader will be sitting there
	// blocking, waiting for data.
	try {
	    istream.close();
	}
	catch (IOException e) {}
	
	try {
	    synchronized (ostream)
		{
		    ostream.flush();
		    ostream.close();
		}

	    // close up my socket
	    socket.close();
	}
	catch (IOException e) {}

	// Empty out our user list
	userList.removeAllElements();

	// No more chat room
	parentWindow.currentRoom = null;

	// Get rid of any 'chat room list' or 'chat room control'
	// dialog boxes that might be hanging around
	if (parentWindow.roomsDialog != null)
	    {
		parentWindow.roomsDialog.dispose();
		parentWindow.roomsDialog = null;
	    }
	if (parentWindow.roomControlDialog != null)
	    {
		parentWindow.roomControlDialog.dispose();
		parentWindow.roomControlDialog = null;
	    }
	
	if (notifyUser)
	    // Make a message to the user
	    new OrbitInfoDialog(parentWindow, "Disconnected", true,
				  ("Disconnected from "
				   + parentWindow.host));

	parentWindow.theClient = null;

	// Force garbage collection, since some clients seem to hold on
	// to the connection somehow
	System.gc();

	return;
    }
}
