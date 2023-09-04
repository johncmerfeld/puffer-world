package jsimulate;

import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/** The Simulation class is the UI for the simulator. It contains the view of the board, and the user's interactions with it
 * 
 * @author johncmerfeld
 *
 */

public class Simulation extends JFrame {

	private static final long serialVersionUID = 1L;

	public Simulation(String[] args) {

        initUI(args);
    }
    
    private void initUI(String[] args) {
    	
    	this.setLayout(new BorderLayout());
        
        Board board = new Board();
    	JScrollPane window = new JScrollPane(board);
    	window.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);  
    	window.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    	
    	window.getHorizontalScrollBar().setUnitIncrement(16);
    	window.getVerticalScrollBar().setUnitIncrement(16);
    	
    	window.validate();
    	Dimension d = window.getPreferredSize();
        d.setSize(d.width, d.height);
    	
        this.getContentPane().add(window,BorderLayout.CENTER);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        JLabel nPufferLabel = new JLabel("Number of puffers:");
        JTextField nPufferField;
        if (args.length > 0) {
        		nPufferField = new JTextField(args[0]);     	
        } else {
        		nPufferField = new JTextField("10"); 
        }
        
        JLabel nScooperLabel = new JLabel("Number of Scoopers:");
        JTextField nScooperField;
        if (args.length > 1) {
            nScooperField = new JTextField(args[1]);     	
        } else {
        		nScooperField = new JTextField("5"); 
        }
        
        JLabel foodGenLabel = new JLabel("Food generation interval:");
        JTextField foodGenField;
        if (args.length > 2) {
            foodGenField = new JTextField(args[2]);     	
        } else {
        		foodGenField = new JTextField("10"); 
        }
        
        JLabel worldSizeLabel = new JLabel("World size:");
        JTextField worldSizeField;
        if (args.length > 3) {
            worldSizeField = new JTextField(args[3]);     	
        } else {
        		worldSizeField = new JTextField("800"); 
        }
        
        JButton startBtn = new JButton("Start!");
        startBtn.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) { 
   			
        			board.start(Integer.parseInt(nPufferField.getText()),
        					Integer.parseInt(nScooperField.getText()),
        					Integer.parseInt(foodGenField.getText()),
        					Integer.parseInt(worldSizeField.getText()));
        		} 
        });
        
        JButton stopBtn = new JButton("Pause!");
        stopBtn.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) { 
   			
        			board.pause();
        		} 
        });
        
        JButton resumeBtn = new JButton("Resume!");
        resumeBtn.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) { 
   			
        			board.resume();
        		} 
        });
        
    	JPanel configPanel = new JPanel();
    	configPanel.setLayout(new GridLayout(4, 2));
    	
    	JPanel actionPanel = new JPanel();
        
        	configPanel.add(nPufferLabel);
        configPanel.add(nPufferField);
        
    	configPanel.add(nScooperLabel);
        configPanel.add(nScooperField);
        
    	configPanel.add(foodGenLabel);
        configPanel.add(foodGenField);
        
        configPanel.add(worldSizeLabel);
        configPanel.add(worldSizeField);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(2, 1));
        textPanel.add(configPanel);
        actionPanel.add(startBtn);
        actionPanel.add(stopBtn);
        actionPanel.add(resumeBtn);
        textPanel.add(actionPanel);
        
        this.getContentPane().add(textPanel, BorderLayout.LINE_END);
        
        validate();
        d = getPreferredSize();
        d.setSize(d.width, d.height);

        setResizable(true);
        pack();
        
        setTitle("Puffer World");    

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);     

    }
}
