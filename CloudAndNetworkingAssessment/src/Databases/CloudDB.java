package Databases;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.logging.Log;

import com.google.api.core.ApiFuture;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.internal.NonNull;

import Common.Message;
import Security.DES;

import com.google.common.collect.Lists;


/**
 * This class connects to an external Firestore database and runs various queries to add and 
 * retrieve messages.
 * 
 * @author tuteredurie
 * @version 1.0
 * @since 1.0
 */
public class CloudDB 
{
	private Firestore db;
	private DES des1;
	
	static GoogleCredentials authExplicit(String jsonPath) throws IOException {
		  GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath))
		        .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
		  
		  return credentials;		  
		}
	/**
	 * 
	 * @param credentials represents the credentials key for access to the firestore database
	 * @param projectId represents the ID of the firestore project
	 * @return returns the firestore instance base don credentials supplied
	 */
	public Firestore getInstance(GoogleCredentials credentials, String projectId)
	{
		FirebaseOptions options = new FirebaseOptions.Builder()
			    .setCredentials(credentials)
			    .setProjectId(projectId)
			    .build();
			FirebaseApp.initializeApp(options);
			Firestore db = FirestoreClient.getFirestore();
			
			return db;	
	}
	
	/**
	 * Takes a message object and adds that to the Firestore database. Messages are decrypted before being added to the database.
	 * @param message represents the message object to be added to the database
	 */
	public void addMessage(Message message) throws InterruptedException, ExecutionException
	{	
		try {
		DocumentReference docRef = db.collection("Messages").document("MessageDoc - " + new java.util.Date().toString());
		
		 Map<String, String> data = new HashMap<>();
		data.put("Sender", message.getSender());
		data.put("Receiver", message.getReceiver());
		data.put("Timestamp", message.getTimeStamp());
		
		byte [] backtobytes = Base64.getDecoder().decode(message.getMessage());
		String decStr = des1.decrypt(backtobytes);
			
		data.put("Message", decStr);
		ApiFuture<WriteResult> result = docRef.set(data);
		
		result.get();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param username represents the username of the user/client making the request
	 * @param selectedRecipient represents the username that the current user is wanting to view messages with
	 * @return returns a list of messages between two supplied users. Messages are encrypted before being returned.
	 */
	public ArrayList<Message> getMessages (String username, String selectedRecipient) throws InterruptedException, ExecutionException {
		
		ArrayList<Message> messages = new ArrayList<>();
		try {
		// asynchronously retrieve all documents
		ApiFuture<QuerySnapshot> future = db.collection("Messages").get();

		List<QueryDocumentSnapshot> documents = future.get().getDocuments();
		for (QueryDocumentSnapshot document : documents) {
			String sender = document.getString("Sender");
			String receiver = document.getString("Receiver");
			
			if ((sender.equals(username) && receiver.equals(selectedRecipient)) || (sender.equals(selectedRecipient) && receiver.equals(username))) {
				Message message = new Message(document.getString("Sender"), document.getString("Receiver"),
						document.getString("Timestamp"), document.getString("Message"));
				
				byte[] encText = des1.encrypt(message.getMessage());
				String byteString = Base64.getEncoder().encodeToString(encText); 
				message.setMessage(byteString);
				messages.add(message);
			}
		}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return messages;
	}
	
	/**
	 * 
	 * @return returns all messages in the Firestore database. Messages are encrypted before being returned.
	 */
	public ArrayList<Message> getAllMessages () throws InterruptedException, ExecutionException {
		
		ArrayList<Message> messages = new ArrayList<>();
		try {
			ApiFuture<QuerySnapshot> future = db.collection("Messages").get();
			List<QueryDocumentSnapshot> documents = future.get().getDocuments();
		
			for (QueryDocumentSnapshot document : documents) {
				Message message = new Message(document.getString("Sender"), document.getString("Receiver"),
						document.getString("Timestamp"), document.getString("Message"));
				byte[] encText = des1.encrypt(message.getMessage());
				String byteString = Base64.getEncoder().encodeToString(encText); 
				message.setMessage(byteString);
				messages.add(message);
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return messages;
	}
	
	/**
	 * 
	 * Main constructor for the CloudDB class.
	 * 
	 * A DES object is created for all encryption/decryption work to be done in the class.
	 * 
	 */
	public CloudDB() throws IOException, InterruptedException, ExecutionException
	{
		
		try {
			des1 = new DES();
			des1.loadKey("sessionKey1.key");
		} catch (NoSuchAlgorithmException | ClassNotFoundException | IOException e) {
			
			e.printStackTrace();
		}
		
		// authentication
		GoogleCredentials credentials = authExplicit("/Users/tuteredurie/Downloads/networkingassessmentdb-e3a93fcfa732.json");
		// get the instance of the project
		db = getInstance(credentials, "networkingassessmentdb");
		
	}
	
}


