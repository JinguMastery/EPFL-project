package ch.epfl.imhof.painting;
import ch.epfl.imhof.geometry.PolyLine;
import ch.epfl.imhof.geometry.Polygon;


interface Canvas {

	public void drawPolyLine(PolyLine polyLine, LineStyle style) ;
	public void drawPolygon(Polygon polygon, Color font) ;
	
}
