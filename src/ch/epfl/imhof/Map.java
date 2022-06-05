package ch.epfl.imhof;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.imhof.geometry.PolyLine;
import ch.epfl.imhof.geometry.Polygon;


public final class Map {        // classe des cartes (non OSM)
    
    private final List<Attributed<PolyLine>> polylines ;
    private final List<Attributed<Polygon>> polygons ;
    
    public Map(List<Attributed<PolyLine>> polyLines, List<Attributed<Polygon>> polygons) {
        polylines = Collections.unmodifiableList(new ArrayList<Attributed<PolyLine>> (polyLines)) ;
        this.polygons = Collections.unmodifiableList(new ArrayList<Attributed<Polygon>> (polygons)) ;
    }
    
    public List<Attributed<PolyLine>> polyLines() {     // retourne la liste des polylignes (fermées et ouvertes)
        return polylines ;
    }
    
    public List<Attributed<Polygon>> polygons() {       // retourne la liste des polygones 
        return polygons ;
    }
    
    public static class Builder {       // bâtisseur des cartes
        
        private List<Attributed<PolyLine>> polylines ;
        private List<Attributed<Polygon>> polygons ;
        
        public Builder() {
        	polylines = new ArrayList<Attributed<PolyLine>> () ;
        	polygons = new ArrayList<Attributed<Polygon>> () ;
        }
        
        public void addPolyLine(Attributed<PolyLine> newPolyLine) {     // ajoute la polyligne au bâtisseur
            polylines.add(newPolyLine) ;
        }
        
        public void addPolygon(Attributed<Polygon> newPolygon) {        // ajoute le polygone au bâtisseur
            polygons.add(newPolygon) ;
        }
        
        public Map build() {            // construit la carte à partir des éléments ajoutés précédemment
            return new Map(polylines, polygons) ;
        }
        
    }

}
