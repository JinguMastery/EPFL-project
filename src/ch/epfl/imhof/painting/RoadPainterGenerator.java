package ch.epfl.imhof.painting;
import java.util.function.Predicate;

import ch.epfl.imhof.Attributed;
import ch.epfl.imhof.painting.LineStyle.Cap;
import ch.epfl.imhof.painting.LineStyle.Join;


public class RoadPainterGenerator {

	private RoadPainterGenerator() {
	}
	
	public static Painter painterForRoads(RoadSpec... specs) {
		Painter[] painters = new Painter[4] ;
		Painter painter, painterF = null ;
		int i ;
		for (i = 4 ; i >= 0 ; i--) {
			painter = specs[0].paintersSpec[i] ;
			for (int j = 1 ; j < specs.length ; j++)
				painter = painter.above(specs[j].paintersSpec[i]) ;
			switch(i) {
			case 0 : case 1 :
				painters[i] = painter.when(Filters.tagged("bridge")) ;
				break ;
			case 2 : case 3 :
				painters[i] = painter.when((Filters.tagged("bridge").or(Filters.tagged("tunnel")).negate())) ;
				break ;
			case 4 :
				painterF = painter.when(Filters.tagged("tunnel")) ;
			}
		}
		for (i = 3 ; i >= 0 ; i--)
			painterF = painters[i].above(painterF) ;
		return painterF.layered() ;
	}
	
	public static class RoadSpec {

		private final Painter[] paintersSpec ;
		
		public RoadSpec(Predicate<Attributed<?>> p, float wi, Color ci, float wc, Color cc) {
			paintersSpec = new Painter[5] ;
			LineStyle iBridge = new LineStyle(wi, ci, Cap.ROUND, Join.ROUND, new float[0]), cBridge = new LineStyle(wi+2*wc, cc, Cap.BUTT, Join.ROUND, new float[0]) ;
			paintersSpec[0] = Painter.line(iBridge).when(p) ;
			paintersSpec[1] = Painter.line(cBridge).when(p) ;
			paintersSpec[2] = Painter.line(iBridge).when(p) ;
			paintersSpec[3] = Painter.line(cBridge.withCap(Cap.ROUND)).when(p) ;
			paintersSpec[4] = Painter.line(wi/2, cc, Cap.BUTT, Join.ROUND, new float[] {2*wi, 2*wi}).when(p) ;
		}

	}
	
}
