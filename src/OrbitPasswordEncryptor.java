
import java.security.*;


public class OrbitPasswordEncryptor
{
    public static String hashAlgorithm = "SHA";
    private MessageDigest passwordEncryptor;
    protected boolean canEncrypt = true;

    public OrbitPasswordEncryptor()
    {
	try {
	    passwordEncryptor =
		MessageDigest.getInstance(hashAlgorithm);
	}
	catch (Exception e) {
	    // A number of exceptions can happen here, including the one
	    // in which the Java implementation doesn't implement the
	    // MessageDigest stuff.  In any of these cases, password
	    // encryption will not be available
	    canEncrypt = false;
	}
    }

    protected String encryptPassword(String original)
    {
	if (original.equals("") || !canEncrypt)
	    return (original);

	byte[] enc = null;
	try {
	    synchronized (passwordEncryptor)
		{
		    passwordEncryptor.reset();
		    enc = passwordEncryptor.digest(original.getBytes());
		}

	    // All set
	    return (new String(enc, "ISO-8859-1"));
	}
	catch (Exception e) {
	    // Doesn't work - disable encryption
	    canEncrypt = false;
	    return (original);
	}
    }
}

