package application;

import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class Information {

	private int count,
				width,
				height,
				fps,
				position;

	public Information(VideoCapture videoCapture) {
		this.count = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT);
		this.width = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH);
		this.height = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
		this.fps = (int) videoCapture.get(Videoio.CAP_PROP_FPS);
		this.position = (int) videoCapture.get(Videoio.CAP_PROP_POS_FRAMES);
	}
	
	public int getCount() {
		return count;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getFPS() {
		return fps;
	}

	public int getPosition() {
		return position;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setFPS(int fps) {
		this.fps = fps;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
