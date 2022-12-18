package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import Common.Message;
import Common.User;
import Databases.CloudDB;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

/**
 * ServicedServer extends the Service class to allow for threading to take place with the server side of the chatting application.
 * It simply sets up a separate Server object and then starts that object/thread. It also includes a method to stop the server.
 * 
 * @author tuteredurie
 * @version 1.0
 * @since 1.0
 */

public class ServicedServer extends Service <String>{
	private int port;
	private ObservableList<User> connectedList;
	private ObservableList<Message> messagesList;
	private Server server;
	private HashMap<String, Socket> clients;
	CloudDB cloudDB;
	
	
	/**
	 * 
	 * @param port represents the port that will be used to set up a communication socket.
	 * @param connectedList represents the list of clients/users that have connected during the current session.
	 * @param messagesList represents the list of messages to show on the server UI.
	 * @param clients represents a list of current clients/users and their associated sockets for future communication.
	 * @param cloudDB represents the cloud database to store and retrieve messages.
	 */
	public ServicedServer (int port, ObservableList<User> connectedList, ObservableList<Message> messagesList,HashMap<String, Socket> clients, CloudDB cloudDB) throws IOException, InterruptedException, ExecutionException
	{
		
					
		this.port = port;
		this.connectedList = connectedList;
		this.messagesList = messagesList;
		this.clients = clients;
		this.cloudDB = cloudDB;
	}
	


	@Override
	protected Task<String> createTask() {
		
		return new Task <String>()
		{
			@Override
			protected String call() throws Exception {
				
				server = new Server(port, connectedList,messagesList,clients, cloudDB);
				server.start();
				return "nothing";
			}
		};        
	}
	
	
	/**
	 * stops the server
	 * 
	 */
	public void myStop() throws Exception {
		if(server != null)
		{
			server.myStop();
			System.out.println("server stopped");
		}
		else
			System.out.println("This is not Ok");	
	}
	
	/**
	 * @return returns the server
	 */
	public Server getServer() {
		return server;
	}
}
