package controller;

import java.io.IOException;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.User;

public class LoginController {
	@FXML
	private TextField name;
	@FXML
	private PasswordField password;
	@FXML
	private Label message;
	@FXML
	private Button login;
	@FXML
	private Button clear;
	
	@FXML
	private Button signup;
	
	protected User user = new User();
	
	@FXML
	private Stage stage;
	
	@FXML
	public void initialize(Stage mainstage) {
	}
	
	public void loginAction(ActionEvent event) {
		int check = user.verifyUserLogin(name.getText(), password.getText());
		if (check == 1) {
			message.setText("Login success.");
		} else if (check == 0) {
			message.setText("Empty username or password.");
		} else {
			message.setText("Incorrect username or password.");
		}
		
		if (check == 1) {
			stage.close();
		
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Canvas.fxml"));
				AnchorPane root = loader.load();
				Scene canvasScene = new Scene(root, 670, 432);
				CanvasController CController = loader.getController();
				Stage cstage = new Stage();
				CController.setStage(cstage);
				System.out.println(root);
				CController.initialize(name.getText());
				cstage.setScene(canvasScene);
				cstage.setResizable(false);
				cstage.setTitle("Canvas Page");
				cstage.show();
			} catch (IOException e){
				e.printStackTrace();	
			}
		}
		
		
	}
	
	
	public void clear(ActionEvent event) {
	
		name.clear();
		password.clear();
		
	}
	
	public void signup(ActionEvent event) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/RegistrationPage.fxml"));
			AnchorPane root = loader.load();
			Scene registrationScene = new Scene(root, 557, 524);
			RegistrationController RController = loader.getController();
			Stage rstage = new Stage();
			RController.setStage(rstage);
			
			rstage.setScene(registrationScene);
			rstage.setResizable(false);
			rstage.setTitle("Register User");
			rstage.show();
		} catch (IOException e){
			e.printStackTrace();	
		}
	}
	
	public void showAboutpage() {
		
	}
	
	public void logout() {
		
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}

}
