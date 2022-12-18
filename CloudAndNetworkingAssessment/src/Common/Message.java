package Common;
import java.io.Serializable;


/**
 * Message represents a message between clients and the server. Messages need to include a sender, recipient, timestamp and message to be shown 
 * on the various UI. Messages are also stored in databases and undergo encryption/decryption by clients and the server.
 * 
 * @author tuteredurie
 * @version 1.0
 * @since 1.0
 */
public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String sender;
	private String receiver;
	private String timeStamp;
	private String message;
	
	/**
	 * Constructor for the Message class.
	 * 
	 * @param sender represents the sender of the message. Should be non null.
	 * @param receiver represents the receiver of the message. Should be non null.
	 * @param tiemSTamp represents the time that the message was sent. Should be non null.
	 * @param message represents the content of the message to bve sent.
	 */
	
	public Message (String sender, String receiver, String tiemSTamp, String message) {
		this.sender = sender;
		this.receiver = receiver;
		this.timeStamp = tiemSTamp;
		this.message = message;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
