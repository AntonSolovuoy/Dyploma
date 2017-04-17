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

public class FrameDifference {
	
	@FXML
	private ImageView imageView;
	
	private Mat currFrame,
				prevFrame;
	
	private VideoCapture videoCapture;
	
	private ScheduledExecutorService timer;
	
	private boolean flag;
	
	private String path;
	
	@FXML
	public void initialize() {
		
		videoCapture = new VideoCapture();
		
		currFrame = prevFrame = new Mat();
		
		timer = Executors.newSingleThreadScheduledExecutor();
		
		flag = false;
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
						
						currFrame = new Mat();
						
						videoCapture.read(currFrame);
						
						if (!currFrame.empty()) {
							
							if (flag) showFrame(getBinaryMask(currFrame.clone(), prevFrame.clone()));
							
							currFrame.copyTo(prevFrame);
							
							flag = true;
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
	
	private Mat getBinaryMask(Mat currFrame, Mat prevFrame) {
		int rows = currFrame.rows(),
			cols = currFrame.cols();
		
		Mat mask = new Mat(rows, cols, CvType.CV_8UC3);
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				
				double[] currBGR = currFrame.get(i, j),
						 prevBGR = prevFrame.get(i, j),
						 
						 data = new double[3];				
				
				double r = Math.abs(currBGR[2] - prevBGR[2]),
					   g = Math.abs(currBGR[1] - prevBGR[1]),
					   b = Math.abs(currBGR[0] - prevBGR[0]),
					   
					   delta = (r + g + b) / 3;
				
				if (delta >= 10) {
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
	/*
	private Mat getBinaryMask(Mat currFrame, Mat prevFrame) {
		int rows = currFrame.rows(),
			cols = currFrame.cols();
		
		int sensivity = 1;
		
		Size size = new Size(3, 3);
		
		Scalar scalar1 = new Scalar(0, 0, 255);
		Scalar scalar2 = new Scalar(0, 255, 0);
		
		Mat frame = new Mat(rows, cols, CvType.CV_8UC3);
		Mat mask = new Mat(rows, cols, CvType.CV_8UC3);
		
		Imgproc.GaussianBlur(currFrame, currFrame, size, 0);
		
		Core.subtract(prevFrame, currFrame, mask);
		
		Imgproc.cvtColor(mask, mask, Imgproc.COLOR_RGB2GRAY);
		
		Imgproc.threshold(mask, mask, sensivity, 255, Imgproc.THRESH_BINARY);
		
		Mat v = new Mat();
		
		List<MatOfPoint>contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(mask, contours, v, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		
		double maxArea = 30;

		for(int idx = 0; idx < contours.size(); idx++) {
			Mat contour = contours.get(idx);
			double contourarea = Imgproc.contourArea(contour);
			if(contourarea > maxArea) {

				Rect r = Imgproc.boundingRect(contours.get(idx));
				Imgproc.drawContours(frame, contours, idx, scalar1);
				Imgproc.rectangle(frame, r.br(), r.tl(), scalar2, 1);
			}
			contour.release();
		}
		
		return frame;
	}*/
	
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