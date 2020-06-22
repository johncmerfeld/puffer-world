package jsimulate;

import java.awt.Color;

public class Wall extends SimObject {
	
	private int crumbleTime = 3000;

	public Wall(int x, int y) {
		super(x, y, SimUtils.defaultEnvObjectSize, Color.LIGHT_GRAY);
		this.maxAge = crumbleTime;
	}

	public Wall(int x, int y, int size) {
		super(x, y, size, Color.LIGHT_GRAY);
		this.maxAge = crumbleTime;
	}
	
	public Wall(int x, int y, int size, Color color) {
		super(x, y, size, color);
		this.maxAge = crumbleTime;
	}

}
