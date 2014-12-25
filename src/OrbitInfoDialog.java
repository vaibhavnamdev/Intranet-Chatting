import java.awt.*;
import java.awt.event.*;


public class OrbitInfoDialog
    extends Dialog
    implements ActionListener, KeyListener, WindowListener
{
    protected Frame parentFrame;
    protected Label message;
    protected Button ok;
    protected GridBagLayout myLayout;
    protected GridBagConstraints myConstraints;


    public OrbitInfoDialog(Frame parent, String TheTitle,
			     boolean IsModal, String TheMessage)
    {
	super(parent, TheTitle, IsModal);

	parentFrame = parent;

	myLayout = new GridBagLayout();
	myConstraints = new GridBagConstraints();

	setLayout(myLayout);

	myConstraints.insets.left = 5; myConstraints.insets.right = 5;

	message = new Label(TheMessage);
	myConstraints.gridwidth = 1; myConstraints.gridheight = 1;
	myConstraints.gridx = 0; myConstraints.gridy = 0;
	myConstraints.anchor = myConstraints.CENTER;
	myConstraints.fill = myConstraints.BOTH;
	myConstraints.insets.top = 5; myConstraints.insets.bottom = 0;
	myLayout.setConstraints(message, myConstraints);
	add(message);

	ok = new Button("Ok");
	ok.addActionListener(this);
	ok.addKeyListener(this);
	myConstraints.gridwidth = 1; myConstraints.gridheight = 1;
	myConstraints.gridx = 0; myConstraints.gridy = 1;
	myConstraints.anchor = myConstraints.CENTER;
	myConstraints.fill = myConstraints.NONE;
	myConstraints.insets.top = 5; myConstraints.insets.bottom = 5;
	myLayout.setConstraints(ok, myConstraints);
	add(ok);

	setBackground(Color.lightGray);
	pack();
	setResizable(false);

	setLocation((((((parentFrame.getBounds()).width) 
		       - ((getSize()).width)) / 2)
		     + ((parentFrame.getLocation()).x)),
		    (((((parentFrame.getBounds()).height) 
		       - ((getSize()).height)) / 2)
		     + ((parentFrame.getLocation()).y)));

	addKeyListener(this);
	addWindowListener(this);
	ok.requestFocus();
	show();
	return;
    }

    public void actionPerformed(ActionEvent E)
    {
	if (E.getSource() == ok)
	    {
		dispose();
		return;
	    }
	return;
    }

    public void keyPressed(KeyEvent E)
    {
    }

    public void keyReleased(KeyEvent E)
    {
	if (E.getKeyCode() == E.VK_ENTER)
	    {
		if (E.getSource() == ok)
		    {
			dispose();
			return;
		    }
	    }
	return;
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
