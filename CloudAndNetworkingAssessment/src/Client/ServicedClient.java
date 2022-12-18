package Client;



import java.net.Socket;
import Common.Message;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.layout.VBox;


/**
 * Serviced Client extends the Service class to allow for threading to take place with the client side of the chatting application.
 * It simply sets up a separate Client object and then starts that object/thread.
 * 
 * @author tuteredurie
 * @version 1.0
 * @since 1.0
 */
public class ServicedClient extends Service <String>{

	Client client;
	Socket socket;
	UIClient uiclient;
	
	
	/**
	 * 
	 * Constructor takes in parameters to be passed to the Client object.
	 * 
	 * @param socket represents a particular socket for an individual client. Should be non null. Can be used to send and receive information from a server socket.
	 * @param uiclient represents a client UI for a particular user of the system.
	 */
	public ServicedClient (Socket socket, UIClient uiclient)
	{
		
		setOnSucceeded(event -> 
			{
//				System.out.println("This is setOnSucceeded in client " + client);
				
			});
		
				
		this.socket = socket;
		this.uiclient = uiclient;
	}
	


	@Override
	protected Task<String> createTask() {
		// TODO Auto-generated method stub
		
		return new Task <String>()
		{
			@Override
			protected String call() throws Exception {
				
				client = new Client(socket, uiclient);
				client.start();
				
				return "nothing";
			}
		};
        
	}
}

