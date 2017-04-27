package application;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class WildfireDetection {
	
	@FXML
	private ImageView imageView;
	
	public void showImage(Image image) {
		
		imageView.setImage(image);
	}
}