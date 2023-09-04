package jsimulate;

import java.util.ArrayList;

/**
 * The GlobalMap class tracks all of the objects residing in the simulation.
 * It can be used as a reference to calculate distances, apply changes, etc.
 * @author johncmerfeld
 *
 */

public class GlobalMap {
    private ArrayList<Creature> creatureList;
    private ArrayList<EnvObject> objectList;
    private ArrayList<Family> familyList;
    
    private int nextFamily = 0;
    private int worldSize;

	public GlobalMap(int worldSize) {
		this.setCreatureList(new ArrayList<Creature>());
		this.setObjectList(new ArrayList<EnvObject>());
		this.setFamilyList(new ArrayList<Family>());
		this.setWorldSize(worldSize);
	}
	
	/**
	 * 
	 */
	public void add(Family family) {
		this.familyList.add(family);
	}
	
	public void growAllByFamily(int fid, int type) {
		for (Family family : familyList) {
			if (family.getId() == fid) {
				family.growAll(type);
				return;
			}
		}
		throw new java.lang.RuntimeException("Family not found, somehow!");
	}

	/**
	 * @return the creatureList
	 */
	public ArrayList<Creature> getCreatureList() {
		return creatureList;
	}

	/**
	 * @param creatureList the creatureList to set
	 */
	public void setCreatureList(ArrayList<Creature> creatureList) {
		this.creatureList = creatureList;
	}
	
	public void add(Creature creature) {
		this.creatureList.add(creature);
	}
	
	public void add(ArrayList<Creature> creatures) {
		this.creatureList.addAll(creatures);
	}
	
	public void removeCreatures(ArrayList<Creature> removeList) {
		this.creatureList.removeAll(removeList);
	}

	/**
	 * @return the objectList
	 */
	public ArrayList<EnvObject> getObjectList() {
		return objectList;
	}

	/**
	 * @param objectList the objectList to set
	 */
	public void setObjectList(ArrayList<EnvObject> objectList) {
		this.objectList = objectList;
	}
	
	public void add(EnvObject eo) {
		this.objectList.add(eo);
	}
	
	public void addObjects(ArrayList<EnvObject> objects) {
		this.objectList.addAll(objects);
	}

	public void removeObjects(ArrayList<EnvObject> removeList) {
		this.objectList.removeAll(removeList);
	}
	
	public int getNextFamily() {
		return nextFamily++;
	}
	
	public ArrayList<EnvObject> getAllOfType(Class<?> c) {
		ArrayList<EnvObject> returnList = new ArrayList<EnvObject>();
		for (EnvObject eo : objectList) {
			if (c.isInstance(eo)) {
				returnList.add(eo);
			}
		}
		return returnList;
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

	/**
	 * @return the familyList
	 */
	public ArrayList<Family> getFamilyList() {
		return familyList;
	}

	/**
	 * @param familyList the familyList to set
	 */
	public void setFamilyList(ArrayList<Family> familyList) {
		this.familyList = familyList;
	}
	
}
