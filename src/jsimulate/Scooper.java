package jsimulate;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Scooper extends Creature {
	
	private Home home;
	private Food myFood;

	public Scooper(int x, int y, int family, int size, Color color, Home home) {
		super(x, y, family, size, color);
		setDefaultStats();
		this.home = home;
	}
	
	private void setDefaultStats() {
		this.speed = 1;
		this.eyesight = 50.0;
	}

	@Override
	public void move(GlobalMap map) {
		if (!alive) {
			return;
		}
		
		if (atHome()) {
			myFood = null;
		}
		
		// if being chased, run away!!
		if (predator != null) {
			if (predator.getPrey().equals(this)) {
				moveAwayFrom(predator.getCoord(), map);
				return;
			}	
		}
		
		if (myFood != null) {
			moveToward(home.getCoord(), map);
		} else if (!tryMovingTowardFood(map)) {
			if (this.movesSinceChange > 20) {
				this.setVelocity(SimUtils.createVelocity(speed));
				movesSinceChange = 0;
				moveForced(map);
			} else {
				moveForced(map);
			}
		}
	}

	private boolean atHome() {
		return this.intersects(home);
	}
	
	public boolean carryingFood() {
		return (myFood != null);
	}
	
	public Food getFood() {
		return myFood;
	}

	/**
	 * Sort of misleading because they don't eat it yet, but... 
	 */
	@Override
	public boolean eat(Food food) {
		myFood = food;
		return false;
	}

	@Override
	protected boolean grow(int type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void die() {
		// TODO Auto-generated method stub

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
			offspring.add(new Scooper(x, y, this.family, SimUtils.defaultCreatureSize, this.livingColor, this.home));
		}		
		return offspring;
	}

}
