package jsimulate;

public class Velocity {
	public int xVel;
	public int yVel;
	
	public Velocity(int xVel, int yVel) {
		this.setxVel(xVel);
		this.setyVel(yVel);
	}

	/**
	 * @return the xVel
	 */
	public int getxVel() {
		return xVel;
	}

	/**
	 * @param xVel the xVel to set
	 */
	public void setxVel(int xVel) {
		this.xVel = xVel;
	}

	/**
	 * @return the yVel
	 */
	public int getyVel() {
		return yVel;
	}

	/**
	 * @param yVel the yVel to set
	 */
	public void setyVel(int yVel) {
		this.yVel = yVel;
	}
}
