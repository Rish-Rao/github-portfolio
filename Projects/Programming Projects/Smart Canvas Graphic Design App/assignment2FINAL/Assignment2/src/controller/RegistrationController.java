package controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.ImageIO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import model.User;



public class RegistrationController {
	
	@FXML 
	private Button register;
	
	@FXML
	private Button close;
	
	@FXML 
	private Button changePP;
	
	@FXML
	private Label errormessage;
	
	@FXML
	private Label success;
	
	@FXML
	private TextField firstname;
	
	@FXML
	private TextField lastname;
	
	@FXML
	private TextField username;
	
	@FXML
	private PasswordField password;
	
	@FXML
	private ImageView profilepic;
	
	@FXML
	private Stage stage;
	
	private User newUser;
	
	private File defaultImage = new File("images/defaultPP.jpeg");
	
	private File chosenImage;
	
	private FileChooser ppChooser;
	
	private InputStream imageinputstream;
	
	private ExtensionFilter imgfilter;
	
	private String fileformat = null;
	
	private byte[] bytearrayforImage;
	
	private ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
	
	//private File path;
	
	public void intialize () {
	}
	
	public void closeOnAction(ActionEvent e) {
		stage.close();
	}
	
	public void registerUser(ActionEvent event) {
		newUser = new User(username.getText(), password.getText(), firstname.getText(), lastname.getText(), bytearrayforImage);
//		try {
//			Connection connection = DriverManager.getConnection("jdbc:sqlite:user.db");
//			//Connection con = DatabaseConnection.getConnection();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		
		try {
			//System.out.println("gay");
			Connection connection = DriverManager.getConnection("jdbc:sqlite:user.db");
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS User (username VAR(50) NOT NULL,"
					+ "firstname VARCHAR(50) NOT NULL,"  + "lastname VARCHAR(50) NOT NULL," + "Hashpassword REAL NOT NULL," + "password VARCHAR(50) NOT NULL," + "profilepic BLOB NOT NULL," + "PRIMARY KEY (username))");
			//Connection con = DatabaseConnection.getConnection(); 
			int check = newUser.addUsertoDB();
			System.out.println(check);
			if (check == 0) {
				errormessage.setText("Username already exist, please try another one.");
			} else if (check == -1) {
				errormessage.setText("Firstname, Lastname and Username must not contain white spaces, please try again.");
			} else if (check == 1)  {
				success.setText("User has been created!");
			} else if (check == -2) {
				errormessage.setText("One of the texfields were left empty, please try again with all the fields filled in.");
			} else {
				errormessage.setText("Error, please try again another time.");
			}
			
			
			firstname.clear();
			lastname.clear();
			username.clear();
			password.clear();
			
			
			BufferedImage renderedImage;
			try {
				renderedImage = ImageIO.read(defaultImage);
				String filename = defaultImage.getPath();
				int i = filename.lastIndexOf(".");
				if (i > 0) {
					fileformat = filename.substring(i+1);
				}
				ImageIO.write(renderedImage, fileformat, outputstream);
				bytearrayforImage = outputstream.toByteArray();
				
				imageinputstream = new FileInputStream(defaultImage);
				profilepic.setImage(new Image(imageinputstream));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} 

	}
	
	public void setPP(ActionEvent event) {
		ppChooser = new FileChooser();
		//ppChooser.setTitle("Select image");
		imgfilter = new FileChooser.ExtensionFilter("*.png", "*.jpg", "*.jpeg");
		ppChooser.getExtensionFilters().add(imgfilter);
		chosenImage = ppChooser.showOpenDialog(stage);
		try {
			System.out.println(chosenImage.getCanonicalPath());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			try {
				BufferedImage renderedImage = ImageIO.read(chosenImage);
				String filename = chosenImage.getPath();
				int i = filename.lastIndexOf(".");
				if (i > 0) {
					fileformat = filename.substring(i+1);
				}
				ImageIO.write(renderedImage, fileformat, outputstream);
				bytearrayforImage = outputstream.toByteArray();
				
				imageinputstream = new FileInputStream(chosenImage);
				profilepic.setImage(new Image(imageinputstream));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IllegalArgumentException e) {
			System.out.println("Image not chosen");
		}
		//System.out.println(profilepic.getImage().getUrl());
	}

	public void setStage(Stage stage) {
		this.stage = stage;
		
	}
	
}
