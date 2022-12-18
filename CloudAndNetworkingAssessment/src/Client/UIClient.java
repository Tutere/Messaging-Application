package Client;
import java.awt.color.ColorSpace;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;

import Common.Message;
import Common.User;
import Security.DES;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * 
 * UIClient runs the user interface for an individual client on the chatting application.
 * <p>
 * The UI includes a control panel for configuration of ports and IP addresses, as well as selection of recipient. 
 * It also includes a chat box to visualize messages to and from a client.
 * 
 * @author tuteredurie
 * @version 1.0
 * @since 1.0
 */
public class UIClient extends Application {
	
	private String ipAddress;
	private int port;
	private String firstName;
	private String lastName;
	private String username;
	private String recipient;
	
	final ObservableList<Message> messagesList = FXCollections.observableArrayList();
	
	private VBox messagesBox;
	
	private Scene landingScene;
	private Scene loginOrCreateScene;
	private Scene loginScene;
	private Scene createAccountScene;
	private Scene MainPageScene;
	
	private Socket clientSocket;
	private BufferedReader input;
	private PrintWriter out;
	
	private ServicedClient servicedClient;
	
	private ArrayList<String> users = new ArrayList<>();
	final ObservableList<String> recipientsList = FXCollections.observableArrayList();
	
	private DES des1;

	
	/**
	 * 
	 * The start method for the client UI. This sets up the overall layout, as well as the interaction between buttons within the UI.
	 * Communication with the server to send and receive messages, as well as get a list of users is also handled in this method.
	 * 
	 * @param primaryStage represents a stage/window upon which to place the various UI elements. SHould be non null.
	 * 
	 */	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		//Landing page setup
		
		VBox landingPage = new VBox();
		
		TextField portInput1 = new TextField();
		Label portLabel1 = new Label();
		portLabel1.setText("Enter Port Number: ");

		HBox portHBox1 = new HBox(10);
		portHBox1.getChildren().addAll(portLabel1, portInput1);

		TextField ipInput1 = new TextField();
		Label ipAddressLabel1 = new Label();
		ipAddressLabel1.setText("Enter IP Address: ");
		
		HBox ipAddressHBox1 = new HBox(10);
		ipAddressHBox1.getChildren().addAll(ipAddressLabel1, ipInput1);


		Button connectButton1 = new Button();
		connectButton1.setText("Connect to Server");
		Label connectLabel1 = new Label();
		connectLabel1.setText("Enter port and IP to connect to server");
		connectLabel1.setFont((Font.font ("Verdana", FontWeight.BOLD, 12)));
		
		
		landingPage.getChildren().addAll(connectLabel1, portHBox1,ipAddressHBox1, connectButton1);
		landingPage.setSpacing(15);
		landingPage.setPadding(new Insets(15,15,15,15));
		
		// Login or create account page
		VBox loginOrCreateAccountPage = new VBox();
		
		HBox LoginCreateBox = new HBox();
		LoginCreateBox.setPadding(new Insets(60, 40, 15, 40));
		LoginCreateBox.setSpacing(30);
		Button logInButton1 = new Button("Login");
		Button createAccountButton = new Button("Create Account");
		LoginCreateBox.getChildren().addAll(logInButton1,createAccountButton);
		
		loginOrCreateAccountPage.getChildren().add(LoginCreateBox);
		
		
		//Login Page Setup
		VBox loginPage = new VBox();
		loginPage.setAlignment(Pos.TOP_CENTER);
		
		
			//username hbox
			HBox usernameHbox = new HBox();
			usernameHbox.setPadding(new Insets(15, 40, 15, 40));
			usernameHbox.setSpacing(40);
			Label usernameLabel = new Label("Username");	
			TextField usernameField = new TextField();
			usernameHbox.getChildren().addAll(usernameLabel,usernameField);
			
			
			//Password hBox			
			HBox passwordHbox = new HBox();
			passwordHbox.setPadding(new Insets(15, 40, 15, 40));
			passwordHbox.setSpacing(40);
			Label passwordLabel = new Label("Password");
			PasswordField passwordField = new PasswordField();
			passwordHbox.getChildren().addAll(passwordLabel,passwordField);
			
			Button logInButton2 = new Button("Login");
			
			Text loginError = new Text ();
			loginError.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
			loginError.setFill(javafx.scene.paint.Color.RED);
			loginError.setWrappingWidth(300);
			
