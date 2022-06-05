package ch.epfl.imhof.geometry;
import java.util.List;


public final class OpenPolyLine extends PolyLine {      // classe des polylignes ouvertes

	public OpenPolyLine(List<Point> points) {
		super(points) ;
	}

	public boolean isClosed() {        // indique que la polyligne est ouverte
		return false ;
	}
	
}
