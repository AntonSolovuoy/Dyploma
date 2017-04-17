package application;

import org.opencv.core.Core;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			/*
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Application.fxml"));
			Pane root = (Pane) loader.load();

			Scene scene = new Scene(root, 590, 290);

			primaryStage.setTitle("Application (binary mask)");
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
	
			primaryStage.show();

			Controller controller = loader.getController();
			controller.initialize();
			controller.start();
			
			primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					controller.setClosed();
				}
			}));
			
			*/
			String path = "C:/Users/admin/workspace/Application/src/application/2_converted.avi";
			
			FXMLLoader wildfireDetectionLoader = new FXMLLoader(getClass().getResource("WildfireDetection.fxml"));
			
			Stage wildfireDetectionStage = new Stage();

			show(wildfireDetectionLoader, wildfireDetectionStage, "Wildfire Detection", 300, 250);
			
			WildfireDetection wildfireDetectionController = wildfireDetectionLoader.getController();
			wildfireDetectionController.initialize();
			wildfireDetectionController.setPath(path);
			wildfireDetectionController.start();
			
			
			wildfireDetectionStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					wildfireDetectionController.setClosed();
				}
			}));

			
			
			
			
			FXMLLoader frameDifferenceLoader = new FXMLLoader(getClass().getResource("FrameDifference.fxml"));
			
			Stage frameDifferenceStage = new Stage();

			show(frameDifferenceLoader, frameDifferenceStage, "Binary Motion Mask", 100, 100);
			
			FrameDifference frameDifferenceController = frameDifferenceLoader.getController();
			frameDifferenceController.initialize();
			frameDifferenceController.setPath(path);
			frameDifferenceController.start();
			
			frameDifferenceStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					frameDifferenceController.setClosed();
				}
			}));

			
			
			
			FXMLLoader YUVLoader = new FXMLLoader(getClass().getResource("YUV.fxml"));

			Stage YUVStage = new Stage();
			
			show(YUVLoader, YUVStage, "YUV Mask", 100, 400);
			
			YUV YUVController = YUVLoader.getController();
			YUVController.initialize();
			YUVController.setPath(path);
			YUVController.start();
			
			YUVStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					YUVController.setClosed();
				}
			}));
			
			
			
			
			FXMLLoader HSVLoader = new FXMLLoader(getClass().getResource("HSV.fxml"));
			
			Stage HSVStage = new Stage();
			
			show(HSVLoader, HSVStage, "HSV Mask", 500, 400);
			
			HSV HSVController = HSVLoader.getController();
			HSVController.initialize();
			HSVController.setPath(path);
			HSVController.start();
			
			HSVStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					HSVController.setClosed();
				}
			}));
			
			
			
			
			
			FXMLLoader MOG2Loader = new FXMLLoader(getClass().getResource("MOG2.fxml"));
			
			Stage MOG2Stage = new Stage();
			
			show(MOG2Loader, MOG2Stage, "MOG2 Mask", 500, 100);
			
			MOG2 MOG2Controller = MOG2Loader.getController();
			MOG2Controller.initialize();
			MOG2Controller.setPath(path);
			MOG2Controller.start();
			
			MOG2Stage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					MOG2Controller.setClosed();
				}
			}));
			
			
		} catch (Exception e) {
			System.out.println("Error in start: " + e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		launch(args);
	}
	
	public void show(FXMLLoader loader, Stage stage, String title, int x, int y) {
		try {
			Pane pane = (Pane) loader.load();
			
			Scene scene = new Scene(pane, 300, 200);
			
			stage.setTitle(title);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.setX(x);
			stage.setY(y);
			
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
