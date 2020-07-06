package jsimulate;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Creature class is a parent class for living things. Any living SimObject,
 * such as a puffer, must be able to perform these actions
 * @author johncmerfeld
 *
 */

public abstract class Creature extends SimObject {
	
	protected boolean alive = true;
	protected int speed;
	protected int movesSinceChange = 0;
	protected int family;
	
	protected double eyesight;
	
	protected Creature predator;
	protected Creature prey;
	
	protected Color livingColor;
	protected Coord dinner;
	
	protected static int SIZE = 0;
	protected static int SPEED = 1;
	
	protected ArrayList<Coord> lastLocations;
	protected ArrayList<Move> nextMoves;

	public Creature(int x, int y, int family, int size, Color color) {
		super(x, y, size, color);
		this.family = family;
		this.lastLocations = new ArrayList<Coord>();
		this.nextMoves = new ArrayList<Move>();
	}
	
	public abstract void move(GlobalMap map);
	
	public abstract boolean eat(Food food);
	protected abstract boolean grow(int type);
	public abstract void die();
	public abstract ArrayList<Creature> reproduce();
	
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
	
	public Creature getPrey() {
		return this.prey;
	}
	
	public void sensePredator(Creature predator) {
		this.predator = predator;
	}
	
	public void forgetPredator() {
		this.predator = null;
	}
	
	public void stopChasing() {
		if (this.prey != null) {
			this.prey.forgetPredator();
			this.prey = null;
		}
	}
	
	public boolean isRelatedTo(Creature creature) {
		return this.family == creature.getFamily();
	}
	
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
	
	/* 
	 * Move along current velocity, no calculation
	 */
	protected void moveForced(GlobalMap map) {
		stopChasing();
		this.executeMove(new Move(velocity.x, velocity.y), map);
		movesSinceChange++;
	}
	
	protected void moveToward(Coord c, GlobalMap map) {
		this.setVelocity(calculateVelocity(c, true));
		this.executeMove(new Move(velocity.x, velocity.y), map);
		movesSinceChange = 0;
	}
	
	protected void moveAwayFrom(Coord c, GlobalMap map) {
		this.setVelocity(calculateVelocity(c, false));
		this.executeMove(new Move(velocity.x, velocity.y), map);
		movesSinceChange = 0;
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
	
	protected boolean tryMovingTowardFood(GlobalMap map) {
		ArrayList<EnvObject> globalFoods = map.getAllOfType(Food.class);
		HashMap<SimObject, Double> foodCoords = new HashMap<SimObject, Double>();
		for (EnvObject food : globalFoods) {
			Coord c = food.getCoord();
			double distance = calculateDistance(c);
			if (distance < eyesight) {
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
	
	protected Coord closestPoint(HashMap<SimObject, Double> map) {
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
	
	protected void executeMove(Move move, GlobalMap map) {
		boolean wallTrouble = false;
		boolean edgeTrouble = false;
		Coord origin = coord;
		Move probe = move;
		ArrayList<EnvObject> globalWalls = map.getAllOfType(Wall.class);
		ArrayList<Move> alreadyTrieds = new ArrayList<Move>();
		EnvObject wall;
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
		
		for (int i = 0; i < globalWalls.size(); i++) {
			wall = globalWalls.get(i);
			if (!wall.isSolid()) {
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
	
}
