package controllers;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class WildfireDetectionController extends Application{

	@FXML
	private ImageView imageView;
	
	private FXMLLoader loader;
	
	private int width, height;

	@Override
	public void start(Stage stage) throws Exception {

		loader = new FXMLLoader(getClass().getResource("/views/WildfireDetection.fxml"));
		
		Pane pane = (Pane) loader.load();
		
	    Scene scene = new Scene(pane, width, height);
		
		stage = new Stage();
		
		stage.setScene(scene);
		stage.setTitle("Wildfire Detection");
		stage.setResizable(false);
		stage.setX(50);
		stage.setY(400);
		
		stage.show();
	}
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public void setImage(Image image) {

		imageView.setImage(image);
	}
	
	public WildfireDetectionController getController() {
		return loader.getController();
	}
}
