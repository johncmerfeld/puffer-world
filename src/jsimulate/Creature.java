package jsimulate;

import java.awt.Color;

public abstract class Creature extends SimObject {
	
	protected boolean alive = true;
	protected int speed;

	public Creature(int x, int y, int size, Color color) {
		super(x, y, size, color);
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public abstract void move(GlobalMap map);
	public abstract void bounce(boolean x, boolean y);
	
	public abstract void eat(Food food);
	
	public abstract void grow(int type);
	public abstract void die();

}
