package jsimulate;

import java.awt.Color;

/** 
 * The EnvObject class is a parent class for non-living SimObjects (e.g. Foods, Walls)
 * @author johncmerfeld
 *
 */

public abstract class EnvObject extends SimObject {
	
	public boolean solid = true;

	public EnvObject(int x, int y, int size, Color color) {
		super(x, y, size, color);
	}
	
	public void crumble() {
		solid = false;
	}
	
	public boolean isSolid() {
		return solid;
	}

}
