package application;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import controllers.FrameDifferenceController;
import controllers.HSVController;
import controllers.InformationController;
import controllers.MOG2Controller;
import controllers.Show;
import controllers.WildfireDetectionController;
import controllers.YUVController;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
	
	private Mat currFrame,
				prevFrame;
	
	private boolean flag;
	
	private VideoCapture videoCapture;
	
	private ScheduledExecutorService timer;
	
	@Override
	public void start(Stage stage) {
		
		String path = "D:/Desktop/VideoClips/1.avi";
		
		try {
			currFrame = new Mat();
			prevFrame = new Mat();
			
			
			videoCapture = new VideoCapture(path);

			Information information = new Information(videoCapture);
			
			int width = information.getWidth() - 10;
			int height = information.getHeight() - 10;
			
			if (width > 400) {
				width = width / 2;
				height = height / 2;
			}
			
			timer = Executors.newSingleThreadScheduledExecutor();
			
			videoCapture.read(currFrame);
			
			
			
			
			InformationController informationController = new InformationController();
			informationController.start(stage);
			InformationController controller = informationController.getController();
			controller.initialize(information);
			
			
			
			YUV yuv = new YUV();
			YUVController yuvController = new YUVController();
			yuvController.setSize(width, height);
			yuvController.start(stage);
			
			HSV hsv = new HSV();
			HSVController hsvController = new HSVController();
			hsvController.setSize(width, height);
			hsvController.start(stage);
			
			
			
			//WildfireDetection wildfireDetection = new WildfireDetection();
			WildfireDetectionController wildfireDetectionController = new WildfireDetectionController();
			wildfireDetectionController.setSize(width, height);
			wildfireDetectionController.start(stage);
			
			
			FrameDifference frameDifference = new FrameDifference();
			FrameDifferenceController frameDifferenceController = new FrameDifferenceController();
			frameDifferenceController.setSize(width, height);
			frameDifferenceController.start(stage);

			MOG2 mog2 = new MOG2();
			MOG2Controller mog2Controller = new MOG2Controller();
			mog2Controller.setSize(width, height);
			mog2Controller.start(stage);
			
			
			
			
			Show showMOG2andHSV = new Show();
			showMOG2andHSV.setTitle("MOG2 and HSV");
			showMOG2andHSV.setSize(width, height);
			showMOG2andHSV.start(stage);
			
			
			Show showMOG2andYUV = new Show();
			showMOG2andYUV.setTitle("MOG2 and YUV");
			showMOG2andYUV.setSize(width, height);
			showMOG2andYUV.start(stage);
			
			
			stage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					setClosed();
				}
			}));
			
			if (videoCapture.isOpened()) {

				Runnable frameGrabber = new Runnable() {
							
					@Override
					public void run() {
						try {		
							
							currFrame = new Mat();
							
							videoCapture.read(currFrame);
							
							controller.setPosition((int) videoCapture.get(Videoio.CAP_PROP_POS_FRAMES) + "");

							Mat hsvMask = hsv.getHSVMask(currFrame.clone());
							Mat yuvMask = yuv.getYUVMask(currFrame.clone());
							Mat mog2Mask = mog2.getMOG2Mask(currFrame.clone());
							
							
							wildfireDetectionController.getController().setImage(Mat2Image.mat2Image(currFrame.clone()));
							yuvController.getController().setImage(Mat2Image.mat2Image(yuvMask));
							hsvController.getController().setImage(Mat2Image.mat2Image(hsvMask));
							mog2Controller.getController().setImage(Mat2Image.mat2Image(mog2Mask));
							
							
							
							
							Mat mat1 = currFrame.clone();
							Core.min(hsvMask, mog2Mask, mat1);
							showMOG2andHSV.getController().setImage(Mat2Image.mat2Image(mat1));
							
							Mat mat2 = currFrame.clone();
							Core.min(yuvMask, mog2Mask, mat2);
					//		Core.Mahalanobis(yuvMask, mog2Mask, mat2);
							showMOG2andYUV.getController().setImage(Mat2Image.mat2Image(mat2));
							
							if (flag) {
								frameDifferenceController
									.getController()
									.setImage(
											Mat2Image.mat2Image(
													frameDifference.getBinaryMask(currFrame.clone(), prevFrame.clone())
											)
									);
							}
							
							currFrame.copyTo(prevFrame);
							
							flag = true;
							
							
							if (videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT) != -1) {
								
								if (videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT) == videoCapture.get(Videoio.CAP_PROP_POS_FRAMES)) {

									setClosed();
								}
							}
						} catch (Exception e) {
							System.err.println("ERROR: " + e);
						}
					}
				};
				
			//	timer.scheduleAtFixedRate(frameGrabber, 0, (int) (1000000000 / videoCapture.get(Videoio.CAP_PROP_FPS)), TimeUnit.NANOSECONDS);
				timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
			} else {
				System.err.println("Failed to open the camera connection...");
			}

			
		} catch (Exception e) {
			System.out.println("Error in start: " + e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		launch(args);
	}
	
	private void setClosed() {
		if (timer != null && !timer.isShutdown()) {
			try {
				timer.shutdown();
				timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}
		
		if (this.videoCapture.isOpened()) {
			this.videoCapture.release();
		}
	}
}
