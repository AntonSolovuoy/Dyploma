package application;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class HSV {
	
	@FXML
	private ImageView imageView;

	public Mat getHSVMask(Mat frame, double[][] tresholdHSV) {
		int rows = frame.rows(),
			cols = frame.cols(),
			type = frame.type();
		
		Mat hsvFrame = new Mat(rows, cols, type);
		
		Imgproc.cvtColor(frame, hsvFrame, Imgproc.COLOR_BGR2HSV);
		
		double[] hsv, data = new double[3];
		
		Mat mask = new Mat(rows, cols, type);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				
				hsv = hsvFrame.get(i,  j);
				System.out.println(hsv[0] + " " + hsv[1] + " " + hsv[2]);
				
				if ((tresholdHSV[0][0] < hsv[0] && hsv[0] < tresholdHSV[0][1]) && 
						(tresholdHSV[1][0] < hsv[1] && hsv[1] < tresholdHSV[1][1]) && 
						(tresholdHSV[2][0] < hsv[2] && hsv[2] < tresholdHSV[2][1])) {
					data[0] = 255;
					data[1] = 255;
					data[2] = 255;
				} else {
					data[0] = 0;
					data[1] = 0;
					data[2] = 0;
				}
				
				mask.put(i, j, data);
			}
		}
		
		return mask;
	}
	
	public void showImage(Image image) {

		imageView.setImage(image);
	}
	
	private double[] BGR2HSV (double[] bgr) {
		double[] hsv = new double[3];
		
		double r = bgr[2], g = bgr[1], b = bgr[0];
		
		double max, min, delta;
		
		min = r < g ? r : g;
		min = min < b ? min : b;
		
		max = r > g ? r : g;
		max = max > b ? max : b;
		
		delta = max - min;
		
		if (max == min) {
			hsv[0] = 0;
		} else if (max == r && g >= b) {
			hsv[0] = 60 * (g - b) / delta;
		} else if (max == r && g < b) {
			hsv[0] = 60 * (g - b) / delta + 360;
		} else if (max == g) {
			hsv[0] = 60 * (b - r) / delta + 120;
		} else if (max == b) {
			hsv[0] = 60 * (r - g) / delta + 240;
		}
		
		if (max == 0) {
			hsv[1] = 0;
		} else {
			hsv[1] = 1 - (min / max);
		}
		
		hsv[2] = max;
		
		return hsv;
	}
}