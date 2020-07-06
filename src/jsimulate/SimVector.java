package jsimulate;

public abstract class SimVector {

	public int x;
	public int y;
	
	public SimVector(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public double similiarity(SimVector sv) {
		int numerator = (x * sv.x) + (y * sv.y);
		double denominator = Math.sqrt((x * x) + (y * y)) * Math.sqrt((sv.x * sv.x) + (sv.y * sv.y));
		return numerator;// / denominator;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof SimVector){
			SimVector toCompare = (SimVector) o;
		    return ((x == toCompare.x) && (y == toCompare.y));
		  }
		  return false;
	}
	
	@Override
	public int hashCode() {
	    return (x * 100000) + y;
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

}
