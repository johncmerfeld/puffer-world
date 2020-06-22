package jsimulate;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/*
 * FIXME:
 *   - Add scrollability
 *   - Don't recreate on resize
 * TODO:
 *   - Food clustering?
 *   - Food size == nutrition?
 *   - Dead puffers become big food after a while??
 */

public class Main {

    public static void main(String[] args) {
        
        EventQueue.invokeLater(() -> {
            JFrame ex = new Simulation();
            ex.setVisible(true);
        });
    }
}