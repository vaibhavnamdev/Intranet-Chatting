import java.awt.*;
import java.awt.event.*;


public class OrbitPasswordDialog
    extends Dialog
    implements ActionListener, KeyListener, WindowListener
{
    private OrbitWindow parentWindow;
    private OrbitPasswordEncryptor passwordEncryptor;
    private Label passwordLabel;
    private TextField passwordField;
    private Label passwordWarningLabel1;
    private Label passwordWarningLabel2;
    private Button ok;
    private String password = "";
    private GridBagLayout myLayout;
    private GridBagConstraints myConstraints;


    public OrbitPasswordDialog(Frame parent, String myLabel,
				 boolean IsModal)
    {
	super(parent, "Enter password", IsModal);

	parentWindow = (OrbitWindow) parent;
	
	myLayout = new GridBagLayout();
	setLayout(myLayout);

	myConstraints = new GridBagConstraints();
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.insets = new Insets(5, 5, 0, 5);
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.weightx = 1.0; myConstraints.weighty = 1.0;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;

	passwordLabel = new Label(myLabel);
	myConstraints.gridx = 0; myConstraints.gridy = 0;
	myLayout.setConstraints(passwordLabel, myConstraints);
	add(passwordLabel);

	passwordField = new TextField(20);
	passwordField.setEchoChar(new String("*").charAt(0));
	passwordField.addKeyListener(this);
	myConstraints.gridx = 0; myConstraints.gridy = 1;
	myLayout.setConstraints(passwordField, myConstraints);
	add(passwordField);

	passwordWarningLabel1 =
	    new Label("Warning: Your Java client cannot encrypt your");
	passwordWarningLabel1
	    .setVisible(!parentWindow.passwordEncryptor.canEncrypt);
	passwordWarningLabel1.setFont(OrbitWindow.XsmallFont);
	myConstraints.gridx = 0; myConstraints.gridy = 2;
	myConstraints.fill = myConstraints.NONE;
	myConstraints.insets.top = 5; myConstraints.insets.bottom = 0; 
	myLayout.setConstraints(passwordWarningLabel1, myConstraints);
	add(passwordWarningLabel1);

	passwordWarningLabel2 =
	    new Label("passwords.  They will be sent as plain text.");
	passwordWarningLabel2
	    .setVisible(!parentWindow.passwordEncryptor.canEncrypt);
	passwordWarningLabel2.setFont(OrbitWindow.XsmallFont);
	myConstraints.gridx = 0; myConstraints.gridy = 3;
	myConstraints.insets.top = 0; myConstraints.insets.bottom = 0; 
	myLayout.setConstraints(passwordWarningLabel2, myConstraints);
	add(passwordWarningLabel2);

	ok = new Button("Ok");
	ok.setFont(parentWindow.smallFont);
	ok.addActionListener(this);
	ok.addKeyListener(this);
	myConstraints.gridx = 0; myConstraints.gridy = 4;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myConstraints.anchor = myConstraints.CENTER;
	myConstraints.insets.top = 5; myConstraints.insets.bottom = 5; 
	myLayout.setConstraints(ok, myConstraints);
	add(ok);

	pack();

	// If this window is bigger than the parent window, place it at
	// the same coordinates as the parent.
	if ((parentWindow.getBounds().width <= getSize().width) ||
	    (parentWindow.getBounds().height <= getSize().height))
	    setLocation(parentWindow.getLocation().x,
			parentWindow.getLocation().y);
	else
	    // Otherwise, place it centered within the parent window.
	    setLocation((((parentWindow.getBounds().width - 
			   getSize().width) / 2)
			 + parentWindow.getLocation().x),
			(((parentWindow.getBounds().height - 
			   getSize().height) / 2)
			 + parentWindow.getLocation().y));

	addKeyListener(this);
	addWindowListener(this);
	setResizable(false);
	setVisible(true);
	passwordField.requestFocus();
    }

    public String getPassword()
    {
	// Return the password that the user entered
	return (password);
    }

    public void actionPerformed(ActionEvent E)
    {
	if (E.getSource() == ok)
	    {
		password = parentWindow.passwordEncryptor
		    .encryptPassword(passwordField.getText());
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
		if ((E.getSource() == passwordField) || (E.getSource() == ok))
		    {
			password = parentWindow.passwordEncryptor
			    .encryptPassword(passwordField.getText());
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
