package jsimulate;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class Simulation extends JFrame {

	private static final long serialVersionUID = 1L;

	public Simulation() {

        initUI();
    }
    
    private void initUI() {
    	
    	this.setLayout(new BorderLayout());
        
        Board board = new Board();
    	JScrollPane window = new JScrollPane(board);
    	window.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);  
    	window.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    	
    	window.getHorizontalScrollBar().setUnitIncrement(16);
    	window.getVerticalScrollBar().setUnitIncrement(16);
    	
    	//window.setPreferredSize(new Dimension(SimUtils.worldSize + 50, SimUtils.worldSize + 50));
    	//setPreferredSize(new Dimension(SimUtils.worldSize + 250, SimUtils.worldSize + 60));
    	
    	window.validate();
    	Dimension d = window.getPreferredSize();
        d.setSize(d.width, d.height);
    	
        this.getContentPane().add(window,BorderLayout.CENTER);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
       
        JLabel nPufferLabel = new JLabel("Number of puffers:");
        JTextField nPufferField = new JTextField("10");
        
        JLabel foodGenLabel = new JLabel("Food generation interval:");
        JTextField foodGenField = new JTextField("10");
        
        JLabel worldSizeLabel = new JLabel("World size:");
        JTextField worldSizeField = new JTextField("800");
        
        
        JButton startBtn = new JButton("Start!");
        startBtn.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) { 
   			
        			board.start(Integer.parseInt(nPufferField.getText()),
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
    	configPanel.setLayout(new GridLayout(3, 2));
    	
    	JPanel actionPanel = new JPanel();
        
        	configPanel.add(nPufferLabel);
        configPanel.add(nPufferField);
        
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
        //board.runBoard();
    }

}
