import java.io.*;
import java.util.*;


public class OrbitUserTool
{
    // This class creates/deletes user account entries in the password file.
    // It can be used from the command line, but it also gets invoked from the
    // server window.

    private Vector users = null;

    OrbitUserTool()
    {
	// Read the information from the current password file, if any.
	// This will fill up the 'users' Vector.
	readPasswordFile();
    }

    private void readPasswordFile()
    {
	// Read the whole password file, and turn all the entries into
	// OrbitUser objects in the 'users' Vector
	
	DataInputStream passwordStream = null;
	users = new Vector();

	try {
	    passwordStream = new DataInputStream(new
		FileInputStream(OrbitServer.userPasswordFileName));
	    
	    // Read entry by entry.
	    while(true)
		{
		    String tempUserName = "";
		    String tempPassword = "";
		    try {
			tempUserName = passwordStream.readUTF();
			tempPassword = passwordStream.readUTF();
		    }
		    catch (EOFException e) {
			break;
		    }

		    // Create the OrbitUser object and append it to
		    // the vector
		    users.addElement(new OrbitUser(0, tempUserName,
						     tempPassword, ""));
		}
	}
	catch (IOException e) {}
    }

    private void writePasswordFile()
	throws Exception
    {
	// Overwrite the password file.  Loop through the users Vector
	// and write each name to the file.

	DataOutputStream passwordStream = null;

	// Open up the password file
	try {
	    passwordStream =
		new DataOutputStream(new
		    FileOutputStream(OrbitServer.userPasswordFileName));

	    for (int count = 0; count < users.size(); count ++)
		{
		    OrbitUser tmpUser =
			(OrbitUser) users.elementAt(count);

		    // Append this user to the end of the file
		    passwordStream.writeUTF(tmpUser.name);
		    passwordStream.writeUTF(tmpUser.password);
		}

	    passwordStream.close();
	}
	catch (IOException E) {
	    if (passwordStream != null)
		passwordStream.close();
	    throw new Exception("Unable to write the password file");
	}
    }

    private void appendUser(String userName, String encryptedPassword)
	throws Exception
    {
	DataOutputStream passwordStream = null;

	// The password should already be encrypted
	
	// Open up the password file
	try {
	    passwordStream =
		new DataOutputStream(new
		    FileOutputStream(OrbitServer.userPasswordFileName,
				     true));
	    // Append our stuff to the end of the file
	    passwordStream.writeUTF(userName);
	    passwordStream.writeUTF(encryptedPassword);
	    passwordStream.close();
	}
	catch (IOException E) {
	    if (passwordStream != null)
		passwordStream.close();
	    throw new Exception("Unable to change the password file");
	}
    }

    public void createUser(String userName, String encryptedPassword)
	throws Exception
    {
	// We can't have empty user names or passwords
	if (userName.equals("") || encryptedPassword.equals(""))
	    throw new Exception("Username or password empty");

	// Make sure that the user doesn't already exist
	for (int count = 0; count < users.size(); count ++)
	    {
		OrbitUser user = (OrbitUser) users.elementAt(count);
		if (user.name.equals(userName))
		    throw new Exception("User already exists");
	    }

	// Add the user to our Vector
	users.addElement(new OrbitUser(0, userName, encryptedPassword, ""));
	
	// Append the new user to the password file
	appendUser(userName, encryptedPassword);

	return;
    }

    public void deleteUser(String userName)
	throws Exception
    {
	// Loop through the list of users, find the one in question,
	// remove it from our users Vector, and rewrite the password
	// file.
	
	OrbitUser user = null;

	for (int count = 0; count < users.size(); count ++)
	    {
		OrbitUser tmpUser = (OrbitUser) users.elementAt(count);

		if (tmpUser.name.equals(userName))
		    {
			user = tmpUser;
			break;
		    }
	    }
	
	if (user == null)
	    // Not found
	    throw new Exception("User does not exist");

	// Get rid
	users.removeElement(user);

	// Write the password file again
	writePasswordFile();
    }

    public String[] listUsers()
	throws Exception
    {
	// Return a String array with all the user names

	String[] userList = new String[users.size()];

	for (int count = 0; count < users.size(); count ++)
	    userList[count] = ((OrbitUser) users.elementAt(count)).name;
	
	return (userList);
    }

    private static void usage()
    {
	System.out.println("\nOrbit Chat User Tool usage:");
	System.out.println("java OrbitUserTool -create <user name> "
			   + "<password>");
	System.out.println("                     -delete <user name>");
	System.out.println("                     -list");
	return;
    }

    public static void main(String[] args)
    {
	OrbitUserTool tool;
	String userName = "";
	String password = "";

	// Create our user tool object
	tool = new OrbitUserTool();

	// Parse the arguments
	for (int count = 0; count < args.length; count ++)
	    {
		if (args[count].equals("-create"))
		    {
			if (++count < args.length)
			    userName = args[count];
			if (++count < args.length)
			    password = args[count];

			// Encrypt the password
			password = new OrbitPasswordEncryptor()
			    .encryptPassword(password);

			try {
			    // Create the user
			    tool.createUser(userName, password);
			}
			catch (Exception e) {
			    System.out.println("Error creating user: " +
					       e.toString());
			    System.exit(1);
			}

			// Success
			System.out.println("User created.");
			return;
		    }

		else if (args[count].equals("-delete"))
		    {
			if (++count < args.length)
			    userName = args[count];

			try {
			    // Delete the user
			    tool.deleteUser(userName);
			}
			catch (Exception e) {
			    System.out.println("Error deleting user: " +
					       e.toString());
			    System.exit(1);
			}

			// Success
			System.out.println("User deleted.");
			return;
		    }

		else if (args[count].equals("-list"))
		    {
			String[] userList = null;

			try {
			    userList = tool.listUsers();
			}
			catch (Exception e) {
			    System.out.println("Error listing users: " +
					       e.toString());
			    System.exit(1);
			}

			// Print out the results
			for (int count2 = 0; count2 < userList.length; 
			     count2 ++)
			    System.out.print(userList[count2] + " ");
			System.out.println("");
		    }

		else if (args[count].equals("-help"))
		    {
			usage();
			System.exit(1);
		    }

		else
		    {
			System.out.println("\nOrbitUserTool: unknown "
					   + "argument " + args[count]);
			System.out.println("Type 'java OrbitUserTool " +
					   "-help' for usage information");
			System.exit(1);
		    }
	    }

	return;
    }
}
