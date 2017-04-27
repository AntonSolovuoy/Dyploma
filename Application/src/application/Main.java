package application;

import java.io.ByteArrayInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	
	private Mat currFrame = new Mat(),
				prevFrame = new Mat();
	
	private boolean flag;
	
	private final String WildfireDetectionName = "Wildfire Detection",
			BinaryMotionMaskName = "Binary Motion Mask",
			YUVMaskName = "YUV Mask",
			HSVMaskName = "HSV Mask",
			MOG2MaskName = "MOG2 Mask";
	
//	private VideoCapture vc = new VideoCapture("C:/Users/admin/workspace/Application/src/application/2_converted.avi");
	private VideoCapture vc = new VideoCapture(0);
	
	@Override
	public void start(Stage primaryStage) {
		try {
			ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
			
			vc.read(currFrame);
			
			
			FXMLLoader HSVLoader = new FXMLLoader(getClass().getResource("HSV.fxml"));
			
			show(HSVLoader, HSVMaskName, 500, 400, timer);
			
			HSV HSVController = HSVLoader.getController();
			/*
			
			FXMLLoader wildfireDetectionLoader = new FXMLLoader(getClass().getResource("WildfireDetection.fxml"));

			show(wildfireDetectionLoader, WildfireDetectionName, 300, 250, timer);
			
			WildfireDetection wildfireDetectionController = wildfireDetectionLoader.getController();
			
			
		
			FXMLLoader frameDifferenceLoader = new FXMLLoader(getClass().getResource("FrameDifference.fxml"));
			
			show(frameDifferenceLoader, BinaryMotionMaskName, 100, 100, timer);
			
			FrameDifference frameDifferenceController = frameDifferenceLoader.getController();
			
			
			
			FXMLLoader YUVLoader = new FXMLLoader(getClass().getResource("YUV.fxml"));
			
			show(YUVLoader, YUVMaskName, 100, 400, timer);
			
			YUV YUVController = YUVLoader.getController();
			
			
			
			
			
			
			
			FXMLLoader MOG2Loader = new FXMLLoader(getClass().getResource("MOG2.fxml"));
			
			show(MOG2Loader, MOG2MaskName, 500, 100, timer);
			
			MOG2 MOG2Controller = MOG2Loader.getController();
			
			*/
			
			if (vc.isOpened()) {

				Runnable frameGrabber = new Runnable() {
							
					@Override
					public void run() {
						try {		
							
							currFrame = new Mat();
							
							vc.read(currFrame);
		
						/*	
							
							wildfireDetectionController.showImage(mat2Image(currFrame.clone()));
							
							
							
							int[] tresholdYUV = {128, 5, 5};
							
							YUVController.showImage(mat2Image(YUVController.getYUVMask(currFrame.clone(), tresholdYUV)));
							*/
							double[][] tresholdHSV = {{100, 210},
									 				  {0.008, 0.5},
									 				  {127, 255}};
							
							HSVController.showImage(mat2Image(HSVController.getHSVMask(currFrame.clone(), tresholdHSV)));
							
							/*MOG2Controller.showImage(mat2Image(MOG2Controller.getMOG2Mask(currFrame.clone())));
							
							
							double treshold = 10;
							
							if (!currFrame.empty()) {
								
								if (flag) {
									Mat mask = new Mat();
									
									mask = frameDifferenceController.getBinaryMask(currFrame.clone(), prevFrame.clone(), treshold);
									
									Image image = mat2Image(mask);
									
									frameDifferenceController.showImage(image);
								}
								
								currFrame.copyTo(prevFrame);
								
								flag = true;
							}
							
							*/
							/*
							if (vc.get(Videoio.CAP_PROP_FRAME_COUNT) == vc.get(Videoio.CAP_PROP_POS_FRAMES)) {
								vc.release();
								timer.shutdown();
							}*/
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
	
	public Image mat2Image(Mat frame) {
		MatOfByte buffer = new MatOfByte();

		Imgcodecs.imencode(".png", frame, buffer);

		return new Image(new ByteArrayInputStream(buffer.toArray()));
	}
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		launch(args);
	}
	
	public void show(FXMLLoader loader, String title, int x, int y, ScheduledExecutorService timer) {
		try {
			Pane pane = (Pane) loader.load();
			
			Stage stage = new Stage();
			
			Scene scene = new Scene(pane, currFrame.cols() - 10, currFrame.rows() - 10);
			System.out.println("Size window: " + currFrame.cols() + " x " + currFrame.rows());
			
			stage.setTitle(title);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.setX(x);
			stage.setY(y);
			
			stage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					setClosed(timer);
				}
			}));
			
			stage.show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setClosed(ScheduledExecutorService timer) {
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
