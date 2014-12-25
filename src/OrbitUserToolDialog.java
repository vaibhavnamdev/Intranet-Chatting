import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class OrbitUserToolDialog
    extends Dialog
    implements ActionListener, ItemListener, KeyListener, WindowListener
{
    private OrbitUserTool userTool = new OrbitUserTool();

    protected OrbitServerWindow parentWindow;

    protected Label nameLabel;
    protected TextField name;
    protected Label passwordLabel;
    protected TextField password;
    protected Button create;
    protected Button finished;
    java.awt.List allUsersList;
    protected Button delete;
    protected Label statusLabel;
    protected GridBagLayout myLayout;
    protected GridBagConstraints myConstraints;


    public OrbitUserToolDialog(OrbitServerWindow parent)
    {
	super(parent, "User Tool", false);

	parentWindow = parent;
	
	myLayout = new GridBagLayout();
	myConstraints = new GridBagConstraints();

	myConstraints.fill = myConstraints.BOTH;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;

	setLayout(myLayout);

	myConstraints.insets = new Insets(0, 0, 0, 0);
	myConstraints.insets.left = 5; myConstraints.insets.right = 0;
	myConstraints.insets.top = 5; myConstraints.insets.bottom = 0;
	myConstraints.anchor = myConstraints.EAST;

	nameLabel = new Label("User name:");
	myConstraints.gridx = 0; myConstraints.gridy = 0;
	myLayout.setConstraints(nameLabel, myConstraints);
	add(nameLabel);

	passwordLabel = new Label("Password:");
	myConstraints.gridx = 0; myConstraints.gridy = 1;
	myLayout.setConstraints(passwordLabel, myConstraints);
	add(passwordLabel);

	myConstraints.anchor = myConstraints.CENTER;

	name = new TextField(20);
	name.addKeyListener(this);
	myConstraints.gridx = 1; myConstraints.gridy = 0;
	myLayout.setConstraints(name, myConstraints);
	add(name);

	password = new TextField(20);
	password.addKeyListener(this);
	password.setEchoChar(new String("*").charAt(0));
	myConstraints.gridx = 1; myConstraints.gridy = 1;
	myLayout.setConstraints(password, myConstraints);
	add(password);

	myConstraints.insets.top = 5; myConstraints.insets.bottom = 5;

	myConstraints.anchor = myConstraints.WEST;
	myConstraints.insets.top = 5; myConstraints.insets.bottom = 0;
	myConstraints.insets.left = 5; myConstraints.insets.right = 5;

	create = new Button("Create");
	create.addActionListener(this);
	create.addKeyListener(this);
	myConstraints.gridx = 2; myConstraints.gridy = 0;
	myConstraints.fill = myConstraints.HORIZONTAL;
	myLayout.setConstraints(create, myConstraints);
	add(create);

	allUsersList = new java.awt.List(10);
	allUsersList.addItemListener(this);
	allUsersList.addKeyListener(this);
	allUsersList.setMultipleMode(true);
	myConstraints.gridx = 0; myConstraints.gridy = 2;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 2;
	myConstraints.weightx = 1.0; myConstraints.weighty = 1.0;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.insets.left = 5; myConstraints.insets.right = 0;
	myLayout.setConstraints(allUsersList, myConstraints);
	add(allUsersList);

	delete = new Button("Delete");
	delete.addActionListener(this);
	delete.addKeyListener(this);
	myConstraints.gridx = 2; myConstraints.gridy = 2;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.fill = myConstraints.HORIZONTAL;
	myConstraints.anchor = myConstraints.NORTHWEST;
	myConstraints.insets.left = 5; myConstraints.insets.right = 5;
	myLayout.setConstraints(delete, myConstraints);
	add(delete);

	statusLabel = new Label("");
	myConstraints.gridx = 1; myConstraints.gridy = 3;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.insets.left = 0; myConstraints.insets.right = 0;
	myConstraints.insets.top = 5; myConstraints.insets.bottom = 5;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.anchor = myConstraints.CENTER;
	myLayout.setConstraints(statusLabel, myConstraints);
	add(statusLabel);

	finished = new Button("Finished");
	finished.addActionListener(this);
	finished.addKeyListener(this);
	myConstraints.gridx = 2; myConstraints.gridy = 3;
	myConstraints.fill = myConstraints.HORIZONTAL;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.insets.left = 5; myConstraints.insets.right = 5;
	myConstraints.insets.top = 5; myConstraints.insets.bottom = 5;
	myLayout.setConstraints(finished, myConstraints);
	add(finished);

	fillUsersList();

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
	name.requestFocus();
    }

    private void fillUsersList()
    {
	// Get the list of users from the userTool and put it in our
	// list widget

	String[] usersList = null;

	try {
	    usersList = userTool.listUsers();
	}
	catch (Exception e) {}

	allUsersList.removeAll();
	delete.setEnabled(false);

	if (usersList == null)
	    return;

	for (int count = 0; count < usersList.length; count ++)
	    allUsersList.add(usersList[count]);
    }

    private void createUser()
    {
	// Encrypt the password
	String encryptedPassword = new OrbitPasswordEncryptor()
	    .encryptPassword(password.getText());

	try {
	    userTool.createUser(name.getText(),
				encryptedPassword);
	}
	catch (Exception e) {
	    statusLabel.setText("Error creating user");
	    new OrbitInfoDialog(parentWindow, "Error creating user",
				  true, e.toString());
	    return;
	}

	name.setText("");
	password.setText("");

	fillUsersList();

	statusLabel.setText("User created");
	name.requestFocus();
	return;
    }

    private void deleteUsers()
    {
	boolean multiple;
	String[] users = allUsersList.getSelectedItems();
		
	if (users.length > 1)
	    multiple = true;
	else
	    multiple = false;

	for (int count = 0; count < users.length; count ++)
	    {
		try {
		    userTool.deleteUser(users[count]);
		}
		catch (Exception e) {
		    statusLabel.setText("Error deleting user");
		    new OrbitInfoDialog(parentWindow, "Error deleting user",
					  true, e.toString());
		    return;
		}
	    }

	fillUsersList();

	if (multiple)
	    statusLabel.setText("Users deleted");
	else
	    statusLabel.setText("User deleted");
	return;
    }

    public void actionPerformed(ActionEvent E)
    {
	if (E.getSource() == create)
	    {
		createUser();
		return;
	    }

	else if (E.getSource() == delete)
	    {
		deleteUsers();
		return;
	    }

	else if (E.getSource() == finished)
	    {
		dispose();
		return;
	    }
    }

    public void itemStateChanged(ItemEvent E)
    {
	if (E.getSource() == allUsersList)
	    {
		// If user names are selected, enable the delete
		// button.  If not, vice-vers
		if (allUsersList.getSelectedItems().length == 0)
		    delete.setEnabled(false);
		else
		    delete.setEnabled(true);
	    }
    }

    public void keyPressed(KeyEvent E)
    {
    }

    public void keyReleased(KeyEvent E)
    {
	if (E.getKeyCode() == E.VK_ENTER) 
	    {
		if (E.getSource() == create)
		    {
			createUser();
			return;
		    }
		
		else if (E.getSource() == delete)
		    {
			deleteUsers();
			return;
		    }

		else if (E.getSource() == finished)
		    {
			dispose();
			return;
		    }
		
		else if ((E.getSource() == name) ||
			 (E.getSource() == password))
		    {
			createUser();
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
