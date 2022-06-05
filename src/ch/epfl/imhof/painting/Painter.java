package ch.epfl.imhof.painting;
import java.util.function.Predicate;

import ch.epfl.imhof.Attributed;
import ch.epfl.imhof.Map;
import ch.epfl.imhof.geometry.ClosedPolyLine;
import ch.epfl.imhof.geometry.PolyLine;
import ch.epfl.imhof.geometry.Polygon;
import ch.epfl.imhof.painting.LineStyle.Cap;
import ch.epfl.imhof.painting.LineStyle.Join;


public interface Painter {

	public void drawMap(Map map, Java2DCanvas canvas) ;
	
	public static Painter polygon(Color c) {
		return (map, canvas) -> {
			for (Attributed<Polygon> p : map.polygons())
				canvas.drawPolygon(p.value(), c) ;
		} ;
	}
	
	public static Painter line(LineStyle style) {
		return (map, canvas) -> {
			for (Attributed<PolyLine> p : map.polyLines())
				canvas.drawPolyLine(p.value(), style) ;
		} ;
	}
	
	public static Painter line(float x, Color c, Cap cap, Join join, float[] tabF) {
		return (map, canvas) -> line(new LineStyle(x, c, cap, join, tabF)) ;
	}
	
	public static Painter line(float x, Color c) {
		return (map, canvas) -> line(new LineStyle(x, c)) ;
	}
	
	public static Painter outline(LineStyle style) {
		return (map, canvas) -> {
			for (Attributed<Polygon> p : map.polygons()) {
				canvas.drawPolyLine(p.value().shell(), style) ;
				for (ClosedPolyLine hole : p.value().holes())
					canvas.drawPolyLine(hole, style) ;
			}
		} ;
	}
	
	public static Painter outline(float x, Color c, Cap cap, Join join, float[] tabF) {
		return (map, canvas) -> outline(new LineStyle(x, c, cap, join, tabF)) ;
	}
	
	public static Painter outline(float x, Color c) {
		return (map, canvas) -> outline(new LineStyle(x, c)) ;
	}
	
	public default Painter when(Predicate<Attributed<?>> p) {
		return (map, canvas) -> {
			Map.Builder bMap = new Map.Builder() ;
			for (Attributed<PolyLine> polyLine : map.polyLines())
				if (p.test(polyLine))
					bMap.addPolyLine(polyLine) ;
			for (Attributed<Polygon> polygon : map.polygons())
				if (p.test(polygon))
					bMap.addPolygon(polygon) ;
			this.drawMap(bMap.build(), canvas) ;
		} ;
	}
	
	public default Painter above(Painter p) {
		return (map, canvas) -> {
			p.drawMap(map, canvas) ;
			this.drawMap(map, canvas) ;
		} ;
	}
	
	public default Painter layered() {
		/*
		return (map, canvas) -> {
			Map.Builder bMap ;
			for (int i = -5 ; i <= 5 ; i++) {
				bMap = new Map.Builder() ;
				for (Attributed<PolyLine> polyLine : map.polyLines())
					if (Filters.onLayer(i).test(polyLine))
						bMap.addPolyLine(polyLine) ;
				for (Attributed<Polygon> polygon : map.polygons())
					if (Filters.onLayer(i).test(polygon))
						bMap.addPolygon(polygon) ;
				this.drawMap(bMap.build(), canvas) ;
			}
		} ;
		*/
		Painter painter = this.when(Filters.onLayer(-5)) ;
		for (int i = -4 ; i <= 5 ; i++)
			painter = this.when(Filters.onLayer(i)).above(painter) ;
		return painter ;
	}
	
}
