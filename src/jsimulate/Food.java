package jsimulate;

import java.awt.Color;
import java.awt.Rectangle;

public class Food extends EnvObject  {
	
	private int lifespan = 1000;
	
	public Food(int x, int y) {
		super(x, y, SimUtils.defaultEnvObjectSize, Color.RED);
		maxAge = lifespan;
	}

	public Food(int x, int y, int size) {
		super(x, y, size, Color.RED);
		maxAge = lifespan;
	}
	
	public Food(int x, int y, int size, Color color) {
		super(x, y, size, color);
		maxAge = lifespan;
	}
}
