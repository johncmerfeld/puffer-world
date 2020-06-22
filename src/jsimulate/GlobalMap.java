package jsimulate;

import java.util.ArrayList;

public class GlobalMap {
    private ArrayList<Puffer> pufferList;
    private ArrayList<Food> foodList;
    private ArrayList<Wall> wallList;

	public GlobalMap() {
		this.setFoodList(new ArrayList<Food>());
		this.setPufferList(new ArrayList<Puffer>());	
		this.setWallList(new ArrayList<Wall>());
	}

	/**
	 * @return the pufferList
	 */
	public ArrayList<Puffer> getPufferList() {
		return pufferList;
	}

	/**
	 * @param pufferList the pufferList to set
	 */
	public void setPufferList(ArrayList<Puffer> pufferList) {
		this.pufferList = pufferList;
	}

	/**
	 * @return the foodList
	 */
	public ArrayList<Food> getFoodList() {
		return foodList;
	}

	/**
	 * @param foodList the foodList to set
	 */
	public void setFoodList(ArrayList<Food> foodList) {
		this.foodList = foodList;
	}
	
	/**
	 * @return the wallList
	 */
	public ArrayList<Wall> getWallList() {
		return wallList;
	}

	/**
	 * @param wallList the wallList to set
	 */
	public void setWallList(ArrayList<Wall> wallList) {
		this.wallList = wallList;
	}

	public void add(Puffer puffer) {
		this.pufferList.add(puffer);
	}
	
	public void add(Food food) {
		this.foodList.add(food);
	}
	
	public void add(Wall wall) {
		this.wallList.add(wall);
	}
	
	public void removeAll(ArrayList<Food> removeList) {
		this.foodList.removeAll(removeList);
	}
	
}
