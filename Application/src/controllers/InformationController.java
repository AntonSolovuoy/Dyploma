package controllers;

import application.Information;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class InformationController extends Application{

	@FXML
	private Text count,
			width,
			height,
			fps,
			position;

	private FXMLLoader loader;
	
	@Override
	public void start(Stage stage) throws Exception {
		loader = new FXMLLoader(getClass().getResource("/views/Information.fxml"));
		
		Pane pane = (Pane) loader.load();
		
		Scene scene = new Scene(pane, 300, 300);
		
		stage = new Stage();
		
		stage.setScene(scene);
		stage.setTitle("Information");
		stage.setResizable(false);
		stage.setX(50);
		stage.setY(50);
		
		stage.show();
	}
	
	public void initialize(Information information) {
		count.setText(information.getCount() + "");
		width.setText(information.getWidth() + "");
		height.setText(information.getHeight() + "");
		fps.setText(information.getFPS() + "");
		position.setText(information.getPosition() + "");
    }

	public void setPosition(String text) {
		position.setText(text);
	}
	
	public InformationController getController() {
		return loader.getController();
	}
}