		loginPage.getChildren().addAll(usernameHbox, passwordHbox, logInButton2, loginError);
		

		//Create account page setup 
		VBox createAccountPage = new VBox();
		createAccountPage.setAlignment(Pos.TOP_CENTER);
		
			//person details hbox
			HBox userDetailsHbox = new HBox();
			userDetailsHbox.setPadding(new Insets(15, 40, 15, 40));
			userDetailsHbox.setSpacing(40);
			Label fNameLabel = new Label("First Name");	
			TextField fNameField = new TextField();
			Label LNameLabel = new Label("Last Name");	
			TextField lNameField = new TextField();
			userDetailsHbox.getChildren().addAll(fNameLabel,fNameField,LNameLabel,lNameField);
			
			
			//account details hBox			
			HBox accountDetailsHbox = new HBox();
			accountDetailsHbox.setPadding(new Insets(15, 40, 15, 40));
			accountDetailsHbox.setSpacing(40);
			Label usernameLabel2 = new Label("Username");	
			TextField usernameField2 = new TextField();
			Label passwordLabel2 = new Label("Password");
			TextField passwordField2 = new TextField();
			accountDetailsHbox.getChildren().addAll(usernameLabel2,usernameField2,passwordLabel2,passwordField2);
			
			Button createAccountButton2 = new Button("Create Account");
			
			Text createError = new Text ();
		
		createAccountPage.getChildren().addAll(userDetailsHbox,accountDetailsHbox,createAccountButton2, createError);
			
		
		//****** Setup of main page once logged in *********
		
		BorderPane root = new BorderPane();
		
		//left pane
		VBox controls = new VBox(15);
		controls.setPadding(new Insets(10,10,10,10));
		
		// port and ip display

		TextField portInput = new TextField("9011");
		Label portLabel = new Label();
		portLabel.setText("Enter Port #:");

		VBox portVBox = new VBox(10);
		portVBox.getChildren().addAll(portLabel, portInput);

		TextField ipInput = new TextField("localhost");
		Label ipAddressLabel = new Label();
		ipAddressLabel.setText("IP Address: ");
		
		VBox ipAddressVBox = new VBox(10);
		ipAddressVBox.getChildren().addAll(ipAddressLabel, ipInput);

		// button set up

		Button connectButton = new Button();
		connectButton.setText("Connect to Server");
		Label connectLabel = new Label();
		connectLabel.setText("Enter port and IP to connect to server");
		
		VBox connectVBox = new VBox(10);
		ipAddressVBox.getChildren().addAll(connectLabel, connectButton);
		
		
		//recipient input
		Label recipientLabel = new Label("Select a recipient: ");
		final ComboBox<String> comboBox = new ComboBox<String>(recipientsList );
		
		VBox recipientVBox = new VBox(10);
		recipientVBox.getChildren().addAll(recipientLabel, comboBox);
		
		
		  //labelto show who is logged in
        Label loginName = new Label();
        loginName.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
              
		controls.getChildren().addAll(loginName,portVBox, ipAddressVBox, connectVBox,recipientVBox);
		
		
		//add to left side of border pane
		root.setLeft(controls);
		
		
		//Scroll messages section
		AnchorPane centreBox = new AnchorPane();
		centreBox.prefHeight(300);
		centreBox.prefWidth(480);
		centreBox.setPadding(new Insets(10,15,10,10));
		
		ScrollPane chatScroll = new ScrollPane();
		chatScroll.setFitToWidth(true);
		chatScroll.setStyle("-fx-focus-color: black; -fx-border-style: solid solid solid solid; ");

	
		messagesBox = new VBox();
		messagesBox.prefWidth(600);
		

		messagesBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                chatScroll.setVvalue((Double) newValue);
            }
        });
			
		
		chatScroll.setContent(messagesBox);
			
		//Message Input
		TextField messageText = new TextField();
		messageText.setPromptText("Input message to send");
				
		Button sendButton = new Button();
		sendButton.setText("Send");
		
		HBox messageBox = new HBox(10);
		messageBox.getChildren().addAll(messageText, sendButton);
		HBox.setHgrow(messageText, Priority.ALWAYS);
		
		//anchor elements of chat box
		AnchorPane.setBottomAnchor(messageBox, 10.0);
		AnchorPane.setLeftAnchor(messageBox, 0.0);
		AnchorPane.setRightAnchor(messageBox, 0.0);
				
		AnchorPane.setTopAnchor(chatScroll, 0.0);
		AnchorPane.setBottomAnchor(chatScroll, 50.0);
		AnchorPane.setLeftAnchor(chatScroll, 0.0);
		AnchorPane.setRightAnchor(chatScroll, 0.0);
		
		centreBox.getChildren().addAll(chatScroll,messageBox);
		
		root.setCenter(centreBox);
		

		// button action set up
		
		connectButton1.setOnAction(value -> {
			try {
				ipAddress = ipInput.getText();
				port = Integer.valueOf(portInput.getText()) ;
								
				clientSocket = new Socket(ipAddress, port);
								
				changePrimaryStage(primaryStage, loginOrCreateScene);
				
			} catch (Exception e) {
				connectLabel1.setText("Error: Server connection has not yet been started");
				connectLabel1.setTextFill(javafx.scene.paint.Color.RED);
			}
			
			//encryption key setup
			
			try {
				des1 = new DES();
				des1.loadKey("sessionKey1.key");
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			
			
		});
		
		connectButton.setOnAction(value -> {
			try {
				ipAddress = ipInput.getText();
				port = Integer.valueOf(portInput.getText()) ;
				
				String connectionStatus = connectToServer();

				if(connectionStatus.equals("connectionSuccess")) {
					connectLabel.setText("Connection to port "+port+" success");
				}
				
				servicedClient = new ServicedClient(clientSocket,this);
				servicedClient.start();
				
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		});
		
		logInButton1.setOnAction(e -> changePrimaryStage(primaryStage, loginScene));
		
		createAccountButton.setOnAction(e -> changePrimaryStage(primaryStage, createAccountScene));
		
		logInButton2.setOnAction(e -> {
			username = usernameField.getText();
			String password = passwordField.getText();
			
			try {
			prepareStreams();	
			
			ObjectInputStream ois = new ObjectInputStream (clientSocket.getInputStream());	
			
					
			out.println("check login");
			out.println(username + "," + password);		

			if (ois.readBoolean() == true) {
				User client = (User) ois.readObject(); //do I need this anywhere?

				firstName = client.getFirstName();
				lastName = client.getLastName();
				ipAddress = client.getIpAddress();
				
				connectToServer();
				
				servicedClient = new ServicedClient(clientSocket,this);
				servicedClient.start();
				
				changePrimaryStage(primaryStage, MainPageScene);
				
				loginName.setText("You are logged in as: " + username);
				
				
			} else {
				ois.readObject();
				loginError.setText("Sorry, that username and password combination does not exist!");
			}
			
			} catch (Exception e2) {
				System.out.println(e.toString());
				e2.printStackTrace();
			}
		});
		
		
		createAccountButton2.setOnAction(e -> {
			try {	
			prepareStreams();	
							
			username = usernameField2.getText();
			firstName = fNameField.getText();
			lastName = lNameField.getText();
			String password = passwordField2.getText();
			
			out.println("createUser");
			
			out.println(username);
			out.println(firstName);
			out.println(lastName);
			out.println(password);			
			
			String answer = input.readLine();
			
			if (answer.contains("success")) {
				changePrimaryStage(primaryStage, MainPageScene);
				
				connectToServer();
				
				servicedClient = new ServicedClient(clientSocket,this);
				servicedClient.start();
				
				loginName.setText("You are logged in as: " + username);
				
				//encryption key setup
				des1 = new DES();
				des1.loadKey("sessionKey1.key");
				
			} else {
				createError.setText("Sorry, that username already exists in our database! Please choose a different one");
				createError.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
				createError.setFill(Color.RED);
			}
			
			} catch (Exception e2) {
				
			}
		});
		
		sendButton.setOnAction(e -> {
			try {			
				ipAddress = ipInput.getText();
				port = Integer.valueOf(portInput.getText()) ;
				
				prepareStreams();
					
				String messageToServer = messageText.getText();
				byte[] encText = des1.encrypt(messageToServer);
				String byteString = Base64.getEncoder().encodeToString(encText); 
				out.println("message"); 
								
				out.println(username);
				out.println(recipient); 
				out.println(new java.util.Date().toString()); 
				out.println(byteString); 

				messageText.clear();
				
				getMessages();
	
			} catch (Exception error) {
				System.out.println(error.toString());
				error.printStackTrace();
			}
				
		});
		
		comboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			  
			try {
				
				recipient = newValue.trim();
				ipAddress = ipInput.getText();
				port = Integer.valueOf(portInput.getText()) ;
				
				getMessages();
				
				
			  } catch (Exception e) {
				e.printStackTrace();
			  } 
			   
		}); 

		// setup different scenes
		
		landingScene = new Scene(landingPage, 400, 300);
		loginOrCreateScene = new Scene(loginOrCreateAccountPage, 300, 200);
		loginScene = new Scene(loginPage, 400,300);
		createAccountScene = new Scene(createAccountPage,700,300);
		MainPageScene = new Scene(root,500,600);
		

		// primary stage setup
		primaryStage.setScene(landingScene);
		primaryStage.sizeToScene();
		primaryStage.setTitle("Chat Box Clientside");
		primaryStage.show();

	}
	
	public String connectToServer() throws UnknownHostException, IOException {
		
		prepareStreams();
		ObjectInputStream ois = new ObjectInputStream (clientSocket.getInputStream());	
		
		out.println("connect");

		String answer = input.readLine();
		
		try {
		if(answer.equals("connectionSuccess")) {
			out.println(firstName);
			out.println(lastName);
			out.println(ipAddress);
			out.println(username);
		}
		
        users =  (ArrayList<String>) ois.readObject();
        for (String user: users) {
        	recipientsList .add(user);
        }
        recipientsList .add("Server");

			return answer;
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return answer;
	}
	
	/**
	 * getMessages requests messages from the server side of the application (through use of a database) for the current user in 
	 * control of the ClientUI. Messages will be received encrypted and will then use a session DES key to decrpyt messages before 
	 * being added to the UI.
	 * 
	 * 
	 */
	public void getMessages () {
		try {
		
			messagesBox.getChildren().clear();
			
			prepareStreams();
			ObjectInputStream ois = new ObjectInputStream (clientSocket.getInputStream());
			
			out.println("getMessages");
			out.println(username + "," + recipient);
			
			ArrayList<Message> messages = (ArrayList<Message>) ois.readObject();
			
			for(Message message:messages) {
				byte [] backtobytes = Base64.getDecoder().decode(message.getMessage()); 
				String decStr = des1.decrypt(backtobytes);
				message.setMessage(decStr);
				
				if (message.getSender().equals(username)) {
					addSentMessageToChat(messagesBox, message);
				} else {
					addReceivedMessageToChat(messagesBox, message);
				}
				
			}
		
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	private void changePrimaryStage (Stage primaryStage, Scene sceneToChangeTo) {
		primaryStage.setScene(sceneToChangeTo);
	}
	
	/*
	 * The below method is used as a workaround to avoid stream corruption exceptions. It resets the 
	 * communication streams to be able to exchange  information/messages.
	 */
	private void prepareStreams () {
				
		try {
			clientSocket = new Socket(ipAddress, port);
			input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private void addReceivedMessageToChat (VBox messages, Message message) {
		 HBox messageBox = new HBox();
	     messageBox.setAlignment(Pos.CENTER_LEFT);
	     messageBox.setPadding(new Insets(5, 5, 5, 10));

	     Text text = new Text(message.getMessage());
	     TextFlow textFlow = new TextFlow(text);

	     textFlow.setStyle(
	                     "-fx-background-color: rgb(233, 233, 235);" +
	                     "-fx-background-radius: 20px;");

	     textFlow.setPadding(new Insets(5, 10, 5, 10));
	     messageBox.getChildren().add(textFlow);
	     
	     messages.getChildren().add(messageBox);
	}
	
	private void addSentMessageToChat (VBox messages, Message message) {
		 HBox messageBox = new HBox();
	     messageBox.setAlignment(Pos.CENTER_RIGHT);
	     messageBox.setPadding(new Insets(5, 5, 5, 10));

	     Text text = new Text(message.getMessage());
	     text.setFill(javafx.scene.paint.Color.WHITE);
	     TextFlow textFlow = new TextFlow(text);

	     textFlow.setStyle(
	    		   "-fx-color: rgb(239, 242, 255);" +
                           "-fx-background-color: rgb(15, 125, 242);" +
                           "-fx-background-radius: 20px;");

	     textFlow.setPadding(new Insets(5, 10, 5, 10));
	     messageBox.getChildren().add(textFlow);
	     
	     messages.getChildren().add(messageBox);
	}
	
	public static void main(String[] args) {
		launch(args);

	}
}
