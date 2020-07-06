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
	
	private float sizePts = 0;
	private float speedPts = 0;
	
	private float sizeLvl = 3;
	private float speedLvl = 10;
	
	private int decayTime = 1000;
	private int starvationTime = 2000;
	private int growthsLeft = 20;
	private int starvationClock = 0;
	
	public Puffer(int x, int y, int family) {
		super(x, y, family, SimUtils.defaultCreatureSize, Color.BLUE);
		this.setDefaultStats();
		this.setVelocity(new Velocity(speed, speed));
		this.maxAge = decayTime;
		this.livingColor = Color.BLUE;
	}
	
	public Puffer(int x, int y, int family, int size) {
		super(x, y, family, size, Color.BLUE);
		this.setDefaultStats();
		this.setVelocity(new Velocity(speed, speed));
		this.maxAge = decayTime;
		this.livingColor = Color.BLUE;
	}
	
	public Puffer(int x, int y, int family, int size, Color color) {
		super(x, y, family, size, color);
		this.setDefaultStats();
		this.setVelocity(new Velocity(speed, speed));
		this.maxAge = decayTime;
		this.livingColor = color;	
	}
	
	private void setDefaultStats() {
		this.speed = 1;
		this.eyesight = 50.0;
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
			
			// if no food nearby, look for creatures to attack
			if (!tryMovingTowardCreature(map)) {
				// otherwise just move randomly
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
	
	private boolean tryMovingTowardCreature(GlobalMap map) {
		ArrayList<Creature> creatureList = map.getCreatureList();
		ArrayList<Double> distanceList = new ArrayList<Double>();
		Creature creature;
		for (int i = 0; i < creatureList.size(); i++) {
			creature = creatureList.get(i);
			Coord c = creature.getCoord();
			double distance = calculateDistance(c);
			if ((distance < eyesight) && (creature.getSize() < size) && (creature.isAlive()) && (!isRelatedTo(creature))) {
				distanceList.add(distance);	
			} else {
				distanceList.add(Double.POSITIVE_INFINITY);
			}
		}
		int closestCreatureIdx = closestIndex(distanceList);
		
		// if near creature, go to nearest creature
		if (closestCreatureIdx != -1) {
			prey = creatureList.get(closestCreatureIdx);
			prey.sensePredator(this);
			return true;
		} else {
			stopChasing();
			return false;
		}
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
			eyesight += 8;
		} else if (type == SPEED) {
			speed += 1;
			this.setVelocity(SimUtils.createVelocity(speed));
			eyesight += 12;
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
	public ArrayList<Creature> reproduce() {
		ArrayList<Creature> offspring = new ArrayList<Creature>();
		
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
}
