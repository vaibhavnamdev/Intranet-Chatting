import java.applet.*;
import java.awt.*;
import java.net.*;
import java.io.*;


public class OrbitApplet
    extends Applet
{
    // This is just a simple applet wrapper class to allow Orbit Chat
    // clients to be embedded in HTML documents.

    public static String usernameParam     = "username";
    public static String passwordParam     = "password";
    public static String servernameParam   = "servername";
    public static String portnumberParam   = "portnumber";
    public static String chatroomParam     = "chatroom";
    public static String widthParam        = "xsize";
    public static String heightParam       = "ysize";
    public static String usepasswordsParam = "usepasswords";
    public static String locksettingsParam = "locksettings";
    public static String autoconnectParam  = "autoconnect";
    public static String hidecanvasParam   = "hidecanvas";

    private OrbitWindow window;
    private String name = "";
    private String password = "";
    private String host = "";
    private String port = "";
    private String room = "";
    private int windowWidth = 0;
    private int windowHeight = 0;
    private boolean requirePasswords = true;
    private boolean lockSettings = false;
    private boolean autoConnect = false;
    private boolean showCanvas = true;
    

    public String getAppletInfo()
    {
	return ("Orbit Chat version " + Orbit.VERSION
		+ " by Andy McLaughlin");
    }
    

    public String[][] getParameterInfo()
    {
	String[][] args = {
	    { usernameParam, "string", "User login handle" },
	    { passwordParam, "string", "User password" },
	    { servernameParam, "string",
	      "Internet hostname/address of Orbit Chat server" },
	    { portnumberParam, "integer 1-65535",
	      "TCP port number to use (default: 12468)" },
	    { chatroomParam, "string",
	      "Name of initial chat room" },
	    { widthParam, "integer > 0",
	      "Initial width of the client window" },
	    { heightParam, "integer > 0",
	      "Initial height of the client window" },
	    { usepasswordsParam, "no",
	      "Do not require the user to enter a password to connect" },
	    { locksettingsParam, "yes",
	      "Don't allow the user to change user name, server, or port" },
	    { autoconnectParam, "yes",
	      "Tells client to connect automatically on startup" },
	    { hidecanvasParam, "yes",
	      "Tells client to hide the drawing canvas on startup" }
	};
	
	return (args);
    }

    
    public void init()
    {
	// Get the user name, password, host name, and port from the launching
	// document, if specified.

	if (getParameter(usernameParam) != null)
	    name = getParameter(usernameParam);

	if (getParameter(passwordParam) != null)
	    password = getParameter(passwordParam);

	if (getParameter(servernameParam) != null)
	    host = getParameter(servernameParam);

	if (getParameter(portnumberParam) != null)
	    port = getParameter(portnumberParam);

	if (getParameter(chatroomParam) != null)
	    room = getParameter(chatroomParam);

	if (getParameter(widthParam) != null)
	    windowWidth = Integer.parseInt(getParameter(widthParam));

	if (getParameter(heightParam) != null)
	    windowHeight = Integer.parseInt(getParameter(heightParam));

	// Get additional behavior parameters

	if (getParameter(usepasswordsParam) != null)
	    {
		if (getParameter(usepasswordsParam).equals("no"))
		    requirePasswords = false;
	    }

	if (getParameter(locksettingsParam) != null)
	    {
		if (getParameter(locksettingsParam).equals("yes"))
		    lockSettings = true;
	    }

	if (getParameter(autoconnectParam) != null)
	    {
		if (getParameter(autoconnectParam).equals("yes"))
		    autoConnect = true;
	    }

	if (getParameter(hidecanvasParam) != null)
	    {
		if (getParameter(hidecanvasParam).equals("yes"))
		    showCanvas = false;
	    }

	// If usernameParam is blank, that's OK -- it's probably a deliberate
	// attempt to get the user to enter a unique one later.  However,
	// if the host and port values are blank, we'll supply some default
	// ones here
	if ((host == null) || host.equals(""))
	    host = "orbititc.com";
	if ((port == null) || port.equals(""))
	    port = "12468";

	// Done
	return;
    }


    public void start()
    {
	// Launch a window.  It will show up as an unsigned applet window.
	window = new OrbitWindow(name, password, host, port, showCanvas,
				   getCodeBase());
	
	// Set the window width and height, if applicable
	Dimension tmpSize = window.getSize();
	if (windowWidth > 0)
	    tmpSize.width = windowWidth;
	if (windowHeight > 0)
	    tmpSize.height = windowHeight;
	window.setSize(tmpSize);

	// Give the window a reference to this applet
	window.setApplet(this);

	// Should the window prompt users for passwords automatically?
	window.requirePassword = requirePasswords;

	// Should the user name, server name, and port name be locked
	// against user changes?
	window.lockSettings = lockSettings;

	// Show the window
	window.show();

	// Are we supposed to attempt an automatic connection?
	if (autoConnect)
	    window.connect();

	// Is the user supposed to be placed in an initial chat room?
	if (!room.equals(""))
	    if (window.theClient != null)
		{
		    try {
			window.theClient.sendEnterRoom(room, false, "");
		    }
		    catch (IOException e) {
			window.theClient.lostConnection();
			return;
		    }
		}

	// Done
	return;
    }


    public void stop()
    {
	return;
    }

      public void destroy()
    {
	// Disconnect from the server if connected
	if (window.connected == true)
	    window.disconnect();
	window.dispose();
	return;
    }
}
