package jsimulate;

import java.awt.Color;
import java.awt.Rectangle;

public class Food extends SimObject  {
	private Coord coord;
	private Color color;
	private int size;
	
	public Food(int x, int y) {
		super(x, y, 5, Color.RED);
	}

	public Food(int x, int y, int size) {
		super(x, y, size, Color.RED);
	}
	
	public Food(int x, int y, int size, Color color) {
		super(x, y, size, color);
	}
}
