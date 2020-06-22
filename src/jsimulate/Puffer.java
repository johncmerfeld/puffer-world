package jsimulate;
import java.awt.Color;
import java.util.HashMap;
import java.util.Random;


public class Puffer extends Creature {
	
	private int movesSinceChange = 0;
	private int foodsSinceGrowth = 0;
	private int foodsForGrowth = 2;
	private double maxFoodDistance = 50.0;
	
	private int decayTime = 1000;
	
	public Puffer(int x, int y) {
		super(x, y, SimUtils.defaultCreatureSize, Color.BLUE);
		this.setVelocity(new Velocity(1, 1));
		this.maxAge = decayTime;
	}
	
	public Puffer(int x, int y, int size) {
		super(x, y, size, Color.BLUE);
		this.setVelocity(new Velocity(1, 1));
		this.maxAge = decayTime;
	}
	
	public Puffer(int x, int y, int size, Color color) {
		super(x, y, size, color);
		this.setVelocity(new Velocity(1, 1));
		this.maxAge = decayTime;
	}
	
	// might be a more efficient way to do this
	@Override
	public void move(GlobalMap map) {
		if (!alive) {
			return;
		}
		int newX, newY;
		HashMap<Coord, Double> foodCoords = new HashMap<Coord, Double>();
		for (Food food : map.getFoodList()) {
			Coord c = food.getCoord();
			double distance = calculateDistance(c);
			if (distance < maxFoodDistance) {
				foodCoords.put(c, distance);			
			}
		}
		// if near food, go to nearest food
		if (!foodCoords.isEmpty()) {
			
			Coord minCoord = closestPoint(foodCoords);
			
			int xdiff = minCoord.x - coord.x;
			int ydiff = minCoord.y - coord.y;
			newX = xdiff > 0 ? 1 : -1;
			newY = ydiff > 0 ? 1 : -1;
			this.setVelocity(new Velocity(newX, newY));
			this.setCoord(new Coord(coord.x + newX, coord.y + newY));
			movesSinceChange = 0;
			return;
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
			moveForced();
		} else {
			moveForced();
		}		
		return;	
	}
	
	/* 
	 * Move along current velocity, no calculation
	 */
	private void moveForced() {
		this.setCoord(new Coord(coord.x + velocity.xVel, coord.y + velocity.yVel));
		movesSinceChange++;
	}
	
	private Coord closestPoint(HashMap<Coord, Double> map) {
		double minDistance = Double.POSITIVE_INFINITY;
		double currentDistance;
		
		// THIS WILL NEVER BE RETURNED UNINITIALIZED
		Coord minCoord = new Coord(0,0);
		
		for (Coord c : map.keySet()) {
			currentDistance = map.get(c);
			if (currentDistance < minDistance) {
				minCoord = c;
				minDistance = currentDistance;
			}
		}	
		return minCoord;	
	}
	
	public void eat() {
		if (!alive) {
			return;
		}
		foodsSinceGrowth++;
		if (foodsSinceGrowth > foodsForGrowth) {
			grow();
		}
	}
	
	@Override
	public void grow() {
		size += 2;
		maxFoodDistance += 10.0;
		color = color.brighter();
	}
	
	@Override
	public void die() {
		color = Color.MAGENTA;
		velocity = new Velocity(0,0);
		alive = false;
	}
	
	@Override
	public void bounce(boolean x, boolean y) {
		int currentX = velocity.xVel;
		int currentY = velocity.yVel;
		int newX = currentX;
		int newY = currentY;
		if (x) {
			newX = -currentX;
		}
		if (y) {
			newY = -currentY;
		}
		velocity = new Velocity(newX, newY);
		moveForced();
	}
	
}
