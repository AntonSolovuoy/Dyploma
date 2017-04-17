package application;

import java.io.ByteArrayInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class WildfireDetection {
	
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
						
						System.out.println(Videoio.CAP_PROP_POS_MSEC);
						
						if (!frame.empty()) {			
							showFrame(frame.clone());
						}
					} catch (Exception e) {
						System.err.println("ERROR in WD: " + e);
					}
				}
			};

			System.out.println("width: " + videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH)  + "\n"
					+ "height: " + videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT) + "\n"
					   + "fps: " + videoCapture.get(Videoio.CAP_PROP_FPS)          + "\n"
			   + "frame count: " + videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT)  + "\n"
			   		  + "msec: " + videoCapture.get(Videoio.CAP_PROP_POS_MSEC)     + "\n"
			    + "pos frames: " + videoCapture.get(Videoio.CAP_PROP_POS_FRAMES));
			
			timer.scheduleAtFixedRate(frameGrabber, 0, (33), TimeUnit.MILLISECONDS);
		} else {
			System.err.println("Failed to open the camera connection...");
		}
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