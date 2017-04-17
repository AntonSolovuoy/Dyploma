package application;

import java.io.ByteArrayInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Controller {
	
	@FXML
	private ImageView originalFrame,
					  binaryFrame,
					  YUVFrame,
					  HSVFrame,
					  MOGFrame,
					  MOG2Frame;
	
	private Mat currFrame,
				prevFrame,
				mask;
	
	private String path = "";
	
	private BackgroundSubtractorMOG2 fgbg;
	
	private VideoCapture capture;
	
	private ScheduledExecutorService timer;
	
	boolean flag;
	
	@FXML
	public void initialize() {
		
		capture = new VideoCapture();
		
		currFrame = prevFrame = mask = new Mat();
		
		fgbg = Video.createBackgroundSubtractorMOG2();
		
		timer = Executors.newSingleThreadScheduledExecutor();
		
		flag = false;
		
	//	path = "C:/Users/admin/workspace/Application/src/application/2_converted.avi";
	//	path = "C:/Users/admin/workspace/Application/src/application/1_converted.avi";
		
		
	}
	
	protected void start() {
		if (path != "") {
			capture.open(path);
		} else {
			capture.open(0);
		}
		
		
		
		if (capture.isOpened()) {

			Runnable frameGrabber = new Runnable() {
						
				@Override
				public void run() {
					try {		
						
						currFrame = new Mat();
						
						capture.read(currFrame);
						
						if (!currFrame.empty()) {
							showOriginal(currFrame.clone());
							
							if (flag) showBinaryMask(currFrame.clone(), prevFrame.clone());
							
							showYUVMask(currFrame.clone());
							
							showHSVMask(currFrame.clone());
							
							showMOG2Mask(currFrame.clone());
							
							currFrame.copyTo(prevFrame);
							
							flag = true;
						}
					} catch (Exception e) {
						System.err.println("ERROR: " + e);
					}
				}
			};
			
			System.out.println("width: " + capture.get(Videoio.CAP_PROP_FRAME_WIDTH)  + "\n"
							+ "height: " + capture.get(Videoio.CAP_PROP_FRAME_HEIGHT) + "\n"
							   + "fps: " + capture.get(Videoio.CAP_PROP_FPS)          + "\n"
					   + "frame count: " + capture.get(Videoio.CAP_PROP_FRAME_COUNT)  + "\n"
					   		  + "msec: " + capture.get(Videoio.CAP_PROP_POS_MSEC)     + "\n"
					    + "pos frames: " + capture.get(Videoio.CAP_PROP_POS_FRAMES));
			
			timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
		} else {
			System.err.println("Failed to open the camera connection...");
		}
	}
	
	private void showOriginal(Mat frame) {
		int x1 = 100, y1 = 130,
			x2 = 120, y2 = 150;
		Point point1 = new Point(x1, y1);
		Point point2 = new Point(x2, y2);
		Imgproc.rectangle(frame, point1, point2, new Scalar(0, 0, 255), 1);
		Image image = mat2Image(frame);
		originalFrame.setImage(image);
	}
	
	private void showBinaryMask(Mat currFrame, Mat prevFrame) {
		
		int rows = currFrame.rows();
		int cols = currFrame.cols();
		
		Mat frame = new Mat(rows, cols, CvType.CV_8UC3);
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				double[] currBGR = currFrame.get(i, j);
				double[] prevBGR = prevFrame.get(i, j);
				
				double r = Math.abs(currBGR[2] - prevBGR[2]);
				double g = Math.abs(currBGR[1] - prevBGR[1]);
				double b = Math.abs(currBGR[0] - prevBGR[0]);
				
				double delta = (r + g + b) / 3;
				
				if (delta >= 10) {
					currBGR[2] = 255;
					currBGR[1] = 255;
					currBGR[0] = 255;
				} else {
					currBGR[2] = 0;
					currBGR[1] = 0;
					currBGR[0] = 0;
				}
				
				frame.put(i, j, currBGR);
			}
		}
		
		binaryFrame.setImage(mat2Image(frame));
	}
	
	private void showYUVMask(Mat frame) {
		
		int rows = frame.rows();
		int cols = frame.cols();
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				
				double[] bgr = frame.get(i, j);
				
				double[] yuv = BGR2YUV(bgr);
				double y = 128, u = 5, v = 5;
				
				if (yuv[0] > y && Math.abs(yuv[1] - 128) < u && Math.abs(yuv[2] - 128) < v) {
					bgr[0] = 255;
					bgr[1] = 255;
					bgr[2] = 255;
				} else {
					bgr[0] = 0;
					bgr[1] = 0;
					bgr[2] = 0;
				}
				
				frame.put(i, j, bgr);
			}
		}
		
		YUVFrame.setImage(mat2Image(frame));
	}
	
	private void showHSVMask(Mat frame) {
		
		int rows = frame.rows();
		int cols = frame.cols();
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				
				double[] bgr = frame.get(i, j);
				
				double[] hsv = BGR2HSV(bgr);
				
				double h = hsv[0], s = hsv[1], v = hsv[2];
				
				if ((100 < h && h < 210) && (0.008 < s && s < 0.5) && (127 < v && v < 255)) {
					bgr[0] = 255;
					bgr[1] = 255;
					bgr[2] = 255;
				} else {
					bgr[0] = 0;
					bgr[1] = 0;
					bgr[2] = 0;
				}
				
				frame.put(i, j, bgr);
			}
		}
		
		HSVFrame.setImage(mat2Image(frame));
	}
	
	private void showMOG2Mask(Mat frame) {
		
		fgbg.apply(frame, mask);
		
		MOG2Frame.setImage(mat2Image(mask));
	}
	
	private Image mat2Image(Mat frame) {
		MatOfByte buffer = new MatOfByte();

		Imgcodecs.imencode(".png", frame, buffer);

		return new Image(new ByteArrayInputStream(buffer.toArray()));
	}
	
	private double[] BGR2YUV (double[] bgr) {
		double[] yuv = new double[3];
		double r = bgr[2], g = bgr[1], b = bgr[0];
		
		yuv[0] = 0.299 * r + 0.587 * g + 0.114 * b;
		yuv[1] = -0.14713 * r - 0.28886 * g + 0.436 * b + 128;
		yuv[2] = 0.615 * r - 0.51499 * g -0.10001 * b + 128;
		
		return yuv;
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
	
	private void stopAcquisition() {
		if (this.timer != null && !this.timer.isShutdown()) {
			try {
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}
		
		if (this.capture.isOpened()) {
			this.capture.release();
		}
	}
	
	protected void setClosed() {
		this.stopAcquisition();
	}
}