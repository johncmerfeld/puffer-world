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

public class Board extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;
	private Thread animator;
    private GlobalMap map;
    private int globalTime = 0;

    public Board() {
    	
    	map = new GlobalMap();
        initBoard();
    }

    private void initBoard() {

        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(SimUtils.worldSize, SimUtils.worldSize));
        
        	for (int i = 0; i < SimUtils.nPuffers; i++) {
        		int xpos = ThreadLocalRandom.current().nextInt(1, SimUtils.worldSize);
        		int ypos = ThreadLocalRandom.current().nextInt(1, SimUtils.worldSize);
        		map.add(new Puffer(xpos, ypos, SimUtils.defaultCreatureSize));
        	}
        	
		for (int x = 0; x < SimUtils.worldSize; x++) {
			for (int y = 0; y < SimUtils.worldSize; y++) {
				if (ThreadLocalRandom.current().nextFloat() < SimUtils.foodDensity) {
					map.add(new Food(x, y, SimUtils.defaultEnvObjectSize));
				}
			}
		}
		
		/*
		for (int x = 0; x < SimUtils.worldSize; x++) {
			for (int y = 0; y < SimUtils.worldSize; y++) {
				if (ThreadLocalRandom.current().nextFloat() < SimUtils.wallDensity) {
					map.add(new Wall(x, y, SimUtils.defaultEnvObjectSize * 2));
				}
			}
		} */
    }

    @Override
    public void addNotify() {
        super.addNotify();

        animator = new Thread(this);
        animator.start();
    }

	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        drawMap(g);
        	for (Puffer puffer : map.getPufferList()) {
        		drawPuffer(g, puffer);
        	}
    }
	
	 private void drawPuffer(Graphics g, Puffer puffer) {
	    	g.setColor(puffer.getColor());
	    	Coord c = puffer.getCoord();
	    	int pSize = puffer.getSize();
	    	g.fillRect(c.x, c.y, pSize, pSize);
    }

    private void cycle() {
    	
    	advanceTime();
    	
    	for (Puffer puffer : map.getPufferList()) {
    		
    		// try to move the puffer, then check for collisions
    		puffer.move(map);
    		Coord c = puffer.getCoord();
    		
    		Rectangle p = puffer.getBounds();
    		Rectangle w;
    		
    		ArrayList<Wall> localWalls = map.getWallList(); 		
    		Wall wall;
    		for (int i = 0; i < localWalls.size(); i++) {
    			wall = localWalls.get(i);
    			w = wall.getBounds();
    			if (p.intersects(w)) {
    				// FIXME probably not right behavior
    				puffer.bounce(true, true);
    				// FIXME puffer won't know how to get around the wall...
    				//break;
    			}
    		}
   	
    		if ((c.x > SimUtils.worldSize) || (c.x < 0)) { 
    			puffer.bounce(true, false);
    		} 
    		else if ((c.y > SimUtils.worldSize) || (c.y < 0)) { 
    			puffer.bounce(false, true);
            }
    		
    		ArrayList<Food> removeList = new ArrayList<Food>();
    		ArrayList<Food> localFoods = map.getFoodList();
    		
    		Food food;
    		
    		for (int i = 0; i < localFoods.size(); i++) {
    			
    			food = localFoods.get(i);
    			
    			Rectangle f = food.getBounds();
    			
    			if (p.intersects(f)) {
    				// Food got eaten!
    				puffer.eat();
    				removeList.add(food);
    			}
    		}
    		
    		map.removeFoods(removeList);
    		
    		for (Puffer puffer2 : map.getPufferList()) {
    			if (puffer2 != puffer) {
    				Rectangle p2 = puffer2.getBounds();
	        			if ((p.intersects(p2)) && (puffer2.getSize() > puffer.getSize())) {
	        				puffer.die();
	        			}
    			}	
    		}	
    	}   
    }

    @Override
    public void run() {

        long beforeTime, timeDiff, sleep;

        beforeTime = System.currentTimeMillis();

        while (true) {

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
                
                JOptionPane.showMessageDialog(this, msg, "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }

            beforeTime = System.currentTimeMillis();
        }
    }
	
	private void drawMap(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh
                = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g2d.setRenderingHints(rh);
        
        	Coord c;
        	
        	ArrayList<Food> localFoods = map.getFoodList(); 		
		Food food;	
		for (int i = 0; i < localFoods.size(); i++) {
			food = localFoods.get(i);
			g.setColor(food.getColor());
    		c = food.getCoord();
    		g.fillRect(c.x, c.y, food.getSize(), food.getSize());
		}
		
		ArrayList<Wall> localWalls = map.getWallList(); 		
		Wall wall;
		for (int i = 0; i < localWalls.size(); i++) {
			wall = localWalls.get(i);
			g.setColor(wall.getColor());
    		c = wall.getCoord();
    		g.fillRect(c.x, c.y, wall.getSize(), wall.getSize());
		}
	}
	
	private void advanceTime() {
		
		/* make some food rot */
		ArrayList<Puffer> decayedPuffers = new ArrayList<Puffer>();
		ArrayList<Food> rottenFoods = new ArrayList<Food>();
		ArrayList<Wall> crumbledWalls = new ArrayList<Wall>();
		ArrayList<Food> localFoods = map.getFoodList();
		
		Food food;
		
		for (int i = 0; i < localFoods.size(); i++) {		
			food = localFoods.get(i);
			if (!food.ageUp()) {
				rottenFoods.add(food);
			}
		}
		
		Coord c;
		
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
		
		for (Puffer decayedPuffer : decayedPuffers) {
			c = decayedPuffer.getCoord();
			map.add(new Food(c.x, c.y, decayedPuffer.getSize()));
		}
		
		map.removePuffers(decayedPuffers);
		
		for (Wall wall : map.getWallList()) {
			if (!wall.ageUp()) {
				crumbledWalls.add(wall);
			}
		}
		
		map.removeWalls(crumbledWalls);
		
		globalTime++;
		
		if (globalTime % SimUtils.foodGenerationInterval == 0) {
			/* create some new food */
			for (int i = 0; i < SimUtils.foodsPerGeneration; i++) {
		    	int xpos = ThreadLocalRandom.current().nextInt(1, SimUtils.worldSize);
		    	int ypos = ThreadLocalRandom.current().nextInt(1, SimUtils.worldSize);
		    	map.add(new Food(xpos, ypos, SimUtils.defaultEnvObjectSize));
			}
		}
	}
}