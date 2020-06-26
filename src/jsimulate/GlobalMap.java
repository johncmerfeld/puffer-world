package jsimulate;

import java.util.ArrayList;

/**
 * The GlobalMap class tracks all of the objects residing in the simulation.
 * It can be used as a reference to calculate distances, apply changes, etc.
 * @author johncmerfeld
 *
 */

public class GlobalMap {
    private ArrayList<Puffer> pufferList;
    private ArrayList<Food> foodList;
    private ArrayList<Wall> wallList;
    
    private int nextFamily = 0;
    private int worldSize;

	public GlobalMap(int worldSize) {
		this.setFoodList(new ArrayList<Food>());
		this.setPufferList(new ArrayList<Puffer>());	
		this.setWallList(new ArrayList<Wall>());
		this.setWorldSize(worldSize);
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
	
	public void removeFoods(ArrayList<Food> removeList) {
		this.foodList.removeAll(removeList);
	}
	
	public void removePuffers(ArrayList<Puffer> removeList) {
		this.pufferList.removeAll(removeList);
	}
	
	public void removeWalls(ArrayList<Wall> removeList) {
		this.wallList.removeAll(removeList);
	}
	
	public void add(ArrayList<Puffer> puffers) {
		this.pufferList.addAll(puffers);
	}
	
	public int getNextFamily() {
		return nextFamily++;
	}

	/**
	 * @return the worldSize
	 */
	public int getWorldSize() {
		return worldSize;
	}

	/**
	 * @param worldSize the worldSize to set
	 */
	public void setWorldSize(int worldSize) {
		this.worldSize = worldSize;
	}
	
}
