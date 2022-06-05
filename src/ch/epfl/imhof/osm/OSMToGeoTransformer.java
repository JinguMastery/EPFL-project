package ch.epfl.imhof.osm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ch.epfl.imhof.Attributed;
import ch.epfl.imhof.Attributes;
import ch.epfl.imhof.Graph;
import ch.epfl.imhof.Map;
import ch.epfl.imhof.geometry.ClosedPolyLine;
import ch.epfl.imhof.geometry.PolyLine;
import ch.epfl.imhof.geometry.Polygon;
import ch.epfl.imhof.osm.OSMRelation.Member;
import ch.epfl.imhof.projection.Projection;


public final class OSMToGeoTransformer {        // classe des convertisseurs de carte OSM en carte non OSM

    private final Projection projection ;

    public OSMToGeoTransformer(Projection projection) {
        this.projection = projection ;
    }

    public Map transform(OSMMap map) {      // transforme la carte OSM spécifiée en carte non OSM
        String area ;
        boolean hasAttribute ;
        Set<String> keysPolyLine = new HashSet<String> (), keysPolygon = new HashSet<String> () ;
        String[] tabAtt = {"aeroway", "amenity", "building", "harbour", "historic",
                "landuse", "leisure", "man_made", "military", "natural",
                "office", "place", "power", "public_transport", "shop",
                "sport", "tourism", "water", "waterway", "wetland"} ;
        String[] tabStr1 = {"bridge", "highway", "layer", "man_made", "railway", "tunnel", "waterway"} ;
        String[] tabStr2 = {"building", "landuse", "layer", "leisure", "natural", "waterway"} ;
        keysPolyLine.addAll(Arrays.asList(tabStr1)) ;
        keysPolygon.addAll(Arrays.asList(tabStr2)) ;
        Map.Builder bMap = new Map.Builder() ;      // on construit la bâtisseur auquel on va ajouter peu à peu les entités de la carte OSM
        PolyLine.Builder bPolyLine ;
        Attributes attFiltred ;
        for (OSMWay way : map.ways()) {     // on itère d'abord sur les chemins OSM, plus tard on itère sur les relations.
            bPolyLine = new PolyLine.Builder() ;
            for (OSMNode node : way.nonRepeatingNodes())    // ici on itère sur les noeuds composant chacun des chemins
            	bPolyLine.addPoint(projection.project(node.position())) ;   // convertit le noeud OSM en un point selon le type de projection du convertisseur, puis l'ajoute au bâtisseur de la polyligne courante
            if (way.isClosed()) {    // il s'agit donc soit d'une future polyligne fermée, ou d'un futur polygone sans trou
                hasAttribute = false ;
                for (String str : tabAtt)
                    if (way.attributes().contains(str))     // on vérifie si l'un des attributs du chemin courant décrit une surface (si oui on l'indique en changeant la valeur du booléen hasAttribute)
                        hasAttribute = true ;
                area = way.attributes().get("area") ;
                if (area != null && (area.equals("1") || area.equals("yes") || area.equals("true") || hasAttribute)) {    // On regarde si la clé "area" a la valeur associée "yes", "true" ou "1". Si la condition est vraie, il s'agit d'une surface, donc d'un polygone sans trous et non une polyligne fermée.
                    attFiltred = way.attributes().keepOnlyKeys(keysPolygon) ;  // on garde seulement les attributs relatifs au polygone
                    if (attFiltred != null)
                        bMap.addPolygon(new Attributed<Polygon> (new Polygon(bPolyLine.buildClosed()), attFiltred)) ; // on ajoute au bâtisseur de la carte un polygone attribué à partir du bâtisseur de la polyligne courante 
                    else
                    	bMap.addPolyLine(new Attributed<PolyLine> (bPolyLine.buildClosed(), attFiltred)) ;
                }
                else {          // ici il s'agit donc de l'autre cas, ce n'est pas une surface donc on va construire une polyligne attribuée fermée
                    attFiltred = way.attributes().keepOnlyKeys(keysPolyLine) ;
                    if (attFiltred != null)
                        bMap.addPolyLine(new Attributed<PolyLine> (bPolyLine.buildClosed(), attFiltred));  
                }
            }
            else {  // ici le chemin n'est pas fermé, donc on va construire une polyligne attribuée ouverte
                attFiltred = way.attributes().keepOnlyKeys(keysPolyLine) ;
                if (attFiltred != null)
                    bMap.addPolyLine(new Attributed<PolyLine> (bPolyLine.buildOpen(), attFiltred)) ;
            }
        }
        for (OSMRelation relation : map.relations()) {        // on itère à présent sur les relations, rappelons que les seules qui nous intéressent sont les multipolygones
            attFiltred = relation.attributes().keepOnlyKeys(keysPolygon) ;
            if (attFiltred != null)
                for (Attributed<Polygon> attributedPolygon : assemblePolygon(relation, attFiltred))      // ici, on ajoute au bâtisseur de la carte chacun des polygones attribués construits à l'aide de la méthode assemblePolygon
                    bMap.addPolygon(attributedPolygon) ; 
        }
        return bMap.build() ;       // maintenant qu'on a itéré sur tous les chemins et toutes les relations, on peut enfin construire la carte avec son bâtisseur et la retourner
    }

