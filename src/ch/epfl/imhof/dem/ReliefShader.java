package ch.epfl.imhof.dem;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import javax.imageio.ImageIO;

import ch.epfl.imhof.Vector3;
import ch.epfl.imhof.geometry.Point;
import ch.epfl.imhof.painting.Color;
import ch.epfl.imhof.projection.Projection;


public class ReliefShader {

	private final Projection projection ;
	private final HGTDigitalElevationModel model ;
	private final Vector3 source ;
	
	public ReliefShader(Projection p, HGTDigitalElevationModel hgt, Vector3 v) {
		projection = p ;
		model = hgt ;
		source = v ;
	}
	
	public BufferedImage shadedRelief(Point pBL, Point pTR, int l, int h, double r) {
		int n = (int)Math.ceil(r) ;
		BufferedImage buffIm1 = rawRelief(l+2*n, h+2*n, Point.alignedCoordinateChanged(new Point(n, h+n), pBL, new Point(l+n, n), pTR)) ;
		/*
		try {
			ImageIO.write(buffIm1, "png", new File("sRelief.png")) ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		if (r == 0.)
			return buffIm1 ;
		float[] tabF = kernel(r) ;
		BufferedImage buffIm2, buffIm3 ;
		buffIm2 = blur(buffIm1, new Kernel(tabF.length, 1, tabF)) ;
		buffIm3 = blur(buffIm2, new Kernel(1, tabF.length, tabF)) ;
		return buffIm3.getSubimage(n, n, l, h) ;
	}
	
	private BufferedImage rawRelief(int l, int h, Function<Point, Point> fct) {
		BufferedImage buffIm = new BufferedImage(l, h, BufferedImage.TYPE_INT_RGB) ;
		Vector3 v ;
		double x ;
		for (int i = 0 ; i < h ; i++)
			for (int j = 0 ; j < l ; j++) {
				v = model.normalAt(projection.inverse(fct.apply(new Point(j, i)))) ;
				x = source.scalarProduct(v) / (source.norm()*v.norm()) ;
				buffIm.setRGB(j, i, Color.convert(Color.rgb((x+1)/2, (x+1)/2, (.7*x+1)/2)).getRGB()) ;
			}
		return buffIm ;
	}
	
	private float[] kernel(double r) {
		int i, n = 2*(int)Math.ceil(r) + 1 ;
		float[] tabF = new float[n] ;
		double x = 0. ;
		for (i = 0 ; i < n ; i++) {
			tabF[i] = (float)Math.exp(-Math.pow((1-n)/2+i, 2) / (2/9.*r*r)) ;
			x += tabF[i] ;
		}
		for (i = 0 ; i < n ; i++)
			tabF[i] /= x ;
		return tabF ;
	}

	private BufferedImage blur(BufferedImage buffIm, Kernel k) {
		ConvolveOp conv = new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null) ;
		return conv.filter(buffIm, null) ;
	}

}
