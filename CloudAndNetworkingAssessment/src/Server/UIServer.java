package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import Common.Message;
import Common.User;
import Databases.CloudDB;
import Databases.Database;
import Security.DES;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


/**
 * 
 * UIServer runs the user interface for the server side of the chatting application.
 * <p>
 * The UI includes a control panel for configuration of ports and IP addresses, as well as selection of recipient. 
 * It also includes a chat box to visualize messages to and from a client.
 * 
 * @author tuteredurie
 * @version 1.0
 * @since 1.0
 */
public class UIServer extends Application {
	
	private TableView<User> tableConnected;
	final ObservableList<User> connectedList;
	private TableView<Message> messagesTable;
	final ObservableList<Message> messagesList;
	private ServicedServer servicedServer;
	private HashMap<String, Socket> clients;
	
	private Database database;
	private CloudDB cloudDB;
	
	private ObservableList<String> recipientsList;
	
	public static String recipient;
	
	private DES des1;
	
	
	public UIServer() throws IOException, InterruptedException, ExecutionException, NoSuchAlgorithmException, ClassNotFoundException {
	
	des1 = new DES();
	des1.saveKey("sessionKey1.key");
	
	tableConnected = new TableView<User>();
	connectedList = FXCollections.observableArrayList();
	messagesTable = new TableView<Message>();
	messagesList = FXCollections.observableArrayList();
	recipientsList = FXCollections.observableArrayList();
	
	clients = new HashMap<>();
	database = new Database();
	cloudDB = new CloudDB();
	}

	/**
	 * 
	 * The start method for the Server UI. This sets up the overall layout, as well as the interaction between buttons within the UI.
	 * Communication with databases to send and receive messages and get user information is also handled in this method.
	 * 
	 * @param primaryStage represents a stage/window upon which to place the various UI elements. SHould be non null.
	 * 
	 */	
	public void start(Stage primaryStage) throws Exception {

		// port and ip display

		TextField portInput = new TextField();
		Label portLabel = new Label();
		portLabel.setText("Enter Port #:");
		
		HBox portHBox = new HBox(10);
		portHBox.getChildren().addAll(portLabel, portInput);

		Label ipAddressLabel = new Label();

		// button set up
		Button serverButton = new Button();
		serverButton.setText("Start Server");

		Button serverOffButton = new Button();
		serverOffButton.setText("Stop Server");

		// Hbox button set up
		HBox buttonsBox = new HBox(10);
		buttonsBox.getChildren().addAll(serverButton, serverOffButton);
		
		//feebacklabel
		Label feedback = new Label();
		feedback.setText("Enter Port to connect");


		Label tableLabel = new Label();
		tableLabel.setText("Users who have connected so far this session:");
		tableLabel.setFont((Font.font ("Verdana", FontWeight.BOLD, 12)));

		tableConnected.setEditable(true);
		 
        TableColumn firstNameCol = new TableColumn("First Name");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<User, String>("firstName"));
 
        TableColumn lastNameCol = new TableColumn("Last Name");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(
                new PropertyValueFactory<User, String>("lastName"));
 
        TableColumn ipCol = new TableColumn("IP Address");
        ipCol.setMinWidth(200);
        ipCol.setCellValueFactory(
                new PropertyValueFactory<User, String>("ipAddress"));
 
        tableConnected.setItems(connectedList);
        tableConnected.getColumns().addAll(firstNameCol, lastNameCol, ipCol);
        
        
        //messages table     
        Label MessagestableLabel = new Label();
        MessagestableLabel.setText("Messages");
        MessagestableLabel.setFont((Font.font ("Verdana", FontWeight.BOLD, 12)));

		messagesTable.setEditable(true);
		 
        TableColumn senderCol = new TableColumn("Sender");
        senderCol.setMinWidth(100);
        senderCol.setCellValueFactory(
                new PropertyValueFactory<Message, String>("sender"));
 
        TableColumn receiverCol = new TableColumn("Receiver");
        receiverCol.setMinWidth(100);
        receiverCol.setCellValueFactory(
                new PropertyValueFactory<Message, String>("receiver"));
 
        TableColumn timestampCol = new TableColumn("Timestamp");
        timestampCol.setMinWidth(200);
        timestampCol.setCellValueFactory(
                new PropertyValueFactory<Message, String>("timeStamp"));
        
