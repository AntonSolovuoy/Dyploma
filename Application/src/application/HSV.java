package application;

import java.io.ByteArrayInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class HSV {
	
	@FXML
	private ImageView imageView;
	
	private Mat frame;
	
	private VideoCapture videoCapture;
	
	private ScheduledExecutorService timer;
	
	private String path;
	
	@FXML
	public void initialize() {
		
		videoCapture = new VideoCapture();
		
		frame = new Mat();
		
		timer = Executors.newSingleThreadScheduledExecutor();
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	protected void start() {

		videoCapture.open(path);
		
		if (videoCapture.isOpened()) {

			Runnable frameGrabber = new Runnable() {
						
				@Override
				public void run() {
					try {		
						
						frame = new Mat();
						
						videoCapture.read(frame);
						
						if (!frame.empty()) {
							showFrame(getHSVMask(frame.clone()));
						}
					} catch (Exception e) {
						System.err.println("ERROR: " + e);
					}
				}
			};
			
			timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
		} else {
			System.err.println("Failed to open the camera connection...");
		}
	}
	
	private Mat getHSVMask(Mat frame) {
		int rows = frame.rows(),
			cols = frame.cols();
		
		Mat mask = new Mat(rows, cols, CvType.CV_8UC3);
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				
				double[] bgr = frame.get(i, j),
						 hsv = BGR2HSV(bgr),
						 
						 data = new double[3];
				
				double h = hsv[0],
					   s = hsv[1],
					   v = hsv[2];
				
				if ((100 < h && h < 210) && (0.008 < s && s < 0.5) && (127 < v && v < 255)) {
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
	
	private void showFrame(Mat frame) {

		imageView.setImage(mat2Image(frame));
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
	
	private Image mat2Image(Mat frame) {
		MatOfByte buffer = new MatOfByte();

		Imgcodecs.imencode(".png", frame, buffer);

		return new Image(new ByteArrayInputStream(buffer.toArray()));
	}
	
	private void stopAcquisition() {
		if (this.timer != null && !this.timer.isShutdown()) {
			try {
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}
		
		if (this.videoCapture.isOpened()) {
			this.videoCapture.release();
		}
	}
	
	protected void setClosed() {
		this.stopAcquisition();
	}
}