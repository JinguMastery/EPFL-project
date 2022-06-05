package ch.epfl.imhof.projection;
import ch.epfl.imhof.PointGeo;
import ch.epfl.imhof.geometry.Point;


public interface Projection {       // classe abstraite décrivant les deux types différents de projection
	
	Point project(PointGeo point) ;
	PointGeo inverse(Point point) ;

}
