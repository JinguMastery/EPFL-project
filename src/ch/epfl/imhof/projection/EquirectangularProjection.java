package ch.epfl.imhof.projection;
import ch.epfl.imhof.PointGeo;
import ch.epfl.imhof.geometry.Point;


public final class EquirectangularProjection implements Projection {        // classe décrivant une projection équirectangulaire

	public Point project(PointGeo point) {                         //convertit un objet de type PointGeo en Point selon la projection équirectangulaire
		return new Point(point.longitude(), point.latitude()) ;
	}

	public PointGeo inverse(Point point) {                         //inverse que précédemment
		return new PointGeo(point.x(), point.y()) ;
	}

}
