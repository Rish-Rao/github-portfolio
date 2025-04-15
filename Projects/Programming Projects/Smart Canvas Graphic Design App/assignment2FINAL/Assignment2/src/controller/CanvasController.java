package controller;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;

import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import javafx.scene.layout.AnchorPane;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.stage.FileChooser.ExtensionFilter;


public class CanvasController {
	
	@FXML
	private MenuBar menubar;
	
	@FXML
	private Menu file;
	
	@FXML
	private Menu edit;
	
	@FXML
	private Menu help;
	
	@FXML
	private Button profile;
	
	@FXML
	private Button logoutbutton;
	
	@FXML
	private Button text;
	
	@FXML
	private Button image;
	
	@FXML
	private Button rect;
	
	@FXML
	private Button circle;
	
	@FXML
	private Button canvasbutton;
	
	@FXML
	private Label UserName;
	
	@FXML
	private ImageView profilepic;
	
	@FXML
	private Button createcanvasbutton; 
	
	@FXML
	private Stage stage;
	
	@FXML
	private Button ok;
	
	@FXML
	private Button cancel;
	
	@FXML
	private Button deletecanvasbutton;
	
	@FXML
	private AnchorPane canvasplaceholder;
	
	@FXML
	private ScrollPane optionslistplaceholder;
	
	@FXML
	private Slider zoomslider;
	
	@FXML
	private Label zoomlabel;
	
	@FXML
	private Label elementvalues;
	
	@FXML
	private MenuItem deletecanvas;
	
	@FXML
	private MenuItem saveas;
	
	@FXML
	private MenuItem deleteelement;
	
	private GridPane optionslist = new GridPane();
	
	private String username;
	
	private FileChooser ppChooser;
	private ExtensionFilter imgfilter;
	private File chosenImage;

	private InputStream imageinputstream;

	private Node selectedElement;
	
	private Color colorOfSelectedElement;
	
	private Color colorOfSelectedElementBorder;
	
	private AnchorPane canvas;
	
	private double canvasWidth;
	private double canvasHeight;


	
	@FXML
	public void initialize(String string) {
		//System.out.println(root);
		//this.root = new AnchorPane(root);s
		//setStage(stage);
		//System.out.println(stage);
		//this.previousstage = stage;
		this.username = string;
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
						profilepic.setImage(new Image(inputstream));
						
						UserName.setText(resultSet.getString("firstname") + " " + resultSet.getString("lastname"));
							
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
		
		zoomslider.valueProperty().addListener((ov, oldValue, newValue) -> {
			
			setZoom(newValue.intValue());
		});
		setZoom(100);
		zoomslider.setValue(100);
		disablemenuitems();
		
	}
	
