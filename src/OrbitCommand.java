
/*
  Here is the list of commands, with brief summary information

  NAME		ARGS
  ----		----
  setproto	version	
  noop
  ping
  connect       name
  userinfo	user_id name password encrypted additional
  servermess	message
  disconnect	user_id message
  roomlist	#rooms [name creator private invited #users username...] ...
  invite	from_id roomname invite_id
  enterroom	from_id	roomname private password encrypted
  bootuser      from_id	roomname boot_id
  banuser       from_id	roomname ban_id
  allowuser     from_id	roomname allow_id
  activity      from_id type #for [for_id...]
  chattext 	from_id private colour data #for [for_id...]
  line		from_id colour x0 y0 x1 y1 thick #for [for_id...]
  rect		from_id colour x0 y0 width height thick fill #for [for_id...]
  oval		from_id colour x0 y0 width height thick fill #for [for_id...]
  drawtext	from_id colour x y type attr size text #for [for_id...]
  drawpicture   from_id x y length data #for [for_id...]
  clearcanv	from_id #for [for_id...]
  pageuser	from_id #for [for_id...]
  instantmess   from_id for_id message
  leavemess	from_id for_name message
  readmess	from_id
  storedmess	number [sender_name message]...
  error         from_id errorcode #for [for_id...]
*/


public class OrbitCommand
{
    // The list of command type ids
    public static final short SETPROTO    = 1;
    public static final short NOOP        = 2;
    public static final short PING        = 3;
    public static final short CONNECT     = 4;
    public static final short USERINFO    = 5;
    public static final short SERVERMESS  = 6;
    public static final short DISCONNECT  = 7;
    public static final short ROOMLIST    = 8;
    public static final short INVITE      = 9;
    public static final short ENTERROOM   = 10;
    public static final short BOOTUSER    = 11;
    public static final short BANUSER     = 12;
    public static final short ALLOWUSER   = 13;
    public static final short ACTIVITY    = 14;
    public static final short CHATTEXT    = 15;
    public static final short LINE        = 16;
    public static final short RECT        = 17;
    public static final short OVAL        = 18;
    public static final short DRAWTEXT    = 19;
    public static final short DRAWPICTURE = 20;
    public static final short CLEARCANV   = 21;
    public static final short PAGEUSER    = 22;
    public static final short INSTANTMESS = 23;
    public static final short LEAVEMESS   = 24;
    public static final short READMESS    = 25;
    public static final short STOREDMESS  = 26;
    public static final short ERROR       = 27;

    // Activity subtypes
    public static final short ACTIVITY_TYPING  = 1;
    public static final short ACTIVITY_DRAWING = 2;

    // Error subtypes
    public static final short ERROR_NOPAGE  = 1;
    public static final short ERROR_NOSOUND = 2;
}