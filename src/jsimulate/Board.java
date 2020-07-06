package jsimulate;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.JPanel;

import java.awt.BasicStroke;

import javax.swing.JOptionPane;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The Board class houses the entire simulation. Its responsibilities include:
 *   - Running the thread of animation
 *   - Repainting the objects in the world as visible components 
 *   - Inducing transformations that occur between objects or over time
 *     - This last point should probably be moved
 * @author johncmerfeld
 *
 */

public class Board extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;

	/* for running the simulation */
	private volatile boolean running = true;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
	private Thread animator;
	
	/* properties of the world */
	private int foodGenInterval;
    private int worldSize;
    private GlobalMap map;
    
    /* realtime attributes */
    private int globalTime = 0;
    
    public Board() {
    	map = new GlobalMap(SimUtils.boardSize);
    	setBackground(Color.BLACK);
        setPreferredSize(new Dimension(SimUtils.boardSize, SimUtils.boardSize));
    }
    
    /**
     * Begin the simulation with the specified parameters
     * @param nPuffers
     * @param foodGenInterval
     * @param worldSize
     */
    public void start(int nPuffers, int nScoopers, int foodGenInterval, int worldSize) {
    	  	
    	this.foodGenInterval = foodGenInterval;
    	this.worldSize = worldSize;
    	map.setWorldSize(worldSize);
    	
    	int remainingPuffers = nPuffers;
        	
        	/* randomly place initial homes, and add scoopers and puffers */
        	for (int i = 0; i < nScoopers; i++) {
        		int xpos = ThreadLocalRandom.current().nextInt(1, worldSize);
        		int ypos = ThreadLocalRandom.current().nextInt(1, worldSize);

        		int family = map.getNextFamily();
        		Color color = new Color(0, (float)xpos/ worldSize, 1);
   
        		Home home = new Home(xpos, ypos, family, SimUtils.defaultHomeSize, color);
        		map.add(home);
        		// FIXME update scooper color
        		map.add(new Scooper(xpos, ypos, family, SimUtils.defaultCreatureSize, color, home));
        		if (remainingPuffers > 0) {
        			map.add(new Puffer(xpos, ypos, family, SimUtils.defaultCreatureSize, color));
        			remainingPuffers--;
        		}
        	}
        	
        	/* randomly place remaining puffers */
        	for (int i = 0; i < remainingPuffers; i++) {
        		int xpos = ThreadLocalRandom.current().nextInt(1, worldSize);
        		int ypos = ThreadLocalRandom.current().nextInt(1, worldSize);
        		map.add(new Puffer(xpos, ypos, map.getNextFamily(), SimUtils.defaultCreatureSize, new Color(0, (float)xpos/ worldSize , 1)));
        	}

		animator = new Thread(this);
        animator.start();
    }

    /**
     * This method ensures that the animation is smooth.
     * It runs the cycle() and repaint() methods and forces them onto a specific cadence
     */
    @Override
    public void run() {

        long beforeTime, timeDiff, sleep;

        beforeTime = System.currentTimeMillis();

        while (running) {
	        	synchronized (pauseLock) {
                if (!running) { // may have changed while waiting to
                    // synchronize on pauseLock
                    break;
                }
                if (paused) {
                    try {
                        synchronized (pauseLock) {
                            pauseLock.wait(); // will cause this Thread to block until 
                            // another thread calls pauseLock.notifyAll()
                            // Note that calling wait() will 
                            // relinquish the synchronized lock that this 
                            // thread holds on pauseLock so another thread
                            // can acquire the lock to call notifyAll()
                            // (link with explanation below this code)
                        }
                    } catch (InterruptedException ex) {
                        break;
                    }
                    if (!running) { // running might have changed since we paused
                        break;
                    }
                }
            }

            cycle();
            repaint();

            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = SimUtils.globalDelay - timeDiff;

            if (sleep < 0) {
                sleep = 2;
            }

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {          
                String msg = String.format("Thread interrupted: %s", e.getMessage());            
                JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
            }

            beforeTime = System.currentTimeMillis();
        }
    }

    public void stop() {
        running = false;
        // you might also want to interrupt() the Thread that is 
        // running this Runnable, too, or perhaps call:
        resume();
        // to unblock
    }
    
    public void pause() {
        // you may want to throw an IllegalStateException if !running
        paused = true;
    }

    public void resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll(); // Unblocks thread
        }
    }
    
    /**
     * This method is for painting the map. Food on the bottom, then walls, then puffers
     * @param g
     */
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        	/* render overall map */
        Graphics2D g2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
        		RenderingHints.VALUE_ANTIALIAS_ON);

        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2d.setRenderingHints(rh);
        
        	/* populate map */
        	Coord c;
        	int size;
        	
			
		for (EnvObject object : map.getObjectList()) {
			g2d.setColor(object.getColor());
    		c = object.getCoord();
    		size = object.getSize();
    		g2d.fillRect(c.x, c.y, size, size);
    		if (object instanceof Home) {
    			g2d.setColor(Color.LIGHT_GRAY);
    			float thickness = 2;
    			Stroke oldStroke = g2d.getStroke();
    			g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0));
    			g2d.drawRect(c.x, c.y, size, size);
    			g2d.setStroke(oldStroke);
    		}
		}	

		
		for (Creature creature : map.getCreatureList()) {
			g2d.setColor(creature.getColor());
    		c = creature.getCoord();
    		size = creature.getSize();
    		g2d.fillRect(c.x, c.y, size, size);
    		if (creature instanceof Scooper) {
    			if (((Scooper) creature).carryingFood()) {
    				Food f = ((Scooper) creature).getFood();
    				g2d.setColor(f.getColor());
    	    		size = f.getSize();
    	    		g2d.fillRect(c.x, c.y, size, size);
    			}
    		}
		}
    }

	/**
	 * This method is for advancing the action of the board, including:
	 *   - Moving every puffer
	 *   - Monitoring when puffers eat food and attack one another
	 *   - Monitoring puffer reproduction
	 */
    private void cycle() {
    	
    	advanceTime();
    		
    	ArrayList<Creature> removedCreatures = new ArrayList<Creature>();
    	ArrayList<Creature> newCreatures = new ArrayList<Creature>();
    	ArrayList<Creature> globalCreatures = map.getCreatureList();
    	ArrayList<EnvObject> removedObjects = new ArrayList<EnvObject>();
		ArrayList<EnvObject> globalFoods = map.getAllOfType(Food.class);
		
		Creature creature;

		for (int j = 0; j < globalCreatures.size(); j++) {
			creature = globalCreatures.get(j);
			if (!creature.isAlive()) {
				continue;
			} else {
				// try to move the creature, then check for collisions
				creature.move(map);	
	
				for (EnvObject food : globalFoods) {						
					
					if (creature.intersects(food)) {
						// Food got eaten!
						// FIXME should all creatures eat food?
						if (creature.eat((Food) food)) { // FIXME this means "if puffer reproduced"
							newCreatures.addAll(creature.reproduce());
						}
						removedObjects.add(food);
					}
				}
				
				for (Creature creature2 : map.getCreatureList()) {
					if ((creature2 != creature) && (creature2.isAlive()) && (!creature.isRelatedTo(creature2))) {
						if ((creature.intersects(creature2)) && (creature2.getSize() > creature.getSize())) {
							creature.die();
						}
					}	
				}
			}  			
    	}   
		
		map.removeObjects(removedObjects);
		map.removeCreatures(removedCreatures);
		map.add(newCreatures);
    }
    
    /**
     * This method is for advancing the global clock, which tracks:
     *   - When new food should be generated
     *   - When uneaten food should "rot" and become a wall
     *   - When old walls should "crumble"
     *   - When dead puffers should "decay" and become food
     * And carrying out those transformations
     */
	private void advanceTime() {
		
		ArrayList<Creature> decayedCreatures = new ArrayList<Creature>();
		ArrayList<EnvObject> rottenFoods = new ArrayList<EnvObject>();
		ArrayList<EnvObject> globalFoods = map.getAllOfType(Food.class);
		ArrayList<EnvObject> globalWalls = map.getAllOfType(Wall.class);
		EnvObject food;
		Coord c;
		
		for (int i = 0; i < globalFoods.size(); i++) {		
			food = globalFoods.get(i);
			if (!food.ageUp()) {
				rottenFoods.add(food);
			}
		}
			
		for (EnvObject rottenFood : rottenFoods) {
			c = rottenFood.getCoord();
			map.add(new Wall(c.x, c.y, rottenFood.getSize()));
		}
		
		map.removeObjects(rottenFoods);
		
		for (Creature creature : map.getCreatureList()) {
			if (!creature.isAlive()) {
				if (!creature.ageUp()) {
					decayedCreatures.add(creature);
				}
			}
		}
		
		for (Creature dc : decayedCreatures) {
			c = dc.getCoord();
			float z = dc.getSize();
			float p = dc.getSpeed();
			if (z > p) {	
				p = p / z;
				z = 1;
			} else {
				z = z / p;
				p = 1;
			}
			
			map.add(new Food(c.x, c.y, z, p, dc.getSize()));
		}
		
		map.removeCreatures(decayedCreatures);
		
		for (EnvObject wall : globalWalls) {
			if (!wall.ageUp()) {
				wall.crumble();
			}
		}
		
		globalTime++;
		
		if (globalTime % foodGenInterval == 0) {
			/* create some new food */
			for (int i = 0; i < SimUtils.foodsPerGeneration; i++) {
		    	map.add(SimUtils.createFood(worldSize));
			}
		}
	}
}