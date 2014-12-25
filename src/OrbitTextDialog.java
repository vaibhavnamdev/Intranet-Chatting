import java.awt.*;
import java.awt.event.*;


public class OrbitTextDialog
    extends Dialog
    implements ActionListener, KeyListener, WindowListener
{
    private OrbitWindow parentWindow;
    private Button dismiss;
    private TextArea thetext;
    private GridBagLayout myLayout;
    private GridBagConstraints myConstraints;


    public OrbitTextDialog(Frame parent, String MyLabel, String contents,
			     int columns, int rows, int scrollbars,
			     boolean IsModal)
    {
	super(parent, MyLabel, IsModal);

	parentWindow = (OrbitWindow) parent;
	
	myLayout = new GridBagLayout();
	setLayout(myLayout);

	myConstraints = new GridBagConstraints();
	myConstraints.anchor = myConstraints.CENTER;
	myConstraints.insets = new Insets(5,5,5,5);
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;

	thetext = new TextArea(contents, rows, columns, scrollbars);
	thetext.addKeyListener(this);
	thetext.setFont(parentWindow.smallFont);
	myConstraints.gridx = 0; myConstraints.gridy = 0;
	myConstraints.weightx = 1; myConstraints.weighty = 1;
	myConstraints.fill = myConstraints.BOTH;
	myLayout.setConstraints(thetext, myConstraints);
	thetext.setEditable(false);
	add(thetext);

	dismiss = new Button("Dismiss");
	dismiss.setFont(parentWindow.smallFont);
	dismiss.addActionListener(this);
	dismiss.addKeyListener(this);
	myConstraints.gridx = 0; myConstraints.gridy = 1;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myConstraints.fill = myConstraints.NONE;
	myLayout.setConstraints(dismiss, myConstraints);
	add(dismiss);

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
	dismiss.requestFocus();
    }

    public void actionPerformed(ActionEvent E)
    {
	if (E.getSource() == dismiss)
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
		if (E.getSource() == dismiss)
		    {
			dispose();
			return;
		    }
	    }

	if (E.getKeyCode() == E.VK_TAB)
	    {
		if (E.getSource() == thetext)
		    {
			// Tab out of the text area
			thetext.transferFocus();
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
