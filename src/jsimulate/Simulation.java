package jsimulate;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class Simulation extends JFrame {

	public Simulation() {

        initUI();
    }
    
    private void initUI() {
        
        Board board = new Board();
    	JScrollPane window = new JScrollPane(board);
    	window.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);  
    	window.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    	
    	window.getHorizontalScrollBar().setUnitIncrement(16);
    	window.getVerticalScrollBar().setUnitIncrement(16);
    	
    	window.setPreferredSize(new Dimension(SimUtils.worldSize, SimUtils.worldSize));
    	setPreferredSize(new Dimension(SimUtils.worldSize + 50, SimUtils.worldSize + 50));
    	
        add(window);

        setResizable(true);
        pack();
        
        setTitle("Puffer World");    
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
    }

}
