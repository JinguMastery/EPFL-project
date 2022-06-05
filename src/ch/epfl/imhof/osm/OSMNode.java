package ch.epfl.imhof.osm;
import ch.epfl.imhof.Attributes;
import ch.epfl.imhof.PointGeo;


public final class OSMNode extends OSMEntity {      // classe des noeuds OSM

	private final PointGeo position ;
	
	public OSMNode(long id, PointGeo position, Attributes attributes) {
		super(id, attributes) ;
		this.position = position ;
	}
	
	public PointGeo position() {           // retourne la position géométrique du point
		return position ;
	}
	
	public static final class Builder extends OSMEntity.Builder {      // bâtisseur de noeuds OSM
		
		private PointGeo position ;
		
		public Builder(long id, PointGeo position) {
			super(id) ;
			this.position = position ;
		}
		
		public OSMNode build() {   // construit un noeud OSM à partir de l'identifiant, la position et l'ensemble
		                           // d'attributs courants
			if (super.isIncomplete())
				throw new IllegalStateException() ;
			return new OSMNode(getId(), position, getB().build()) ;
		}
		
	}
	
}
