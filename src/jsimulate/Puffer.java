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
	
	private Puffer predator;
	private Puffer prey;
	
	private int decayTime = 1000;
	private int starvationTime = 2000;
	private int growthsLeft = 20;
	private int starvationClock = 0;
	
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
		
		if (starvationClock >= starvationTime) {
			die();
			return;
		}
		
		starvationClock++;
		
		
		// if being chased, run away!!
		if (predator != null) {
			moveAwayFrom(predator.getCoord());
			return;
		}
		
		// not being chased, look for food
		if (!tryMovingTowardFood(map.getFoodList())) {
			
			if ((prey != null) && (prey.getSize() < size)) {
				moveToward(prey.getCoord());
				return;
			}
			
			// if no food nearby, look for puffers to attack
			if (!tryMovingTowardPuffer(map.getPufferList())) {
				if (this.movesSinceChange > 20) {
					this.setVelocity(SimUtils.createVelocity(speed));
					movesSinceChange = 0;
					moveForced();
				} else {
					moveForced();
				}
			}
		}			
		return;	
	}

	private boolean tryMovingTowardFood(ArrayList<Food> localFoods) {
		HashMap<SimObject, Double> foodCoords = new HashMap<SimObject, Double>();
		Food food;	
		for (int i = 0; i < localFoods.size(); i++) {		
			food = localFoods.get(i);
			Coord c = food.getCoord();
			double distance = calculateDistance(c);
			if (distance < maxFoodDistance) {
				foodCoords.put(food, distance);			
			}
		}
		// if near food, go to nearest food
		if (!foodCoords.isEmpty()) {		
			moveToward(closestPoint(foodCoords));
			stopChasing();
			return true;
		} else return false;
	}
	
	private boolean tryMovingTowardPuffer(ArrayList<Puffer> pufferList) {
		ArrayList<Double> distanceList = new ArrayList<Double>();
		Puffer puffer;
		for (int i = 0; i < pufferList.size(); i++) {
			puffer = pufferList.get(i);
			Coord c = puffer.getCoord();
			double distance = calculateDistance(c);
			if ((distance < maxFoodDistance) && (puffer.getSize() < size) && (puffer.isAlive())) {
				distanceList.add(distance);	
			} else {
				distanceList.add(Double.POSITIVE_INFINITY);
			}
		}
		int closestPufferIdx = closestIndex(distanceList);
		
		// if near puffer, go to nearest puffer
		if (closestPufferIdx != -1) {
			prey = pufferList.get(closestPufferIdx);
			prey.sensePredator(this);
			return true;
		} else {
			stopChasing();
			return false;
		}
	}
	
	/* 
	 * Move along current velocity, no calculation
	 */
	private void moveForced() {
		stopChasing();
		this.setCoord(new Coord(coord.x + velocity.x, coord.y + velocity.y));
		movesSinceChange++;
	}
	
	private void moveToward(Coord c) {
		this.setVelocity(calculateVelocity(c, true));
		this.setCoord(new Coord(coord.x + velocity.x, coord.y + velocity.y));
		movesSinceChange = 0;
	}
	
	private void moveAwayFrom(Coord c) {
		this.setVelocity(calculateVelocity(c, false));
		this.setCoord(new Coord(coord.x + velocity.x, coord.y + velocity.y));
		movesSinceChange = 0;
	}
	
	private Coord closestPoint(HashMap<SimObject, Double> map) {
		double minDistance = Double.POSITIVE_INFINITY;
		double currentDistance;
		
		// THIS WILL NEVER BE RETURNED UNINITIALIZED
		Coord minCoord = new Coord(0,0);
		Coord c;
		
		for (SimObject so : map.keySet()) {
			c = so.getCoord();
			currentDistance = map.get(so);
			if (currentDistance < minDistance) {
				minCoord = c;
				minDistance = currentDistance;
			}
		}	
		return minCoord;	
	}
	
	private int closestIndex(ArrayList<Double> distanceList) {
		double minDistance = Double.POSITIVE_INFINITY;
		double currentDistance;
		
		// THIS WILL NEVER BE RETURNED UNINITIALIZED
		int minIndex = -1;
		
		for (int i = 0; i < distanceList.size(); i++) {
			
			currentDistance = distanceList.get(i);
			if (currentDistance < minDistance) {
				minIndex = i;
				minDistance = currentDistance;
			}
		}	
		return minIndex;	
	}
	
	@Override
	public void eat(Food food) {
		if (!alive) {
			return;
		}
		
		/* reset starvation clock */
		starvationClock = 0;
		
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
		growthsLeft--;
		if (growthsLeft < 0) {
			die();
			return;
		}
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
		if (predator != null) {
			// communicate to my predator that I am dead
			predator.stopChasing();
		}
	}
	
	@Override
	public void bounce(boolean x, boolean y) {
		int currentX = velocity.x;
		int currentY = velocity.y;
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
	
	public void sensePredator(Puffer puffer) {
		predator = puffer;
	}
	
	public void forgetPredator() {
		predator = null;
	}
	
	public void stopChasing() {
		if (prey != null) {
			prey.forgetPredator();
			prey = null;
		}
	}
	
	public Velocity calculateVelocity(Coord c, boolean toward) {
		
		int newX, newY;
		newX = newY = 0;
		int xdiff = c.x - coord.x;
		int ydiff = c.y - coord.y;
		if ((xdiff == 0) && (ydiff == 0)) {
			return new Velocity(velocity.x, velocity.y);
		}
		
		int signX = xdiff > 0 ? 1 : -1;
		int signY = ydiff > 0 ? 1 : -1;
		if (!toward) {
			signX *= -1;
			signY *= -1;
		}
		
		int totalDiff = (Math.abs(xdiff) + Math.abs(ydiff));
			
		float ratioX = (float)Math.abs(xdiff) / totalDiff;
		float ratioY = (float)Math.abs(ydiff) / totalDiff;
		if ((ratioX > 1) || (ratioY > 1)) {
			System.out.println(xdiff);
			System.out.println(ydiff);
			System.out.println(ratioX);
			System.out.println(ratioY);
			System.out.println("-----");
		}
		
		newX = (int)Math.round(ratioX * speed) * signX;
		newY = (int)Math.round(ratioY * speed) * signY;

		return new Velocity(newX, newY);
	}
	
}
