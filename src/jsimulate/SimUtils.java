package jsimulate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class SimUtils {
	public static int scaleFactor = 1;
	public static int nPuffers = 3;
	public static double foodDensity = 0.00001;
	public static double wallDensity = 0.00002;
	public static int worldSize = 800;
	public static int boardSize = 800;
	public static int globalDelay = 25;
	
	public static int defaultCreatureSize = 8;
	public static int defaultEnvObjectSize = 6;
	
	public static int foodsPerGeneration = 1;
	public static int foodGenerationInterval = 5;
	
	public static Food createFood(int worldSize) {
		int x = ThreadLocalRandom.current().nextInt(1, worldSize);
    	int y = ThreadLocalRandom.current().nextInt(1, worldSize);
		float sizePts = ThreadLocalRandom.current().nextFloat();
		float speedPts = 1 - sizePts;
		return new Food(x, y, sizePts, speedPts);
	}
	
	public static Food createFood(int x, int y) {
		float sizePts = ThreadLocalRandom.current().nextFloat();
		float speedPts = 1 - sizePts;
		return new Food(x, y, sizePts, speedPts);
	}
	
	public static Velocity createVelocity(int speed) {
		int newX;
		int newY;
		float r = ThreadLocalRandom.current().nextInt(3);
		if (r == 0) {
			newX = -speed;
		} else if (r == 1) {
			newX = 0;
		} else {
			newX = speed;
		}
		r = ThreadLocalRandom.current().nextInt(3);
		if (r == 0) {
			newY = -speed;
		} else if (r == 1) {
			newY = 0;
		} else {
			newY = speed;
		}
		
		return new Velocity(newX, newY);
	}
	
	public static ArrayList<Move> mostSimilarMoves(Move move, int speed) {
		ArrayList<Move> nextBestMoves = new ArrayList<Move>();
		for (int i = -speed; i <= speed; i++) {
			for (int j = -speed; j <= speed; j++) {
				nextBestMoves.add(new Move(i,j));
			}
		}
		Collections.sort(nextBestMoves, new VectorComparator(move));
		return nextBestMoves;
	}
	
}
