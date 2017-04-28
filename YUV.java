package application;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class YUV {
	
	@FXML
	private ImageView imageView;
	
	public Mat getYUVMask(Mat frame, Scalar lowerb, Scalar upperb) {
		int rows = frame.rows(),
			cols = frame.cols(),
			type = frame.type();
		
		Mat yuvFrame = new Mat(rows, cols, type),
				mask = new Mat(rows, cols, type);
		
		Imgproc.cvtColor(frame, yuvFrame, Imgproc.COLOR_BGR2YUV);
		
		Core.inRange(yuvFrame, lowerb, upperb, mask);
		
		return mask;
	}
	
	public void showImage(Image image) {

		imageView.setImage(image);
	}
}