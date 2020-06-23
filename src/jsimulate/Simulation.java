package jsimulate;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Simulation extends JFrame {

	public Simulation() {

        initUI();
    }
    
    private void initUI() {
    	
    	this.setLayout(new FlowLayout(FlowLayout.LEADING));
        
        Board board = new Board();
    	JScrollPane window = new JScrollPane(board);
    	window.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);  
    	window.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    	
    	window.getHorizontalScrollBar().setUnitIncrement(16);
    	window.getVerticalScrollBar().setUnitIncrement(16);
    	
    	window.setPreferredSize(new Dimension(SimUtils.worldSize, SimUtils.worldSize));
    	setPreferredSize(new Dimension(SimUtils.worldSize + 50, SimUtils.worldSize + 50));
    	
        add(window);
        
        JPanel textPanel = new JPanel();
        JButton startBtn = new JButton("Start!");
        startBtn.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) { 
        			
        			//board.runBoard();
        		} 
        });
        JLabel label1 = new JLabel("(TODO: stats)");
        
        textPanel.add(label1);
        textPanel.add(startBtn);
        
        add(textPanel);

        setResizable(true);
        pack();
        
        setTitle("Puffer World");    
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);     
        board.runBoard();
    }

}
