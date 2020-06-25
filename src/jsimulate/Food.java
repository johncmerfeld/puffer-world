package jsimulate;

import java.awt.Color;
import java.awt.Rectangle;

public class Food extends EnvObject  {
	
	private int lifespan = 1500;
	private float sizePts;
	private float speedPts;
	
	public Food(int x, int y, float sizePts, float speedPts) {
		super(x, y, SimUtils.defaultEnvObjectSize, new Color(sizePts, speedPts, 0));
		this.setSizePts(sizePts);
		this.setSpeedPts(speedPts);
		maxAge = lifespan;
	}

	public Food(int x, int y, float sizePts, float speedPts, int size) {
		super(x, y, size, new Color(sizePts, speedPts, 0));
		this.setSizePts(sizePts);
		this.setSpeedPts(speedPts);
		maxAge = lifespan;
	}

	/**
	 * @return the sizePts
	 */
	public float getSizePts() {
		return sizePts;
	}

	/**
	 * @param sizePts the sizePts to set
	 */
	public void setSizePts(float sizePts) {
		this.sizePts = sizePts;
	}

	/**
	 * @return the speedPts
	 */
	public float getSpeedPts() {
		return speedPts;
	}

	/**
	 * @param speedPts the speedPts to set
	 */
	public void setSpeedPts(float speedPts) {
		this.speedPts = speedPts;
	}
}
