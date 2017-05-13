package controllers;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Show extends Application{

	@FXML
	private ImageView imageView;
	
	private int w, h;
	private String title;
	
	private FXMLLoader loader;
	
	private Pane pane;
	
	private Scene scene;
	
	private Show show;
	
	public void setSize(int w, int h) {
		this.w = w;
		this.h = h;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		
		loader = new FXMLLoader(getClass().getResource("/views/Show.fxml"));
		
		pane = (Pane) loader.load();
		
		scene = new Scene(pane, w, h);
		
		stage = new Stage();
		
		stage.setScene(scene);
		stage.setTitle(title);
		stage.setResizable(false);
		
		stage.show();
		
		show = loader.getController();
	}
	
	public Show getController() {
		return show;
	}
	
	public void setImage(Image image) {

		imageView.setImage(image);
	}
}