package application;

import org.opencv.core.Mat;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MOG2 {
	
	@FXML
	private ImageView imageView;

	private BackgroundSubtractorMOG2 backgroundSubtractorMOG2 = Video.createBackgroundSubtractorMOG2(100, 100, false);

	public Mat getMOG2Mask(Mat frame) {
		Mat mask = new Mat();
		
		backgroundSubtractorMOG2.apply(frame, mask);
		
		return mask;
	}

	public void showImage(Image image) {
		imageView.setImage(image);
	}
}