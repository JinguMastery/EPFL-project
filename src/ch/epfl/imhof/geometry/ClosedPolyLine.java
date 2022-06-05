package ch.epfl.imhof.geometry;
import java.util.List;


public final class ClosedPolyLine extends PolyLine {      // classe des polylignes fermées

	public ClosedPolyLine(List<Point> points) {
		super(points) ;
	}

	public boolean isClosed() {            // indique que la polyligne est fermée
		return true ;
	}
	
	public double area() {                 // retourne l'aire de la polyligne grâce à la méthode des triangles vue dans la théorie
		double x = 0. ;
		Point pt1, pt2 ;
		for (int i = 0 ; i < points().size() ; i++) {
			pt1 = points().get(i) ;
			if (i < points().size()-1)
				pt2 = points().get(i+1) ;
			else
				pt2 = points().get(0) ;
			x += (pt1.x()*pt2.y()-pt2.x()*pt1.y()) ;
		}
		return Math.abs(x)/2 ;
	}
	
	public boolean containsPoint(Point p) {            // retourne vrai si la polyligne courante contient le point spécifié selon la méthode de la théorie
		int inc = 0 ;
		Point pt1, pt2 ;
		for (int i = 0 ; i < points().size() ; i++) {
			pt1 = points().get(i) ;
			if (i < points().size()-1)
				pt2 = points().get(i+1) ;
			else
				pt2 = points().get(0) ;
			boolean b = (pt1.x()-p.x())*(pt2.y()-p.y()) > (pt2.x()-p.x())*(pt1.y()-p.y()) ;
			if (pt1.y() <= p.y()) {
				if (pt2.y() > p.y() && b)
					inc++ ;
			}
			else
				if (pt2.y() <= p.y() && !b)
					inc-- ;
		}
		return (inc != 0) ;
	}
	
}
