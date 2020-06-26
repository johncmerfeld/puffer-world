package jsimulate;

import java.awt.Color;

/**
 * The Creature class is a parent class for living things. Any living SimObject,
 * such as a puffer, must be able to perform these actions
 * @author johncmerfeld
 *
 */

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
}
