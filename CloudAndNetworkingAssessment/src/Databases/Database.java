package Databases;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import Common.User;
import Security.DES;

/**
 * This class connects to an external php sql database and runs various queries to add and 
 * retrieve information regarding users.
 * 
 * @author tuteredurie
 * @version 1.0
 * @since 1.0
 */
public class Database{
	
	private DES des1;
	
	
	
	public Database () {
	
	}
	
	/**
	 * Checks whether or not some supplied user login information is valid and matches an account in the database.
	 * 
	 * @param username represents the username of the user making the request
	 * @param password represents the password of the user making the request
	 * @return returns a boolean true if the name exists in the database and false if not.
	 */
	public Boolean VerifyUser(String username, String password){

		try {
			String databaseUser = "myuser";
			String databaseUserPass = "mypass"; 
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = null;
			String url = "jdbc:mysql://localhost/Networking";
			connection = DriverManager.getConnection(url, databaseUser, databaseUserPass);
			PreparedStatement stmt = connection.prepareStatement("select * from Users WHERE username = ? AND password = ?");
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				return true;
			}
			rs.close();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Login Error: " + e.toString());
		}
		return false;
	}
	
	
	/**
	 * 
	 * Gets the acocunt information for a particular user based on the login credentials added.
	 * 
	 * @param username represents the username of the user making the request
	 * @param password password represents the password of the user making the request
	 * @return returns a User object based on matching account details in the database
	 */
	public User getUser(String username, String password){
		User user = null;
		try {
			String databaseUser = "myuser";
			String databaseUserPass = "mypass"; 
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = null;
			String url = "jdbc:mysql://localhost/Networking";
			connection = DriverManager.getConnection(url, databaseUser, databaseUserPass);
			PreparedStatement stmt = connection.prepareStatement("select * from Users WHERE username = ? AND password = ?");
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
	
			if (rs.next()) {
				user = new User(rs.getString("First Name"), rs.getString("Last Name"), "localhost");
			}
			rs.close();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Login Error: " + e.toString());
		}
		return user;
	}
	
	
	/**
	 * adds a user and returns feedback in string form to the requester
	 * 
	 * @param username username of the user to be added
	 * @param fname first name of the user to be added
	 * @param lname last name of the user to be added
	 * @param password password of the user to be added
	 * @return returns either a success or failure string depending on if the user account information already exists.
	 */
	public String addUser (String username, String fname, String lname, String password) {
		try {
		
		String databaseUser = "myuser";
		String databaseUserPass = "mypass"; 
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection connection = null;
		String url = "jdbc:mysql://localhost/Networking";
		connection = DriverManager.getConnection(url, databaseUser, databaseUserPass);
		PreparedStatement stmt = connection.prepareStatement("INSERT INTO `Users` (`Username`, `First Name`, `Last Name`, `Password`) VALUES (?,?,?,?)");
		stmt.setString(1, username);
		stmt.setString(2, fname);
		stmt.setString(3, lname);
		stmt.setString(4, password);
		stmt.executeUpdate();
		
		connection.close();
		
		return "success";
	
		} catch (Exception e) {
			return "Fail: " + e.toString();
		}
	
	}
	/**
	 * 
	 * @return returns all users in the connected database
	 */
	public ArrayList<String> getAllUsers(){
		ArrayList<String> users = new ArrayList<>();
		try {
			String databaseUser = "myuser";
			String databaseUserPass = "mypass"; 
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = null;
			String url = "jdbc:mysql://localhost/Networking";
			connection = DriverManager.getConnection(url, databaseUser, databaseUserPass);
			Statement s = connection.createStatement();
			ResultSet rs = s.executeQuery("select * from Users");
			
			while (rs.next()) {
				users.add(rs.getString("Username"));
			}
			rs.close();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}
}

