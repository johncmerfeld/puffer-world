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
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Board extends JPanel implements Runnable {
    
    private Thread animator;
    
    private ArrayList<Puffer> pufferList;
    private ArrayList<Food> foodList;

    public Board() {

        initBoard();
    }

    private void initBoard() {

        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(SimUtils.worldSize, SimUtils.worldSize));
        
        	pufferList = new ArrayList<Puffer>();
        	for (int i = 0; i < SimUtils.nPuffers; i++) {
        		int xpos = ThreadLocalRandom.current().nextInt(1, SimUtils.worldSize);
        		int ypos = ThreadLocalRandom.current().nextInt(1, SimUtils.worldSize);
        		pufferList.add(new Puffer(xpos, ypos, 10));
        	}
        	
        	foodList = new ArrayList<Food>();
        	Random random = new Random();
        	
		for (int x = 0; x < SimUtils.worldSize; x++) {
			for (int y = 0; y < SimUtils.worldSize; y++) {
				if (random.nextFloat() < SimUtils.foodDensity) {
					foodList.add(new Food(x, y, 10));
				}
			}
		}
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
        	for (Puffer puffer : pufferList) {
        		drawPuffer(g, puffer);
        	}
    }
	
	 private void drawPuffer(Graphics g, Puffer puffer) {
	    	g.setColor(puffer.getColor());
	    	Coord c = puffer.getCoord();
	    	int pSize = puffer.getSize();
	    	g.fillRect(c.x * SimUtils.scaleFactor, c.y * SimUtils.scaleFactor, pSize, pSize);
    }

    private void cycle() {
    	
    	for (Puffer puffer : pufferList) {
    		//System.out.println("Trying to move...");
    		puffer.move(foodList);
    		Coord c = puffer.getCoord();
    		// FIXME should bounce
   	
    		if ((c.x > SimUtils.worldSize) || (c.x < 0)) { 
    			puffer.bounce(true, false);
    		} 
    		if ((c.y > SimUtils.worldSize) || (c.y < 0)) { 
    			puffer.bounce(false, true);
            }
    		
    		Rectangle p = puffer.getBounds();
    		
    		ArrayList<Food> removeList = new ArrayList<Food>();
    		
    		for (Food food : foodList) {
    			Rectangle f = food.getBounds();
    			
    			if (p.intersects(f)) {
    				// Food got eaten!
    				puffer.eat();
    				removeList.add(food);
    			}
    		}
    		foodList.removeAll(removeList);
    		
    		for (Puffer puffer2 : pufferList) {
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
        
        	for (Food food : foodList) {
        		g.setColor(food.getColor());
        		c = food.getCoord();
        		// TODO cleanup, remove scale factor
        		g.fillRect(c.x * SimUtils.scaleFactor, c.y * SimUtils.scaleFactor, food.getSize(), food.getSize());
        	}

	}

 
}