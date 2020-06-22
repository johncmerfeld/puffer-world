package jsimulate;

import java.awt.Color;

public abstract class Creature extends SimObject {
	
	protected boolean alive = true;

	public Creature(int x, int y, int size, Color color) {
		super(x, y, size, color);
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public abstract void move(GlobalMap map);
	public abstract void bounce(boolean x, boolean y);
	
	public abstract void eat();
	
	public abstract void grow();
	public abstract void die();

}
