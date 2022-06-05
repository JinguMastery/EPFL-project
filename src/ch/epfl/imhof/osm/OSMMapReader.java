package ch.epfl.imhof.osm;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.zip.GZIPInputStream;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import ch.epfl.imhof.PointGeo;
import ch.epfl.imhof.osm.OSMRelation.Member.Type;


public final class OSMMapReader {       // classe des lecteurs de cartes OSM

    private static OSMMap map ;

    private OSMMapReader() {
    }

    public static OSMMap readOSMFile(String fileName, boolean unGZip) throws IOException, SAXException {        // méthode construisant une carte OSM à partir d'un fichier OSM 
    	InputStream i ;                                                                                         // préalablement décompressé si besoin est (si unGZip est true)
    	if (unGZip)
    		i = new GZIPInputStream(new FileInputStream(fileName)) ;     // décompresse le fichier si unGZip est true
    	else
    		i = new FileInputStream(fileName) ;       // pas de décompression ici
    	XMLReader r = XMLReaderFactory.createXMLReader();
    	r.setContentHandler(new DefaultHandler() {    // on définit une classe anonyme redéfinissant les méthodes startElement et endElement

    		private OSMMap.Builder bMap = new OSMMap.Builder() ;
    		private OSMWay.Builder bWay ;
    		private OSMRelation.Builder bRelation ;
    		private OSMNode.Builder bNode ;
    		private boolean isWay, isRelation, isNode ;

    		@Override
    		public void startElement(String uri, String lName, String qName, Attributes atts) throws SAXException {     // méthode appelée lorsque le lecteur rencontre une balise ouvrante XML
    			long id = 0 ;                                                                                           // Le principe est d'ajouter peu à peu les entités OSM rencontrées au bâtisseur de la carte
    			boolean hasId = true ;
    			try {
    				id = Long.parseLong(atts.getValue("id")) ;   // on obtient la valeur de la clé id (l'identifiant) et on le convertir de String en long
    			}
    			catch (NumberFormatException exc) {      // si cela a échoué, on indique que l'entité n'a pas d'id à l'aide du booléen hasId déclaré vrai au début de startElement
    				hasId = false ;
    			}
    			switch(qName) {      // la stratégie ici est d'identifier à quel élément nous avons affaire (à l'aide d'un bloc switch), et d'agir en conséquence

    			case "node" :
    				double x = 0, y = 0 ;
    				boolean hasPosition = true ;
    				try {
    					x = Double.parseDouble(atts.getValue("lon")) ;
    					y = Double.parseDouble(atts.getValue("lat")) ;
        				bNode = new OSMNode.Builder(id, new PointGeo(Math.toRadians(x), Math.toRadians(y))) ;   // on crée un bâtisseur pour notre noeud et lui ajoutons le bon identifiant et la bonne position
    				}
    				catch (NumberFormatException exc) {         
    					hasPosition = false ;
    				}                                       // dans les deux cas ça signifie une position invalide, et on l'indique en changeant le booléen hasPosition initialement vrai
    				catch (IllegalArgumentException exc) {
    					hasPosition = false ;
    				}
    				if (!hasId || !hasPosition)       // dans le cas où il y a un problème d'identifiant ou de position, on indique au bâtisseur que le noeud est incomplet
    					bNode.setIncomplete() ;
    				if (!bNode.isIncomplete())
    					bMap.addNode(bNode.build()) ;   // si on a lancé setIncomplete(), cela lance une exception. Si non, le noeud est ajouté au bâtisseur de la carte.
    				isNode = true ;
    				break ;

    			case "way" :
    				bWay = new OSMWay.Builder(id) ;  //création d'un bâtisseur de chemin ayant le bon identifiant
    				if (!hasId)
    					bWay.setIncomplete() ;      // si il y a un problème d'identifiant on l'indique au bâtisseur
    				isWay = true ;                     // sera utile pour les tags, on indique que l'élément actuellement ouvert du fichier est un chemin (et non une relation par exemple)
    				break ;

    			case "nd" :
    				bWay.addNode(bMap.nodeForId(Long.parseLong(atts.getValue("ref")))) ;     // on ajoute au bâtisseur du chemin en cours de construction le noeud d'identifiant correspondant 
    				break ;                                                                     // (rappelons que les "nd" sont toujours dans des chemins dans le fichier OSM)

    			case "relation" :
    				bRelation = new OSMRelation.Builder(id) ;     // on crée un bâtisseur de relation et lui ajoutons le bon identifiant
    				if (!hasId)
    					bRelation.setIncomplete() ;        // si problème d'identifiant on l'indique au bâtisseur
    				isRelation = true ;     // sera utile pour les tags
    				break ;

    			case "member" :
    				OSMEntity entite = null ;
    				Type type = null ;
    				long ref = Long.parseLong(atts.getValue("ref")) ;     // même principe que vu plus haut, mais cette fois avec ref plutôt qu'id
    				switch(atts.getValue("type")) {     // il nous faut un bloc switch pour agir différemment selon le type du membre courant
    				case "node" :
    					entite = bMap.nodeForId(ref) ;    // retourne le noeud précédemment stocké d'identifiant correspondant à "ref"
    					type = Type.NODE ;                // on indique le type
    					break ;
    				case "way" :                        // comme plus haut
    					entite = bMap.wayForId(ref) ;
    					type = Type.WAY ;
    					break ;
    				case "relation" :                       // comme plus haut
    					entite = bMap.relationForId(ref) ;
    					type = Type.RELATION ;
    					break ;
    				default :
    					throw new SAXException() ;
    				}
    				if (entite != null)
    					bRelation.addMember(type, atts.getValue("role"), entite) ;      // ajoute le membre au bâtisseur de la relation courante
    				break ;

    			case "tag" :
    				String key = atts.getValue("k"), value = atts.getValue("v") ;
    				if (isWay)
    					bWay.setAttribute(key, value) ;
    				if (isRelation)
    					bRelation.setAttribute(key, value) ;
    				if (isNode)
    					bNode.setAttribute(key, value) ;
    				
    			}
    		}

    		@Override
    		public void endElement(String uri,        // se lance lorsque le lecteur rencontre une balise fermante (par exemple fin d'un chemin ou d'une relation)
    				String lName,
    				String qName) {
    			switch(qName) {

    			case "way" :
    				if (!bWay.isIncomplete())
    					bMap.addWay(bWay.build()) ;      // on construit un chemin maintenant qu'on le sait achevé, et l'ajoutons directement au bâtisseur de carte
    				break ;

    			case "relation" :
    				if (!bRelation.isIncomplete())
    					bMap.addRelation(bRelation.build()) ;  // idem pour la relation achevée
    				break ;

    			case "osm" :                     // moyen de savoir que le fichier est fini ; on peut construire la carte avec le bâtisseur
    				map = bMap.build() ;
    			}
    		}

    	});
    	r.parse(new InputSource(i));

    	return map ;            // fin de la méthode readOSMFile, on retourne finalement la carte construite
    }

}