	public void setZoom(int percentage) {
		if (canvas != null) {
			canvas.setScaleX(percentage/100.0);
			canvas.setScaleY(percentage/100.0);
			zoomlabel.setText("Zoom: " + percentage + "%");
		}
	}

	
	public void editProfile(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/EditProfile.fxml"));
			AnchorPane root = loader.load();
			Scene canvasScene = new Scene(root, 400, 250);
			EditProfileController EPController = loader.getController();
			Stage epstage = new Stage();
			EPController.setStage(epstage);
			EPController.initialize(username);
			epstage.setScene(canvasScene);
			epstage.setResizable(false);
			epstage.setTitle("Edit Profile");
			epstage.show();
		} catch (IOException e){
			e.printStackTrace();	
		}
		//changeProfile();
		//initialize(username);
	}
	
	public void newCanvas(ActionEvent event) {
//		boolean check = false;
//		while (!check) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Dialog<Pair<String,String>> d = new Dialog();
			d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
			AnchorPane ap = new AnchorPane();
			Label widthlabel = new Label("Width, px:");
			Label heightlabel = new Label("Height, px:");
			widthlabel.setLayoutX(61);
			widthlabel.setLayoutY(33);
			heightlabel.setLayoutX(59);
			heightlabel.setLayoutY(72);
			TextField widthTF = new TextField();
			TextField heightTF = new TextField();
			widthTF.setLayoutX(133);
			widthTF.setLayoutY(28);
			heightTF.setLayoutX(133);
			heightTF.setLayoutY(68);
			ap.getChildren().addAll(widthlabel, heightlabel, widthTF, heightTF);
			d.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
			widthTF.textProperty().addListener((observable, oldValue, newValue) -> {
				heightTF.textProperty().addListener((ob, ov, nv) -> {
					
						if (newValue.isEmpty() || nv.isEmpty() || !isInt(newValue) || !isInt(nv)) {
							
								d.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
							
						} else {
							d.getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
						}
				});
				
			});
			d.getDialogPane().setContent(ap);
			d.setTitle("Create Canvas");
			d.setHeaderText(null);
			d.setResultConverter(dialogButton -> {
				if(dialogButton == ButtonType.OK) {
					return new Pair<>(widthTF.getText(), heightTF.getText());
				}
				return null;
			});
			Optional<Pair<String,String>> result = d.showAndWait();
							if (result.isPresent()) {
								canvasWidth = Double.parseDouble(result.get().getKey());
								canvasHeight = Double.parseDouble(result.get().getValue());
								createCanvas();
							}
							//check = true;
						
			}
			
		
	//}
	
	public void clearCanvas() {
		canvas.getChildren().clear();
		optionslist.getChildren().clear();
		optionslistplaceholder.setContent(null);
		canvas.setStyle("-fx-background-color:white");
		showvalues(0,0,0,0,0);
		disablemenuitems();
	}
	
	public void createCanvas() {

		canvas = new AnchorPane();
		canvas.setPrefWidth(canvasWidth);
		canvas.setPrefHeight(canvasHeight);
		canvas.setLayoutX((414-canvasWidth)/2);
		canvas.setLayoutY((346-canvasHeight)/2);
		canvas.setMaxHeight(canvasHeight);
		System.out.println("gay");
		canvas.setStyle("-fx-background-color:white");
		canvasplaceholder.getChildren().add(canvas);
		
		
		
		//createcanvasbutton.setVisible(false);
		
//		canvasplaceholder.setOnMouseClicked(e -> {
//			unselect();
//		});
		
	}
	
	public void deleteElement() {
		
		canvas.getChildren().remove(selectedElement);
		selectedElement = null;
		optionslist.getChildren().clear();
		optionslistplaceholder.setContent(null);
		showvalues(0,0,0,0,0);
		deleteelement.setDisable(true);
	
	}
	
	public void addImage(ActionEvent event) {

		ppChooser = new FileChooser();
		//ppChooser.setTitle("Select image");
		imgfilter = new FileChooser.ExtensionFilter("*.png", "*.jpg", "*.jpeg");
		ppChooser.getExtensionFilters().add(imgfilter);
		chosenImage = ppChooser.showOpenDialog(stage);
		
		try {
			try {
				imageinputstream = new FileInputStream(chosenImage);
				ImageView image = new ImageView(new Image(imageinputstream));
				image.setFitHeight(150);
				image.setFitWidth(150);
				canvas.getChildren().add(image);
				enablemenuitems();
				image.setOnMouseClicked(e -> {
					unselect();
					select(image);
				});
			} catch (NullPointerException e) {
				System.out.println("No image chosen");
			}
			
			 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	public void addRect() {
		if (canvas != null) {
			Rectangle rect = new Rectangle(100,50);
			rect.setFill(Color.WHITE);
			rect.setStroke(Color.BLACK);
			rect.setLayoutX(50);
			rect.setLayoutY(100);
			canvas.getChildren().add(rect);
			enablemenuitems();
			rect.setOnMouseClicked(e -> {
				unselect();
				select(rect);
			});
		}
	}
	
	public void addCirc() {
		if (canvas != null) {
			Circle circ = new Circle(100,50,50);
			circ.setFill(Color.WHITE);
			circ.setStroke(Color.BLACK);
			circ.setLayoutX(100);
			circ.setLayoutY(50);
			canvas.getChildren().add(circ);
			enablemenuitems();
			circ.setOnMouseClicked(e -> {
				unselect();
				select(circ);
			});
			
		}
	}
	
	public void addText() {
		if (canvas != null) {
			Text text = new Text("TEXT");
			text.setLayoutX(10);
			text.setLayoutY(20);
			canvas.getChildren().add(text);
			enablemenuitems();
			text.setOnMouseClicked(e -> {
				unselect();
				select(text);
			});
		}
	}
	
	public void enablemenuitems(){
		deletecanvas.setDisable(false);
		deleteelement.setDisable(false);
		saveas.setDisable(false);
	}
	
	public void disablemenuitems(){
		deletecanvas.setDisable(true);
		deleteelement.setDisable(true);
		saveas.setDisable(true);
	}
	
	public void showcanvasOP() {
		//deletecanvasbutton.setVisible(true);
		unselect();
//		selectedElement = null;
//		optionslist.getChildren().clear();
//		optionslistplaceholder.setContent(null);
		if (canvas != null) {
			showoptions();
		}
		
		
	}
	
	public void deletecanvas() {
		canvas.getChildren().clear();
		canvasplaceholder.getChildren().clear();
	}
	
	public void select(Node node) {
		selectedElement = node;
		if (selectedElement instanceof Shape) {
			colorOfSelectedElementBorder = (Color) ((Shape) selectedElement).getStroke();
			((Shape) node).setStroke(Color.RED);
			deleteelement.setDisable(false);
		} else if (selectedElement instanceof ImageView) {
			deleteelement.setDisable(false);
		}
		showoptions();
		
	}
	
	public void unselect() {
		if (selectedElement != null) {
			if (selectedElement instanceof Shape) {
				((Shape) selectedElement).setStroke(colorOfSelectedElementBorder);
				selectedElement = null;
				optionslist.getChildren().clear();
				optionslistplaceholder.setContent(null);
				deleteelement.setDisable(true);
			} else{
				
				selectedElement = null;
				optionslist.getChildren().clear();
				optionslistplaceholder.setContent(null);
				deleteelement.setDisable(true);
			}
		
		} else if (selectedElement == null) {
			optionslist.getChildren().clear();
			optionslistplaceholder.setContent(null);
			showvalues(0,0,0,0,0);
			deleteelement.setDisable(true);
		}
		
		
		
	}
	
	public void showvalues(int x, int y, int w, int h, int angle) {

			elementvalues.setText("x: " + x + " y:" + y + " w:" + w + " h:" + h + " angle:" + angle + "°");
		
	}
	
	
	@SuppressWarnings("static-access")
	public void showoptions() {
		if (selectedElement instanceof Circle) {
			optionslist.setHgap(10);
			optionslist.setVgap(10);
			optionslist.setPadding(new Insets(10, 10, 10, 10 ));			
			Label borderC = new Label("Border colour");
			Label bordW = new Label("Border width");
			Label backgC = new Label("Background");
			Label layoutX = new Label("LayoutX");
			Label layoutY = new Label("LayoutY");
			Label radius = new Label("Radius");
			ColorPicker cpborder = new ColorPicker();
			ColorPicker cpbackg = new ColorPicker();
			TextField bordWTF = new TextField();
			TextField layoutXTF = new TextField();
			TextField layoutYTF = new TextField();
			TextField radiusTF = new TextField();
			
			cpborder.setPrefWidth(90);
			cpborder.setPrefHeight(26);
			cpbackg.setPrefWidth(90);
			cpbackg.setPrefHeight(26);
			bordWTF.setPrefWidth(90);
			bordWTF.setPrefHeight(26);
			layoutXTF.setPrefWidth(90);
			layoutXTF.setPrefHeight(26);
			layoutYTF.setPrefWidth(90);
			layoutYTF.setPrefHeight(26);
			radiusTF.setPrefWidth(90);
			radiusTF.setPrefHeight(26);
			
			cpborder.setValue(colorOfSelectedElementBorder);
			cpbackg.setValue((Color) ((Shape) selectedElement).getFill());
			bordWTF.setText(String.valueOf((int) Math.round(((Shape) selectedElement).getStrokeWidth())));
			layoutXTF.setText(String.valueOf((int) Math.round(((Shape) selectedElement).getLayoutX())));
			layoutYTF.setText(String.valueOf((int) Math.round(((Shape) selectedElement).getLayoutY())));
			radiusTF.setText(String.valueOf((int) Math.round(((Circle) selectedElement).getRadius())));
			
			showvalues((int) Math.round(((Shape) selectedElement).getLayoutX()), (int) Math.round(((Shape) selectedElement).getLayoutY()), (int) Math.round(((Circle) selectedElement).getRadius())/2, (int) Math.round(((Circle) selectedElement).getRadius())/2, (int) Math.round(((Circle) selectedElement).getRotate()));
			
			optionslist.add(borderC, 0, 0);
			optionslist.add(bordW, 0, 1);
			optionslist.add(backgC, 0, 2);
			optionslist.add(cpborder, 1, 0);
			optionslist.add(bordWTF, 1, 1);
			optionslist.add(cpbackg, 1, 2);
			optionslist.add(new Separator(Orientation.HORIZONTAL), 0, 3);
			optionslist.add(new Separator(Orientation.HORIZONTAL), 1, 3);
			optionslist.add(layoutX, 0, 4);
			optionslist.add(layoutY, 0, 5);
			optionslist.add(radius, 0, 6);
			optionslist.add(layoutXTF, 1, 4);
			optionslist.add(layoutYTF, 1, 5);
			optionslist.add(radiusTF, 1, 6);
			optionslistplaceholder.setContent(optionslist);
			
			cpborder.setOnAction(e -> {
				colorOfSelectedElementBorder = (Color) cpborder.getValue();
				((Shape) selectedElement).setStroke(colorOfSelectedElementBorder);
				
			});
			
			cpbackg.setOnAction(e -> {
				colorOfSelectedElement = (Color) cpbackg.getValue();
				((Shape) selectedElement).setFill(colorOfSelectedElement);

			});
			
			bordWTF.textProperty().addListener((ov, oldValue, newValue) -> {
				//System.out.println(Integer.parseInt(newValue));
				if (!newValue.isEmpty()) {
					if (isInt(newValue)) {
						((Shape) selectedElement).setStrokeWidth(Integer.parseInt(newValue));
					}
				}
			});
		
			
			layoutXTF.textProperty().addListener((ov, oldValue, newValue) -> {
				//System.out.println(Integer.parseInt(newValue));
				if (!newValue.isEmpty()) {
					if (isInt(newValue)) {
						selectedElement.setLayoutX(Integer.parseInt(newValue));
						showvalues(Integer.parseInt(newValue), (int) Math.round(((Shape) selectedElement).getLayoutY()), (int) Math.round(((Circle) selectedElement).getRadius())/2, (int) Math.round(((Circle) selectedElement).getRadius())/2, (int) Math.round(((Circle) selectedElement).getRotate()));
					}
				}
					
				
			});
			
			layoutYTF.textProperty().addListener((ov, oldValue, newValue) -> {
				//System.out.println(Integer.parseInt(newValue));
				if (!newValue.isEmpty()) {
					if (isInt(newValue)) {
						selectedElement.setLayoutY(Integer.parseInt(newValue));
						showvalues((int) Math.round(((Shape) selectedElement).getLayoutY()), Integer.parseInt(newValue), (int) Math.round(((Circle) selectedElement).getRadius())/2, (int) Math.round(((Circle) selectedElement).getRadius())/2, (int) Math.round(((Circle) selectedElement).getRotate()));
					}
				}
					
				
			});
			
			radiusTF.textProperty().addListener((ov, oldValue, newValue) -> {

					if (!newValue.isEmpty()) {
						if (isInt(newValue)) {
							((Circle) selectedElement).setRadius(Double.parseDouble(newValue));
							showvalues((int) Math.round(((Shape) selectedElement).getLayoutY()), (int) Math.round(((Shape) selectedElement).getLayoutY()), Integer.parseInt(newValue)/2, (int) Integer.parseInt(newValue)/2, (int) Math.round(((Circle) selectedElement).getRotate()));
						}
					}
				
			});
			
			
			
			
			
			
		} else if (selectedElement instanceof Rectangle) {
			optionslist.setHgap(10);
			optionslist.setVgap(10);
			optionslist.setPadding(new Insets(10, 10, 10, 10 ));			
			Label borderC = new Label("Border colour");
			Label bordW = new Label("Border width");
			Label backgC = new Label("Background");
			Label layoutX = new Label("LayoutX");
			Label layoutY = new Label("LayoutY");
			Label width = new Label("Width");
			Label height = new Label("Height");
			Label rotate = new Label("Rotate");
			TextField rotateDegrees = new TextField();
			ColorPicker cpborder = new ColorPicker();
			ColorPicker cpbackg = new ColorPicker();
			TextField bordWTF = new TextField();
			TextField layoutXTF = new TextField();
			TextField layoutYTF = new TextField();
			TextField widthtf = new TextField();
			TextField heighttf = new TextField();
			
			cpborder.setPrefWidth(90);
			cpborder.setPrefHeight(26);
			cpbackg.setPrefWidth(90);
			cpbackg.setPrefHeight(26);
			bordWTF.setPrefWidth(90);
			bordWTF.setPrefHeight(26);
			layoutXTF.setPrefWidth(90);
			layoutXTF.setPrefHeight(26);
			layoutYTF.setPrefWidth(90);
			layoutYTF.setPrefHeight(26);
			widthtf.setPrefWidth(90);
			widthtf.setPrefHeight(26);
			heighttf.setPrefWidth(90);
			heighttf.setPrefHeight(26);
			rotateDegrees.setPrefWidth(90);
			rotateDegrees.setPrefHeight(26);
			cpborder.setValue(colorOfSelectedElementBorder);
			cpbackg.setValue((Color) ((Shape) selectedElement).getFill());
			bordWTF.setText(String.valueOf((int) Math.round(((Shape) selectedElement).getStrokeWidth())));
			layoutXTF.setText(String.valueOf((int) Math.round(((Shape) selectedElement).getLayoutX())));
			layoutYTF.setText(String.valueOf((int) Math.round(((Shape) selectedElement).getLayoutY())));
			widthtf.setText(String.valueOf((int) Math.round(((Rectangle) selectedElement).getWidth())));
			heighttf.setText(String.valueOf((int) Math.round(((Rectangle) selectedElement).getHeight())));
			rotateDegrees.setText(String.valueOf((int) Math.round(((Rectangle) selectedElement).getRotate())));
			
			showvalues((int) Math.round(((Shape) selectedElement).getLayoutX()), (int) Math.round(((Shape) selectedElement).getLayoutY()), (int) Math.round(((Rectangle) selectedElement).getWidth()), (int) Math.round(((Rectangle) selectedElement).getHeight()), (int) Math.round(((Rectangle) selectedElement).getRotate()));
			
			optionslist.add(borderC, 0, 0);
			optionslist.add(bordW, 0, 1);
			optionslist.add(backgC, 0, 2);
			optionslist.add(cpborder, 1, 0);
			optionslist.add(bordWTF, 1, 1);
			optionslist.add(cpbackg, 1, 2);
			optionslist.add(new Separator(Orientation.HORIZONTAL), 0, 3);
			optionslist.add(new Separator(Orientation.HORIZONTAL), 1, 3);
			optionslist.add(layoutX, 0, 4);
			optionslist.add(layoutY, 0, 5);
			optionslist.add(width, 0, 6);
			optionslist.add(height, 0, 7);
			optionslist.add(rotate, 0, 8);
			optionslist.add(layoutXTF, 1, 4);
			optionslist.add(layoutYTF, 1, 5);
			optionslist.add(widthtf, 1, 6);
			optionslist.add(heighttf, 1, 7);
			optionslist.add(rotateDegrees, 1, 8);
			
			optionslistplaceholder.setContent(optionslist);
			
			cpborder.setOnAction(e -> {
				colorOfSelectedElementBorder = (Color) cpborder.getValue();
				((Shape) selectedElement).setStroke(colorOfSelectedElementBorder);

			});
			
			cpbackg.setOnAction(e -> {
				colorOfSelectedElement = (Color) cpbackg.getValue();
				((Shape) selectedElement).setFill(colorOfSelectedElement);

			});
			
			
			bordWTF.textProperty().addListener((ov, oldValue, newValue) -> {
				//System.out.println(Integer.parseInt(newValue));
				if (!newValue.isEmpty()) {
					if (isInt(newValue)) {
						selectedElement.setLayoutX(Integer.parseInt(newValue));
						((Shape) selectedElement).setStrokeWidth(Integer.parseInt(newValue));
					}
				}
					
					
			});
			
			
			layoutXTF.textProperty().addListener((ov, oldValue, newValue) -> {
				//System.out.println(Integer.parseInt(newValue));
				if (!newValue.isEmpty()) {
					if (isInt(newValue)) {
						selectedElement.setLayoutX(Integer.parseInt(newValue));
						showvalues(Integer.parseInt(newValue), (int) Math.round(((Shape) selectedElement).getLayoutY()), (int) Math.round(((Rectangle) selectedElement).getWidth()), (int) Math.round(((Rectangle) selectedElement).getHeight()), (int) Math.round(((Rectangle) selectedElement).getRotate()));
					}
				}
					
				
			});
			
			layoutYTF.textProperty().addListener((ov, oldValue, newValue) -> {
				//System.out.println(Integer.parseInt(newValue));
				if (!newValue.isEmpty()) {
					if (isInt(newValue)) {
						selectedElement.setLayoutY(Integer.parseInt(newValue));
						showvalues((int) Math.round(((Shape) selectedElement).getLayoutX()), Integer.parseInt(newValue), (int) Math.round(((Rectangle) selectedElement).getWidth()), (int) Math.round(((Rectangle) selectedElement).getHeight()), (int) Math.round(((Rectangle) selectedElement).getRotate()));
					}
				}
					
				
			});
			
			widthtf.textProperty().addListener((ov, oldValue, newValue) -> {
				
				if (!newValue.isEmpty()) {
					if (isInt(newValue)) {
						((Rectangle) selectedElement).setWidth(Double.parseDouble(newValue));
						showvalues((int) Math.round(((Shape) selectedElement).getLayoutX()), (int) Math.round(((Shape) selectedElement).getLayoutY()), Integer.parseInt(newValue), (int) Math.round(((Rectangle) selectedElement).getHeight()), (int) Math.round(((Rectangle) selectedElement).getRotate()));
					}
				}
			});
			
			heighttf.textProperty().addListener((ov, oldValue, newValue) -> {

					if (!newValue.isEmpty()) {
						if (isInt(newValue)) {
							((Rectangle) selectedElement).setHeight(Double.parseDouble(newValue));
							showvalues((int) Math.round(((Shape) selectedElement).getLayoutX()), (int) Math.round(((Shape) selectedElement).getLayoutY()), (int) Math.round(((Rectangle) selectedElement).getWidth()), Integer.parseInt(newValue), (int) Math.round(((Rectangle) selectedElement).getRotate()));
						}
					}
			});
			
			rotateDegrees.textProperty().addListener((ov, oldValue, newValue) -> {

				if (!newValue.isEmpty()) {
					if (isInt(newValue)) {
						((Rectangle) selectedElement).setRotate(Double.parseDouble(newValue));
						showvalues((int) Math.round(((Shape) selectedElement).getLayoutX()), (int) Math.round(((Shape) selectedElement).getLayoutY()), (int) Math.round(((Rectangle) selectedElement).getWidth()), (int) Math.round(((Rectangle) selectedElement).getHeight()), Integer.parseInt(newValue));
					}
				} else if (newValue.isEmpty()) {
					((Rectangle) selectedElement).setRotate(0);
					showvalues((int) Math.round(((Shape) selectedElement).getLayoutX()), (int) Math.round(((Shape) selectedElement).getLayoutY()), (int) Math.round(((Rectangle) selectedElement).getWidth()), (int) Math.round(((Rectangle) selectedElement).getHeight()), 0);
				}
			});
			
			
		} else if(selectedElement instanceof Text) {
			optionslist.setHgap(10);
			optionslist.setVgap(10);
			optionslist.setPadding(new Insets(10, 10, 10, 10 ));			
			Label textedit = new Label("Text");
			Label font = new Label("Font");
			Label fontsize = new Label("Font size");
			Label attributes = new Label("Attributes");
			Label textC = new Label("Text colour");
			Label allignment = new Label("Allignment");
			Label borderC = new Label("Border color");
			Label borderW = new Label("Border width");
			Label backC = new Label("Background");
			Label layoutX = new Label("LayoutX");
			Label layoutY = new Label("LayoutY");
			Label width = new Label("Width");
			Label height = new Label("Height");
			Label rotate = new Label("Rotate");
			TextField rotateDegrees = new TextField();
			ColorPicker cpborder = new ColorPicker();
			ColorPicker cpbackg = new ColorPicker();
			ColorPicker textCpicker = new ColorPicker();
			TextField bordWTF = new TextField();
			TextField layoutXTF = new TextField();
			TextField layoutYTF = new TextField();
			TextField widthtf = new TextField();
			TextField heighttf = new TextField();
			TextField textTF = new TextField();
			TextField fontsizeTF = new TextField();
			ComboBox<String> fontpicker = new ComboBox<>();
			Button bold = new Button("B");
			Button italic = new Button("I");
			Button left = new Button("L");
			Button middle = new Button("M");
			Button right = new Button("R");
			ScrollBar sb = new ScrollBar();

			sb.setOrientation(Orientation.VERTICAL);
			sb.setLayoutX(187);
			sb.setLayoutY(5);
			sb.setPrefHeight(optionslistplaceholder.getHeight());
			sb.setMin(0);
			sb.setMax(150);
			fontpicker.getItems().addAll(Font.getFamilies());
			fontpicker.setValue(Font.getFamilies().get(0));
			textTF.setText(((Text) selectedElement).getText());
			fontpicker.setPrefWidth(90);
			fontpicker.setPrefHeight(26);
			textCpicker.setPrefWidth(90);
			textCpicker.setPrefHeight(26);
			textTF.setPrefWidth(90);
			textTF.setPrefHeight(26);
			fontsizeTF.setPrefWidth(90);
			fontsizeTF.setPrefHeight(26);
			cpborder.setPrefWidth(90);
			cpborder.setPrefHeight(26);
			cpbackg.setPrefWidth(90);
			cpbackg.setPrefHeight(26);
			bordWTF.setPrefWidth(90);
			bordWTF.setPrefHeight(26);
			layoutXTF.setPrefWidth(90);
			layoutXTF.setPrefHeight(26);
			layoutYTF.setPrefWidth(90);
			layoutYTF.setPrefHeight(26);
			widthtf.setPrefWidth(90);
			widthtf.setPrefHeight(26);
			heighttf.setPrefWidth(90);
			heighttf.setPrefHeight(26);
			rotateDegrees.setPrefWidth(90);
			rotateDegrees.setPrefHeight(26);
			
			showvalues((int) Math.round(((Text) selectedElement).getLayoutX()), (int) Math.round(((Text) selectedElement).getLayoutY()), (int) Math.round(((Text) selectedElement).getWrappingWidth()), (int) Math.round(((Text) selectedElement).BASELINE_OFFSET_SAME_AS_HEIGHT), (int) Math.round(((Text) selectedElement).getRotate()));
			
			cpborder.setValue(colorOfSelectedElementBorder);
			cpbackg.setValue((Color) ((Shape) selectedElement).getFill());
			bordWTF.setText(String.valueOf((int) Math.round(((Shape) selectedElement).getStrokeWidth())));
			fontsizeTF.setText(String.valueOf((int) Math.round(((Text) selectedElement).getFont().getSize())));
			textCpicker.setValue((Color) ((Shape) selectedElement).getFill());
			fontpicker.setValue(((Text) selectedElement).getFont().getName());
			layoutXTF.setText(String.valueOf((int) Math.round(((Shape) selectedElement).getLayoutX())));
			layoutYTF.setText(String.valueOf((int) Math.round(((Shape) selectedElement).getLayoutY())));
			widthtf.setText(String.valueOf((int) Math.round(((Text) selectedElement).getWrappingWidth())));
			heighttf.setText(String.valueOf((int) Math.round(((Text) selectedElement).BASELINE_OFFSET_SAME_AS_HEIGHT)));
			rotateDegrees.setText(String.valueOf((int) Math.round(((Text) selectedElement).getRotate())));
			textTF.setText(((Text) selectedElement).getText());
			
			optionslist.add(textedit, 0, 0);
			optionslist.add(font, 0, 1);
			optionslist.add(fontsize, 0, 2);
			optionslist.add(attributes, 0, 3);
			optionslist.add(textC, 0, 4);
			optionslist.add(allignment, 0, 5);
			optionslist.add(borderC, 0, 7);
			optionslist.add(borderW, 0, 8);
			optionslist.add(backC, 0, 9);
			optionslist.add(layoutX, 0, 11);
			optionslist.add(layoutY, 0, 12);
			optionslist.add(width, 0, 13);
			optionslist.add(height, 0, 14);
			optionslist.add(rotate, 0, 15);
			
			
			optionslist.add(new Separator(Orientation.HORIZONTAL), 0, 6);
			optionslist.add(new Separator(Orientation.HORIZONTAL), 1, 6);
			optionslist.add(new Separator(Orientation.HORIZONTAL), 0, 10);
			optionslist.add(new Separator(Orientation.HORIZONTAL), 1, 10);
			
			optionslist.add(textTF, 1, 0);
			optionslist.add(fontpicker, 1, 1);
			optionslist.add(fontsizeTF, 1, 2);
			optionslist.add(bold, 1, 3);
			optionslist.add(italic, 1, 3);
			optionslist.add(textCpicker, 1, 4);
			optionslist.add(left, 1, 5);
			optionslist.add(middle, 1, 5);
			optionslist.add(right, 1, 5);
			optionslist.add(cpborder, 1, 7);
			optionslist.add(bordWTF, 1, 8);
			optionslist.add(cpbackg, 1, 9);
			optionslist.add(layoutXTF, 1, 11);
			optionslist.add(layoutYTF, 1, 12);
			optionslist.add(widthtf, 1, 13);
			optionslist.add(heighttf, 1, 14);
			optionslist.add(rotateDegrees, 1, 15);
			optionslist.setMargin(italic, new Insets(0,0,0,26));
			optionslist.setMargin(right, new Insets(0,0,0,52));
			optionslist.setMargin(middle, new Insets(0,0,0,23));
			optionslistplaceholder.setContent(optionslist);
			
			textTF.textProperty().addListener((ov, oldValue, newValue) -> {
				//System.out.println((newValue));
				
					((Text) selectedElement).setText(newValue);
				
			});
			
			fontpicker.setOnAction(e -> {
				((Text) selectedElement).setFont(Font.font(fontpicker.getValue(), FontWeight.findByName(((Text) selectedElement).getFont().getStyle()), FontPosture.findByName(((Text) selectedElement).getFont().getStyle()) ,(((Text) selectedElement).getFont().getSize())));
				
			});
			
			fontsizeTF.textProperty().addListener((ov, oldValue, newValue) -> {
				//System.out.println((newValue));
				if (isInt(newValue)) {
					((Text) selectedElement).setFont(Font.font(fontpicker.getValue(), FontWeight.findByName(((Text) selectedElement).getFont().getStyle()), FontPosture.findByName(((Text) selectedElement).getFont().getStyle()) , Double.parseDouble(newValue)));
				}
			});
			
			bold.setOnAction(e -> {
				if (!((Text) selectedElement).getFont().getStyle().contains("Bold")) {
					((Text) selectedElement).setFont(Font.font(fontpicker.getValue(), FontWeight.BOLD, FontPosture.findByName(((Text) selectedElement).getFont().getStyle()) ,(((Text) selectedElement).getFont().getSize())));
					System.out.println( ((Text) selectedElement).getFont().getStyle());
				} else if((((Text) selectedElement).getFont().getStyle().contains("Bold"))) {
					((Text) selectedElement).setFont(Font.font(fontpicker.getValue(), FontWeight.NORMAL, FontPosture.findByName(((Text) selectedElement).getFont().getStyle()) ,(((Text) selectedElement).getFont().getSize())));
					System.out.println( ((Text) selectedElement).getFont().getStyle());
				}
			});
			
			italic.setOnAction(e -> {
				if (!((Text) selectedElement).getFont().getStyle().contains("Italic")) {
					((Text) selectedElement).setFont(Font.font(fontpicker.getValue(), FontWeight.findByName(((Text) selectedElement).getFont().getStyle()), FontPosture.ITALIC ,(((Text) selectedElement).getFont().getSize()))); 
				} else if (((Text) selectedElement).getFont().getStyle().contains("Italic")) {
					((Text) selectedElement).setFont(Font.font(fontpicker.getValue(), FontWeight.findByName(((Text) selectedElement).getFont().getStyle()), FontPosture.REGULAR ,(((Text) selectedElement).getFont().getSize()))); 
				}
				((Text) selectedElement).getFont().getName();
			});
			
			textCpicker.setOnAction(e -> {
				colorOfSelectedElement = (Color) textCpicker.getValue();
				((Shape) selectedElement).setFill(colorOfSelectedElement);

			});
			
			left.setOnAction(e -> {
				((Text) selectedElement).setTextAlignment(TextAlignment.LEFT);
			});
			
			middle.setOnAction(e -> {
				((Text) selectedElement).setTextAlignment(TextAlignment.CENTER);
			});
			
			right.setOnAction(e -> {
				((Text) selectedElement).setTextAlignment(TextAlignment.RIGHT);
			});
			
			cpborder.setOnAction(e -> {
				colorOfSelectedElementBorder = (Color) cpborder.getValue();
				((Shape) selectedElement).setStroke(colorOfSelectedElement);
			});
			
			cpborder.setOnAction(e -> {
				colorOfSelectedElementBorder = (Color) cpborder.getValue();
				((Shape) selectedElement).setStroke(colorOfSelectedElement);
			});
			
			bordWTF.textProperty().addListener((ov, oldValue, newValue) -> {
				//System.out.println(Integer.parseInt(newValue));
				if (!newValue.isEmpty()) {
					if (isInt(newValue)) {
						((Shape) selectedElement).setStrokeWidth(Integer.parseInt(newValue));
						
					}
				}
					
			});
			
			layoutXTF.textProperty().addListener((ov, oldValue, newValue) -> {
				//System.out.println(Integer.parseInt(newValue));
				if (!newValue.isEmpty()) {
					if (isInt(newValue)) {
						selectedElement.setLayoutX(Integer.parseInt(newValue));
						showvalues(Integer.parseInt(newValue), (int) Math.round(((Text) selectedElement).getLayoutY()), (int) Math.round(((Text) selectedElement).getWrappingWidth()), (int) Math.round(((Text) selectedElement).BASELINE_OFFSET_SAME_AS_HEIGHT), (int) Math.round(((Text) selectedElement).getRotate()));
						
					}
				}
				
			});
			
			layoutYTF.textProperty().addListener((ov, oldValue, newValue) -> {
				//System.out.println(Integer.parseInt(newValue));
				if (!newValue.isEmpty()) {
					if (isInt(newValue)) {
						selectedElement.setLayoutY(Integer.parseInt(newValue));
						showvalues((int) Math.round(((Text) selectedElement).getLayoutX()), Integer.parseInt(newValue), (int) Math.round(((Text) selectedElement).getWrappingWidth()), (int) Math.round(((Text) selectedElement).BASELINE_OFFSET_SAME_AS_HEIGHT), (int) Math.round(((Text) selectedElement).getRotate()));
					}
				}
				
			});
			
			widthtf.textProperty().addListener((ov, oldValue, newValue) -> {
				
				if (!newValue.isEmpty()) {
					if (isInt(newValue)) {
						//System.out.println(Integer.parseInt(newValue));
						((Text) selectedElement).setWrappingWidth(Double.parseDouble(newValue));
						showvalues((int) Math.round(((Text) selectedElement).getLayoutX()), (int) Math.round(((Text) selectedElement).getLayoutY()), Integer.parseInt(newValue), (int) Math.round(((Text) selectedElement).BASELINE_OFFSET_SAME_AS_HEIGHT), (int) Math.round(((Text) selectedElement).getRotate()));
					}
				}
				
			});
			
			heighttf.textProperty().addListener((ov, oldValue, newValue) -> {
				//System.out.println(Integer.parseInt(newValue));
				if (!newValue.isEmpty()) {
					if (isInt(newValue)) {
						((Text) selectedElement).prefHeight(Double.parseDouble(newValue));
						showvalues((int) Math.round(((Text) selectedElement).getLayoutX()), (int) Math.round(((Text) selectedElement).getLayoutY()), (int) Math.round(((Text) selectedElement).getWrappingWidth()), Integer.parseInt(newValue), (int) Math.round(((Text) selectedElement).getRotate()));
					}
				}
					
			});
			
			rotateDegrees.textProperty().addListener((ov, oldValue, newValue) -> {

				if (!newValue.isEmpty()) {
					if (isInt(newValue)) {
						((Text) selectedElement).setRotate(Double.parseDouble(newValue));
						showvalues((int) Math.round(((Text) selectedElement).getLayoutX()), (int) Math.round(((Text) selectedElement).getLayoutY()), (int) Math.round(((Text) selectedElement).getWrappingWidth()), (int) Math.round(((Text) selectedElement).BASELINE_OFFSET_SAME_AS_HEIGHT), Integer.parseInt(newValue));
					}
				} else if (newValue.isEmpty()) {
					((Text) selectedElement).setRotate(0);
					showvalues((int) Math.round(((Text) selectedElement).getLayoutX()), (int) Math.round(((Text) selectedElement).getLayoutY()), (int) Math.round(((Text) selectedElement).getWrappingWidth()), (int) Math.round(((Text) selectedElement).BASELINE_OFFSET_SAME_AS_HEIGHT), 0);
				}
			});
		
			
		} else if (selectedElement instanceof ImageView) {
			optionslist.setHgap(10);
			optionslist.setVgap(10);
			optionslist.setPadding(new Insets(10, 10, 10, 10 ));	
			Label layoutX = new Label("LayoutX");
			Label layoutY = new Label("LayoutY");
			Label width = new Label("Width");
			Label height = new Label("Height");
			Label imagepath = new Label("Image");
			Label rotate = new Label("Rotate");
			TextField rotateDegrees = new TextField();
			Button changepath = new Button("Change path");
			TextField layoutXTF = new TextField();
			TextField layoutYTF = new TextField();
			TextField widthtf = new TextField();
			TextField heighttf = new TextField();
			
			layoutXTF.setPrefWidth(90);
			layoutXTF.setPrefHeight(26);
			layoutYTF.setPrefWidth(90);
			layoutYTF.setPrefHeight(26);
			widthtf.setPrefWidth(90);
			widthtf.setPrefHeight(26);
			heighttf.setPrefWidth(90);
			heighttf.setPrefHeight(26);
			changepath.setPrefWidth(90);
			changepath.setPrefHeight(26);
			rotateDegrees.setPrefWidth(90);
			rotateDegrees.setPrefHeight(26);
			
			showvalues((int) Math.round(((ImageView) selectedElement).getLayoutX()), (int) Math.round(((ImageView) selectedElement).getLayoutY()), (int) Math.round(((ImageView) selectedElement).getFitWidth()), (int) Math.round(((ImageView) selectedElement).getFitHeight()), (int) Math.round(((ImageView) selectedElement).getRotate()));
			
			layoutXTF.setText(String.valueOf((int) Math.round(((ImageView) selectedElement).getLayoutX())));
			layoutYTF.setText(String.valueOf((int) Math.round(((ImageView) selectedElement).getLayoutY())));
			widthtf.setText(String.valueOf((int) Math.round(((ImageView) selectedElement).getFitWidth())));
			heighttf.setText(String.valueOf((int) Math.round(((ImageView) selectedElement).getFitHeight())));
			rotateDegrees.setText(String.valueOf((int) Math.round(((ImageView) selectedElement).getRotate())));
			
			optionslist.add(imagepath, 0, 0);
			optionslist.add(layoutX, 0, 1);
			optionslist.add(layoutY, 0, 2);
			optionslist.add(width, 0, 3);
			optionslist.add(height, 0, 4);
			optionslist.add(rotate, 0, 5);
			optionslist.add(changepath, 1, 0);
			optionslist.add(layoutXTF, 1, 1);
			optionslist.add(layoutYTF, 1, 2);
			optionslist.add(widthtf, 1, 3);
			optionslist.add(heighttf, 1, 4);
			optionslist.add(rotateDegrees, 1, 5);
			optionslistplaceholder.setContent(optionslist);
			
			layoutXTF.textProperty().addListener((ov, oldValue, newValue) -> {
				//System.out.println(Integer.parseInt(newValue));
				if (!newValue.isEmpty()) {
					if (isInt(newValue)) {
						selectedElement.setLayoutX(Integer.parseInt(newValue));
						showvalues(Integer.parseInt(newValue), (int) Math.round(((ImageView) selectedElement).getLayoutY()), (int) Math.round(((ImageView) selectedElement).getFitWidth()), (int) Math.round(((ImageView) selectedElement).getFitHeight()), (int) Math.round(((ImageView) selectedElement).getRotate()));
					}
				}
				
			});
			
			layoutYTF.textProperty().addListener((ov, oldValue, newValue) -> {
				//System.out.println(Integer.parseInt(newValue));
				if (!newValue.isEmpty()) {
					if (isInt(newValue)) {
						selectedElement.setLayoutY(Integer.parseInt(newValue));
						showvalues((int) Math.round(((ImageView) selectedElement).getLayoutX()), Integer.parseInt(newValue), (int) Math.round(((ImageView) selectedElement).getFitWidth()), (int) Math.round(((ImageView) selectedElement).getFitHeight()), (int) Math.round(((ImageView) selectedElement).getRotate()));
					}
				}
				
			});
			
			widthtf.textProperty().addListener((ov, oldValue, newValue) -> {
				
				if (!newValue.isEmpty()) {
					if (isInt(newValue)) {
						//System.out.println(Integer.parseInt(newValue));
						((ImageView) selectedElement).setFitWidth(Double.parseDouble(newValue));
						showvalues((int) Math.round(((ImageView) selectedElement).getLayoutX()), (int) Math.round(((ImageView) selectedElement).getLayoutY()), Integer.parseInt(newValue), (int) Math.round(((ImageView) selectedElement).getFitHeight()), (int) Math.round(((ImageView) selectedElement).getRotate()));
					}
				}
				
			});
			
			heighttf.textProperty().addListener((ov, oldValue, newValue) -> {
				//System.out.println(Integer.parseInt(newValue));
				if (!newValue.isEmpty()) {
					if (isInt(newValue)) {
						((ImageView) selectedElement).setFitHeight(Double.parseDouble(newValue));
						showvalues((int) Math.round(((ImageView) selectedElement).getLayoutX()), (int) Math.round(((ImageView) selectedElement).getLayoutY()), (int) Math.round(((ImageView) selectedElement).getFitWidth()), Integer.parseInt(newValue), (int) Math.round(((ImageView) selectedElement).getRotate()));
					}
				}
					
			});
			
			changepath.setOnAction(event -> {
				
				ppChooser = new FileChooser();
				//ppChooser.setTitle("Select image");
				imgfilter = new FileChooser.ExtensionFilter("*.png", "*.jpg", "*.jpeg");
				ppChooser.getExtensionFilters().add(imgfilter);
				chosenImage = ppChooser.showOpenDialog(stage);
				
				try {
					try {
						imageinputstream = new FileInputStream(chosenImage);
						Image image = new Image(imageinputstream);
						((ImageView) selectedElement).setImage(image);
					} catch (NullPointerException e) {
						System.out.println("No image chosen");
					}
					
					 
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			
			rotateDegrees.textProperty().addListener((ov, oldValue, newValue) -> {

				if (!newValue.isEmpty()) {
					if (isInt(newValue)) {
						((ImageView) selectedElement).setRotate(Double.parseDouble(newValue));
						showvalues((int) Math.round(((ImageView) selectedElement).getLayoutX()), (int) Math.round(((ImageView) selectedElement).getLayoutY()), (int) Math.round(((ImageView) selectedElement).getFitWidth()), (int) Math.round(((ImageView) selectedElement).getFitHeight()), Integer.parseInt(newValue));
					}
				} else if (newValue.isEmpty()) {
					((ImageView) selectedElement).setRotate(0);
					showvalues((int) Math.round(((ImageView) selectedElement).getLayoutX()), (int) Math.round(((ImageView) selectedElement).getLayoutY()), (int) Math.round(((ImageView) selectedElement).getFitWidth()), (int) Math.round(((ImageView) selectedElement).getFitHeight()), 0);
				}
			});
			
			
			
		} else {
			optionslist.setHgap(10);
			optionslist.setVgap(10);
			optionslist.setPadding(new Insets(10, 10, 10, 10 ));	
			Label backC = new Label("Background");
			ColorPicker cpbackg = new ColorPicker();
			cpbackg.setPrefWidth(90);
			cpbackg.setPrefHeight(26);
			cpbackg.setValue(Color.valueOf(canvas.getStyle().toString().substring(21,canvas.getStyle().toString().length())));
			//cpbackg.setValue(canvas.getStyle());
			optionslist.add(backC, 0, 0);
			optionslist.add(cpbackg, 1, 0);
			optionslistplaceholder.setContent(optionslist);
			cpbackg.setOnAction(e -> {
				System.out.println(cpbackg.getValue());
				String prestate = "-fx-background-color:#";
				canvas.setStyle(prestate.concat(cpbackg.getValue().toString().substring(2,cpbackg.getValue().toString().length())));
			});
			
		}
		
		
	}
	
	public boolean isInt(String s) {
		boolean check = true;
		try {																			// try block that parses the string user input to a integer.
			Integer userinput = Integer.parseInt(s);
		} catch (NumberFormatException e) {												// catches the NumberFormatException
			return false;																// returns false if this exception is thrown
		}
		return check;																	// returns true if the exception is not thrown and the user input is valid
	}
	
	public void saveAs() {
		try {
			ppChooser = new FileChooser();
			File savefile = ppChooser.showSaveDialog(new Stage());
			if (file != null) {
				WritableImage save = canvas.snapshot(new SnapshotParameters(), null);
				
				RenderedImage bsave = SwingFXUtils.fromFXImage(save, null);
				
				try {
					ImageIO.write(bsave, "png", savefile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IllegalArgumentException e) {
				System.out.println("Save Incomplete");
			}
		
	}
	
	public void logout() {
		Alert logout = new Alert(AlertType.CONFIRMATION);
		logout.setHeaderText("Logging out...");
		logout.setContentText("Would you like to save your work?");
		logout.setTitle("Logout");
		
		if (logout.showAndWait().get() == ButtonType.OK) {
			stage.close();
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/LoginPage.fxml"));
				// Load an object hierarchy from a FXML document
				GridPane root = loader.load();
				LoginController lController = loader.getController();
				Stage mainStage = new Stage();
				lController.setStage(mainStage);
				
				Scene scene = new Scene(root, 500, 300);

				mainStage.setScene(scene);
				// Set the value of the property resizable
				mainStage.setResizable(false);
				mainStage.setTitle("Login Page");
				mainStage.show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//staoge.close();
		
	}
	
	
//	public void setHW() {
//		heightTF.textProperty().bindBidirectional(CH, NumberFormat.getInstance());
//		widthTF.textProperty().bindBidirectional(CW, NumberFormat.getInstance());
//		
//		
//	}
	
//	public void initialiseWandH(ActionEvent event) {
//		
//		if (!widthTF.getText().isEmpty() && !heightTF.getText().isEmpty()) {
//			Double width = Double.parseDouble(widthTF.getText());
//			Double height = Double.parseDouble(widthTF.getText());
//			canvasWidth = width;
//			canvasHeight = height;
//			stage.close();
//			
//			//stage.toBack();
//			
//			
////			try {
////				FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Canvas.fxml"));
////				AnchorPane root = loader.load();
////				Scene canvasScene = new Scene(root, 670, 432);
////				//CanvasController CController = loader.getController();
//////				Stage cstage = new Stage();
//////				CController.setStage(cstage);
//////				System.out.println(root);
////				stage.setScene(canvasScene);
////				stage.setResizable(false);
////				stage.setTitle("Canvas Page");
////				stage.show();
////			} catch (IOException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//			//canvasplaceholder.getParent();
//			AnchorPane canvas = new AnchorPane();
//			canvas.setPrefWidth(canvasWidth);
//			canvas.setPrefHeight(canvasHeight);
//			canvas.setStyle("-fx-background-color:white");
//			canvasplaceholder.getChildren().add(canvas);
//			//Scene scanvas = new Scene(canvasplaceholder);
//		} else {
//			
//		}
//		
//		
//		
//	}
	
//	public void cancelNewCanvas(ActionEvent event) {
//		stage.close();
//	}
	
//	public void setCanvasHW(double height, double width) {
//		this.canvasHeight = height;
//		this.canvasWidth = width;
//		
//		//Scene scanvas = new Scene(canvasplaceholder);
//		
//	}
//	public void changeProfile() {
//		File file = new File("output.jpg");
//		try {
//			Connection connection = DriverManager.getConnection("jdbc:sqlite:user.db");
//			Statement stmt = connection.createStatement();
//			String query = "SELECT * FROM User";
//			boolean found = false;
//			try (ResultSet resultSet = stmt.executeQuery(query)) {
//				while(resultSet.next() && !found) {		
//					if (resultSet.getString("username").compareTo(username) == 0) {
//						found = true;
//						System.out.println("gays");
//						ByteArrayInputStream incomingPP = new ByteArrayInputStream(resultSet.getBytes("profilepic"));
//						try {
//							BufferedImage bimg = ImageIO.read(incomingPP);
//							ImageIO.write(bimg, "jpg", file);
//							InputStream inputstream = new FileInputStream(file);
//						profilepic.setImage(new Image(inputstream));
//						
//						UserName.setText(resultSet.getString("firstname") + " " + resultSet.getString("lastname"));
//							
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						
//					} 
//				}
//			}
//		} catch (SQLException e) {
//			System.out.println(e.getMessage());
//		}
//	}

	
	
	public void setStage(Stage stage) {
		this.stage = stage;
	} 
	

	
}
