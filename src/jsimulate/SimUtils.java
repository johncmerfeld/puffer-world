package jsimulate;

import java.util.concurrent.ThreadLocalRandom;

public class SimUtils {
	public static int scaleFactor = 1;
	public static int nPuffers = 25;
	public static double foodDensity = 0.00001;
	public static double wallDensity = 0.00002;
	public static int worldSize = 800;
	public static int globalDelay = 25;
	
	public static int defaultCreatureSize = 8;
	public static int defaultEnvObjectSize = 6;
	
	public static int foodsPerGeneration = 1;
	public static int foodGenerationInterval = 10;
	
	public static Food createFood() {
		int x = ThreadLocalRandom.current().nextInt(1, SimUtils.worldSize);
    	int y = ThreadLocalRandom.current().nextInt(1, SimUtils.worldSize);
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
		float r = ThreadLocalRandom.current().nextFloat();
		if (r < 0.3333) {
			newX = -speed;
		} else if (r < 0.6666) {
			newX = 0;
		} else {
			newX = speed;
		}
		r = ThreadLocalRandom.current().nextFloat();
		if (r < 0.3333) {
			newY = -speed;
		} else if (r < 0.6666) {
			newY = 0;
		} else {
			newY = speed;
		}
		
		return new Velocity(newX, newY);
	}
}
