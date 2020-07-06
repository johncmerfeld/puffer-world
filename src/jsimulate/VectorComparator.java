package jsimulate;

import java.util.Comparator;

/**
 * The VectorComparator is for sorting Vector-like objects according to their dot product with a reference vector
 * @author johncmerfeld
 *
 */

public class VectorComparator implements Comparator<Move> {
	
	private Move reference;
	
	public VectorComparator(Move move) {
		this.reference = move;
	}

    @Override
    public int compare(Move m1, Move m2) {
    	double sim1 = reference.similiarity(m1);
    	double sim2 = reference.similiarity(m2);
    	if (sim1 > sim2) {
    		return -1;
    	} else if (sim1 < sim2) {
    		return 1;
    	} else {
    		return 0;
    	}
    }
}
