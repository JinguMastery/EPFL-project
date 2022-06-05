package ch.epfl.imhof.geometry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class PolyLine {        // classe abstraites des polylignes (fermées ou ouvertes)

	private final List<Point> points ;
	
	public PolyLine(List<Point> points) {
		if (points.isEmpty())
			throw new IllegalArgumentException() ;
		this.points = Collections.unmodifiableList(new ArrayList<Point> (points));
	}
	
	public abstract boolean isClosed() ;
	
	public List<Point> points() {                  // retourne la liste de points qui composent la PolyLine
		return points ;
	}
	
	public Point firstPoint() {             // retourne le premier point de la polyligne
		return points().get(0) ;
	}
	
	public static final class Builder {       // bâtisseur de polylignes
		
		private List<Point> points ;
		
		public Builder() {
			points = new ArrayList<Point> () ;
		}
		
		public void addPoint(Point newPoint) {     // ajoute un nouveau Point au Builder
			points.add(newPoint) ;
		}
		
		public OpenPolyLine buildOpen() {          // construit une polyligne ouverte (premier et dernier 
		                                           // points différents) à partir des points ajoutés précédemment au builder
			return new OpenPolyLine(points) ;
		}
		
		public ClosedPolyLine buildClosed() {     // construit une polyligne fermée (premier et dernier point égaux)
			return new ClosedPolyLine(points) ;
		}
		
	}
	
}
