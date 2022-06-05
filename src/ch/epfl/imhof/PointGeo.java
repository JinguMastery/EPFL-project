package ch.epfl.imhof;


public final class PointGeo {       // classe des points décrits par leur position sur une sphère

	private final double longitude, latitude ;
	private final static double PI = Math.PI ;
	
	public PointGeo(double x, double y) {
		if (x < -PI || x > PI || y < -PI/2 || y > PI/2)
			throw new IllegalArgumentException() ;
		longitude = x ;
		latitude = y ;
	}

	public double longitude() {            // retourne la longitude du point
		return longitude;
	}

	public double latitude() {             // retourne la latitude du point
		return latitude;
	}
	
}
