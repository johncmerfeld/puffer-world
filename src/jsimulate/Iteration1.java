package jsimulate;
import java.awt.EventQueue;
import javax.swing.JFrame;

public class Iteration1 extends JFrame {
	private int boardSize;
	
	public Iteration1(int size) {
		this.boardSize = size;
		initUI(size);
	}
	
	private void initUI(int size) {

        add(new Board());

        setSize(size, size);

        setTitle("Iteration 1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
	
	public int getBoardSize() {
		return this.boardSize;
	}
	
    public static void main(String[] args) {
        
        EventQueue.invokeLater(() -> {
        	Iteration1 ex = new Iteration1(500);
            ex.setVisible(true);
        });
    }
}
