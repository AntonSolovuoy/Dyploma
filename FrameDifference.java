package application;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FrameDifference {
	
	@FXML
	private ImageView imageView;

	public Mat getBinaryMask(Mat currFrame, Mat prevFrame, double treshold) {
		int rows = currFrame.rows(),
			cols = currFrame.cols(),
			type = currFrame.type();
		
		Mat diffFrame = new Mat(rows, cols, type),
				diffGrayFrame = new Mat(rows, cols, type),
				mask = new Mat(rows, cols, type);

		Core.absdiff(currFrame, prevFrame, diffFrame);
		
		Imgproc.cvtColor(diffFrame, diffGrayFrame, Imgproc.COLOR_BGR2GRAY);
		
		Imgproc.threshold(diffGrayFrame, mask, treshold, 255, Imgproc.THRESH_BINARY);
		
		return mask;
	}
	
	public void showImage(Image image) {
		
		imageView.setImage(image);
	}
}