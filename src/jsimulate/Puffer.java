package jsimulate;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;


public class Puffer extends SimObject {
	
	private int movesSinceChange = 0;
	private int foodsSinceGrowth = 0;
	private int foodsForGrowth = 2;
	private double maxFoodDistance = 50.0;
	
	public Puffer(int x, int y) {
		super(x, y, 7, Color.BLUE);
		this.setVelocity(new Velocity(1, 1));
		
	}
	
	public Puffer(int x, int y, int size) {
		super(x, y, size, Color.BLUE);
		this.setVelocity(new Velocity(1, 1));
	}
	
	public Puffer(int x, int y, int size, Color color) {
		super(x, y, size, color);
		this.setVelocity(new Velocity(1, 1));
	}
	
	// TODO might be a more efficient way to do this
	public void move(ArrayList<Food> foodList) {
		int newX, newY;
		//	FIXME this does not necessarily go to nearest food
		for (Food food : foodList) {
			Coord c = food.getCoord();
			double distance = calculateDistance(c);
			if (distance < maxFoodDistance) {
				int xdiff = c.x - coord.x;
				int ydiff = c.y - coord.y;
				newX = xdiff > 0 ? 1 : -1;
				newY = ydiff > 0 ? 1 : -1;
				this.setVelocity(new Velocity(newX, newY));
				this.setCoord(new Coord(coord.x + newX, coord.y + newY));
				movesSinceChange = 0;
				return;
			}
		}
		// not near any food
		if (this.movesSinceChange > 20) {
			Random random = new Random();
			float r = random.nextFloat();
			if (r < 0.3333) {
				newX = -1;
			} else if (r < 0.6666) {
				newX = 0;
			} else {
				newX = 1;
			}
			r = random.nextFloat();
			if (r < 0.3333) {
				newY = -1;
			} else if (r < 0.6666) {
				newY = 0;
			} else {
				newY = 1;
			}
			this.setVelocity(new Velocity(newX, newY));
			movesSinceChange = 0;
		} else {
			this.setCoord(new Coord(coord.x + velocity.xVel, coord.y + velocity.yVel));
			movesSinceChange++;
		}		
		return;	
	}
	
	public void eat() {
		foodsSinceGrowth++;
		if (foodsSinceGrowth > foodsForGrowth) {
			grow();
		}
	}
	
	public void grow() {
		size += 2;
		maxFoodDistance += 10.0;
		color = color.brighter();
	}
	
}
