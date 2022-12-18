package Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import Common.Message;
import Common.User;
import Databases.CloudDB;
import Databases.Database;
import Security.DES;
import javafx.collections.ObservableList;

/**
 * ServerThread represents a single thread that handles client requests and the associated business logic.
 * Strings are received from clients upon which the ServerTHread will carry out an action related to messages, user information or connection.
 * 
 * @author tuteredurie
 * @version 1.0
 * @since 1.0
 */

public class ServerThread extends Thread{

	private Socket socket;
	private ObservableList<User> connectedList;
	private ObservableList<Message> messagesList;
	private HashMap<String, Socket> clients;
	private DES des1;
	private CloudDB cloudDB;
		
	
	/**
	 * Main constructor for the ServerThread class.
	 * 
	 * @param port represents the port that will be used to set up a communication socket.
	 * @param connectedList represents the list of clients/users that have connected during the current session.
	 * @param messagesList represents the list of messages to show on the server UI.
	 * @param clients represents a list of current clients/users and their associated sockets for future communication.
	 * @param cloudDB represents the cloud database to store and retrieve messages.
	 */
	public ServerThread(Socket s, ObservableList<User> connectedList,ObservableList<Message> messagesList,
			HashMap<String, Socket> clients, CloudDB cloudDB) throws IOException, InterruptedException, ExecutionException {
		this.socket = s;
		this.connectedList = connectedList;
		this.messagesList = messagesList;
		this.clients = clients;
		this.cloudDB = cloudDB;
		try {
			des1 = new DES();
			des1.loadKey("sessionKey1.key");
		} catch (NoSuchAlgorithmException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * runs all the business logic related to different client requests throughout the application
	 * 
	 */
	public void run() {
		try {

			Database database = new Database();
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));			
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());			
			
			String answer = input.readLine();		
			
			if(answer.equals("connect")) {
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

				out.println("connectionSuccess");				
				
				String clientFName = input.readLine();
				String clientLName = input.readLine();
				String clientipAddress = input.readLine();
				
				String username = input.readLine();
				
				oos.writeObject(database.getAllUsers());
				oos.flush();
				
				User user = new User(clientFName, clientLName, clientipAddress);
				connectedList.add(user);
				
				clients.put(username, socket);
								
			} else if (answer.equals("message")) {
				
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);					
				
				String sender =  answer = input.readLine();
				String receiver =  answer = input.readLine();
				String timestamp =  answer = input.readLine();
				String message =  answer = input.readLine();
				
				Message messageObj = new Message(sender, receiver, timestamp, message);
				
				if (receiver.equals("Server") || UIServer.recipient.equals("View all")) {
					byte [] backtobytes = Base64.getDecoder().decode(messageObj.getMessage()); 
					String decStr = des1.decrypt(backtobytes);
					
					messagesList.add(new Message(sender, receiver, timestamp, decStr));
				}
									
				if (sender.equals("null") || receiver.equals("null")) {
					
				} else {
					Socket receiverSocket = clients.get(receiver);	
					if (receiverSocket != null ) {
						PrintWriter out2 = new PrintWriter(receiverSocket.getOutputStream(), true);
						out2.println("message");
						out2.println(answer);
					}
					cloudDB.addMessage(messageObj);
				}
				
			} else if (answer.equals("getMessages")) {
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);			
				
				answer = input.readLine();
				String[] temp;
				temp = answer.split(",");
				String username =  temp[0];
				String recipient =  temp[1];
				
				ArrayList<Message> messages = cloudDB.getMessages(username, recipient);			
				oos.writeObject(messages);
				
			} else if (answer.equals("check login")) {
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);			
				
				answer = input.readLine();
				String[] temp;
				temp = answer.split(",");
				String username =  temp[0];
				String password =  temp[1];
						
				oos.writeBoolean(database.VerifyUser(username, password));
				oos.writeObject(database.getUser(username, password));
				
			} else if(answer.equals("createUser")) {
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					
				String username = input.readLine();
				String firstName = input.readLine();
				String lastName = input.readLine();				
				String password = input.readLine();
				
				String createUserAttempt = database.addUser(username, firstName, lastName, password);
				
				out.println(createUserAttempt);
							
				clients.put(username, socket);
				
			}
				
					
		} catch (Exception e) {
			
		}
	
	}
	
}
