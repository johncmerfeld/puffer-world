package jsimulate;

import java.awt.Color;

public class Wall extends SimObject {

	public Wall(int x, int y) {
		super(x, y, SimUtils.defaultEnvObjectSize, Color.LIGHT_GRAY);
	}

	public Wall(int x, int y, int size) {
		super(x, y, size, Color.LIGHT_GRAY);
	}
	
	public Wall(int x, int y, int size, Color color) {
		super(x, y, size, color);
	}

}
