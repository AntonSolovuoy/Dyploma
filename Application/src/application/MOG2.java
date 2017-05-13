package application;

import org.opencv.core.Mat;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

public class MOG2 {
	
	private MOG2 mog2;
	
	private BackgroundSubtractorMOG2 backgroundSubtractorMOG2 = Video.createBackgroundSubtractorMOG2(1000, 40, false);

	public Mat getMOG2Mask(Mat frame) {
		Mat mask = new Mat();
		
		backgroundSubtractorMOG2.apply(frame, mask);
		
		return mask;
	}
	
	public MOG2 getMOG2() {
		return mog2;
	}
}