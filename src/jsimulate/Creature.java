package jsimulate;

import java.awt.Color;

public abstract class Creature extends SimObject {

	public Creature(int x, int y, int size, Color color) {
		super(x, y, size, color);
		// TODO Auto-generated constructor stub
	}
	
	public abstract void move(GlobalMap map);
	public abstract void bounce(boolean x, boolean y);
	
	public abstract void eat();
	
	public abstract void grow();
	public abstract void die();

}
