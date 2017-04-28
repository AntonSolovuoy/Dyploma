package application;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class Information {

	@FXML
	private Text count,
			width,
			height,
			fps,
			position;
	
	private String countString,
			widthString,
			heigthString,
			fpsString,
			positionString;

	@FXML
	public void initialize() {
		count.setText(countString);
		width.setText(widthString);
		height.setText(heigthString);
		fps.setText(fpsString);
		position.setText(positionString);
    }

	public void setCountString(String countString) {
		this.countString = countString;
	}

	public void setWidthString(String widthString) {
		this.widthString = widthString;
	}

	public void setHeigthString(String heigthString) {
		this.heigthString = heigthString;
	}

	public void setFpsString(String fpsString) {
		this.fpsString = fpsString;
	}

	public void setPositionString(String positionString) {
		this.positionString = positionString;
	}
	
	
}
