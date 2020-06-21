package jsimulate;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import java.awt.Toolkit;

import javax.swing.JPanel;
import javax.swing.JOptionPane;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Board extends JPanel implements Runnable {
	
	private final int B_WIDTH = 600;// * SimUtils.scaleFactor;
    private final int B_HEIGHT = 600;// * SimUtils.scaleFactor;
    private final int INITIAL_X = 40;// * SimUtils.scaleFactor;
    private final int INITIAL_Y = 40;// * SimUtils.scaleFactor;
    private final int DELAY = 25;
    
    private Thread animator;
    
   // private Map map;
    private int mapSize;
    
    private ArrayList<Puffer> pufferList;
    private ArrayList<Food> foodList;

    public Board() {

        initBoard();
    }

    private void initBoard() {

        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        
        	pufferList = new ArrayList<Puffer>();
        	for (int i = 0; i < SimUtils.nPuffers; i++) {
        		int xpos = ThreadLocalRandom.current().nextInt(1, B_WIDTH);
        		int ypos = ThreadLocalRandom.current().nextInt(1, B_HEIGHT);
        		pufferList.add(new Puffer(xpos, ypos, 10));
        	}
        	
        	foodList = new ArrayList<Food>();
        	Random random = new Random();
        	
        mapSize = B_WIDTH;
		
		for (int x = 0; x < mapSize; x++) {
			for (int y = 0; y < mapSize; y++) {
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
        
        	// TODO if no map, draw one, else use existing map
        drawMap(g);
        	for (Puffer puffer : pufferList) {
        		drawPuffer(g, puffer);
        	}
    }
	
	 private void drawPuffer(Graphics g, Puffer puffer) {
	    	g.setColor(puffer.getColor());
	    	Coord c = puffer.getCoord();
	    	g.fillRect(c.x * SimUtils.scaleFactor, c.y * SimUtils.scaleFactor, puffer.getSize(), puffer.getSize()); // TODO puffer size!!
        Toolkit.getDefaultToolkit().sync();
    }

    private void cycle() {
    	
    	for (Puffer puffer : pufferList) {
    		//System.out.println("Trying to move...");
    		puffer.move(foodList);
    		Coord c = puffer.getCoord();
    		// FIXME only covers bottom -- should bounce
    		if ((c.y > B_HEIGHT) || (c.y < 0) || (c.x > B_WIDTH) || (c.x < 0)){

    			c.y = INITIAL_Y;
    			c.x = INITIAL_X;
    			puffer.setCoord(c);
            }
    		
    		ArrayList<Food> removeList = new ArrayList<Food>();
    		
    		for (Food food : foodList) {
    			// FIXME too exact
    			Rectangle f = food.getBounds();
    			Rectangle p = puffer.getBounds();
    			if (p.intersects(f)) {
    				// Food got eaten!
    				puffer.eat();
    				removeList.add(food);
    				//System.out.println("Puffer ate a food!");
    			}
    		}
    		foodList.removeAll(removeList);
    		
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
            sleep = DELAY - timeDiff;

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
	
	/*
	 * FIXME:
	 *   - Look into not filling entire window
	 *   - Add scrollability
	 *   - Don't recreate on resize
	 * TODO:
	 *   - Need boopers
	 *   - Food clustering?
	 */
	
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
        		// TODO fix this scale factor thing
        		g.fillRect(c.x * SimUtils.scaleFactor, c.y * SimUtils.scaleFactor, food.getSize(), food.getSize());
        	}

	}

 
}