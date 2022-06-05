package ch.epfl.imhof;


public class Vector3 {

	private final double x, y, z ;
	
	public Vector3(double x, double y, double z) {
		this.x = x ;
		this.y = y ;
		this.z = z ;
	}
	
	public double norm() {
		return Math.sqrt(x*x+y*y+z*z) ;
	}
	
	public Vector3 normalized() {
		double d = norm() ;
		return new Vector3(x/d, y/d, z/d) ;
	}
	
	public double scalarProduct(Vector3 v) {
		return x*v.x + y*v.y + z*v.z ;
	}
	
}
