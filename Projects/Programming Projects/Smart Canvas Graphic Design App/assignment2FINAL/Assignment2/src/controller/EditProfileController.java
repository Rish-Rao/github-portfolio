package controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.ImageIO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class EditProfileController {
	
	@FXML
	private ImageView profilepicEdit;
	
	@FXML
	private Label name;
	
	@FXML
	private TextField firstnameTF;
	
	@FXML
	private TextField lastnameTF;
	
	@FXML
	private Button changePP;
	
	@FXML
	private Button ok;
	
	@FXML
	private Button cancel;
	
	@FXML
	private Stage stage;
	
	private String username;
	
	private FileChooser ppChooser;
	private ExtensionFilter imgfilter;
	private File chosenImage;
	private String fileformat = null;
	private InputStream imageinputstream;
	private byte[] bytearrayforImage;
	private ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
	
	@FXML
	public void initialize(String string) {
		this.username = string;
		name.setText(string);
		File file = new File("output.jpg");
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:user.db");
			Statement stmt = connection.createStatement();
			String query = "SELECT * FROM User";
			boolean found = false;
			try (ResultSet resultSet = stmt.executeQuery(query)) {
				while(resultSet.next() && !found) {		
					if (resultSet.getString("username").compareTo(string) == 0) {
						found = true;
						System.out.println("gays");
						ByteArrayInputStream incomingPP = new ByteArrayInputStream(resultSet.getBytes("profilepic"));
						try {
							BufferedImage bimg = ImageIO.read(incomingPP);
							ImageIO.write(bimg, "jpg", file);
							InputStream inputstream = new FileInputStream(file);
						profilepicEdit.setImage(new Image(inputstream));
						
						
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} 
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void changePP(ActionEvent event) {
		ppChooser = new FileChooser();
		//ppChooser.setTitle("Select image");
		imgfilter = new FileChooser.ExtensionFilter("*.png", "*.jpg", "*.jpeg");
		ppChooser.getExtensionFilters().add(imgfilter);
		chosenImage = ppChooser.showOpenDialog(stage);
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
				profilepicEdit.setImage(new Image(imageinputstream));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	catch (IllegalArgumentException e) {
			System.out.println("Image not chosen");
		}
		
		
	}
	
	public void confirmEdit(ActionEvent event) {
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:user.db");
			PreparedStatement pstmt = connection.prepareStatement("UPDATE User SET profilepic=? WHERE username=?");
			//Statement stmt = connection.createStatement();
//			String sql = "UPDATE User" +
//					" SET profilepic = " + bytearrayforImage +
//					" WHERE username LIKE '" + username + "'";
			pstmt.setBytes(1, bytearrayforImage);
			pstmt.setString(2, username);
			pstmt.addBatch();
			pstmt.executeBatch();
			pstmt.close();
//			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		if (!firstnameTF.getText().isEmpty()) {
		
			try {
				Connection connection = DriverManager.getConnection("jdbc:sqlite:user.db");
				Statement stmt = connection.createStatement();
				String sql = "UPDATE User" +
						" SET firstname = '" + firstnameTF.getText() + "'" +
						" WHERE username LIKE '" + username + "'";
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		
		if (!lastnameTF.getText().isEmpty()) {
			try {
				Connection connection = DriverManager.getConnection("jdbc:sqlite:user.db");
				Statement stmt = connection.createStatement();
				String sql = "UPDATE User" +
						" SET lastname = '" + lastnameTF.getText() + "'" +
						" WHERE username LIKE '" + username + "'";
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		
//		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Canvas.fxml"));
//		CanvasController Ccon = loader.getController();
//		Ccon.initialize(username);
		
		stage.close();
		
		
	}
	
	public void cancel(ActionEvent event) {
		stage.close();
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
