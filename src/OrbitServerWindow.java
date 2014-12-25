import java.awt.*;
import java.awt.event.*;
import javax.swing.ImageIcon;
import java.io.*;
import java.net.*;


public class OrbitServerWindow
    extends Frame
    implements ActionListener, ItemListener, WindowListener
{
    protected GridBagLayout myLayout;
    protected GridBagConstraints myConstraints;
    protected Label listening;
    protected List userList;
    protected Button disconnect;
    protected Button disconnectAll;
    protected Button console;
    protected Button userAdmin;
    protected Checkbox logChat;
    protected Button shutdown;
    protected TextField stats;
    protected TextArea logWindow;
    protected OrbitPictureCanvas canvas;
    protected OrbitServer myParent;
    protected OrbitWindow consoleWindow;


    public OrbitServerWindow(OrbitServer parent, String Name)
    {
	super(Name);
	myParent = parent;

	myLayout = new GridBagLayout();
	myConstraints = new GridBagConstraints();
	setLayout(myLayout);

        Color mycolor = Color.DARK_GRAY;
	setBackground(mycolor);
      
	     
        myConstraints.insets.top = 0; myConstraints.insets.bottom = 0;
	myConstraints.insets.right = 5; myConstraints.insets.left = 5;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.BOTH;

	listening = new Label("Listening on port " + myParent.port);
	myConstraints.gridwidth = 1; myConstraints.gridheight = 1;
	myConstraints.gridx = 0; myConstraints.gridy = 0;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myLayout.setConstraints(listening, myConstraints);
	add(listening);

	userList = new List(4, false);
	myConstraints.gridwidth = 1; myConstraints.gridheight = 5;
	myConstraints.gridx = 0; myConstraints.gridy = 1;
	myConstraints.weightx = 1; myConstraints.weighty = 0;
	myLayout.setConstraints(userList, myConstraints);
	add(userList);

	logChat = new Checkbox("Log chat(s)", myParent.logChats);
	logChat.addItemListener(this);
	myConstraints.gridwidth = 1; myConstraints.gridheight = 1;
	myConstraints.gridx = 1; myConstraints.gridy = 0;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myLayout.setConstraints(logChat, myConstraints);
	logChat.setEnabled(true);
	add(logChat);

	userAdmin = new Button("User management");
	userAdmin.addActionListener(this);
	myConstraints.gridwidth = 1; myConstraints.gridheight = 1;
	myConstraints.gridx = 1; myConstraints.gridy = 1;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myLayout.setConstraints(userAdmin, myConstraints);
	userAdmin.setEnabled(true);
	add(userAdmin);

	console = new Button("Administrator client");
	console.addActionListener(this);
	myConstraints.gridwidth = 1; myConstraints.gridheight = 1;
	myConstraints.gridx = 1; myConstraints.gridy = 2;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myLayout.setConstraints(console, myConstraints);
	console.setEnabled(true);
	add(console);

	disconnect = new Button("Disconnect user");
	disconnect.addActionListener(this);
	myConstraints.gridwidth = 1; myConstraints.gridheight = 1;
	myConstraints.gridx = 1; myConstraints.gridy = 3;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myLayout.setConstraints(disconnect, myConstraints);
	disconnect.setEnabled(false);
	add(disconnect);

	disconnectAll = new Button("Disconnect all");
	disconnectAll.addActionListener(this);
	myConstraints.gridwidth = 1; myConstraints.gridheight = 1;
	myConstraints.gridx = 1; myConstraints.gridy = 4;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myLayout.setConstraints(disconnectAll, myConstraints);
	disconnectAll.setEnabled(false);
	add(disconnectAll);

	shutdown = new Button("Shut down");
	shutdown.addActionListener(this);
	myConstraints.gridwidth = 1; myConstraints.gridheight = 1;
	myConstraints.gridx = 1; myConstraints.gridy = 5;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myLayout.setConstraints(shutdown, myConstraints);
	shutdown.setEnabled(true);
	add(shutdown);

	myConstraints.insets.top = 5; myConstraints.insets.bottom = 5;

	stats =
	    new TextField("Connections - current: 0  peak: 0  total: 0", 40);
	stats.setEditable(false);
	myConstraints.gridwidth = 2; myConstraints.gridheight = 1;
	myConstraints.gridx = 0; myConstraints.gridy = 7;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myLayout.setConstraints(stats, myConstraints);
	add(stats);

	logWindow = new TextArea("Server activity log:\n", 20, 40,
				 TextArea.SCROLLBARS_VERTICAL_ONLY);
	logWindow.setEditable(false);
	myConstraints.gridwidth = 2; myConstraints.gridheight = 1;
	myConstraints.gridx = 0; myConstraints.gridy = 8;
	myConstraints.weightx = 1; myConstraints.weighty = 1;
	myLayout.setConstraints(logWindow, myConstraints);
	add(logWindow);

	canvas = new OrbitPictureCanvas(this);
	myConstraints.gridwidth = 2; myConstraints.gridheight = 1; 
	myConstraints.gridx = 0; myConstraints.gridy = 9;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myLayout.setConstraints(canvas, myConstraints);
	add(canvas);

	try {
	    URL url = new URL ("file", "localhost", "OrbitPic.gif");
	    Image image = getToolkit().getImage(url);

	    canvas.setimage(image);
	} 
	catch (Exception e) { 
	    System.out.println(e);
	}

	try {
	    URL iconUrl = new URL("file", "localhost", "OrbitIcon.gif");
	    ImageIcon icon = new ImageIcon(iconUrl);
	    this.setIconImage(icon.getImage());
	}
	catch (Exception e) { /* Not important */ }

	addWindowListener(this);
    }

    
    public void updateStats()
    {
	// This just updates any statistics that are shown on the face of
	// the server window
	stats.setText("Connections - current: " +
		      myParent.currentConnections + "  peak: " +
		      myParent.peakConnections + "  total: " +
		      myParent.totalConnections);
	return;
    }


    public void actionPerformed(ActionEvent E)
    {
	if (E.getSource() == userAdmin)
	    {
		OrbitUserToolDialog userTool =
		    new OrbitUserToolDialog(this);
		return;
	    }

	if (E.getSource() == console)
	    {
		OrbitInfoDialog tmp =
		    new OrbitInfoDialog(this, "Loading", false,
			  "Starting the client, one moment please...");
		consoleWindow =
		    new OrbitWindow("Administrator", "", "localhost",
				      Integer.toString(myParent.port),
				      true, myParent.myURL);
		tmp.dispose();

		consoleWindow.adminConsole = true;
		consoleWindow.lockSettings = true;
		consoleWindow.requirePassword = false;

		// Show the window
		consoleWindow.show();

		// Connect
		consoleWindow.connect();

		return;
	    }

	if (E.getSource() == disconnect)
	    {
		String disconnectUser;

		synchronized (userList) {
		    disconnectUser = userList.getSelectedItem();
		}

		if (disconnectUser != null)
		    {
			// Loop through all of the current connections to find
			// the object that corresponds to this name
			
			for (int count = 0;
			     count < myParent.currentConnections;
			     count ++)
			    {
				OrbitClientSocket tempuser =
				    (OrbitClientSocket)
				    myParent.connections.elementAt(count);
				
				if (tempuser.user.name.equals(disconnectUser))
				    {
					myParent.disconnect(tempuser, true);
					break;
				    }
			    }
		    }
		return;
	    }

	if (E.getSource() == disconnectAll)
	    {
		myParent.disconnectAll(true);
		return;
	    }

	if (E.getSource() == shutdown)
	    {
		if (myParent.currentConnections > 0)
		    new OrbitServerShutdownDialog(this, myParent);
		else
		    myParent.shutdown();
		return;
	    }
    }

    public void itemStateChanged(ItemEvent E)
    {
	// The 'log chat' checkbox
	if (E.getSource() == logChat)
	    {
		myParent.logChats = logChat.getState();
		
		// Loop through all of the chat rooms, and set the logging
		// state to be the same as the value of the checkbox
		for (int count = 0; count < myParent.chatRooms.size();
		     count ++)
		    {
			OrbitChatRoom tmp = (OrbitChatRoom)
			    myParent.chatRooms.elementAt(count);
			try {
			    tmp.setLogging(myParent.logChats);
			}
			catch (IOException e) {
			    myParent
				.serverOutput("Unable to toggle logging " +
					      "for chat room " + tmp.name +
					      "\n");
			}
		    }
	    }
    }

    public void windowActivated(WindowEvent E)
    {
    }

    public void windowClosed(WindowEvent E)
    {
    }

    public void windowClosing(WindowEvent E)
    {
	if (myParent.currentConnections > 0)
	    new OrbitServerShutdownDialog(this, myParent);
	else
	    myParent.shutdown();
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


class OrbitServerShutdownDialog
    extends Dialog
    implements ActionListener, KeyListener, WindowListener
{
    protected OrbitServerWindow myParent;
    protected OrbitServer theServer;

    protected Label message1;
    protected Label message2;
    protected Button yes;
    protected Button cancel;
    protected GridBagLayout myLayout;
    protected GridBagConstraints myConstraints;

    public OrbitServerShutdownDialog(OrbitServerWindow serverwindow,
				       OrbitServer server)
    {
	super(serverwindow, "Server shutdown", true);
	myParent = serverwindow;
	theServer = server;

	myLayout = new GridBagLayout();
	myConstraints = new GridBagConstraints();

	setLayout(myLayout);

	myConstraints.insets = new Insets(0, 5, 0, 5);
	myConstraints.weightx = 1.0; myConstraints.weighty = 1.0; 

	message1 = new Label("Are you sure you want to disconnect");
	myConstraints.gridwidth = 2; myConstraints.gridheight = 1;
	myConstraints.gridx = 0; myConstraints.gridy = 0;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.NONE;
	myLayout.setConstraints(message1, myConstraints);
	add(message1);

	message2 = new Label("all users and shut down the server?");
	myConstraints.gridx = 0; myConstraints.gridy = 1;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 5;
	myLayout.setConstraints(message2, myConstraints);
	add(message2);

	myConstraints.fill = myConstraints.NONE;

	yes = new Button("Yes");
	myConstraints.gridwidth = 1; myConstraints.gridheight = 1;
	myConstraints.gridx = 0; myConstraints.gridy = 2;
	myConstraints.anchor = myConstraints.EAST;
	myConstraints.insets.left = 0; myConstraints.insets.right = 0;
	myLayout.setConstraints(yes, myConstraints);
	yes.addKeyListener(this);
	yes.addActionListener(this);
	add(yes);

	cancel = new Button("Cancel");
	myConstraints.gridwidth = 1; myConstraints.gridheight = 1;
	myConstraints.gridx = 1; myConstraints.gridy = 2;
	myConstraints.anchor = myConstraints.WEST;
	myLayout.setConstraints(cancel, myConstraints);
	cancel.addKeyListener(this);
	cancel.addActionListener(this);
	add(cancel);

	setBackground(Color.LIGHT_GRAY);
	pack();
	setResizable(false);

	// Center it in the middle of the server window.
	setLocation((((((myParent.getBounds()).width) - 
		       ((getSize()).width)) / 2)
		     + ((myParent.getLocation()).x)),
		    (((((myParent.getBounds()).height) - 
		       ((getSize()).height)) / 2)
		     + ((myParent.getLocation()).y)));

	addKeyListener(this);
	addWindowListener(this);
	setVisible(true);
	yes.requestFocus();
    }

    public void actionPerformed(ActionEvent E)
    {
	if (E.getSource() == yes)
	    {
		dispose();
		theServer.shutdown();
		return;
	    }

	else if (E.getSource() == cancel)
	    {
		dispose();
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
		if (E.getSource() == yes)
		    {
			dispose();
			theServer.shutdown();
			return;
		    }

		else if (E.getSource() == cancel)
		    {
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


class OrbitPictureCanvas
    extends Canvas
{

    private OrbitServerWindow main;
    private Image image;
    
    public OrbitPictureCanvas(OrbitServerWindow mainwindow)
    {
	super();
	main = mainwindow;
	setBackground(Color.lightGray);
	setSize(200, 75);
	repaint();
	setVisible(true);
    }

    public void paint(Graphics g)
    {
	if (image != null)
	    {
		g.drawImage(image, 0, 0, getSize().width, getSize().height, 
			    this);
	    }
    }

    public void setimage(Image theimage)
    {
	image = theimage;
	repaint();
    }
}
