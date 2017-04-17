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

public class YUV {
	
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
							showFrame(getYUVMask(frame.clone()));
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
	
	private Mat getYUVMask(Mat frame) {
		int rows = frame.rows(),
			cols = frame.cols();
		
		Mat mask = new Mat(rows, cols, CvType.CV_8UC3);
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {

				double[] bgr = frame.get(i, j),
						 yuv = BGR2YUV(bgr),
						 
						 data = new double[3];
				
				double y = 128,
					   u = 5,
					   v = 5;
				
				if (yuv[0] > y && Math.abs(yuv[1] - 128) < u && Math.abs(yuv[2] - 128) < v) {
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
	
	private double[] BGR2YUV (double[] bgr) {
		double[] yuv = new double[3];
		double r = bgr[2], g = bgr[1], b = bgr[0];
		
		yuv[0] = 0.299 * r + 0.587 * g + 0.114 * b;
		yuv[1] = -0.14713 * r - 0.28886 * g + 0.436 * b + 128;
		yuv[2] = 0.615 * r - 0.51499 * g -0.10001 * b + 128;
		
		return yuv;
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