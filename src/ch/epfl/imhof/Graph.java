package ch.epfl.imhof;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public final class Graph <N> {      // classe des graphes

    private final Map<N, Set<N>> map ;
    
    public Graph(Map<N, Set<N>> neighbors) {
        map = Collections.unmodifiableMap(new HashMap<N, Set<N>> (neighbors)) ;
    }
    
    public Set<N> nodes() {     // retourne l'ensemble de noeuds du graphe
        return Collections.unmodifiableSet(map.keySet()) ;
    }
    
    public Set<N> neighborsOf(N node) {     // retourne les noeuds voisins du noeud donné ; lance une exception si le noeud n'appartient pas au graphe
        if (!map.containsKey(node))
            throw new IllegalArgumentException() ;
        return map.get(node) ;
    }
    
    public static final class Builder <N> {     // bâtisseur des graphes

        private final Map<N, Set<N>> map ;
        
        public Builder() {
        	map = new HashMap<N, Set<N>> () ;
        }
        
        public void addNode(N n) {      // ajoute le noeud donné au bâtisseur du graphe
        	if (!map.containsKey(n)) {
        		HashSet <N> set = new HashSet <N> () ;
        		map.put(n, set) ;
        	}
        }
        
        public void addEdge(N n1, N n2) {       // établit que deux noeuds sont voisins l'un de l'autre ; lance une exception si l'un des deux noeuds n'appartient pas au bâtisseur
            if (!map.containsKey(n1) || !map.containsKey(n2))
                throw new IllegalArgumentException() ;
            map.get(n1).add(n2) ;
            map.get(n2).add(n1) ;
        }
        
        public Graph<N> build() {       // construit le graphe à partir de l'ensemble de noeuds
            return new Graph<N> (map) ;
        }

    }
    
}
