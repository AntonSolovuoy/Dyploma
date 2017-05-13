package application;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class YUV {
	
	private Scalar lowerb = new Scalar(128, 0, 0),
				   upperb = new Scalar(255, 128 + 5, 128 + 5);
	
	public Mat getYUVMask(Mat frame) {
		
		int rows = frame.rows(),
			cols = frame.cols(),
			type = frame.type();
		
		Mat yuvFrame = new Mat(rows, cols, type),
				mask = new Mat(rows, cols, type);
		
		Imgproc.cvtColor(frame, yuvFrame, Imgproc.COLOR_BGR2YUV);
		
		Core.inRange(yuvFrame, lowerb, upperb, mask);
		
		return mask;
	}
}