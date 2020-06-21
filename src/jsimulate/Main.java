package jsimulate;

import java.awt.EventQueue;
import javax.swing.JFrame;

/*
 * FIXME:
 *   - Add scrollability
 *   - Don't recreate on resize
 * TODO:
 *   - Food clustering?
 */

public class Main extends JFrame {

    public Main() {

        initUI();
    }
    
    private void initUI() {
        
        add(new Board());

        setResizable(true);
        pack();
        
        setTitle("Puffer World");    
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
    }

    public static void main(String[] args) {
        
        EventQueue.invokeLater(() -> {
            JFrame ex = new Main();
            ex.setVisible(true);
        });
    }
}