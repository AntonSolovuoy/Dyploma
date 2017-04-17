package application;

import java.io.ByteArrayInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MOG2 {
	
	@FXML
	private ImageView imageView;
	
	private Mat frame;
	
	private BackgroundSubtractorMOG2 fgbg;
	
	private VideoCapture videoCapture;
	
	private ScheduledExecutorService timer;
	
	private String path;
	
	@FXML
	public void initialize() {
		
		videoCapture = new VideoCapture();
		
		frame = new Mat();
		
		fgbg = Video.createBackgroundSubtractorMOG2();
		
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
							showFrame(getMOG2Mask(frame.clone()));
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
	
	private Mat getMOG2Mask(Mat frame) {
		Mat mask = new Mat();
		
		fgbg.apply(frame, mask);
		
		return mask;
	}
	
	private void showFrame(Mat frame) {
		
		imageView.setImage(mat2Image(frame));
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