    private List<ClosedPolyLine> ringsForRole(OSMRelation relation, String role) {
        Graph.Builder<OSMNode> bGraph = new Graph.Builder<OSMNode> () ;     // on crée d'abord le graphe, puis on relie ses points
        for (Member m : relation.members())
            if (m.role().equals(role)) {     // on ajoute au graphe seulement les membres du bon rôle
            	List<OSMNode> listNodes = null ;
            	if (!(m.member() instanceof OSMWay))
            		return new ArrayList<ClosedPolyLine> () ;
            	listNodes = ((OSMWay)m.member()).nodes() ;
                bGraph.addNode(listNodes.get(0)) ;
                for (int i = 1 ; i < listNodes.size() ; i++) {
                    bGraph.addNode(listNodes.get(i)) ;
                    bGraph.addEdge(listNodes.get(i-1), listNodes.get(i));
                }
                bGraph.addEdge(listNodes.get(0), listNodes.get(listNodes.size()-1)) ;
            }
        Graph<OSMNode> g = bGraph.build() ;
        ArrayList<ClosedPolyLine> closedPolyLines = new ArrayList<ClosedPolyLine> () ;
        ArrayList<OSMNode> visitedNodes = new ArrayList<OSMNode> () ;  // cette liste va s'agrandir au fur et à mesure ; on lui ajoute tous les noeuds déjà reliés du graphe (qui peuvent être d'anneaux différents)
        PolyLine.Builder bPolyLine ;
        Iterator<OSMNode> it ;
        OSMNode node1, node2, anteriorNode ;        // sera utilisé lors d'une boucle for plus bas ; noeud1 et noeud2 sont les deux voisins de anteriorNode
        boolean ringEnded ;     // devient vrai quand l'anneau est fini
        for (OSMNode node : g.nodes())     // on itère sur tous les noeuds du graphe
            if (!visitedNodes.contains(node)) {   // si un des noeuds n'est pas encore visité, alors ça veut dire qu'il reste au moins un anneau à former
                ringEnded = false ;
                visitedNodes.add(node) ;  // on ajoute systématiquement les noeuds visités à cette liste
                anteriorNode = node ;
                bPolyLine = new PolyLine.Builder() ;      // bâtisseur servant à créer l'anneau
                bPolyLine.addPoint(projection.project(node.position())) ;
                do {
                    if (g.neighborsOf(anteriorNode).size() != 2)  // on doit ignorer le cas où le noeud n'a pas exactement deux voisins
                        return new ArrayList<ClosedPolyLine> () ;
                    it = g.neighborsOf(anteriorNode).iterator() ;
                    node1 = it.next() ;        // node1 est donc le premier voisin de anteriorNode, node2 le deuxième
                    node2 = it.next() ;
                    if (visitedNodes.contains(node1))
                        if (visitedNodes.contains(node2))
                            ringEnded = true ;      // c'est le cas où les deux voisins sont visités => fin de l'anneau
                        else {  // cas où node2 est non visité, on l'ajoute à l'anneau et devient le nouveau noeud antérieur
                            bPolyLine.addPoint(projection.project(node2.position())) ;
                            visitedNodes.add(node2) ;
                            anteriorNode = node2 ;
                        }
                    else {  // cas où node1 est non visité, on l'ajoute à l'anneau et devient le nouveau neaud antérieur
                        bPolyLine.addPoint(projection.project(node1.position())) ;
                        visitedNodes.add(node1) ;
                        anteriorNode = node1 ;
                    }
                } while (!ringEnded) ;    // on continue de relier des points tant que l'anneau est non fini
                closedPolyLines.add(bPolyLine.buildClosed()) ; // on ajoute l'anneau formé à la liste de polylignes fermées
            }
        return closedPolyLines ;
    }

    private List<Attributed<Polygon>> assemblePolygon(OSMRelation relation, Attributes attributes) {
        List<ClosedPolyLine> outerRings, innerRings ;
        outerRings = ringsForRole(relation, "outer") ;
        innerRings = ringsForRole(relation, "inner") ;
        Collections.sort(outerRings, new Comparator<ClosedPolyLine> () {    // on trie l'ensemble des anneaux extérieurs par aire croissante

            @Override
            public int compare(ClosedPolyLine o1, ClosedPolyLine o2) {  // on redéfinit la méthode compare du comparateur de sorte à ce qu'il compare deux anneaux par aire croissante
                if (o1.area() > o2.area())
                    return 1 ;
                else
                    if (o1.area() == o2.area())
                        return 0 ;
                    else
                        return -1 ;
            }

        }) ;
        ArrayList<Attributed<Polygon>> attributedPolygons = new ArrayList<Attributed<Polygon>> () ;
        ArrayList<ClosedPolyLine> holes = new ArrayList<ClosedPolyLine> () ;
        ArrayList<ClosedPolyLine> visitedHoles = new ArrayList<ClosedPolyLine> () ;
        for (ClosedPolyLine shell : outerRings) {   // on itère sur les anneaux extérieurs puis sur ceux intérieurs
            holes.clear() ;
            for (ClosedPolyLine hole : innerRings)
                if (!visitedHoles.contains(hole))
                    if (shell.containsPoint(hole.firstPoint())) {   // si un des points du trou (ici le premier) est contenu dans l'enveloppe et qu'il n'est pas encore visité, alors on ajoute le trou à la liste
                        holes.add(hole) ;
                        visitedHoles.add(hole) ;
                    }
            attributedPolygons.add(new Attributed<Polygon> (new Polygon(shell, holes), attributes)) ;  // on ajoute donc à l'ensemble de polygones attribués le polygone formé à partir de l'enveloppe, de la liste de trous et de l'ensemble d'attributs
        }
        return attributedPolygons ;
    }

}
