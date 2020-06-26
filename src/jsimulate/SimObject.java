package jsimulate;

import java.awt.Color;
import java.awt.Rectangle;

/**
 * The SimObject class is an abstract class for everything that might appear on the map
 * Current inheritors are:
 *   - CREATURES
 *   	- Puffer
 *   - ENVOBJECTS
 *   	- Food
 *   	- Wall
 */

public abstract class SimObject {
	protected Coord coord;
	protected Color color;
	protected int size;
	protected Velocity velocity;
	protected int currentAge = 0;
	protected int maxAge;
	
	public SimObject(int x, int y, int size, Color color) {
		this.coord = new Coord(x, y);
		this.size = size;
		this.color = color;
		this.velocity = new Velocity(0, 0);
	}
	
	/**
	 * @return the coord
	 */
	public Coord getCoord() {
		return this.coord;
	}

	/**
	 * @param coord the coord to set
	 */
	public void setCoord(Coord coord) {
		this.coord = coord;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return this.color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}
	

	/**
	 * @return the velocity
	 */
	public Velocity getVelocity() {
		return velocity;
	}

	/**
	 * @param velocity the velocity to set
	 */
	public void setVelocity(Velocity velocity) {
		this.velocity = velocity;
	}
	
	public Rectangle getBounds() {
		return new Rectangle(coord.x, coord.y, size, size);
	}
	
	protected double calculateDistance(Coord c) {
		return Math.hypot(coord.x - c.x, coord.y - c.y);
	}
	
	/*
	 * Returns false if the object is too old
	 * FIXME rename this? Make more intuitive?
	 */
	public boolean ageUp() {
		currentAge++;
		return currentAge > maxAge ? false : true;
	}
}
