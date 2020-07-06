package jsimulate;

import java.awt.Color;

/**
 * The Wall class is an object on the map through which puffers cannot pass.
 * 
 * After a while, walls will crumble - though they remain visible, they will no longer impede movement
 * @author johncmerfeld
 *
 */

public class Wall extends EnvObject {
	
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

	@Override
	public void crumble() {
		this.solid = false;
		this.color = Color.DARK_GRAY;
	}

}