        TableColumn messageCol = new TableColumn("Message");
        messageCol.setMinWidth(200);
        messageCol.setMaxWidth(250);
        messageCol.setCellValueFactory(
                new PropertyValueFactory<Message, String>("message"));
        
        
        messagesTable.setItems(messagesList);
        messagesTable.getColumns().addAll(senderCol, receiverCol, timestampCol, messageCol);
		
        
        //Message Input
      	TextField messageText = new TextField();
      	messageText.setPromptText("Input message to send");
      		
      	Button sendButton = new Button();
      	sendButton.setText("Send");
      		
      	HBox messageBox = new HBox(10);
      	messageBox.getChildren().addAll(messageText, sendButton);
      	HBox.setHgrow(messageText, Priority.ALWAYS);
      	
      	//recipient input
      	Label recipientLabel = new Label();
      	recipientLabel.setText("Select a recipient: ");
      	
	
      	final ComboBox<String> comboBox = new ComboBox<String>(recipientsList);
      	
      			
      	HBox recipientBox = new HBox(10);
      	recipientBox.getChildren().addAll(recipientLabel, comboBox);
      	
		VBox bigBox = new VBox(10);
		bigBox.getChildren().addAll(portHBox, buttonsBox, feedback, tableLabel, tableConnected,MessagestableLabel, messagesTable,recipientBox,messageBox);
		bigBox.setPadding(new Insets(15,15,15,15));

		// button action set up
		
		serverButton.setOnAction(value -> {
			if(portInput.getText().equals("")) {
				feedback.setText("Invalid port entered");
			}
			else {
			
				try {
					
					String port = portInput.getText();
					int portGo = Integer.valueOf(port);
	
					servicedServer = new ServicedServer(portGo, connectedList,messagesList,clients,cloudDB);
					servicedServer.start();
					
					feedback.setText("Connected to port: " + portGo);
					feedback.setTextFill(Color.GREEN);
					
					
					//fill the recipients box upon server starting		
					for (String user: database.getAllUsers()) {
			      		recipientsList.add(user);
			      	}
					recipientsList.add("View all");
							
				} catch (Exception e) {
					feedback.setText("Could not connect to port: " + portInput.getText());
				}
			}
		});
		
		serverOffButton.setOnAction(value -> {
			try {
				
				Server s = servicedServer.getServer();				
				
				if(s != null) {
					feedback.setText("Server stopped");
					feedback.setTextFill(Color.RED);
					s.myStop();
				
				} else {
					System.out.println("");
				} 
				
			}catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		sendButton.setOnAction(e -> { //need to be able to send if offline!!
			try {
				if (recipient == null) {
					
				} else {
					Socket clientSocket = clients.get(recipient.trim());
	
					String messageToServer = messageText.getText();
					byte[] encText = des1.encrypt(messageToServer);
					String encTextAsString = Base64.getEncoder().encodeToString(encText); 
					cloudDB.addMessage(new Message("Server", recipient, new java.util.Date().toString(), encTextAsString));
					
					if (clientSocket != null) {
						PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
						out.println("message");
					} 															
					messageText.clear();
					getMessages();
				}	
			} catch (Exception error) {
				error.printStackTrace();
			}			
		});
		
		
		comboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			try {		
				recipient = newValue.trim();			
				getMessages();						
			  } catch (Exception e) {
				// TODO: handle exception
			  } 
			}); 


		// scene set up
		primaryStage.setScene(new Scene(bigBox, 700, 700));
		primaryStage.sizeToScene();
		primaryStage.setTitle("Chat Box Severside");
		primaryStage.show();

	}
	
	/**
	 * getMessages requests messages from the server side of the application (through use of a database) for the current user in 
	 * control of the ClientUI. Messages will be received encrypted and will then use a session DES key to decrpyt messages before 
	 * being added to the UI.
	 * 
	 */
	public void getMessages () {
		try {
			messagesList.clear();
			
			if (recipient.equals("View all")) {
				for (Message message:cloudDB.getAllMessages()) {
					byte [] backToBytes = Base64.getDecoder().decode(message.getMessage()); 
					String decStr = des1.decrypt(backToBytes);
					message.setMessage(decStr);
					messagesList.add(message);
				}			
			} else {
				for (Message message:cloudDB.getMessages("Server", recipient)) {
					byte [] backtoBytes = Base64.getDecoder().decode(message.getMessage()); 
					String decStr = des1.decrypt(backtoBytes);
					message.setMessage(decStr);
					messagesList.add(message);
				}			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public static void main(String[] args) {
		launch(args);
	}
}
