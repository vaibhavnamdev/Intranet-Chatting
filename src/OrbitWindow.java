import java.awt.*;
import java.awt.event.*;
import javax.swing.ImageIcon;
import java.awt.datatransfer.*;
import java.io.*;
import java.net.*;
import javax.swing.JLabel;


public class OrbitWindow
    extends Frame
    implements ActionListener, ItemListener, KeyListener, MouseListener, 
	       WindowListener, ClipboardOwner
{
    public int id = 0;
    public String name = "";
    public String plainPassword = "";
    public String encryptedPassword = "";
    public String host = "localhost";
    public String port = "12468";
    public int portNumber = 12468;
    public String additional = "";
    protected OrbitRoomInfo currentRoom;
    protected OrbitRoomInfo[] roomInfoArray;
    protected boolean lockSettings = false;
    protected boolean requirePassword = true;
    public int drawFontNumber;
    public int drawStyle;
    public int drawSize;
    public String drawText;
    public URL OrbitURL;
    protected String buffer;
    protected int overallSizex;
    protected int overallSizey;

    public GridBagLayout myLayout = new GridBagLayout();
    public GridBagConstraints myConstraints = new GridBagConstraints();
    static final Font XsmallFont = new Font("Helvetica", Font.PLAIN, 10);
    static final Font smallFont = new Font("Helvetica", Font.PLAIN, 12);
    static final Font largeFont = new Font("Helvetica", Font.PLAIN, 14);
    static final Font XlargeFont = new Font("Helvetica", Font.PLAIN, 16);

    protected OrbitPasswordEncryptor passwordEncryptor = null;

    protected OrbitRoomsDialog roomsDialog;
    protected OrbitRoomControl roomControlDialog;

    // socket stuff

    public OrbitClient theClient;
    protected boolean connected;

    // If we're operating as an applet
    protected OrbitApplet thisApplet = null;

    // If this is an administrator console (setting this to true won't
    // grant special privileges as far as the server is concerned -- it's
    // only for the benefit of this window so don't bother trying ;-)
    protected boolean adminConsole = false;

    // the menu items

    protected MenuItem menuConnect;
    protected MenuItem menuDisconnect;
    protected MenuItem menuSaveText;
    protected MenuItem menuBuggerOff;
    protected Menu fileMenu;
    protected MenuItem menuCopy;
    protected MenuItem menuPaste;
    protected MenuItem menuPastePictureFile;
    protected MenuItem menuSavePicture;
    protected Menu editMenu; 
    protected MenuItem menuPage;
    protected MenuItem menuMessaging;
    protected MenuItem menuClear;
    protected MenuItem menuIgnore;
    protected Menu actionsMenu;
    protected MenuItem menuSettings;
    protected MenuItem menuChatRooms;
    protected MenuItem menuChatRoomControl;
    protected MenuItem menuUserInfo;
    protected CheckboxMenuItem menuPlaySound;
    protected CheckboxMenuItem menuShowCanvas;
    protected Menu viewMenu;
    protected MenuItem menuManual;
    protected MenuItem menuAbout;
    protected Menu helpMenu;
 
    // Left side
    protected Label MadeBy1;
    protected Label sendLineLabel;
    protected Label conferenceLabel;
    protected TextArea typed;
    protected TextArea messages;
    protected Label drawCanvasLabel;
    public OrbitCanvas canvas;

    // Right side

    protected Label MadeBy;
    protected Label nameLabel;
    public TextField userId;
    protected Label activityLabel;
    protected TextField activity;
    protected Label sendToLabel;
    protected Checkbox sendToAll;
    public java.awt.List sendTo;
    protected Button whosThis;
    protected Button page;
    protected Button chatRooms;
    protected Button messaging;
    protected Label drawingControlsLabel;
    protected Choice colorChoice;
    protected Choice thickness;
    protected Choice fillType;
    protected Checkbox freehand;
    protected Checkbox line;
    protected Checkbox rectangle;
    protected Checkbox oval;
    protected Checkbox text;
    protected CheckboxGroup drawType;
    protected Button clearCanvas;
    protected JLabel label1;
    // set up

    public OrbitWindow(String userName, String userPassword,
			 String hostName, String portName,
			 boolean isCanvas, URL myURL)
    {
	super();

	// Set the username, host, and port values if they've been specified
	if (userName != null)
	    if (!userName.equals(""))
		name = userName;
	if (userPassword != null)
	    if (!userPassword.equals(""))
		// This password is still unencrypted
		plainPassword = userPassword;
	if (hostName != null)
	    if (!hostName.equals(""))
		host = hostName;
	if (portName != null)
	    if (!portName.equals(""))
		port = portName;

	// Get the URL of the current directory, so that we can find the
	// rest of our files.
	OrbitURL = myURL;

	// set background color
	Color mycolor = Color.DARK_GRAY;
	setBackground(mycolor);
      
	// set default font
	drawFontNumber = 0;
	drawStyle = Font.PLAIN;
	drawSize = 10;

	overallSizex = 600;
	overallSizey = 500;
	setSize(overallSizex, overallSizey);
	myConstraints.fill = myConstraints.BOTH;

	// set up all of the window crap

	setLayout(myLayout);
	myConstraints.insets = new Insets(0, 5, 0, 5);

	// the menu bar

	menuConnect = new MenuItem("Connect");
	menuConnect.addActionListener(this);
	menuConnect.setEnabled(true);

	menuDisconnect = new MenuItem("Disconnect");
	menuDisconnect.addActionListener(this);
	menuDisconnect.setEnabled(false);

	menuSaveText = new MenuItem("Save chat as...");
	menuSaveText.addActionListener(this);
	menuSaveText.setEnabled(true);

	menuBuggerOff = new MenuItem("Exit");
	menuBuggerOff.addActionListener(this);
	menuBuggerOff.setEnabled(true);

	fileMenu = new Menu("File");
	fileMenu.add(menuConnect);
	fileMenu.add(menuDisconnect);
	fileMenu.add(menuSaveText);
	fileMenu.add(menuBuggerOff);

	menuCopy = new MenuItem("Copy text");
	menuCopy.addActionListener(this);
	menuCopy.setEnabled(true);

	menuPaste = new MenuItem("Paste text");
	menuPaste.addActionListener(this);
	menuPaste.setEnabled(false);

	menuPastePictureFile = new MenuItem("Paste picture from file");
	menuPastePictureFile.addActionListener(this);
	menuPastePictureFile.setEnabled(true);

	menuSavePicture = new MenuItem("Save whiteboard as file");
	menuSavePicture.addActionListener(this);
	menuSavePicture.setEnabled(false);

	editMenu = new Menu("Edit");
	editMenu.add(menuCopy);
	editMenu.add(menuPaste);
	editMenu.add(menuPastePictureFile);
	editMenu.add(menuSavePicture);

	menuPage = new MenuItem("Page user(s)");
	menuPage.addActionListener(this);
	menuPage.setEnabled(false);

	menuMessaging = new MenuItem("Messaging...");
	menuMessaging.addActionListener(this);
	menuMessaging.setEnabled(false);

	menuClear = new MenuItem("Clear canvas");
	menuClear.addActionListener(this);
	menuClear.setEnabled(true);

	menuIgnore = new MenuItem("Ignore user(s)");
	menuIgnore.addActionListener(this);
	menuIgnore.setEnabled(true);

	actionsMenu = new Menu("Actions");
	actionsMenu.add(menuPage);
	actionsMenu.add(menuMessaging);
	actionsMenu.add(menuClear);
	actionsMenu.add(menuIgnore);

	menuSettings = new MenuItem("Connection settings");
	menuSettings.addActionListener(this);
	menuSettings.setEnabled(true);

	menuChatRooms = new MenuItem("Chat rooms...");
	menuChatRooms.addActionListener(this);
	menuChatRooms.setEnabled(false);

	menuChatRoomControl = new MenuItem("Chat room control panel...");
	menuChatRoomControl.addActionListener(this);
	menuChatRoomControl.setEnabled(false);

	menuUserInfo = new MenuItem("User information");
	menuUserInfo.addActionListener(this);
	menuUserInfo.setEnabled(false);

	menuPlaySound = new CheckboxMenuItem("Play sound when paged");
	menuPlaySound.addItemListener(this);
	menuPlaySound.setEnabled(true);
	menuPlaySound.setState(true);

	menuShowCanvas = new CheckboxMenuItem("Show drawing canvas");
	menuShowCanvas.addItemListener(this);
	menuShowCanvas.setEnabled(true);
	menuShowCanvas.setState(isCanvas);

	viewMenu = new Menu("View");
	viewMenu.add(menuSettings);
	viewMenu.add(menuChatRooms);
	viewMenu.add(menuChatRoomControl);
	viewMenu.add(menuUserInfo);
	viewMenu.add(menuPlaySound);
	viewMenu.add(menuShowCanvas);

	menuManual = new MenuItem("Manual");
	menuManual.addActionListener(this);
	menuManual.setEnabled(true);

	menuAbout = new MenuItem("About Intranet Chatting");
	menuAbout.addActionListener(this);
	menuAbout.setEnabled(true);

	helpMenu = new Menu("Help");
	helpMenu.add(menuManual);
	helpMenu.add(menuAbout);

	MenuBar menubar = new MenuBar();
	menubar.add(fileMenu);
	menubar.add(editMenu);
	menubar.add(actionsMenu);
	menubar.add(viewMenu);
	menubar.add(helpMenu);
	menubar.setHelpMenu(helpMenu);
	setMenuBar(menubar);

	sendLineLabel = new Label("Text lines to send:");
	sendLineLabel.setFont(smallFont);
        sendLineLabel.setForeground(Color.WHITE);
	myConstraints.gridx = 0; myConstraints.gridy =14;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myLayout.setConstraints(sendLineLabel, myConstraints);
	add(sendLineLabel);

	typed = new TextArea("", 2, 50, TextArea.SCROLLBARS_VERTICAL_ONLY);
	typed.setEditable(true);
	typed.setFont(largeFont);
	typed.addKeyListener(this);
	myConstraints.gridx = 0; myConstraints.gridy = 15;
	myConstraints.gridheight = 3; myConstraints.gridwidth = 1;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.weightx = 1.0; myConstraints.weighty = 0.0;
	myLayout.setConstraints(typed, myConstraints);
	add(typed);

	conferenceLabel = new Label("Conference text:");
	conferenceLabel.setFont(largeFont);
        conferenceLabel.setForeground(Color.WHITE);
        myConstraints.gridx = 0; myConstraints.gridy = 0;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myLayout.setConstraints(conferenceLabel, myConstraints);
	add(conferenceLabel);

	messages = new TextArea("", 10, 50,TextArea.SCROLLBARS_VERTICAL_ONLY);
	messages.setEditable(false);
	messages.setFont(smallFont);
	myConstraints.gridx = 0; myConstraints.gridy = 1;
	myConstraints.gridheight = 13; myConstraints.gridwidth = 1;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.insets.top = 5; myConstraints.insets.bottom = 5;
	myConstraints.weightx = 1.0; myConstraints.weighty = 1.0;
	myLayout.setConstraints(messages, myConstraints);
	add(messages);

	drawCanvasLabel = new Label("Drawing Area:");
	drawCanvasLabel.setForeground(Color.WHITE);
        myConstraints.gridx =3 ; myConstraints.gridy = 0;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 5;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myLayout.setConstraints(drawCanvasLabel, myConstraints);
	add(drawCanvasLabel);

	canvas = new OrbitCanvas(this);
	myConstraints.gridx = 3; myConstraints.gridy = 1;
	myConstraints.gridheight = 17; myConstraints.gridwidth = 1;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 5;
	myConstraints.insets.right = 5; myConstraints.insets.left = 5;
	myConstraints.weightx = 1.0; myConstraints.weighty = 2.0;
	myLayout.setConstraints(canvas, myConstraints);
	add(canvas);

	myConstraints.insets.top = 0; myConstraints.insets.bottom = 0;

	nameLabel = new Label("User name:");
	nameLabel.setFont(smallFont);
	nameLabel.setForeground(Color.WHITE);
        myConstraints.gridx = 1; myConstraints.gridy = 0;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 5; myConstraints.insets.left = 0;
	myLayout.setConstraints(nameLabel, myConstraints);
	add(nameLabel);

	userId = new TextField(name);
	userId.setFont(smallFont);
	userId.setEditable(false);
	myConstraints.gridx = 1; myConstraints.gridy = 1;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 5; myConstraints.insets.left = 0;
	myLayout.setConstraints(userId, myConstraints);
	add(userId);

	activityLabel = new Label("Current activity:");
	activityLabel.setFont(smallFont);
	activityLabel.setForeground(Color.WHITE);
        myConstraints.gridx = 1; myConstraints.gridy = 2;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 5; myConstraints.insets.left = 0;
	myLayout.setConstraints(activityLabel, myConstraints);
	add(activityLabel);

	activity = new TextField();
	activity.setEditable(false);
	activity.setFont(smallFont);
        myConstraints.gridx = 1; myConstraints.gridy = 3;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.anchor = myConstraints.CENTER;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 5; myConstraints.insets.left = 0;
	myLayout.setConstraints(activity, myConstraints);
	add(activity);

	sendToLabel = new Label("Currently sending to:");
	sendToLabel.setFont(smallFont);
	sendToLabel.setForeground(Color.WHITE);
        myConstraints.gridx = 1; myConstraints.gridy = 4;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 5; myConstraints.insets.left = 0;
	myLayout.setConstraints(sendToLabel, myConstraints);
	add(sendToLabel);

	sendTo = new java.awt.List(4);
	sendTo.setFont(XsmallFont);
	sendTo.addItemListener(this);
	sendTo.setMultipleMode(true);
	myConstraints.gridx = 1; myConstraints.gridy = 5;
	myConstraints.gridheight = 6; myConstraints.gridwidth = 2;
	myConstraints.anchor = myConstraints.CENTER;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.weightx = 0.0; myConstraints.weighty = 1.0;
	myConstraints.insets.right = 5; myConstraints.insets.left = 0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 0;
	myLayout.setConstraints(sendTo, myConstraints);
	add(sendTo);

	sendToAll = new Checkbox("send to everyone", true);
	sendToAll.setFont(XsmallFont);
	sendToAll.setForeground(Color.WHITE);
        sendToAll.addItemListener(this);
	myConstraints.gridx = 1; myConstraints.gridy = 11;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 6;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.NONE;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 5; myConstraints.insets.left = 0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 0;
	myLayout.setConstraints(sendToAll, myConstraints);
	add(sendToAll);

	whosThis = new Button("User information");
	whosThis.setFont(XsmallFont);
	whosThis.addActionListener(this);
	whosThis.setEnabled(false);
	myConstraints.gridx = 1; myConstraints.gridy = 7;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.anchor = myConstraints.CENTER;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 5; myConstraints.insets.left = 0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 0;
	myLayout.setConstraints(whosThis, myConstraints);
	add(whosThis);

	page = new Button("Page user(s)");
	page.setFont(XsmallFont);
	page.addActionListener(this);
	page.setEnabled(false);
	myConstraints.gridx = 1; myConstraints.gridy = 8;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.anchor = myConstraints.CENTER;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 5; myConstraints.insets.left = 0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 0;
	myLayout.setConstraints(page, myConstraints);
	add(page);

	chatRooms = new Button("Chat rooms...");
	chatRooms.setFont(XsmallFont);
	chatRooms.addActionListener(this);
	chatRooms.setEnabled(false);
	myConstraints.gridx = 1; myConstraints.gridy = 9;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.anchor = myConstraints.CENTER;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 5; myConstraints.insets.left = 0;
	myLayout.setConstraints(chatRooms, myConstraints);
	add(chatRooms);

	messaging = new Button("Messaging...");
	messaging.setFont(XsmallFont);
	messaging.addActionListener(this);
	messaging.setEnabled(false);
	myConstraints.gridx = 1; myConstraints.gridy = 10;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.anchor = myConstraints.CENTER;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 5;
	myConstraints.insets.right = 5; myConstraints.insets.left = 0;
	myLayout.setConstraints(messaging, myConstraints);
	add(messaging);

	drawingControlsLabel = new Label("Drawing controls:");
	drawingControlsLabel.setFont(smallFont);
	myConstraints.gridx = 1; myConstraints.gridy = 11;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 5;
	myConstraints.insets.right = 5; myConstraints.insets.left = 0;
	myLayout.setConstraints(drawingControlsLabel, myConstraints);
	add(drawingControlsLabel);

	drawType = new CheckboxGroup();

	freehand = new Checkbox("Freehand", drawType, true);
	freehand.setFont(smallFont);
	freehand.addItemListener(this);
	myConstraints.gridx = 1; myConstraints.gridy = 12;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.NONE;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 0; myConstraints.insets.left = 0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 0;
	myLayout.setConstraints(freehand, myConstraints);
	add(freehand);

	line = new Checkbox("Line", drawType, false);
	line.setFont(smallFont);
	line.addItemListener(this);
	myConstraints.gridx = 2; myConstraints.gridy = 12;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.NONE;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 5; myConstraints.insets.left = 0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 0;
	myLayout.setConstraints(line, myConstraints);
	add(line);

	rectangle = new Checkbox("Rectangle", drawType, false);
	rectangle.setFont(smallFont);
	rectangle.addItemListener(this);
	myConstraints.gridx = 1; myConstraints.gridy = 13;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.NONE;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 0; myConstraints.insets.left = 0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 0;
	myLayout.setConstraints(rectangle, myConstraints);
	add(rectangle);

	oval = new Checkbox("Oval", drawType, false);
	oval.setFont(smallFont);
	oval.addItemListener(this);
	myConstraints.gridx = 2; myConstraints.gridy = 13;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.NONE;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 5; myConstraints.insets.left = 0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 0;
	myLayout.setConstraints(oval, myConstraints);
	add(oval);

	text = new Checkbox("Text", drawType, false);
	text.setFont(smallFont);
	text.addItemListener(this);
	myConstraints.gridx = 1; myConstraints.gridy = 14;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.NONE;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 0; myConstraints.insets.left = 0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 5;
	myLayout.setConstraints(text, myConstraints);
	add(text);
            
	colorChoice = new Choice();
	colorChoice.setFont(smallFont);
	colorChoice.addItemListener(this);
	colorChoice.addItem("black");
	colorChoice.addItem("blue");
	colorChoice.addItem("cyan");
	colorChoice.addItem("dark gray");
	colorChoice.addItem("gray");
	colorChoice.addItem("green");
	colorChoice.addItem("light gray");
	colorChoice.addItem("magenta");
	colorChoice.addItem("orange");
	colorChoice.addItem("pink");
	colorChoice.addItem("red");
	colorChoice.addItem("white");
	colorChoice.addItem("yellow");
	myConstraints.gridx = 1; myConstraints.gridy = 15;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.HORIZONTAL;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 5; myConstraints.insets.left = 0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 5;
	myLayout.setConstraints(colorChoice, myConstraints);
	add(colorChoice);

	thickness = new Choice();
	thickness.setFont(smallFont);
	thickness.addItemListener(this);
	for (int count = 1; count < 10; count ++)
	    thickness.addItem("thickness: " + count);
	myConstraints.gridx = 1; myConstraints.gridy = 16;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.HORIZONTAL;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 5; myConstraints.insets.left = 0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 5;
	myLayout.setConstraints(thickness, myConstraints);
	add(thickness);
	thickness.setEnabled(true);

	fillType = new Choice();
	fillType.setFont(smallFont);
	fillType.addItemListener(this);
	fillType.addItem("outlined");
	fillType.addItem("filled");
	myConstraints.gridx = 1; myConstraints.gridy = 17;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.HORIZONTAL;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 5; myConstraints.insets.left = 0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 5;
	myLayout.setConstraints(fillType, myConstraints);
	add(fillType);
	fillType.setEnabled(false);

	clearCanvas = new Button("Clear canvas");
	clearCanvas.setFont(smallFont);
	clearCanvas.addActionListener(this);
	clearCanvas.setEnabled(true);
	myConstraints.gridx = 1; myConstraints.gridy = 18;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.anchor = myConstraints.SOUTHWEST;
	myConstraints.fill = myConstraints.HORIZONTAL;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myConstraints.insets.right = 5; myConstraints.insets.left = 0;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 5;
	myLayout.setConstraints(clearCanvas, myConstraints);
	add(clearCanvas);
        
        MadeBy1 = new Label("SWAMI VIVEKANAND COLLEGE OF ENGINEERING");
        MadeBy1.setFont(XsmallFont);
        MadeBy1.setForeground(Color.WHITE);
	myConstraints.gridx = 0; myConstraints.gridy =18;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myLayout.setConstraints(MadeBy1, myConstraints);
	add(MadeBy1);
        
        MadeBy = new Label("                        Made By: VAIBHAV NAMDEV,MAMTA BHAMRE & BHAVNA KUSHWAH");
	MadeBy.setFont(XsmallFont);
        MadeBy.setForeground(Color.WHITE);
	myConstraints.gridx = 3; myConstraints.gridy =18;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.weightx = 0.0; myConstraints.weighty = 0.0;
	myLayout.setConstraints(MadeBy, myConstraints);
	add(MadeBy);

	
        // register to receive the various events
	addKeyListener(this);
	addMouseListener(this);
	addWindowListener(this);
	
	// Show the drawing canvas or not?
	showCanvas(isCanvas);
	
	// show the window and get going
	pack();
	offline();
	typed.requestFocus();

	try {
	    URL imageUrl =
		new URL(OrbitURL.getProtocol(), OrbitURL.getHost(),
		OrbitURL.getFile() + "OrbitPic.gif");
	    Image image = getToolkit().getImage(imageUrl);
	    canvas.setImage(image);
	}
	catch (Exception e) { 
	    e.printStackTrace();
	}

	// Create our password encryptor and excrupt our password, if
	// applicable
	passwordEncryptor = new OrbitPasswordEncryptor();
	encryptedPassword = passwordEncryptor.encryptPassword(plainPassword);
    }

    

    public void online()
    {
	connected = true;
	menuConnect.setEnabled(false);
	menuDisconnect.setEnabled(true);
	chatRooms.setEnabled(false);
	menuChatRooms.setEnabled(false);
	page.setEnabled(false);
	menuMessaging.setEnabled(false);
	messaging.setEnabled(false);
	menuPage.setEnabled(false);
	if (adminConsole)
	    roomOwner(true);
	setTitle("Intranet Chatting - online at " + host);
	return;
    }

    public void offline()
    {
	connected = false;
	menuConnect.setEnabled(true);
	menuDisconnect.setEnabled(false);
	chatRooms.setEnabled(false);
	menuChatRooms.setEnabled(false);
	page.setEnabled(false);
	menuIgnore.setEnabled(false);
	whosThis.setEnabled(false);
	menuMessaging.setEnabled(false);
	messaging.setEnabled(false);
	menuPage.setEnabled(false);
	menuUserInfo.setEnabled(false);
	if (sendTo.getItemCount() > 0)
	    sendTo.removeAll();
	sendToAll.setState(true);
	currentRoom = new OrbitRoomInfo();
	roomOwner(false);
	setTitle("Intranet Chatting  - offline");
	return;
    }

    protected boolean haveRequiredInformation()
    {
	// Do we have everything we need to connect to the server?
	if (name.equals("") ||
	    (requirePassword && encryptedPassword.equals("")) ||
	    host.equals("") ||
	    port.equals(""))
	    return (false);
	else
	    return (true);
    }

    protected void connect()
    {
	// Have all the settings been entered?
	if (!haveRequiredInformation())
	    {
		// Give the user a chance to enter it
		new OrbitSettings(this);
		
		// Do we have it NOW?
		if (!haveRequiredInformation())
		    {
			new OrbitInfoDialog(this, "Connection canceled",
					      true,
					      "You are missing some " +
					      "required information!");
			return;
		    }
	    }

	canvas.clear();
	synchronized (messages)
	    {
		messages.setText("");
	    }

	// open up my socket
	try {
	    portNumber = Integer.parseInt(port);
	}
	catch (NumberFormatException n) {;}

	try {
	    theClient = new OrbitClient(host, name, portNumber, this);
	}
	catch (UnknownHostException a) {
	    new OrbitInfoDialog(this, "Couldn't connect", true,
				  "Couldn't find the server " + host);
	    return;
	}
	catch (IOException b) {
	    new OrbitInfoDialog(this, "Couldn't connect", true,
				  "Couldn't connect to port " + portNumber
				  + " on server " + host);
	    return;
	}
	catch (Exception c)
	    {
		// Hmm, some other connection error.  Try to make the error
		// dialog helpful at least for debugging
		
		String errorString =
		    "Java exception while attempting connection:\n";
		errorString += c.toString() + "\n";
		
		// Is this an applet?
		if (thisApplet != null)
		    errorString += "\n(Note that an unsigned applet can only "
			+ "connect to the same server from whence it came)\n";

		errorString += "\nIf you think this is a bug, please send a "
		    + "bug report to jamesamc@yahoo.com\n";

		new OrbitTextDialog(this, "Couldn't connect",
				      errorString, 60, 15,
				      TextArea.SCROLLBARS_VERTICAL_ONLY,
				      true);
		return;
	    }
	
	online();
	return;
    }

    protected synchronized void disconnect()
    {
	if (theClient != null)
	    {
		// Tell the server
		try {
		    theClient.sendDisconnect();
		    theClient.shutdown(false);
		}
		catch (IOException e) {
		    theClient.lostConnection();
		}
	    }
	theClient = null;
	offline();
	return;
    }

    protected void setApplet(OrbitApplet ap)
    {
	// This should get called when we're being used as an applet
	
	// Save a reference to the applet
	thisApplet = ap;

	// Disable features that aren't supported for applets
	menuSaveText.setEnabled(false);
	menuPastePictureFile.setEnabled(false);
	menuSavePicture.setEnabled(false);
    }

    protected void roomOwner(boolean owner)
    {
	if (owner)
	    {
		if (currentRoom != null)
		    currentRoom.roomOwner = true;
		menuChatRoomControl.setEnabled(true);
	    }

	else if (!adminConsole)
	    {
		if (currentRoom != null)
		    currentRoom.roomOwner = false;
		menuChatRoomControl.setEnabled(false);
		if (roomControlDialog != null)
		    {
			roomControlDialog.dispose();
			roomControlDialog = null;
		    }
	    }
    }

    protected void showCanvas(boolean state)
    {
	drawCanvasLabel.setVisible(state);
	canvas.setVisible(state);
	drawingControlsLabel.setVisible(state);
	clearCanvas.setVisible(state);
	colorChoice.setVisible(state);
	thickness.setVisible(state);
	fillType.setVisible(state);
	freehand.setVisible(state);
	line.setVisible(state);
	oval.setVisible(state);
	rectangle.setVisible(state);
	text.setVisible(state);
	menuClear.setEnabled(state);
			
	if (state)
	    {
		setSize((getSize()).width + 1,
			(getSize()).height + 1);
	    }
	else
	    {
		messages.setSize((messages.getSize()).width, 
				 (messages.getSize()).height);
		setSize((getSize()).width + 1, 
			(getSize()).height + 1);
	    }
	
	pack();
	return;
    }

    protected void showUserInfo()
    {
	// This will show info about another user in a text dialog
	
	String message = "";
	String[] selectedUsers;

	selectedUsers = sendTo.getSelectedItems();

	// Loop for each user that's selected
	for (int count1 = 0; count1 < selectedUsers.length; count1 ++)
	    {
		// Find this user in our user list
		for (int count2 = 0; count2 < theClient.userList.size();
		     count2++)
		    {
			OrbitUser tmp =
			    (OrbitUser) theClient.userList.elementAt(count2);

			if (selectedUsers[count1].equals(tmp.name))
			    {
				// Here's one.
				message = "Login name:\t" + tmp.name
				    + "\nAdditional info:\n\n"
				    + tmp.additional;

				new OrbitTextDialog(this,
				      "User information for " + tmp.name, 
				      message, 40, 10,
				      TextArea.SCROLLBARS_VERTICAL_ONLY,
				      false);
			    }
		    }
	    }
    }

    protected void saveText()
    {
	// Save the chat text as a file.

	File textFile = null;
	FileOutputStream fileStream = null;

	// Fire up a file dialog to let the user choose the file location
	FileDialog saveTextDialog =
	    new FileDialog(this, "Save chat text as...", 
			   FileDialog.SAVE);
	saveTextDialog.show();

	// Try to create the file
	try {
	    textFile = new File(saveTextDialog.getDirectory() +
			       saveTextDialog.getFile());
	    fileStream = new FileOutputStream(textFile);
	    byte[] bytes = messages.getText().getBytes();
	    fileStream.write(bytes);
	} 
	catch (IOException F) { 
	    new OrbitInfoDialog(this, "Failed", true,
				  "Can't write to that file");
	    return;
	}
    }

    protected void pastePictureFile()
    {
	File pictureFile = null;
	
	FileDialog getPictureDialog =
	    new FileDialog(this, "Paste picture to whiteboard...", 
			   FileDialog.LOAD);
	getPictureDialog.show();
	try {
	    pictureFile = new File(getPictureDialog.getDirectory() +
				   getPictureDialog.getFile());
	}
	catch (NullPointerException e)
	    {
		// The user didn't choose anything
		return;
	    }
	
	if (pictureFile == null)
	    {
		// ???
		new OrbitInfoDialog(this, "Failed", true,
				      "Can't open that file");
		return;
	    }
	
	// Are we allowed to read this file?
	if (!pictureFile.canRead())
	    {
		// Not allowed to read the file
		new OrbitInfoDialog(this, "Permission denied", true,
			      "You don't have permission to read that file");
		return;
	    }
	
	canvas.floatPicture(pictureFile);
	return;
    }

    protected void savePictureFile()
    {
	// Get the Image from the canvas.  No way to do this until Java 1.4
	
    }

    public void actionPerformed(ActionEvent E)
    {
	// the menu items

	if (E.getSource() == menuConnect)
	    {
		connect();
		return;
	    }

	if (E.getSource() == menuDisconnect)
	    {
		disconnect();
		return;
	    }
    
	if (E.getSource() == menuSaveText)
	    {
		saveText();
		return;
	    }
    
	if (E.getSource() == menuBuggerOff)
	    {
		if (connected == true)
		    disconnect();
		dispose();

		if (thisApplet != null)
		    thisApplet.destroy();
		else if (!adminConsole)
		    System.exit(0);

		return;
	    }

	if (E.getSource() == menuCopy)
	    {
		buffer = messages.getSelectedText();
		menuPaste.setEnabled(true);
		return;
	    }

	if (E.getSource() == menuPaste)
	    {
		typed.setText(buffer);
		return;
	    }

	if (E.getSource() == menuPastePictureFile)
	    {
		pastePictureFile();
		return;
	    }

	if (E.getSource() == menuSavePicture)
	    {
		savePictureFile();
		return;
	    }

	// the 'page users' menu item
	if (E.getSource() == menuPage)
	    {
		try {
		    theClient.sendPageUser();
		}
		catch (IOException e) {
		    theClient.lostConnection();
		    return;
		}
		return;
	    }

	// the 'messaging' menu item
	if (E.getSource() == menuMessaging)
	    {
		new OrbitMessagingDialog(this);
		return;
	    }

	// the 'clear canvas' menu item
	if (E.getSource() == menuClear)
	    {
		canvas.clear();
		if (connected)
		    try {
			theClient.sendClearCanv();
		    }
		    catch (IOException e) {
			theClient.lostConnection();
			return;
		    }
		return;
	    }

	// The 'ignore user(s)' menu item
	if (E.getSource() == menuIgnore)
	    {
		theClient.addToIgnored();
		return;
	    }

	// the 'settings' menu item
	if (E.getSource() == menuSettings)
	    {
		new OrbitSettings(this);
		return;
	    }

	// the 'chat rooms' menu item
	if (E.getSource() == menuChatRooms)
	    {
		if (roomsDialog != null)
		    roomsDialog.dispose();
		roomsDialog = new OrbitRoomsDialog(this);
		return;
	    }

	// the 'chat room control panel' menu item
	if (E.getSource() == menuChatRoomControl)
	    {
		if (roomControlDialog != null)
		    roomControlDialog.dispose();
		roomControlDialog = new OrbitRoomControl(this);
		return;
	    }

	// the 'who is' menu item
	if (E.getSource() == menuUserInfo)
	    {
		showUserInfo();
		return;
	    }

	if (E.getSource() == menuManual)
	    {
		try {
		    URL manualURL = new URL(OrbitURL.getProtocol(),
				    OrbitURL.getHost(),
				    OrbitURL.getFile() + "MANUAL.TXT");

		    BufferedReader in = new BufferedReader(
                                        new InputStreamReader(
					manualURL.openStream()));

		    String inputLine = new String("");
		    String input = new String("");

		    while ((inputLine = in.readLine()) != null) 
			input = input.concat(inputLine + "\n");
		    in.close();

		    new OrbitTextDialog(this, "Orbit Online Manual", 
				  input, 65, 25,
				  TextArea.SCROLLBARS_VERTICAL_ONLY, false);
		    return;
		}
		catch (IOException G) {
		    new OrbitInfoDialog(this, "Data not available", 
			  true,
			  "The MANUAL.TXT file could not be read!");
		    return;
		}
	    }

	if (E.getSource() == menuAbout)
	    {
String abouttext = new String("Intranet Chatting 'Swami Vivekanand College of Engginering'\nGUIDED BY: Prof. Brajesh Chaturvedi\n\nCopyright to:\nVAIBHAV NAMDEV,\nMAMTA BHAMRE,\nBHAVNA KUSHWAH\n\n");
		
		new OrbitTextDialog(this, "About Intranet Chatting", 
				      abouttext, 60, 22,
				      TextArea.SCROLLBARS_NONE, false);
		return;
	    }

	// the 'who is' button
	if (E.getSource() == whosThis)
	    {
		showUserInfo();
		return;
	    }

	// the 'page users' button
	if (E.getSource() == page)
	    {
		try {
		    theClient.sendPageUser();
		}
		catch (IOException e) {
		    theClient.lostConnection();
		    return;
		}
		return;
	    }

	// the 'chat rooms' button
	if (E.getSource() == chatRooms)
	    {
		if (roomsDialog != null)
		    roomsDialog.dispose();
		roomsDialog = new OrbitRoomsDialog(this);
		return;
	    }

	// the 'messaging' button
	if (E.getSource() == messaging)
	    {
		new OrbitMessagingDialog(this);
		return;
	    }

	// the 'clear canvas button'
	if (E.getSource() == clearCanvas)
	    {
		canvas.clear();
		if (connected)
		    try {
			theClient.sendClearCanv();
		    }
		    catch (IOException e) {
			theClient.lostConnection();
			return;
		    }
		return;
	    }
    }

    public void keyPressed(KeyEvent E)
    {
    }

    public void keyReleased(KeyEvent E)
    {
	if (E.getSource() == typed)
	    {
		// the 'enter' key in the send text field
		if (E.getKeyCode() == E.VK_ENTER) 
		    {
			// Is this a private communication?
			if (!sendToAll.getState())
			    {
				String wholist[];

				messages.append("*private to ");
				wholist = sendTo.getSelectedItems();
				for (int count = 0; count < wholist.length;
				     count ++)
				    {
					messages.append(wholist[count]);
					if (count < (wholist.length - 1))
					    messages.append(", ");
				    }
				messages.append("*> ");
			    }
			else
			    messages.append(name + "> ");

			// Print the rest.
			messages.append(typed.getText());

			if (connected == true)
			    try {
				// Send it.
				theClient.sendChatText(typed.getText());
			    }
			    catch (IOException e) {
				theClient.lostConnection();
				return;
			    }

			// Empty the typing and activity fields
			typed.setText("");
			activity.setText("");
			return;
		    }
		else
		    {
			if (!activity.getText().equals("typing: " + name))
			    activity.setText("typing: " + name);
			if (connected == true)
			    try {
				// Send a message to indicate that our user is
				// busy typing something
				theClient.sendActivity(OrbitCommand
						       .ACTIVITY_TYPING);
			    }
			    catch (IOException e) {
				theClient.lostConnection();
				return;
			    }
			return;
		    }
	    }
    }

    public void keyTyped(KeyEvent E)
    {
    }   

    public void mouseClicked(MouseEvent E)
    {
    }   

    public void mouseEntered(MouseEvent E)
    {
    }   

    public void mouseExited(MouseEvent E)
    {
    }   

    public void mousePressed(MouseEvent E)
    {
    }   

    public void mouseReleased(MouseEvent E)
    {
    }   

    public void itemStateChanged(ItemEvent E)
    {
	// the 'show canvas' menu item
	if (E.getSource() == menuShowCanvas)
	    {
		showCanvas(menuShowCanvas.getState());
		return;
	    }

	// The 'sendToAll' checkbox
	if (E.getSource() == sendToAll)
	    {
		// If 'send to all' is selected, we should deselect all
		// the items in the sendTo list.
		if (sendToAll.getState())
		    {
			int items = sendTo.getRows();
			for (int count = 0; count < items; count ++)
			    sendTo.deselect(count);
			menuIgnore.setEnabled(false);
			whosThis.setEnabled(false);
			menuUserInfo.setEnabled(false);
		    }

		// Also make sure that this checkbox is selected if
		// nothing is selected in the sendTo window
		else
		    {
			if (sendTo.getSelectedItems().length == 0)
			    {
				sendToAll.setState(true);
				menuIgnore.setEnabled(false);
				whosThis.setEnabled(false);
				menuUserInfo.setEnabled(false);
			    }
		    }
	    }

	// the 'sendTo' window
	if (E.getSource() == sendTo)
	    {
		if (sendTo.getSelectedItems().length == 0)
		    {
			// Nothing is selected in this list.  Make the
			// 'sendToAll' checkbox be checked.
			sendToAll.setState(true);
			menuIgnore.setEnabled(false);
			whosThis.setEnabled(false);
			menuUserInfo.setEnabled(false);
		    }
		
		else
		    {
			// Don't allow "everyone" to be selected if any
			// individual users are selected
			sendToAll.setState(false);
			menuIgnore.setEnabled(true);
			whosThis.setEnabled(true);
			menuUserInfo.setEnabled(true);
		    }
		return;
	    }

	// the line thicknesses
	if (E.getSource() == thickness)
	    {
		canvas.drawThickness = (thickness.getSelectedIndex() + 1);
		return;
	    }

	// the fill types
	if (E.getSource() == fillType)
	    {
		if (fillType.getSelectedIndex() == 0)
		    canvas.fill = false;

		if (fillType.getSelectedIndex() == 1)
		    canvas.fill = true;

		return;
	    }

	// the draw colors
	if (E.getSource() == colorChoice)
	    {
		canvas.drawColor =
		    canvas.colourArray[colorChoice.getSelectedIndex()];
		return;
	    }            

	// the draw types
	if (E.getSource() == freehand)
	    {
		canvas.drawType = canvas.FREEHAND;
		thickness.setEnabled(true);
		fillType.setEnabled(false);
		return;
	    }

	if (E.getSource() == line)
	    {
		canvas.drawType = canvas.LINE;
		thickness.setEnabled(true);
		fillType.setEnabled(false);
		return;
	    }

	if (E.getSource() == oval)
	    {
		canvas.drawType = canvas.OVAL;
		thickness.setEnabled(true);
		fillType.setEnabled(true);
		return;
	    }

	if (E.getSource() == rectangle)
	    {
		canvas.drawType = canvas.RECTANGLE;
		thickness.setEnabled(true);
		fillType.setEnabled(true);
		return;
	    }

	if (E.getSource() == text)
	    {
		canvas.drawType = canvas.TEXT;
		thickness.setEnabled(false);
		fillType.setEnabled(false);
		return;
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
	if (connected == true)
	    disconnect();
	dispose();

	if (thisApplet != null)
	    thisApplet.destroy();
	else if (!adminConsole)
	    System.exit(0);

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

    public void lostOwnership(Clipboard clipboard, Transferable contents)
    {
	System.out.println("Lost ownership");
    }
}
