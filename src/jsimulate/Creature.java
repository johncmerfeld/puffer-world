package jsimulate;

import java.awt.Color;

public abstract class Creature extends SimObject {
	
	protected boolean alive = true;
	protected int speed;
	protected int family;

	public Creature(int x, int y, int family, int size, Color color) {
		super(x, y, size, color);
		this.family = family;
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
	
	public int getFamily() {
		return this.family;
	}
	
	public abstract void move(GlobalMap map);
	public abstract void bounce(boolean x, boolean y, GlobalMap map);
	
	public abstract boolean eat(Food food);
	
	protected abstract boolean grow(int type);
	
	public abstract void die();
	
	//public abstract void chase(Creature creature);

}
