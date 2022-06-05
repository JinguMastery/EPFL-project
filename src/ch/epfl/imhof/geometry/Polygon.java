package ch.epfl.imhof.geometry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class Polygon {        // classe des polygones

	private final ClosedPolyLine shell ;
	private final List<ClosedPolyLine> holes ;
	
	public Polygon(ClosedPolyLine shell, List <ClosedPolyLine> holes) {
		this.shell = shell ;
		this.holes = Collections.unmodifiableList(new ArrayList<ClosedPolyLine> (holes)) ;
	}
	
	public Polygon(ClosedPolyLine shell) {
		this.shell = shell ;
		holes = new ArrayList<ClosedPolyLine> () ;
	}

	public ClosedPolyLine shell() {            // retourne l'enveloppe du polygone
		return shell ;
	}

	public List<ClosedPolyLine> holes() {       // retourne uniquement les trous du polygone
		return holes ;
	}
	
}
