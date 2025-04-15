package model;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;




public class User {
	private String username;
	private String password;
	private String firstname;
	private String lastname;
	private byte[] profilePhoto;

	public User() {
		
	}
	public User(String username, String password, String firstname, String lastname, byte[] pp) {
		this.username = username;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.profilePhoto = pp;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public String getLastname() {
		return this.lastname;
	}

	public byte[] getProfilepicture() {
		return this.profilePhoto;
	}

	public void setProfilepicture(byte[] image) {
		this.profilePhoto = image;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public int verifyUserLogin(String name, String password) {
		int number = 0;
		if (!name.isEmpty() && !password.isEmpty()) {
			try {
				Connection connection = DriverManager.getConnection("jdbc:sqlite:user.db");
				Statement stmt = connection.createStatement();
				String query = "SELECT * FROM User";
				boolean found = false;
				try (ResultSet resultSet = stmt.executeQuery(query)) {
					while(resultSet.next() && !found) {		
						System.out.println(name);
						System.out.println(resultSet.getString("username"));
						System.out.println(password);
						System.out.println(resultSet.getString("password"));
						if (resultSet.getString("username").compareTo(name) == 0 && resultSet.getString("password").compareTo(password) == 0) {
							System.out.println("gay");
							found = true;
							number = 1;
						} 
					}
					if (!found) {
						number = -1;
					}
				} 
				System.out.println(number);	
				return number;
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
			
		return number;
	}

	public int addUsertoDB() {
		System.out.println(username);
		System.out.println(firstname);
		System.out.println(lastname);
		System.out.println(password);
		System.out.println(profilePhoto);

		//System.out.println("qwedqwejqwdnnf");
		int check = 2;
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:user.db");
			Statement stmt = connection.createStatement();
			PreparedStatement pstmt = connection.prepareStatement("INSERT INTO User VALUES (?, ?, ?, ?, ?, ?)");

			String query = "SELECT * FROM User";

			try (ResultSet resultSet = stmt.executeQuery(query)) {
				System.out.println("gay");
				if (!resultSet.next()) {
					if (username.contains(" ") || lastname.contains(" ") || firstname.contains(" ")) {
						check = -1;
					} else if (username == null || firstname == null || lastname == null || password == null) {
						check = -2;
					} else {
						pstmt.setString(1, username);
						pstmt.setString(2, firstname);
						pstmt.setString(3, lastname);
						pstmt.setInt(4, hashPassword());
						pstmt.setString(5, password);
						pstmt.setBytes(6, profilePhoto);
						pstmt.addBatch();
						pstmt.executeBatch();
						pstmt.close();
						check = 1;
					}
				} else {
					//System.out.println("gay");
					while (resultSet.next()) {
						if (resultSet.getString("username") == username) {
							check = 0;
						}
					}
					
					if (username.contains(" ") || lastname.contains(" ") || firstname.contains(" ")) {
						check = -1;
					} else if (username == null || firstname == null || lastname == null || password == null) {
						check = -2;
					} else if (check != 0) {
						pstmt.setString(1, username);
						pstmt.setString(2, firstname);
						pstmt.setString(3, lastname);
						pstmt.setInt(4, hashPassword());
						pstmt.setString(5, password);
						pstmt.setBytes(6, profilePhoto);
						pstmt.addBatch();
						pstmt.executeBatch();
						pstmt.close();

						check = 1;
						
					}
				}

			}
			System.out.println("asfnqerfj qewrf");
			return check;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return check;
	}
	
	public byte[] getUserPPDB(String username) {
		byte[] imagearray = null;
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:user.db");
			Statement stmt = connection.createStatement();
			String query = "SELECT * FROM User";
			boolean found = false;
			try (ResultSet resultSet = stmt.executeQuery(query)) {
				while(resultSet.next() && !found) {		
					if (resultSet.getString("username").compareTo(username) == 0) {
						found = true;
						imagearray =  resultSet.getString("profilepic").getBytes();
					} 
				}
			}
			return imagearray;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return imagearray;
	}

	

	public int hashPassword() {
		return password.hashCode();
	}
}
