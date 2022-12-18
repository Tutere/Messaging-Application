package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import Common.Message;
import Common.User;
import Databases.CloudDB;
import Databases.Database;
import javafx.collections.ObservableList;

/**
 * 
 * Server represents the application server and sets up separate threads to allow for infinite listening of any incoming connection requests which can then be 
 * used to setup a new server thread for further processing. 
 * 
 * The extension of thread is to allow for easier stopping of the server
 * 
 * @author tuteredurie
 * @version 1.0
 * @since 1.0
 */
public class Server extends Thread { 
	ServerSocket listener;
	ObservableList<User> connectedList;
	ObservableList<Message> messagesList;
	HashMap<String, Socket> clients;
	Socket socket;
	String username;
	
	Database database;
	CloudDB cloudDB;
	
	BufferedReader input;
	PrintWriter out;
	ObjectOutputStream oos;
	
	/**
	 * Main constructor for the Server class
	 * 
	 * 
	 * @param port represents the port that will be used to set up a communication socket.
	 * @param connectedList represents the list of clients/users that have connected during the current session.
	 * @param messagesList represents the list of messages to show on the server UI.
	 * @param clients represents a list of current clients/users and their associated sockets for future communication.
	 * @param cloudDB represents the cloud database to store and retrieve messages.
	 */
	public Server(int port, ObservableList<User> connectedList,ObservableList<Message> messagesList,HashMap<String, Socket> clients, CloudDB cloudDB)throws IOException, ExecutionException {
		
		listener = new ServerSocket(port);
		this.connectedList = connectedList;
		this.messagesList = messagesList;
		this.clients = clients;
		this.cloudDB = cloudDB;
	}
		
	@Override
	public void run () {
		try {
			while(true && listener!=null) {
				Thread.sleep(1000);
				Socket socket = listener.accept();
				ServerThread st =  new ServerThread(socket, connectedList, messagesList, clients,cloudDB);
				st.start();

			}
			
		} catch (InterruptedException | IOException | ExecutionException | NullPointerException e) {
			
		} 
	}
	
	/**
	 * stops the application server
	 * 
	 */
	public void myStop()
	{
		try {
			listener.close();
			listener = null;
			System.out.println("Server Stopped!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
