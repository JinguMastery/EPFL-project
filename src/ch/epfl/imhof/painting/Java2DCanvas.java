package ch.epfl.imhof.painting;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Function;

import ch.epfl.imhof.geometry.ClosedPolyLine;
import ch.epfl.imhof.geometry.Point;
import ch.epfl.imhof.geometry.PolyLine;
import ch.epfl.imhof.geometry.Polygon;


public class Java2DCanvas implements Canvas {

	private static Function <Point, Point> fct ;
	private final BufferedImage buffIm ;
	private final Graphics2D g ;
	
	public Java2DCanvas(Point pBL, Point pTR, int l, int h, int res, Color c) {
		if (l < 0 || h < 0 || res < 0)
			throw new IllegalArgumentException() ;
		buffIm = new BufferedImage(l, h, BufferedImage.TYPE_INT_RGB) ;
		fct = Point.alignedCoordinateChanged(pBL, new Point(0, h*res/72.), pTR, new Point(l*res/72., 0)) ;
		g = buffIm.createGraphics() ;
		g.scale(res/72., res/72.) ;
		g.setColor(Color.convert(c)) ;
		g.fillRect(0, 0, l, h) ;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON) ;
	}
	
	public BufferedImage image() {
		return buffIm ;
	}

	@Override
	public void drawPolyLine(PolyLine polyLine, LineStyle style) {
		int nCap, nJoin ;
		switch (style.getCap()) {
		case BUTT :
			nCap = BasicStroke.CAP_BUTT ;
			break;
		case ROUND :
			nCap = BasicStroke.CAP_ROUND ;
			break;
		case SQUARE :
			nCap = BasicStroke.CAP_SQUARE ;
		default :
			throw new IllegalArgumentException() ;
		}
		switch(style.getJoin()) {
		case BEVEL :
			nJoin = BasicStroke.JOIN_BEVEL ;
			break;
		case MITER :
			nJoin = BasicStroke.JOIN_MITER ;
			break;
		case ROUND :
			nJoin = BasicStroke.JOIN_ROUND ;
			break;
		default :
			throw new IllegalArgumentException() ;
		}
		if (style.getDashingPattern().length == 0)
		    g.setStroke(new BasicStroke(style.getWidth(), nCap, nJoin, 10.f)) ;
		else
			g.setStroke(new BasicStroke(style.getWidth(), nCap, nJoin, 10.f, style.getDashingPattern(), 0.f)) ;
		g.setColor(Color.convert(style.getColor())) ;
		g.draw(Java2DCanvas.toPath(polyLine)) ;
	}

	@Override
	public void drawPolygon(Polygon polygon, Color font) {
		Area area = new Area(Java2DCanvas.toPath(polygon.shell())) ;
		for (ClosedPolyLine hole : polygon.holes())
            area.subtract(new Area(Java2DCanvas.toPath(hole))) ;
		g.setColor(Color.convert(font)) ;
		g.fill(area) ;
	}
	
	private static Path2D.Double toPath(PolyLine polyLine) {
		Path2D.Double line = new Path2D.Double() ;
		line.moveTo(fct.apply(polyLine.firstPoint()).x(), fct.apply(polyLine.firstPoint()).y()) ;
		List<Point> listPoints = polyLine.points() ;
		for (int i = 1 ; i < listPoints.size() ; i++)
			line.lineTo(fct.apply(listPoints.get(i)).x(), fct.apply(listPoints.get(i)).y()) ;
		if (polyLine.isClosed())
			line.closePath() ;
		return line ;
	}

}
