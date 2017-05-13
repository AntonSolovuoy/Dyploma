package application;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class FrameDifference {

	private double treshold = 10;
	
	public Mat getBinaryMask(Mat currFrame, Mat prevFrame) {
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
}