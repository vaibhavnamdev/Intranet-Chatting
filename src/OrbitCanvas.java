import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


class OrbitLine
{
    public Color color;
    public int startx;
    public int starty;
    public int endx;
    public int endy;
    public int thickness;

    public OrbitLine(Color mycolor, int mystartx, int mystarty,
		       int myendx, int myendy, int mythickness)
    {
	color = mycolor;
	startx = mystartx;
	starty = mystarty;
	endx = myendx;
	endy = myendy;
	thickness = mythickness;
    }
}


class OrbitRectangle
{
    public Color color;
    public int x;
    public int y;
    public int width;
    public int height;
    public boolean fill;
    public int thickness;

    public OrbitRectangle(Color mycolor, int myx, int myy, int mywidth,
			    int myheight, boolean myfill, int mythickness)
    {
	color = mycolor;
	x = myx;
	y = myy;
	width = mywidth;
	height = myheight;
	fill = myfill;
	thickness = mythickness;
    }
}


class OrbitOval
{
    public Color color;
    public int x;
    public int y;
    public int width;
    public int height;
    public boolean fill;
    public int thickness;

    public OrbitOval(Color mycolor, int myx, int myy, int mywidth,
		       int myheight, boolean myfill, int mythickness)
    {
	color = mycolor;
	x = myx;
	y = myy;
	width = mywidth;
	height = myheight;
	fill = myfill;
	thickness = mythickness;
    }
}


class OrbitText
{
    public Color color;
    public int x;
    public int y;
    public int fontnumber;
    public int attribs;
    public int size;
    public String text;

    public OrbitText(Color mycolor, int myx, int myy, int myfontnumber,
		       int myattribs, int mysize, String mytext)
    {
	color = mycolor;
	x = myx;
	y = myy;
	fontnumber = myfontnumber;
	attribs = myattribs;
	size = mysize;
	text = mytext;
    }
}


class OrbitPicture
{
    public int x;
    public int y;
    public int width;
    public int height;
    public File file;
    public Image picture;

    public OrbitPicture(int myX, int myY, int myWidth, int myHeight,
			  File myFile, Image myPicture)
    {
	x = myX;
	y = myY;
	width = myWidth;
	height = myHeight;
	file = myFile;
	picture = myPicture;
    }
}


class OrbitSampleCanvas
    extends Canvas
{
    private static final int X = 300;
    private static final int Y = 75;
    
    public String text;
    public OrbitWindow parentWindow;
    
    public OrbitSampleCanvas(OrbitWindow mainWindow)
    {
	super();
	parentWindow = mainWindow;
	setBackground(Color.lightGray);
	setSize(X, Y);
	repaint();
	setVisible(true);
	text = new String("Orbit and on");
    }

    public void paint(Graphics g)
    {
	g.setColor(Color.black);
	g.setFont(new Font(OrbitCanvas
			   .fontArray[parentWindow.drawFontNumber],
			   parentWindow.drawStyle, parentWindow.drawSize));
	if (parentWindow.drawSize > Y)
	    g.drawString(text, 0, (Y - (Y / 10)));
	else
	    g.drawString(text, 0, parentWindow.drawSize);

	g.dispose();
    }
}


