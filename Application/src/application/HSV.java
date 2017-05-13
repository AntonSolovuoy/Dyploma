package application;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class HSV {
	
	private Scalar lowerb = new Scalar(50, 2, 127),
				   upperb = new Scalar(105, 128, 255);

	public Mat getHSVMask(Mat frame) {
		
		int rows = frame.rows(),
			cols = frame.cols(),
			type = frame.type();
		
		Mat hsvFrame = new Mat(rows, cols, type),
				mask = new Mat(rows, cols, type);
		
		Imgproc.cvtColor(frame, hsvFrame, Imgproc.COLOR_BGR2HSV);
		
		Core.inRange(hsvFrame, lowerb, upperb, mask);
		
		return mask;
	}
}