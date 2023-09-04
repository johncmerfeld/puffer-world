package jsimulate;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 * The Main class initializes the UI
 * @author johncmerfeld
 *
 */

public class Main {

    public static void main(String[] args) {
        
        EventQueue.invokeLater(() -> {
            JFrame ex = new Simulation(args);
            ex.setVisible(true);
        });
    }
}