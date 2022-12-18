package Client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import javafx.application.Platform;


/**
 * 
 * Client sets up a thread separate to the CLient UI to allow for infinite listening of any incoming messages which can then be processed back to
 * the ClientUI for the user to view.
 * 
 * @author tuteredurie
 * @version 1.0
 * @since 1.0
 */
public class Client extends Thread{
	private BufferedReader input;
	private UIClient uiclient;
	
	/**
	 * the main constructor for the Client class.
	 * 
	 * @param socket represents a particular socket for an individual client. Should be non null. Can be used to send and receive information from a server socket.
	 * @param uiclient represents a client UI for a particular user of the system.
	 */	
	public Client(Socket socket,UIClient uiclient)throws IOException {
		
		this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.uiclient = uiclient;
    }
	
	/**
	 * run is inherited from the Thread class. It represents the infinite listening that needs to take place for the client
	 * and then allows the ClientUI to refresh the messages on the user screen, once a message has been received.
	 * 
	 */
	@Override
	public void run () {
		while (true) {

			try {
				String answer = input.readLine();
					
				if (answer.equals("message")) {
											
					Platform.runLater(new Runnable() {
			            @Override
			            public void run() {
			              uiclient.getMessages();
			            }
			        });
				}
			} catch (Exception e) {
			
			}
			
		}
	}	

}

