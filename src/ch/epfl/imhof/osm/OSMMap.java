package ch.epfl.imhof.osm;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public final class OSMMap {     // classe des cartes OSM

    private final List<OSMWay> ways ;
    private final List<OSMRelation> relations ;
    
    public OSMMap(Collection<OSMWay> ways, Collection<OSMRelation> relations) {
        this.ways = Collections.unmodifiableList(new ArrayList<OSMWay> (ways)) ;
        this.relations = Collections.unmodifiableList(new ArrayList<OSMRelation> (relations)) ;
    }
    
    public List<OSMWay> ways() {        // retourne la liste des chemins de la carte courante
        return ways ;
    }
    
    public List<OSMRelation> relations() {      // retourne ici la liste des relations de la carte
        return relations ;
    }
    
	public static final class Builder {         // bâtisseur de cartes OSM
        
        private HashMap<Long, OSMNode> nodes ;
        private HashMap<Long, OSMWay> ways ;
        private HashMap<Long, OSMRelation> relations ;
        private ArrayList<OSMWay> listWays ;
        private ArrayList<OSMRelation> listRelations ;
        
        public Builder() {
        	nodes = new HashMap<Long, OSMNode> () ;
        	ways = new HashMap<Long, OSMWay> () ;
        	relations = new HashMap<Long, OSMRelation> () ;
        	listWays = new ArrayList<OSMWay> () ;
        	listRelations = new ArrayList<OSMRelation> () ;
        }
        
        public void addNode(OSMNode newNode) {          // ajoute un noeud OSM à la liste de noeuds du bâtisseur
        	if (newNode != null)
        		nodes.put(newNode.id(), newNode) ;
        }
        
        public OSMNode nodeForId(long id) {         // retourne le noeud OSM d'identifiant spécifié
            return nodes.get(id) ;
        }
        
        public void addWay(OSMWay newWay) {     // ajoute un chemin à la liste de chemins du bâtisseur
        	if (newWay != null) {
        		ways.put(newWay.id(), newWay) ;
        		listWays.add(newWay) ;
        	}
        }
        
        public OSMWay wayForId(long id) {       // comme pour nodeForId mais pour les chemins
            return ways.get(id) ;
        }
        
        public void addRelation(OSMRelation newRelation) {      // ajoute une relation à la liste de relations du bâtisseur
        	if (newRelation != null) {
        		relations.put(newRelation.id(), newRelation) ;
        		listRelations.add(newRelation) ;
        	}
        }
        
        public OSMRelation relationForId(long id) {         // comme pour nodeForId mais pour les relations
            return relations.get(id) ;
        }
        
        public OSMMap build() {         // contruit une carte OSM à partir des trois listes d'entités OSM du bâtisseur
            return new OSMMap(listWays, listRelations) ;
        }
        
    }
    
}
