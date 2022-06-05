package ch.epfl.imhof.geometry;
import java.util.function.Function ;


public final class Point {      // classe des points "algébriques"
	
	private double x, y ;
	
	public Point(double x, double y) {         
		this.x = x ;
		this.y = y ;
	}

	public double x() {            // retourne la coordonée x du point.
		return x;
	}

	public double y() {            // même chose mais avec y.
		return y;
	}
	
	public static Function<Point, Point> alignedCoordinateChanged(Point p1, Point p2, Point q1, Point q2) {
		if (p1.x == q1.x || p1.y == q1.y)
			throw new IllegalArgumentException() ;
		double dx, tx, dy, ty ;
		dx = (p2.x-q2.x) / (p1.x-q1.x) ;
		dy = (p2.y-q2.y) / (p1.y-q1.y) ;
		tx = p2.x - p1.x*dx ;
		ty = p2.y - p1.y*dy ;
		return point -> new Point(point.x*dx+tx, point.y*dy+ty) ;
	}

}
