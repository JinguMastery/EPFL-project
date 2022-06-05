package ch.epfl.imhof;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public final class Attributes {     // classe des ensembles d'attributs

	private final Map<String, String> attributes ;
	
	public Attributes(Map<String, String> attributes) {
		this.attributes = Collections.unmodifiableMap(new HashMap<String, String> (attributes)) ;
	}
	
	public boolean isEmpty() {   // Comme dit dans les instructions de l'etape 3, on ne fait rien de plus que de mettre
								 // les methodes predefinies de la table associative privee correspondantes
		return attributes.isEmpty() ;
	}
	
	public boolean contains(String key) {   // idem
		return attributes.containsKey(key) ;
	}
	
	public String get(String key) {   // idem
		return attributes.get(key) ;
	}
	
	public String get(String key, String defaultValue) {   // idem
		return attributes.getOrDefault(key, defaultValue) ;
	}
	
	public int get(String key, int defaultValue) {   // cette fois-ci, on doit en plus s'assurer que la valeur de la cle est un entier
		int n ;
		try {
			n = Integer.parseInt(attributes.get(key)) ;
		}
		catch (NumberFormatException exc) {   // retourne "defaultValue" si la valeur associee a la cle n'est pas un entier
											  // ou si elle n'existe pas ou encore si la cle elle-meme n'est pas presente
			return defaultValue ;
		}
		return n ;
	}
	
	public Attributes keepOnlyKeys(Set<String> keysToKeep) {   // ici, on ne garde que les attributs dont le nom
															   // figure dans l'ensemble passe en parametre
		Map <String, String> map = new HashMap <String, String> () ;
		for (String str : attributes.keySet())
			if (keysToKeep.contains(str))
				map.put(str, attributes.get(str)) ;
		return new Attributes(map) ;
	}
	
	public static final class Builder {        // bâtisseur d'attributs
		
		private final Map<String, String> attributes ;
		
		public Builder() {
			attributes = new HashMap<String, String> () ;
		}
		
		public void put(String key, String value) {   // associe la valeur "value" a la cle "key" dans l'ensemble
													  // d'attributs en cours de construction
			attributes.put(key, value) ;
		}
		
		public Attributes build() {   // construit une copie de l'ensemble d'attributs courant
			return new Attributes(attributes) ;
		}
		
	}
	
}
