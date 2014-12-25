import java.util.*;


public class OrbitRoomInfo
{
    // This class is just a data structure to hold information about a
    // chat room.  It needs its own source file because it's used by both
    // the OrbitRoomsDialog class and the OrbitClient class

    String name;
    String creatorName;
    boolean priv;
    boolean invited;
    boolean roomOwner;
    Vector userNames;

    OrbitRoomInfo()
    {
	name = "";
	creatorName = "";
	priv = false;
	invited = false;
	roomOwner = false;
	userNames = new Vector();
    }
}
