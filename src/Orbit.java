import java.awt.*;
import java.net.*;
import java.io.*;


public class Orbit
    extends Object
    implements Runnable
{
    public static final String VERSION = "1.0";

    public static String usernameParam     = "-username";
    public static String passwordParam     = "-password";
    public static String servernameParam   = "-servername";
    public static String portnumberParam   = "-portnumber";
    public static String chatroomParam     = "-chatroom";
    public static String widthParam        = "-xsize";
    public static String heightParam       = "-ysize";
    public static String nopasswordsParam  = "-nopasswords";
    public static String locksettingsParam = "-locksettings";
    public static String autoconnectParam  = "-autoconnect";
    public static String hidecanvasParam   = "-hidecanvas";

    private OrbitWindow window;
    private URL myURL = null;
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


    private void usage()
    {
	System.out.println("\nIntranet Chatting usage:");
	System.out.println("java Orbit [" +
			   usernameParam + " name] [" +
			   passwordParam + " password] [" +
			   servernameParam + " host] [" +
			   portnumberParam + " port] [" + 
			   chatroomParam + " room] [" +
			   widthParam + " number] [" +
			   heightParam + " number] [" +
			   nopasswordsParam + "] [" +
			   locksettingsParam + "] [" +
			   autoconnectParam + "] [" +
			   hidecanvasParam + "]");
	return;
    }

    private boolean parseArgs(String[] args)
    {
	// Loop through any command line arguments
	for (int count = 0; count < args.length; count ++)
	    {
		if (args[count].equals(usernameParam))
		    {
			if (++count < args.length)
			    name = args[count];
		    }

		else if (args[count].equals(passwordParam))
		    {
			if (++count < args.length)
			    password = args[count];
		    }

		else if (args[count].equals(servernameParam))
		    {
			if (++count < args.length)
			    host = args[count];
		    }

		else if (args[count].equals(portnumberParam))
		    {
			if (++count < args.length)
			    port = args[count];
		    }

		else if (args[count].equals(chatroomParam))
		    {
			if (++count < args.length)
			    room = args[count];
		    }

		else if (args[count].equals(widthParam))
		    {
			if (++count < args.length)
			    windowWidth = Integer.parseInt(args[count]);
		    }

		else if (args[count].equals(heightParam))
		    {
			if (++count < args.length)
			    windowHeight = Integer.parseInt(args[count]);
		    }

		else if (args[count].equals(nopasswordsParam))
		    requirePasswords = false;

		else if (args[count].equals(locksettingsParam))
		    lockSettings = true;

		else if (args[count].equals(autoconnectParam))
		    autoConnect = true;

		else if (args[count].equals(hidecanvasParam))
		    showCanvas = false;

		else if (args[count].equals("-help"))
		    {
			usage();
			return (false);
		    }

		else
		    {
			System.out.println("\nOrbit: unknown argument "
					   + args[count]);
			System.out.println("Type 'java Orbit -help' for "
					   + "usage information");
			return (false);
		    }
	    }

	return (true);
    }

    public static void main(String[] args)
    {
	Orbit firstinstance = new Orbit(args);
	firstinstance.run();
	return;
    }

    public Orbit(String[] args)
    {
	// Get a URL to describe the invocation directory
	try {
	    myURL = new URL("file", "localhost", "./");
	}
	catch (Exception E) {
	    System.out.println(E);
	    System.exit(1);
	}
	
	// Parse our args.  Only continue if successful
	if (!parseArgs(args))
	    System.exit(1);

	// If "username" is blank, that's OK.  However, if the server and/or
	// port are blank, we'll supply some default ones here
	if ((host == null) || host.equals(""))
	    host = "localhost";
	if ((port == null) || port.equals(""))
	    port = "12468";

	// Open the window
	window = new OrbitWindow(name, password, host, port, showCanvas,
				   myURL);

	// Set the window width and height, if applicable
	Dimension tmpSize = window.getSize();
	if (windowWidth > 0)
	    tmpSize.width = windowWidth;
	if (windowHeight > 0)
	    tmpSize.height = windowHeight;
	window.setSize(tmpSize);

	// Make the pretty icon
//	window.setIcon();

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
		try {
		    window.theClient.sendEnterRoom(room, false, "");
		}
		catch (IOException e) {
		    window.theClient.lostConnection();
		    return;
		}

	// Done
	return;
    }

    public void run()
    {
	// Nothing to do here.
	return;
    }
}
