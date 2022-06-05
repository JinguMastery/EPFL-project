package ch.epfl.imhof.projection;
import ch.epfl.imhof.PointGeo;
import ch.epfl.imhof.geometry.Point;


public final class CH1903Projection implements Projection {     // classe décrivant une projection CH1903

	public Point project(PointGeo point) {                 // convertit un PointGeo en un Point selon la projection CH1903
		double x1, x2, y1, y2 ;
		x1 = 1/10000. * (Math.toDegrees(point.longitude())*3600-26782.5) ;
		y1 = 1/10000. * (Math.toDegrees(point.latitude())*3600-169028.66) ;
		x2 = 600072.37 + 211455.93*x1 - 10938.51*x1*y1 - 0.36*x1*y1*y1 - 44.54*Math.pow(x1, 3) ;
		y2 = 200147.07 + 308807.95*y1 + 3745.25*x1*x1 + 76.63*y1*y1 - 194.56*x1*x1*y1 + 119.79*Math.pow(y1, 3) ;
		return new Point(x2, y2) ;
	}

	public PointGeo inverse(Point point) {             // idem que plus haut mais de Point à PointGeo
		double x0, y0, x1, y1, x2, y2 ;
		x1 = (point.x()-600000)/1000000 ;
		y1 = (point.y()-200000)/1000000 ;
		x0 = 2.6779094 + 4.728982*x1 + .791484*x1*y1 + .1306*x1*y1*y1 - .0436*Math.pow(x1, 3) ;
		y0 = 16.9023892 + 3.238272*y1 - .270978*x1*x1 - .002528*y1*y1 - .0447*x1*x1*y1 - .0140*Math.pow(y1, 3) ;
		x2 = x0 * 100/36. ;
		y2 = y0 * 100/36. ;
		return new PointGeo(Math.toRadians(x2), Math.toRadians(y2)) ;
	}

}
