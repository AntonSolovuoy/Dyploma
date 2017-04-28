package application;

import application.Mat2Image;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	
	private Mat currFrame,
				prevFrame;
	
	private boolean flag;
	
	private final String WildfireDetectionName = "Wildfire Detection",
			BinaryMotionMaskName = "Binary Motion Mask",
			YUVMaskName = "YUV Mask",
			HSVMaskName = "HSV Mask",
			MOG2MaskName = "MOG2 Mask",
			InformationName = "Information";
	
	private VideoCapture vc;
	
	private ScheduledExecutorService timer;
	
	private String path = "C:/Users/admin/workspace/Application/src/application/2_converted.avi";
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			currFrame = new Mat();
			prevFrame = new Mat();
			
			System.out.println("Camera - 1\n"
					+ "Video - 2");
			
			@SuppressWarnings("resource")
			Scanner cin = new Scanner(System.in);
			String choice = cin.nextLine();
			
			
			
			if (choice.equals("1")) {
				vc = new VideoCapture(0);
			} else {
				vc = new VideoCapture(path);
			}
			
			
			
			
			
			
			FXMLLoader InformationLoader = new FXMLLoader(getClass().getResource("Information.fxml"));
			
			
			Pane pane = (Pane) InformationLoader.load();
			
			Stage stage = new Stage();
			
			Scene scene = new Scene(pane, 300, 300);
			
			stage.setTitle(InformationName);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.setX(50);
			stage.setX(150);
			
			stage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					setClosed();
				}
			}));
			
			stage.show();
			
			Information InformationController = InformationLoader.getController();
			
			InformationController.setCountString((int) vc.get(Videoio.CAP_PROP_FRAME_COUNT) + "");
			InformationController.setWidthString((int) vc.get(Videoio.CAP_PROP_FRAME_WIDTH) + "");
			InformationController.setHeigthString((int) vc.get(Videoio.CAP_PROP_FRAME_HEIGHT) + "");
			InformationController.setFpsString((int) vc.get(Videoio.CAP_PROP_FPS) + "");
			InformationController.setPositionString((int) vc.get(Videoio.CAP_PROP_POS_FRAMES) + "");
			InformationController.initialize();
			
			
			
			
			
			timer = Executors.newSingleThreadScheduledExecutor();
			
			vc.read(currFrame);
			
			
			System.out.println("Size window: " + currFrame.cols() + " x " + currFrame.rows());
			
			
			
			FXMLLoader HSVLoader = new FXMLLoader(getClass().getResource("HSV.fxml"));
			
			show(HSVLoader, HSVMaskName);
			
			HSV HSVController = HSVLoader.getController();
			
			
			
			FXMLLoader wildfireDetectionLoader = new FXMLLoader(getClass().getResource("WildfireDetection.fxml"));

			show(wildfireDetectionLoader, WildfireDetectionName);
			
			WildfireDetection wildfireDetectionController = wildfireDetectionLoader.getController();
			
			
		
			FXMLLoader frameDifferenceLoader = new FXMLLoader(getClass().getResource("FrameDifference.fxml"));
			
			show(frameDifferenceLoader, BinaryMotionMaskName);
			
			FrameDifference frameDifferenceController = frameDifferenceLoader.getController();
			
			
			
			FXMLLoader YUVLoader = new FXMLLoader(getClass().getResource("YUV.fxml"));
			
			show(YUVLoader, YUVMaskName);
			
			YUV YUVController = YUVLoader.getController();
			
			
			
			FXMLLoader MOG2Loader = new FXMLLoader(getClass().getResource("MOG2.fxml"));
			
			show(MOG2Loader, MOG2MaskName);
			
			MOG2 MOG2Controller = MOG2Loader.getController();
			
			
			
			
			
			

			
			
			
			
			
			if (vc.isOpened()) {

				Runnable frameGrabber = new Runnable() {
							
					@Override
					public void run() {
						try {		
							
							currFrame = new Mat();
							
							vc.read(currFrame);
							
							
							InformationController.setPositionString((int) vc.get(Videoio.CAP_PROP_POS_FRAMES) + "");
							InformationController.initialize();
							
							
							
							
							wildfireDetectionController.showImage(Mat2Image.mat2Image(currFrame.clone()));
							
							
							Scalar lowerbYUV = new Scalar(128, 0, 0);
							Scalar upperbYUV = new Scalar(255, 128 + 5, 128 + 5);
							
							YUVController.showImage(Mat2Image.mat2Image(YUVController.getYUVMask(currFrame.clone(), lowerbYUV, upperbYUV)));
							
							
							Scalar lowerbHSV = new Scalar(50, 2, 127);
							Scalar upperbHSV = new Scalar(105, 128, 255);
							
							HSVController.showImage(Mat2Image.mat2Image(HSVController.getHSVMask(currFrame.clone(), lowerbHSV, upperbHSV)));
							
							
							MOG2Controller.showImage(Mat2Image.mat2Image(MOG2Controller.getMOG2Mask(currFrame.clone())));
							
							
							double treshold = 10;
							
							if (flag) {
								frameDifferenceController.showImage(Mat2Image.mat2Image(frameDifferenceController.getBinaryMask(currFrame.clone(), prevFrame.clone(), treshold)));
							}
							
							currFrame.copyTo(prevFrame);
							
							flag = true;
							
							
							if (!choice.equals("1")) {
								
								if (vc.get(Videoio.CAP_PROP_FRAME_COUNT) == vc.get(Videoio.CAP_PROP_POS_FRAMES)) {

									setClosed();
								}
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

			
		} catch (Exception e) {
			System.out.println("Error in start: " + e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		launch(args);
	}
	
	public void show(FXMLLoader loader, String title) {
		try {
			Pane pane = (Pane) loader.load();
			
			Stage stage = new Stage();
			
			Scene scene = new Scene(pane, currFrame.cols() - 10, currFrame.rows() - 10);
			
			stage.setTitle(title);
			stage.setScene(scene);
			stage.setResizable(false);
			
			stage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					setClosed();
				}
			}));
			
			stage.show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		
		if (this.vc.isOpened()) {
			this.vc.release();
		}
	}
}
