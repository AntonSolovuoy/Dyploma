package application;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class YUV {
	
	@FXML
	private ImageView imageView;
	
	public Mat getYUVMask(Mat frame, int[] tresholdYUV) {
		int rows = frame.rows(),
			cols = frame.cols(),
			type = frame.type();
		
		double[] yuv, data = new double[3];
		
		Mat hsvFrame = new Mat(frame.size(), frame.type());
		
		Imgproc.cvtColor(frame, hsvFrame, Imgproc.COLOR_BGR2YUV);
		
		Mat mask = new Mat(rows, cols, type);
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {

				yuv = hsvFrame.get(i , j);
				
				if (yuv[0] > tresholdYUV[0] && 
						Math.abs(yuv[1] - 128) < tresholdYUV[1] && 
						Math.abs(yuv[2] - 128) < tresholdYUV[2]) {
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
}