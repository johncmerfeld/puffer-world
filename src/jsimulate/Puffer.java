package jsimulate;
import java.awt.Color;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The Puffer class is a fully implemented Creature. What makes it unique are the rules for its
 * movement, and how it eats, grows, dies, and reproduces.
 * 
 * General facts about puffers:
 *   - Puffers like Food
 *   - Larger puffers will attack smaller ones if they are not in the same family
 *   - Newborn puffers have poor eyesight, but this improves as they grow
 *   - Puffers are able to navigate walls and the edges of the world fairly well
 *   - Eating food allows puffers to grow, increasing their size, speed, and eyesight
 *   - After a certain number of growth cycles, puffers will reproduce and die
 * @author johncmerfeld
 *
 */

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
	private Coord dinner;
	
	private int decayTime = 1000;
	private int starvationTime = 2000;
	private int growthsLeft = 20;
	private int starvationClock = 0;
	
    private Color livingColor;
    private ArrayList<Move> nextMoves;
    
    private ArrayList<Coord> lastLocations;
	
	public Puffer(int x, int y, int family) {
		super(x, y, family, SimUtils.defaultCreatureSize, Color.BLUE);
		this.speed = 1;
		this.setVelocity(new Velocity(speed, speed));
		this.maxAge = decayTime;
		this.livingColor = Color.BLUE;
		this.nextMoves = new ArrayList<Move>();
		this.lastLocations = new ArrayList<Coord>();
	}
	
	public Puffer(int x, int y, int family, int size) {
		super(x, y, family, size, Color.BLUE);
		this.speed = 1;
		this.setVelocity(new Velocity(speed, speed));
		this.maxAge = decayTime;
		this.livingColor = Color.BLUE;
		this.nextMoves = new ArrayList<Move>();
		this.lastLocations = new ArrayList<Coord>();
	}
	
	public Puffer(int x, int y, int family, int size, Color color) {
		super(x, y, family, size, color);
		this.speed = 1;
		this.setVelocity(new Velocity(speed, speed));
		this.maxAge = decayTime;
		this.livingColor = color;
		this.nextMoves = new ArrayList<Move>();
		this.lastLocations = new ArrayList<Coord>();
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

		if (!nextMoves.isEmpty()) {
			this.executeMove(nextMoves.get(0), map);
			nextMoves.remove(0);
			return;
		}
		
		// if being chased, run away!!
		if (predator != null) {
			if (predator.getPrey().equals(this)) {
				moveAwayFrom(predator.getCoord(), map);
				return;
			}
			
		}
		
		/*
		if (dinner != null) {
			moveToward(dinner, map);
			return;
		} */
		
		// not being chased, look for food
		if (!tryMovingTowardFood(map)) {
			
			if ((prey != null) && (prey.getSize() < size) && (prey.isAlive() && prey.getFamily() != family)) {
				moveToward(prey.getCoord(), map);
				return;
			}
			
			// if no food nearby, look for puffers to attack
			if (!tryMovingTowardPuffer(map)) {
				if (this.movesSinceChange > 20) {
					this.setVelocity(SimUtils.createVelocity(speed));
					movesSinceChange = 0;
					moveForced(map);
				} else {
					moveForced(map);
				}
			}
		}			
		return;	
	}

	private boolean tryMovingTowardFood(GlobalMap map) {
		ArrayList<Food> localFoods = map.getFoodList();
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
			dinner = closestPoint(foodCoords);
			moveToward(dinner, map);
			stopChasing();
			return true;
		} else return false;
	}
	
	private boolean tryMovingTowardPuffer(GlobalMap map) {
		ArrayList<Puffer> pufferList = map.getPufferList();
		ArrayList<Double> distanceList = new ArrayList<Double>();
		Puffer puffer;
		for (int i = 0; i < pufferList.size(); i++) {
			puffer = pufferList.get(i);
			Coord c = puffer.getCoord();
			double distance = calculateDistance(c);
			if ((distance < maxFoodDistance) && (puffer.getSize() < size) && (puffer.isAlive()) && puffer.getFamily() != this.family) {
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
	
	private void executeMove(Move move, GlobalMap map) {
		boolean wallTrouble = false;
		boolean edgeTrouble = false;
		Coord origin = coord;
		Move probe = move;
		ArrayList<Wall> localWalls = map.getWallList();
		ArrayList<Move> alreadyTrieds = new ArrayList<Move>();
		Wall wall;
		Rectangle w;
		Rectangle p;
		
		this.setCoord(new Coord(origin.x + probe.x, origin.y + probe.y));	
		if ((coord.x > map.getWorldSize() - size) || (coord.x < 0) || (coord.y > map.getWorldSize() - size) || (coord.y < 0)) {
			edgeTrouble = true;
			alreadyTrieds.add(probe);
			ArrayList<Move> nextBestMoves = SimUtils.mostSimilarMoves(probe, speed, alreadyTrieds);
			for (Move nextBestMove : nextBestMoves) {	
				this.setCoord(new Coord(origin.x + nextBestMove.x, origin.y + nextBestMove.y));
				if ((coord.x <= map.getWorldSize() - size) && (coord.x >= 0) && (coord.y <= map.getWorldSize() - size) && (coord.y >= 0)) {			
					probe = nextBestMove;
					break;
				}		
			}
		} 
		
		for (int i = 0; i < localWalls.size(); i++) {
			wall = localWalls.get(i);
			if (wall.isCrumbled()) {
				continue;
			}
			this.setCoord(new Coord(origin.x + probe.x, origin.y + probe.y));
			p = this.getBounds();
			w = wall.getBounds();
			if (p.intersects(w)) {
				alreadyTrieds.add(probe);
				wallTrouble = true;
				ArrayList<Move> nextBestMoves = SimUtils.mostSimilarMoves(probe, speed, alreadyTrieds);
				for (Move nextBestMove : nextBestMoves) {	
					this.setCoord(new Coord(origin.x + nextBestMove.x, origin.y + nextBestMove.y));
					p = this.getBounds();
					if (!p.intersects(w)) {			
						probe = nextBestMove;
						i = -1;
						break;
					}		
				}
			}
		}
		Coord destination = new Coord(origin.x + probe.x, origin.y + probe.y);
		for (Coord lastLocation : lastLocations) {
			if ((destination.x == lastLocation.x) && (destination.y == lastLocation.y)) {
				/* FIXME this is totally fucked */
				destination = new Coord(origin.x - probe.x, origin.y - probe.y);
				this.setCoord(destination); 
			}
		}	
		this.setCoord(destination);
		if ((wallTrouble) || (edgeTrouble)) {
			lastLocations.add(destination);
		}
		
		this.setVelocity(new Velocity(probe.x, probe.y));
	}
	
	/* 
	 * Move along current velocity, no calculation
	 */
	private void moveForced(GlobalMap map) {
		stopChasing();
		this.executeMove(new Move(velocity.x, velocity.y), map);
		movesSinceChange++;
	}
	
	private void moveToward(Coord c, GlobalMap map) {
		this.setVelocity(calculateVelocity(c, true));
		this.executeMove(new Move(velocity.x, velocity.y), map);
		movesSinceChange = 0;
	}
	
	private void moveAwayFrom(Coord c, GlobalMap map) {
		this.setVelocity(calculateVelocity(c, false));
		this.executeMove(new Move(velocity.x, velocity.y), map);
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
	public boolean eat(Food food) {
		if (!alive) {
			return false;
		}
		
		boolean reproduced = false;
		
		/* reset starvation clock */
		starvationClock = 0;
		dinner = null;	
		lastLocations = new ArrayList<Coord>();
		
		this.sizePts += food.getSizePts();
		this.speedPts += food.getSpeedPts();

		while (true) {
			if (sizePts > sizeLvl) {
				if (grow(SIZE)) {
					reproduced = true;
				}
				sizePts -= sizeLvl;
			} else if (speedPts > speedLvl) {
				if (grow(SPEED)) {
					reproduced = true;
				}
				speedPts -= speedLvl;
			} else break;
		}
		return reproduced;
	}
	
	@Override
	public boolean grow(int type) {
		growthsLeft--;
		if (growthsLeft < 0) {
			die();
			return true;
		}
		if (type == SIZE) {
			size += 2;
			maxFoodDistance += 8;
		} else if (type == SPEED) {
			speed += 1;
			this.setVelocity(SimUtils.createVelocity(speed));
			maxFoodDistance += 12;
		}
		return false;
	}
	
	@Override
	public void die() {
		color = Color.MAGENTA;
		velocity = new Velocity(0,0);
		alive = false;
		stopChasing();
		if (predator != null) {
			// communicate to my predator that I am dead
			predator.stopChasing();
		}
	}
	
	@Override
	public void bounce(boolean x, boolean y, GlobalMap map) {
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
		moveForced(map);
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

	protected ArrayList<Puffer> reproduce() {
		ArrayList<Puffer> offspring = new ArrayList<Puffer>();
		
		int nOffspring = size / 15;
		int x, y;
		
		for (int i = 0; i < nOffspring; i++) {
			x = ThreadLocalRandom.current().nextInt(size) + coord.x;
			y = ThreadLocalRandom.current().nextInt(size) + coord.y;
			// pass down other characterisitcs?
			offspring.add(new Puffer(x, y, this.family, SimUtils.defaultCreatureSize, this.livingColor));
		}		
		return offspring;
	}
	
	public void addLastLocation(Coord c) {
		lastLocations.add(c);
	}
	
	public Puffer getPrey() {
		return this.prey;
	}
}
