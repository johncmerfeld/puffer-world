package jsimulate;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JPanel;
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
    public void start(int nPuffers, int foodGenInterval, int worldSize) {
    	  	
    	this.foodGenInterval = foodGenInterval;
    	this.worldSize = worldSize;
    	map.setWorldSize(worldSize);
    	
    	/* randomly place initial puffers */
        	for (int i = 0; i < nPuffers; i++) {
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
        	Food food;
        	Wall wall;
        	Puffer puffer;
        	int size;
        	
        	ArrayList<Food> localFoods = map.getFoodList();
        	ArrayList<Wall> localWalls = map.getWallList(); 
        	ArrayList<Puffer> localPuffers = map.getPufferList();
			
		for (int i = 0; i < localFoods.size(); i++) {
			food = localFoods.get(i);
			g.setColor(food.getColor());
    		c = food.getCoord();
    		size = food.getSize();
    		g.fillRect(c.x, c.y, size, size);
		}	
				
		for (int i = 0; i < localWalls.size(); i++) {
			wall = localWalls.get(i);
			g.setColor(wall.getColor());
    		c = wall.getCoord();
    		size = wall.getSize();
    		g.fillRect(c.x, c.y, size, size);
		}
		
		for (int i = 0; i < localPuffers.size(); i++) {
			puffer = localPuffers.get(i);
			g.setColor(puffer.getColor());
    		c = puffer.getCoord();
    		size = puffer.getSize();
    		g.fillRect(c.x, c.y, size, size);
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
    	
    	ArrayList<Puffer> localPuffers = map.getPufferList(); 		
    	ArrayList<Puffer> removedPuffers = new ArrayList<Puffer>();
    	ArrayList<Puffer> newPuffers = new ArrayList<Puffer>();	
    	
    	ArrayList<Food> removedFoods = new ArrayList<Food>();
		ArrayList<Food> localFoods = map.getFoodList();
			
		Food food;
		Puffer puffer;
		Rectangle p;
		Rectangle f;
		for (int j = 0; j < localPuffers.size(); j++) {
			puffer = localPuffers.get(j);
			if (!puffer.isAlive()) {
				continue;
			} else {
				// try to move the puffer, then check for collisions
				puffer.move(map);	
				p = puffer.getBounds();
	
				for (int i = 0; i < localFoods.size(); i++) {				
					food = localFoods.get(i);			
					f = food.getBounds();
					
					if (p.intersects(f)) {
						// Food got eaten!
						if (puffer.eat(food)) { // FIXME this means "if puffer reproduced"
							newPuffers.addAll(puffer.reproduce());
						}
						removedFoods.add(food);
					}
				}
				
				for (Puffer puffer2 : map.getPufferList()) {
					if ((puffer2 != puffer) && (puffer2.isAlive()) && (puffer2.getFamily() != puffer.getFamily())) {
						Rectangle p2 = puffer2.getBounds();
						if ((p.intersects(p2)) && (puffer2.getSize() > puffer.getSize())) {
							puffer.die();
						}
					}	
				}
			}  			
    	}   
		
		map.removeFoods(removedFoods);
		map.removePuffers(removedPuffers);
		map.add(newPuffers);
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
		
		ArrayList<Puffer> decayedPuffers = new ArrayList<Puffer>();
		ArrayList<Food> rottenFoods = new ArrayList<Food>();
		ArrayList<Food> localFoods = map.getFoodList();
		Food food;
		Coord c;
		
		for (int i = 0; i < localFoods.size(); i++) {		
			food = localFoods.get(i);
			if (!food.ageUp()) {
				rottenFoods.add(food);
			}
		}
			
		for (Food rottenFood : rottenFoods) {
			c = rottenFood.getCoord();
			map.add(new Wall(c.x, c.y, rottenFood.getSize()));
		}
		
		map.removeFoods(rottenFoods);
		
		for (Puffer puffer : map.getPufferList()) {
			if (!puffer.isAlive()) {
				if (!puffer.ageUp()) {
					decayedPuffers.add(puffer);
				}
			}
		}
		
		for (Puffer dp : decayedPuffers) {
			c = dp.getCoord();
			float z = dp.getSize();
			float p = dp.getSpeed();
			if (z > p) {	
				p = p / z;
				z = 1;
			} else {
				z = z / p;
				p = 1;
			}
			
			map.add(new Food(c.x, c.y, z, p, dp.getSize()));
		}
		
		map.removePuffers(decayedPuffers);
		
		for (Wall wall : map.getWallList()) {
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