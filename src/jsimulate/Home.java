package jsimulate;

import java.awt.Color;

public class Home extends EnvObject {
	
	private int family;

	public Home(int x, int y, int family, int size, Color color) {
		super(x, y, size, color);
		this.setFamily(family);
	}

	/**
	 * @return the family
	 */
	public int getFamily() {
		return family;
	}

	/**
	 * @param family the family to set
	 */
	public void setFamily(int family) {
		this.family = family;
	}
	
	public void acceptFood(Food food) {
		/*food.getSizePts();
		food.getSpeedPts(); */
	}

}
