package Common;

import java.io.Serializable;

/**
 * 
 * User class represents a single user of the application and is used to keep track of user information while connected to the system.
 * Information is retrieved upon login and only used on the server side of the application.
 * 
 * @author tuteredurie
 *
 */

public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private final String firstName;
    private final String lastName;
    private final String ipAddress;
 
    /**
     * 
     * Main constructor for the user class
     * 
     * @param fName represents the first name of the user
     * @param lName represents the surname of the user
     * @param ip represents the ip address that the user connected with.
     */
    
    public User(String fName, String lName, String ip) {
        this.firstName = fName;
        this.lastName = lName;
        this.ipAddress = ip;
    }

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}


	public String getIpAddress() {
		return ipAddress;
	}


	@Override
	public String toString() {
		return "Client [firstName=" + firstName + ", lastName=" + lastName + ", ipAddress=" + ipAddress + "]";
	}
}
