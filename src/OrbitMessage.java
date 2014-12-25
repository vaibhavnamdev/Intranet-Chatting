
public class OrbitMessage
{
    public String messageFor;
    public String messageFrom;
    public String text;

    public OrbitMessage(String whoFor, String whoFrom, String info)
    {
	messageFor = whoFor;
	messageFrom = whoFrom;
	text = info;
    }
}

