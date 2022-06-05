package ch.epfl.imhof;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

import javax.imageio.ImageIO;

import org.xml.sax.SAXException;

import ch.epfl.imhof.dem.Earth;
import ch.epfl.imhof.dem.HGTDigitalElevationModel;
import ch.epfl.imhof.dem.ReliefShader;
import ch.epfl.imhof.geometry.Point;
import ch.epfl.imhof.osm.OSMMapReader;
import ch.epfl.imhof.osm.OSMToGeoTransformer;
import ch.epfl.imhof.painting.Color;
import ch.epfl.imhof.painting.Filters;
import ch.epfl.imhof.painting.Java2DCanvas;
import ch.epfl.imhof.painting.Painter;
import ch.epfl.imhof.projection.CH1903Projection;

public class Main {
	
	private final static double RADIUS = 1.7 ; 
	
	public static void main(String[] args) throws IOException, SAXException {
		
		CH1903Projection p = new CH1903Projection() ;
		
		System.out.println("1");
		OSMToGeoTransformer transformer = new OSMToGeoTransformer(p) ;
		
		System.out.println("2");
		Map map = transformer.transform(OSMMapReader.readOSMFile(args[0], true)) ;
		
		System.out.println("3");
		HGTDigitalElevationModel hgt = new HGTDigitalElevationModel(new File(args[1])) ;
		
		System.out.println("4");
		ReliefShader reliefS = new ReliefShader(p, hgt, new Vector3(-1., 1., 1.)) ;
		
		double lat1 = Math.toRadians(Double.parseDouble(args[3])), lat2 = Math.toRadians(Double.parseDouble(args[5])) ;
		Point pBL = p.project(new PointGeo(Math.toRadians(Double.parseDouble(args[2])), lat1)),
				pTR = p.project(new PointGeo(Math.toRadians(Double.parseDouble(args[4])), lat2)) ;
		int l, h, res ;
		res = Integer.parseInt(args[6]) ;
		h = (int)Math.round(res * 39.37/25000 * (lat2-lat1) * Earth.RADIUS) ;
		l = (int)Math.round(((pTR.x()-pBL.x()) / (pTR.y()-pBL.y()) * h)) ;
		
		System.out.println("5");
		Java2DCanvas canvas = new Java2DCanvas(pBL, pTR, l, h, res, Color.WHITE) ;
		
		System.out.println("6");
		SwissPainter.painter().drawMap(map, canvas) ;
		
		BufferedImage flatMap, relief, reliefMap ;
		flatMap = canvas.image() ;
		relief = reliefS.shadedRelief(pBL, pTR, l, h, RADIUS*.039370*res) ;
		reliefMap = new BufferedImage(l, h, BufferedImage.TYPE_INT_RGB) ;
		Color c1, c2 ;
		for (int i = 0 ; i < h ; i++)
			for (int j = 0 ; j < l ; j++) {
				c1 = Color.rgb(flatMap.getRGB(j, i)) ;
				c2 = Color.rgb(relief.getRGB(j, i)) ;
				reliefMap.setRGB(j, i, Color.convert(Color.multiply(c1, c2)).getRGB()) ;
			}
		
		System.out.println("7");
		ImageIO.write(flatMap, "png", new File("flat.png")) ;
		ImageIO.write(relief, "png", new File("relief.png")) ;
		ImageIO.write(reliefMap, "png", new File(args[7])) ;
		

		Predicate<Attributed<?>> isLake =
				Filters.tagged("natural", "water");
		Painter lakesPainter =
				Painter.polygon(Color.BLUE).when(isLake);

		Predicate<Attributed<?>> isBuilding =
				Filters.tagged("building");
		Painter buildingsPainter =
				Painter.polygon(Color.BLACK).when(isBuilding);

		Painter painter = buildingsPainter.above(lakesPainter);

		/*
		CH1903Projection p = new CH1903Projection() ;
		
		System.out.println("1");
		OSMToGeoTransformer transformer = new OSMToGeoTransformer(p) ;
		
		System.out.println("2");
		Map map = transformer.transform(OSMMapReader.readOSMFile(args[0], true)) ;

		// La toile
		Point bl = new Point(532510, 150590);
		Point tr = new Point(539570, 155260);
		Java2DCanvas canvas =
				new Java2DCanvas(bl, tr, 800, 530, 72, Color.WHITE);

		// Dessin de la carte et stockage dans un fichier
		painter.drawMap(map, canvas);
		ImageIO.write(canvas.image(), "png", new File("loz.png"));
		*/
	}

}
