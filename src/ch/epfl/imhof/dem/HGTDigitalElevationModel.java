package ch.epfl.imhof.dem;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import ch.epfl.imhof.PointGeo;
import ch.epfl.imhof.Vector3;


public class HGTDigitalElevationModel implements DigitalElevationModel {

	private final short[][] alts ;
	private final int res ;
	private final PointGeo pBL ;
	private final DataInputStream inStrm ;

	public HGTDigitalElevationModel(File file) throws IOException {
		String str = file.getName() ;
		if (file.length() == 0 || Math.sqrt(file.length()/2.) % 1 != 0. || !str.substring(7).equals(".hgt"))
			throw new IllegalArgumentException() ;
		int lat = Integer.parseInt(str.substring(1, 3)), lon = Integer.parseInt(str.substring(4, 7)) ;
		if (str.charAt(0) != 'N') {
			if (str.charAt(0) == 'S')
				lat = -lat ;
			else
				throw new IllegalArgumentException() ;
		}
		if (str.charAt(3) != 'E') {
			if (str.charAt(3) == 'W')
				lon = -lon ;
			else
				throw new IllegalArgumentException() ;
		}
		pBL = new PointGeo(Math.toRadians(lon), Math.toRadians(lat)) ;
		res = (int)Math.sqrt(file.length()/2.) ;
		alts = new short[res][res] ;
		inStrm = new DataInputStream(new FileInputStream(file)) ;
		for (int i = 0 ; i < res ; i++)
			for (int j = 0 ; j < res ; j++)
				alts[i][j] = inStrm.readShort() ;
	}
	
	@Override
	public void close() throws Exception {
		inStrm.close() ;
	}
	
	@Override
	public Vector3 normalAt(PointGeo pt) {
		if (pt.latitude() < pBL.latitude() || pt.latitude() > pBL.latitude()+1
				|| pt.longitude() < pBL.longitude() || pt.longitude() > pBL.longitude()+1)
			throw new IllegalArgumentException() ;
		double x = res * Math.abs(pt.longitude() - pBL.longitude()), y = res * Math.abs(pt.latitude() - pBL.latitude()) ;
		int c1 = (int)x, l1 = res - (int)y, c2, l2 ;
		c2 = c1 + 1 ;
		if (x % 1 == 0.)
			c1 -= 1 ;
		l2 = l1 - 1 ;
		if (y % 1 == 0.)
			l1 += 1 ;
		double s = (double)Earth.RADIUS / res ;
		return new Vector3(s/2 * (alts[l1][c1]-alts[l1][c2]+alts[l2][c1]-alts[l2][c2]),
				s/2 * (alts[l1][c1]+alts[l1][c2]-alts[l2][c1]-alts[l2][c2]), s*s) ;
	}

}
