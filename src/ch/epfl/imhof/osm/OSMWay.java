package ch.epfl.imhof.osm;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.imhof.Attributes;


public final class OSMWay extends OSMEntity {       // classe des chemins OSM

	private final ArrayList<OSMNode> nodes ;
	
	public OSMWay(long id, List<OSMNode> nodes, Attributes attributes) {
		super(id, attributes) ;
		if (nodes.size() < 2)
			throw new IllegalArgumentException() ;
		this.nodes = new ArrayList<OSMNode> (nodes) ;
	}
	
	public int nodesCount() {    // retourne la taille de la liste de noeuds, càd le nombre de points qui composent le chemin
		return nodes.size() ;
	}
	
	public List<OSMNode> nodes() {     // retourne la liste de noeuds courante
		return Collections.unmodifiableList(nodes) ;
	}
	
	public List<OSMNode> nonRepeatingNodes() {       // dans le cas où le premier et le dernier noeud sont les mêmes, on enlève le dernier
		ArrayList<OSMNode> list = new ArrayList<OSMNode> (nodes) ;
		if (isClosed())
			list.remove(nodesCount()-1) ;
		return list ;
	}
	
	public OSMNode firstNode() {           // retourne le premier noeud
		return nodes.get(0) ;
	}
	
	public OSMNode lastNode() {            // ici le dernier
		return nodes.get(nodesCount()-1) ;
	}
	
	public boolean isClosed() {            //retourne vrai si la liste correspond à une polyligne fermée (càd premier et dernier noeud égaux)
		return firstNode().equals(lastNode()) ;
	}
	
	public static final class Builder extends OSMEntity.Builder {      // bâtisseur de chemins OSM
		
		private final ArrayList<OSMNode> nodes ;
		
		public Builder(long id) {
			super(id) ;
			nodes = new ArrayList<OSMNode> () ;
		}
		
		public void addNode(OSMNode newNode) {    //ajoute un noeud à la liste de noeuds du bâtisseur
			if (newNode != null)
				nodes.add(newNode) ;
		}
		
		public OSMWay build() {   // construit un chemin OSM à partir de l'identifiant, la liste de noeuds
		                          // et l'ensemble d'attributs courants. Dans le cas où le chemin est incomplet, on lance une exception
			if (isIncomplete())
				throw new IllegalStateException() ;
			return new OSMWay(getId(), nodes, getB().build()) ;
		}
		
		public boolean isIncomplete() {           // en plus de vérifier si on a déclaré l'entité incomplète avec le Builder d'OSMEntity,
			return (nodes.size() < 2 || super.isIncomplete()) ; // on vérifie que le chemin est composé d'au moins 2 noeuds
		}
		
	}
	
}
