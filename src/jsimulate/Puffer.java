package jsimulate;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

public class Puffer extends Creature {
	
	private int movesSinceChange = 0;
	private double maxFoodDistance = 50.0;
	
	private float sizePts = 0;
	private float speedPts = 0;
	
	private float sizeLvl = 3;
	private float speedLvl = 10;
	
	private static int SIZE = 0;
	private static int SPEED = 1;
	
	private int decayTime = 1000;
	
	public Puffer(int x, int y) {
		super(x, y, SimUtils.defaultCreatureSize, Color.BLUE);
		this.speed = 1;
		this.setVelocity(new Velocity(speed, speed));
		this.maxAge = decayTime;
	}
	
	public Puffer(int x, int y, int size) {
		super(x, y, size, Color.BLUE);
		this.speed = 1;
		this.setVelocity(new Velocity(speed, speed));
		this.maxAge = decayTime;
	}
	
	public Puffer(int x, int y, int size, Color color) {
		super(x, y, size, color);
		this.speed = 1;
		this.setVelocity(new Velocity(speed, speed));
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
		ArrayList<Food> localFoods = map.getFoodList();	
		Food food;	
		for (int i = 0; i < localFoods.size(); i++) {		
			food = localFoods.get(i);
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
			newX = xdiff > 0 ? speed : -speed;
			newY = ydiff > 0 ? speed : -speed;
			this.setVelocity(new Velocity(newX, newY));
			this.setCoord(new Coord(coord.x + newX, coord.y + newY));
			movesSinceChange = 0;
			return;
		}
		// not near any food
		if (this.movesSinceChange > 20) {
			this.setVelocity(SimUtils.createVelocity(speed));
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
	
	@Override
	public void eat(Food food) {
		if (!alive) {
			return;
		}
		
		this.sizePts += food.getSizePts();
		this.speedPts += food.getSpeedPts();

		while (true) {
			if (sizePts > sizeLvl) {
				grow(SIZE);
				sizePts -= sizeLvl;
			} else if (speedPts > speedLvl) {
				grow(SPEED);
				speedPts -= speedLvl;
			} else break;
		}
	}
	
	@Override
	public void grow(int type) {
		if (type == SIZE) {
			size += 2;
			maxFoodDistance += 8;
		} else if (type == SPEED) {
			speed += 1;
			this.setVelocity(SimUtils.createVelocity(speed));
			maxFoodDistance += 12;
		}
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