class OrbitFontSelect
    extends Dialog
    implements ActionListener, ItemListener, KeyListener, WindowListener
{
    private GridBagLayout myLayout;
    private GridBagConstraints myConstraints;
    private OrbitWindow parentWindow;

    private Panel p1;
    private Label typeLabel;
    private Choice type;
    private Label sizeLabel;
    private Choice size;
    private Label styleLabel;
    private Checkbox bold;
    private Checkbox italics;

    private Panel p2;
    private Label sampleLabel;
    private OrbitSampleCanvas sample;
    private Label textLabel;
    private TextField text;
    private Panel p3;
    private Button ok;
    private Button cancel;


    public OrbitFontSelect(Frame parent)
    {
	super(parent, "Choose font", true);
	parentWindow = (OrbitWindow) parent;
	myLayout = new GridBagLayout();
	myConstraints = new GridBagConstraints();
	setLayout(myLayout);

	p1 = new Panel();
	p1.setLayout(myLayout);

	myConstraints.insets = new Insets(0,5,0,5);
	myConstraints.anchor = myConstraints.WEST;

	typeLabel = new Label("Font type:");
	myConstraints.gridx = 0; myConstraints.gridy = 0;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myConstraints.fill = myConstraints.NONE;
	myLayout.setConstraints(typeLabel, myConstraints);
	p1.add(typeLabel);

	sizeLabel = new Label("Font size:");
	myConstraints.gridx = 1; myConstraints.gridy = 0;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myConstraints.fill = myConstraints.NONE;
	myLayout.setConstraints(sizeLabel, myConstraints);
	p1.add(sizeLabel);

	styleLabel = new Label("Font style:");
	myConstraints.gridx = 2; myConstraints.gridy = 0;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myConstraints.fill = myConstraints.NONE;
	myLayout.setConstraints(styleLabel, myConstraints);
	p1.add(styleLabel);

	type = new Choice();
	type.addItemListener(this);
	type.addItem("Arial / Helvetica");
	type.addItem("Times New Roman / Adobe-Times");
	type.addItem("Courier New / Courier");
	type.addItem("MS Sans Serif / Lucida");
	type.addItem("MS Sans Serif II / Lucida Typewriter");
	type.select(parentWindow.drawFontNumber);
	myConstraints.gridx = 0; myConstraints.gridy = 1;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myConstraints.fill = myConstraints.BOTH;
	myLayout.setConstraints(type, myConstraints);
	p1.add(type);

	size = new Choice();
	size.addItemListener(this);
	size.addItem("10");
	size.addItem("12");
	size.addItem("14");
	size.addItem("18");
	size.addItem("20");
	size.addItem("24");
	size.addItem("36");
	size.addItem("48");
	size.addItem("60");
	size.addItem("72");
	size.addItem("120");
	if (parentWindow.drawSize == 10) size.select(0);
	if (parentWindow.drawSize == 12) size.select(1);
	if (parentWindow.drawSize == 14) size.select(2);
	if (parentWindow.drawSize == 18) size.select(3);
	if (parentWindow.drawSize == 20) size.select(4);
	if (parentWindow.drawSize == 24) size.select(5);
	if (parentWindow.drawSize == 36) size.select(6);
	if (parentWindow.drawSize == 48) size.select(7);
	if (parentWindow.drawSize == 60) size.select(8);
	if (parentWindow.drawSize == 72) size.select(9);
	if (parentWindow.drawSize == 120) size.select(10);
	myConstraints.gridx = 1; myConstraints.gridy = 1;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myConstraints.fill = myConstraints.BOTH;
	myLayout.setConstraints(size, myConstraints);
	p1.add(size);

	bold = new Checkbox("Bold");
	bold.addItemListener(this);
	if ((parentWindow.drawStyle == Font.BOLD) ||
	    (parentWindow.drawStyle == (Font.BOLD + Font.ITALIC)))
	    bold.setState(true);
	else bold.setState(false);
	myConstraints.gridx = 2; myConstraints.gridy = 1;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myConstraints.fill = myConstraints.NONE;
	myLayout.setConstraints(bold, myConstraints);
	p1.add(bold);

	italics = new Checkbox("Italics");
	italics.addItemListener(this);
	if ((parentWindow.drawStyle == Font.ITALIC) ||
	    (parentWindow.drawStyle == (Font.BOLD + Font.ITALIC)))
	    italics.setState(true);
	else italics.setState(false);
	myConstraints.gridx = 2; myConstraints.gridy = 2;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myConstraints.fill = myConstraints.NONE;
	myLayout.setConstraints(italics, myConstraints);
	p1.add(italics);

	myConstraints.gridx = 0; myConstraints.gridy = 0;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.fill = myConstraints.BOTH;
	myLayout.setConstraints(p1, myConstraints);
	add(p1);

	p2 = new Panel();
	p2.setLayout(myLayout);

	sampleLabel = new Label("Font sample:");
	myConstraints.gridx = 0; myConstraints.gridy = 0;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myConstraints.fill = myConstraints.BOTH;
	myLayout.setConstraints(sampleLabel, myConstraints);
	p2.add(sampleLabel);

	sample = new OrbitSampleCanvas(parentWindow);
	myConstraints.gridx = 0; myConstraints.gridy = 1;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.weightx = 0; myConstraints.weighty = 1;
	myConstraints.anchor = myConstraints.CENTER;
	myConstraints.fill = myConstraints.BOTH;
	myLayout.setConstraints(sample, myConstraints);
	p2.add(sample);

	textLabel = new Label("Text to insert:");
	myConstraints.gridx = 0; myConstraints.gridy = 2;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myConstraints.fill = myConstraints.BOTH;
	myLayout.setConstraints(textLabel, myConstraints);
	p2.add(textLabel);

	text = new TextField(40);
	text.addKeyListener(this);
	myConstraints.gridx = 0; myConstraints.gridy = 3;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myConstraints.fill = myConstraints.BOTH;
	myLayout.setConstraints(text, myConstraints);
	p2.add(text);

	myConstraints.gridx = 0; myConstraints.gridy = 1;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.fill = myConstraints.BOTH;
	myLayout.setConstraints(p2, myConstraints);
	add(p2);

	p3 = new Panel();
	p3.setLayout(myLayout);

	myConstraints.insets = new Insets(5,5,5,5);

	ok = new Button("Ok");
	ok.addActionListener(this);
	ok.addKeyListener(this);
	myConstraints.gridx = 0; myConstraints.gridy = 0;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myConstraints.anchor = myConstraints.EAST;
	myConstraints.fill = myConstraints.NONE;
	myLayout.setConstraints(ok, myConstraints);
	p3.add(ok);

	cancel = new Button("Cancel");
	cancel.addActionListener(this);
	cancel.addKeyListener(this);
	myConstraints.gridx = 1; myConstraints.gridy = 0;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.weightx = 0; myConstraints.weighty = 0;
	myConstraints.anchor = myConstraints.WEST;
	myConstraints.fill = myConstraints.NONE;
	myLayout.setConstraints(cancel, myConstraints);
	p3.add(cancel);

	myConstraints.gridx = 0; myConstraints.gridy = 2;
	myConstraints.gridheight = 1; myConstraints.gridwidth = 1;
	myConstraints.fill = myConstraints.BOTH;
	myLayout.setConstraints(p3, myConstraints);
	add(p3);


	setSize(600,600);
	pack();
	setLocation((((((parentWindow.getBounds()).width) 
			    - ((getSize()).width)) / 2)
			  + ((parentWindow.getLocation()).x)),
			 (((((parentWindow.getBounds()).height)
			    - ((getSize()).height)) / 2)
			  + ((parentWindow.getLocation()).y)));

	sample.repaint();
	addKeyListener(this);
	addWindowListener(this);
	setResizable(false);
	setVisible(true);
	text.requestFocus();
    }
    
    private void floatText()
    {
	parentWindow.canvas.draftText = 
	    new OrbitText(parentWindow.canvas.drawColor,
			    parentWindow.canvas.oldx,
			    parentWindow.canvas.oldy,
			    parentWindow.drawFontNumber,
			    parentWindow.drawStyle, parentWindow.drawSize,
			    text.getText());

	parentWindow.canvas.floatingText = true;
    }

    public void actionPerformed(ActionEvent E)
    {
	if (E.getSource() == ok)
	    {
		floatText();
		dispose();
		return;
	    }
	
	else if (E.getSource() == cancel)
	    {
		dispose();
		return;
	    }
    }

    public void itemStateChanged(ItemEvent E)
    {
	if (E.getSource() == bold)
	    {
		if (bold.getState())
		    parentWindow.drawStyle = Font.BOLD;
		else
		    parentWindow.drawStyle = Font.PLAIN;

		if (italics.getState())
		    parentWindow.drawStyle += Font.ITALIC;

		sample.repaint();
		return;
	    }
	
	else if (E.getSource() == italics)
	    {
		if (italics.getState())
		    parentWindow.drawStyle = Font.ITALIC;
		else
		    parentWindow.drawStyle = Font.PLAIN;

		if (bold.getState())
		    parentWindow.drawStyle += Font.BOLD;

		sample.repaint();
		return;
	    }

	else if (E.getSource() == type)
	    {
		parentWindow.drawFontNumber = type.getSelectedIndex();
		sample.repaint();
		return;
	    }

	else if (E.getSource() == size)
	    {
		parentWindow.drawSize =
		    Integer.parseInt(size.getSelectedItem());
		sample.repaint();
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
		if ((E.getSource() == ok) ||
		    (E.getSource() == text))
		    {
			floatText();
			dispose();
			return;
		    }

		else if (E.getSource() == cancel)
		    {
			dispose();
			return;
		    }
	    }

	else if (E.getKeyCode() == E.VK_TAB)
	    {
		text.transferFocus();
		return;
	    }

	else if (E.getSource() == text)
	    {
		// We need to do the sample update after the key is released
		// or else the keystroke won't show up in the text.getText()
		// call below.
		sample.text = text.getText();
		sample.repaint();
		return;
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


public class OrbitCanvas
    extends Canvas
    implements MouseListener, MouseMotionListener
{
    public static final int FREEHAND = 0;
    public static final int LINE = 1;
    public static final int OVAL = 2;
    public static final int RECTANGLE = 3;
    public static final int TEXT = 4;

    public static final int MODE_PAINT = 0;
    public static final int MODE_XOR = 1;

    public Color drawColor;
    public int drawThickness = 1;

    public int drawType = 0;
    public boolean fill = false;

    private boolean dragging = false;
    public int oldx = 0;
    public int oldy = 0;

    private int ovalWidth;
    private int ovalHeight;
    private int rectangleWidth;
    private int rectangleHeight;

    // For the draft stuff
    private OrbitLine draftLine;
    private OrbitRectangle draftRect;
    private OrbitOval draftOval;
    protected OrbitText draftText;
    protected OrbitPicture draftPicture;
    protected boolean floatingText = false;
    protected boolean floatingPicture = false;

    private OrbitWindow parentWindow;

    boolean showingPicture;
    protected Image offScreen;

    protected static Color[] colourArray = 
    {
	// This is an easy way to index colours with their positions in
	// the colorChoice list.  Basically, it enumerates them, which helps
	// us to send them across the network indexed by number.

	Color.black, Color.blue, Color.cyan, Color.darkGray,
	Color.gray, Color.green, Color.lightGray, Color.magenta,
	Color.orange, Color.pink, Color.red, Color.white, Color.yellow
    };

    protected static String[] fontArray = 
    {
	// Like the colourArray above, this helps to enumerate font names
	// in a way we can index
	"Helvetica", "TimesRoman", "Courier", "Dialog", "DialogInput"
    };

    public OrbitCanvas(OrbitWindow parent)
    {
	super();
	parentWindow = parent;
	setBackground(Color.white);
	setForeground(Color.black);
	drawColor = Color.black;
	setSize(400,125);
	setVisible(true);
	addMouseListener(this);
	addMouseMotionListener(this);

	offScreen = null;
    }

    public void setImage(Image theImage)
    {
	// Fit it to the canvas
	offScreen = theImage;
	repaint();
	showingPicture = true;
    }

    public void floatPicture(File pictureFile)
    {
	// The user wants to paste a picture onto the canvas.  The user needs
	// to specify the location of the picture on the canvas, so we draw
	// an empty draft rectangle which follows the mouse cursor until
	// a click is entered to place it.

	Image theImage = null;
	
	parentWindow.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	try {
	    theImage = getToolkit().getImage(pictureFile.getAbsolutePath());
	    getToolkit().prepareImage(theImage, -1, -1, this);

	    // We have to wait until the image is ready
	    while ((getToolkit().checkImage(theImage, -1, -1, this) &
		    this.ALLBITS) == 0)
		{}
	}
	catch (Exception e) {
	    parentWindow.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	    new OrbitInfoDialog(parentWindow, "Failed", true, e.toString());
	    return;
	}

	// If the thing showing on the canvas is a picture, clear it
	if (showingPicture)
	    clear();

	// I think this is a java bug... for some reason, the first calls
	// to getWidth() and getHeight() return -1
	theImage.getWidth(this);
	theImage.getHeight(this);

	draftPicture =
	    new OrbitPicture(10, 10, theImage.getWidth(this),
			       theImage.getHeight(this), pictureFile,
			       theImage);

	// Draw the initial draft rectangle
	drawRect(Color.black, draftPicture.x, draftPicture.y,
		 draftPicture.width, draftPicture.height, false,
		 2, this.MODE_XOR);

	parentWindow.setCursor(new Cursor(Cursor.HAND_CURSOR));
	floatingPicture = true;
    }

    synchronized public void paint(Graphics g)
    {
	int width = 0;
	int height = 0;

	if (offScreen != null)
	    {
		if (!showingPicture)
		    {
			width = offScreen.getWidth(this);
			height = offScreen.getHeight(this);
		    }
		else
		    {
			width = getSize().width;
			height = getSize().height;
		    }

		g.drawImage(offScreen, 0, 0, width, height, this);
		g.dispose();
	    }
    }

    synchronized public void clear()
    {
	// The maximum size of the usable drawing canvas, for now, will be
	// 1024x768
	offScreen = createImage(1024, 768);
	setBackground(Color.white);
	showingPicture = false;
	repaint();
    }

    synchronized public void drawLine(Color color, int startx, int starty,
				      int endx, int endy, int thickness,
				      int mode)
    {
	int dx, dy;
	Graphics g1 = getGraphics();
        Graphics g2 = offScreen.getGraphics();

	if (mode == this.MODE_XOR)
	    {
		g1.setXORMode(Color.white);
		g2.setXORMode(Color.white);
	    }
	else
	    {
		g1.setColor(color);
		g2.setColor(color);
	    }

	if (endx > startx)
	    dx = (endx - startx);
	else
	    dx = (startx - endx);
	if (endy > starty)
	    dy = (endy - starty);
	else
	    dy = (starty - endy);

	if (dx >= dy)
	    {
		starty -= (thickness / 2);
		endy -= (thickness / 2);
	    }
	else
	    {
		startx -= (thickness / 2);
		endx -= (thickness / 2);
	    }

	for (int count = 0; count < thickness; count ++)
	    {
		g1.drawLine(startx, starty, endx, endy);
		g2.drawLine(startx, starty, endx, endy);
		if (dx >= dy)
		    { starty++; endy++; }
		else
		    { startx++; endx++; }
	    }

	g1.dispose();
	g2.dispose();
    }

    synchronized public void drawOval(Color color, int x, int y, int width,
		      int height, boolean filled, int thickness, int mode)
    {
	Graphics g1 = getGraphics();
	Graphics g2 = offScreen.getGraphics();

	if (mode == this.MODE_XOR)
	    {
		g1.setXORMode(Color.white);
		g2.setXORMode(Color.white);
	    }
	else
	    {
		g1.setColor(color);
		g2.setColor(color);
	    }

        if (filled)
	    {
		g1.fillOval(x, y, width, height);
		g2.fillOval(x, y, width, height);
	    }
        else
	    for (int count = 0; count < thickness; count ++)
		{
		    g1.drawOval(x, y, width, height);
		    g2.drawOval(x, y, width, height);
		    x++; y++;
		    width -= 2; height -= 2;
		}
 
	g1.dispose();
	g2.dispose();
   }

    synchronized public void drawRect(Color color, int x, int y, int width,
		      int height, boolean filled, int thickness, int mode)
    {
	Graphics g1 = getGraphics();
	Graphics g2 = offScreen.getGraphics();

	if (mode == this.MODE_XOR)
	    {
		g1.setXORMode(Color.white);
		g2.setXORMode(Color.white);
	    }
	else
	    {
		g1.setColor(color);
		g2.setColor(color);
	    }

	if (filled)
	    {
		g1.fillRect(x, y, width, height);
		g2.fillRect(x, y, width, height);
	    }
	else
	    {
		for (int count = 0; count < thickness; count ++)
		    {
			g1.drawRect(x, y, width, height);
			g2.drawRect(x, y, width, height);
			x++; y++;
			width -= 2; height -= 2;
		    }
	    }
 
	g1.dispose();
	g2.dispose();
    }

    synchronized public void drawText(Color color, int x, int y,
	      int fontnumber, int attribs, int size, String text, int mode)
    {
	Graphics g1 = getGraphics();
	Graphics g2 = offScreen.getGraphics();

	if (mode == this.MODE_XOR)
	    {
		g1.setXORMode(Color.white);
		g2.setXORMode(Color.white);
	    }
	else
	    {
		g1.setColor(color);
		g2.setColor(color);
	    }

	g1.setFont(new Font (OrbitCanvas.fontArray[fontnumber],
				    attribs, size));
	g2.setFont(new Font (OrbitCanvas.fontArray[fontnumber],
				    attribs, size));
	g1.drawString(text, x, y);
	g2.drawString(text, x, y);
 
	g1.dispose();
	g2.dispose();
    }                         

    synchronized public void drawPicture(int x, int y, Image picture)
    {
	Graphics g1 = getGraphics();
	Graphics g2 = offScreen.getGraphics();

	picture.getWidth(this);
	picture.getHeight(this); 

	// Place the picture on the canvas at the given coordinate
	g1.drawImage(picture, x, y, this);
	g2.drawImage(picture, x, y, this);
    }                         

    public void mouseClicked(MouseEvent E)
    {
    }

    public void mouseEntered(MouseEvent E)
    {
	if (!floatingPicture)
	    parentWindow.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }   

    public void mouseExited(MouseEvent E)
    {
	if (!floatingPicture)
	    parentWindow.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }   

    public void mousePressed(MouseEvent E)
    {
	// Save the coordinates of the initial click
	oldx = E.getX();
	oldy = E.getY();

	// If the thing showing on the canvas is a picture, clear it
	if (showingPicture)
	    clear();

	// If we are doing lines, rectangles, or ovals, we will show
	// draft lines to suggest the final shape of the object
	if (drawType == this.LINE)
	    {
		draftLine = new OrbitLine(drawColor, oldx, oldy, oldx,
					    oldy, drawThickness);
		
		// Set the draw mode to XOR and draw it.
		drawLine(draftLine.color, draftLine.startx,
			 draftLine.starty, draftLine.endx,
			 draftLine.endy, drawThickness, this.MODE_XOR);
	    }

	else if (drawType == this.OVAL)
	    {
		draftOval = new OrbitOval(drawColor, oldx, oldy,
					    1, 1, false, drawThickness);
		
		// Set the draw mode to XOR and draw it.
		drawOval(draftOval.color, draftOval.x, draftOval.y,
			 draftOval.width, draftOval.height, false,
			 drawThickness, this.MODE_XOR);
	    }

	else if (drawType == this.RECTANGLE)
	    {
		draftRect = new OrbitRectangle(drawColor, oldx, oldy,
						 1, 1, false, drawThickness);
		
		// Set the draw mode to XOR and draw it.
		drawRect(draftRect.color, draftRect.x, draftRect.y,
			 draftRect.width, draftRect.height, false,
			 drawThickness, this.MODE_XOR);
	    }

	// Set the activity message
	if (!parentWindow.activity.getText().equals("drawing: " +
						    parentWindow.name))
	    parentWindow.activity.setText("drawing: " + parentWindow.name);

	if (parentWindow.connected)
	    try {
		parentWindow.theClient.sendActivity(OrbitCommand.ACTIVITY_DRAWING);
	    }
	    catch (IOException e) {
		parentWindow.theClient.lostConnection();
		return;
	    }
    }
	
    public void mouseReleased(MouseEvent E)
    {
	if (floatingPicture)
	    {
		// The user wants to place the picture (s)he is pasting

		// Erase the old draft image rectangle
		drawRect(Color.black, draftPicture.x, draftPicture.y,
			 draftPicture.width, draftPicture.height, false,
			 2, this.MODE_XOR);

		// Set the new coordinates
		draftPicture.x = E.getX();
		draftPicture.y = E.getY();
		
		// Draw the new floating picture rectangle
		drawPicture(draftPicture.x, draftPicture.y, 
			    draftPicture.picture);

		// Send the image to the socket
		if (parentWindow.connected)
		    try {
			parentWindow.theClient
			    .sendDrawPicture((short) draftPicture.x,
					     (short) draftPicture.y,
					     draftPicture.file);
		    }
		    catch (IOException e) {
			parentWindow.theClient.lostConnection();
			return;
		    }
		
		parentWindow.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		floatingPicture = false;
		dragging = false;
	    }
	
	else if (drawType == this.LINE)
	    {
		// Erase the draft line
		drawLine(draftLine.color, draftLine.startx, draftLine.starty,
			 draftLine.endx, draftLine.endy, drawThickness,
			 this.MODE_XOR);

		// Add the real line to the canvas
		drawLine(drawColor, oldx, oldy, E.getX(), E.getY(),
			 drawThickness, this.MODE_PAINT);
		
		// send the draw command to the socket
		if (parentWindow.connected)
		    try {
			parentWindow.theClient.sendLine((short)
				parentWindow.colorChoice.getSelectedIndex(),
				(short) oldx, (short) oldy, (short) E.getX(),
				(short) E.getY(), (short) drawThickness);
		    }
		    catch (IOException e) {
			parentWindow.theClient.lostConnection();
			return;
		    }

		dragging = false;
	    }

	else if (drawType == this.OVAL)
	    {
		// Erase the draft oval
		drawOval(draftOval.color, draftOval.x, draftOval.y,
			 draftOval.width, draftOval.height, false,
			 drawThickness, this.MODE_XOR);

		if (oldx <= E.getX())
		    ovalWidth = E.getX() - oldx;
		else
		    ovalWidth = oldx - E.getX();
		if (oldy <= E.getY())
		    ovalHeight = E.getY() - oldy;
		else
		    ovalHeight = oldy - E.getY();

		if ((oldx <= E.getX()) && (E.getY() >= oldy))
		    { oldx = oldx; oldy = oldy; }
		else if ((oldx <= E.getX()) && (E.getY() <= oldy))
		    { oldx = oldx; oldy = E.getY(); }
		else if ((oldx >= E.getX()) && (E.getY() >= oldy))
		    { oldx = E.getX(); oldy = oldy; }
		else if ((oldx >= E.getX()) && (E.getY() <= oldy))
		    { oldx = E.getX(); oldy = E.getY(); }

		// Add the real oval to the canvas
		drawOval(drawColor, oldx, oldy, ovalWidth, ovalHeight,
			 fill, drawThickness, this.MODE_PAINT);

		if (parentWindow.connected)
		    try {
			parentWindow.theClient.sendPoly((short)
				parentWindow.colorChoice.getSelectedIndex(),
				(short) oldx, (short) oldy, (short) ovalWidth,
				(short) ovalHeight, (short) drawThickness,
				fill, OrbitCommand.OVAL);
		    }
		    catch (IOException e) {
			parentWindow.theClient.lostConnection();
			return;
		    }

		dragging = false;
	    }

	else if (drawType == this.RECTANGLE)
	    {
		// Erase the draft recatngle
		drawRect(draftRect.color, draftRect.x, draftRect.y,
			 draftRect.width, draftRect.height, false,
			 drawThickness, this.MODE_XOR);

		if (oldx <= E.getX())
		    rectangleWidth = E.getX() - oldx;
		else
		    rectangleWidth = oldx - E.getX();

		if (oldy <= E.getY())
		    rectangleHeight = E.getY() - oldy;
		else
		    rectangleHeight = oldy - E.getY();

		if ((oldx <= E.getX()) && (E.getY() >= oldy))
		    { oldx = oldx; oldy = oldy; }
		else if ((oldx <= E.getX()) && (E.getY() <= oldy))
		    { oldx = oldx; oldy = E.getY(); }
		else if ((oldx >= E.getX()) && (E.getY() >= oldy))
		    { oldx = E.getX(); oldy = oldy; }
		else if ((oldx >= E.getX()) && (E.getY() <= oldy))
		    { oldx = E.getX(); oldy = E.getY(); }

		// Add the real rectangle to the vector
		drawRect(drawColor, oldx, oldy, rectangleWidth,
			 rectangleHeight, fill, drawThickness,
			 this.MODE_PAINT);

		// Send it out to the other clients
		if (parentWindow.connected)
		    try {
			parentWindow.theClient.sendPoly((short)
				parentWindow.colorChoice.getSelectedIndex(),
				(short) oldx, (short) oldy,
				(short) rectangleWidth,
				(short) rectangleHeight,
				(short) drawThickness, fill,
				OrbitCommand.RECT);
		    }
		    catch (IOException e) {
			parentWindow.theClient.lostConnection();
			return;
		    }

		dragging = false;
	    }

	else if (drawType == this.TEXT)
	    {
		if (floatingText)
		    {
			// The user wants to place the text (s)he created.
	
			// Erase the old draft text
			drawText(drawColor, draftText.x, draftText.y,
				 draftText.fontnumber, draftText.attribs,
				 draftText.size, draftText.text,
				 this.MODE_XOR);

			// Set the new coordinates
			draftText.x = E.getX();
			draftText.y = E.getY();
		
			// Draw the permanent text
			drawText(drawColor, draftText.x, draftText.y,
				 draftText.fontnumber, draftText.attribs,
				 draftText.size, draftText.text,
				 this.MODE_PAINT);

			// Output to the other clients
			if (parentWindow.connected)
			    try {
				parentWindow.theClient
				    .sendDrawText((short) parentWindow
					  .colorChoice.getSelectedIndex(),
					  (short) draftText.x,
					  (short) draftText.y,
					  (short) draftText.fontnumber,
					  (short) draftText.attribs, 
					  (short) draftText.size,
					  draftText.text);
			    }
			    catch (IOException e) {
				parentWindow.theClient.lostConnection();
				return;
			    }

			floatingText = false;
		    }

		else
		    {
			new OrbitFontSelect(parentWindow);

			if (floatingText)
			    // Draw a draft version
			    drawText(drawColor, draftText.x, draftText.y,
				     draftText.fontnumber, draftText.attribs,
				     draftText.size, draftText.text,
				     this.MODE_XOR);
		    }
	    }
    }   

    public void mouseDragged(MouseEvent E)
    {
	if (drawType == this.FREEHAND)
	    {
		drawLine(drawColor, oldx, oldy, E.getX(), E.getY(),
			 drawThickness, this.MODE_PAINT);
	
		// send the draw command to the socket
		if (parentWindow.connected)
		    try {
			parentWindow.theClient.sendLine((short)
				parentWindow.colorChoice.getSelectedIndex(),
				(short) oldx, (short) oldy, (short) E.getX(),
				(short) E.getY(), (short) drawThickness);
		    }
		    catch (IOException e) {
			parentWindow.theClient.lostConnection();
			return;
		    }

		oldx = E.getX();
		oldy = E.getY();
	    }

	else
	    dragging = true;

	if (drawType == this.LINE)
	    {
		// Erase the old draft line
		drawLine(draftLine.color, draftLine.startx, draftLine.starty,
			 draftLine.endx, draftLine.endy, drawThickness,
			 this.MODE_XOR);

		// Draw the new draft line
		draftLine.endx = E.getX();
		draftLine.endy = E.getY();
		drawLine(draftLine.color, draftLine.startx, draftLine.starty,
			 draftLine.endx, draftLine.endy, drawThickness,
			 this.MODE_XOR);
	    }

	else if (drawType == this.OVAL)
	    {
		// Erase the old draft oval
		drawOval(draftOval.color, draftOval.x, draftOval.y,
			 draftOval.width, draftOval.height, false, 
			 drawThickness, this.MODE_XOR);

		if (oldx <= E.getX())
		    draftOval.width = E.getX() - oldx;
		else
		    draftOval.width = oldx - E.getX();

		if (oldy <= E.getY())
		    draftOval.height = E.getY() - oldy;
		else
		    draftOval.height = oldy - E.getY();

		if ((oldx <= E.getX()) && (E.getY() >= oldy))
		    { draftOval.x = oldx; draftOval.y = oldy; }
		else if ((oldx <= E.getX()) && (E.getY() <= oldy))
		    { draftOval.x = oldx; draftOval.y = E.getY(); }
		else if ((oldx >= E.getX()) && (E.getY() >= oldy))
		    { draftOval.x = E.getX(); draftOval.y = oldy; }
		else if ((oldx >= E.getX()) && (E.getY() <= oldy))
		    { draftOval.x = E.getX(); draftOval.y = E.getY(); }

		// Draw the new draft oval
		drawOval(draftOval.color, draftOval.x, draftOval.y,
			 draftOval.width, draftOval.height, false, 
			 drawThickness, this.MODE_XOR);
	    }

	else if (drawType == this.RECTANGLE)
	    {
		// Erase the old draft rectangle
		drawRect(draftRect.color, draftRect.x, draftRect.y,
			 draftRect.width, draftRect.height, false, 
			 drawThickness, this.MODE_XOR);

		if (oldx <= E.getX())
		    draftRect.width = E.getX() - oldx;
		else
		    draftRect.width = oldx - E.getX();

		if (oldy <= E.getY())
		    draftRect.height = E.getY() - oldy;
		else
		    draftRect.height = oldy - E.getY();

		if ((oldx <= E.getX()) && (E.getY() >= oldy))
		    { draftRect.x = oldx; draftRect.y = oldy; }
		else if ((oldx <= E.getX()) && (E.getY() <= oldy))
		    { draftRect.x = oldx; draftRect.y = E.getY(); }
		else if ((oldx >= E.getX()) && (E.getY() >= oldy))
		    { draftRect.x = E.getX(); draftRect.y = oldy; }
		else if ((oldx >= E.getX()) && (E.getY() <= oldy))
		    { draftRect.x = E.getX(); draftRect.y = E.getY(); }

		// Draw the new draft rectangle
		drawRect(draftRect.color, draftRect.x, draftRect.y,
			 draftRect.width, draftRect.height, false, 
			 drawThickness, this.MODE_XOR);
	    }
    }

    public void mouseMoved(MouseEvent E)
    {
	if (floatingText)
	    {
		// When the user has entered some text to place on the
		// canvas, it remains sticky with the cursor until another
		// click is entered to place it.

		// Erase the old draft text
		drawText(drawColor, draftText.x, draftText.y,
			 draftText.fontnumber, draftText.attribs,
			 draftText.size, draftText.text, this.MODE_XOR);

		// Set the new coordinates
		draftText.x = E.getX();
		draftText.y = E.getY();
		
		// Draw the new floating text
		drawText(drawColor, draftText.x, draftText.y,
			 draftText.fontnumber, draftText.attribs, 
			 draftText.size, draftText.text, this.MODE_XOR);
	    }

	if (floatingPicture)
	    {
		// When the user has opted to paste a picture to the canvas,
		// it remains sticky with the cursor until another click
		// is entered to place it

		// Erase the old draft image rectangle
		drawRect(Color.black, draftPicture.x, draftPicture.y,
			 draftPicture.width, draftPicture.height, false,
			 2, this.MODE_XOR);

		// Set the new coordinates
		draftPicture.x = E.getX();
		draftPicture.y = E.getY();
		
		// Draw the new floating picture rectangle
		drawRect(drawColor, draftPicture.x, draftPicture.y,
			 draftPicture.width, draftPicture.height, false,
			 2, this.MODE_XOR);
	    }
    }   
}
