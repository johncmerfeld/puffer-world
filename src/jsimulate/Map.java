package jsimulate;

// DEPRECATED; maybe useful when I'm keeping statistics...

import java.util.Random;

public class Map {
	// change these
	public int[][] map;
	public int mapSize;
	
	/*
	 * Default constructor
	 */
	public Map(int size, double density) {
		this.mapSize = size;
		this.map = new int[size][size];
		
		Random random = new Random();
		
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (random.nextFloat() < density) {
					map[x][y] = 1;
				} else {
					map[x][y] = 0;
				}
			}
		}
	}
}
