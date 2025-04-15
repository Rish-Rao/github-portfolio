package controller;


import java.io.IOException;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	@Override
	public void init() throws Exception {
	}

	@Override
	public void start(Stage mainStage) {
		
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/LoginPage.fxml"));
			// Load an object hierarchy from a FXML document
			GridPane root = loader.load();
			LoginController lController = loader.getController();
			lController.setStage(mainStage);
			
			Scene scene = new Scene(root, 500, 300);
			// Get an observable list of string URLs linking to the stylesheets
			// to use with this scene's contents
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			mainStage.setScene(scene);
			// Set the value of the property resizable
			mainStage.setResizable(false);
			mainStage.setTitle("Login Page");
			mainStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return the controller associated with the root object
		
		
	}


	@Override
	public void stop() throws Exception {
	}

	public static void main(String[] args) {
		launch(args);
	}
}