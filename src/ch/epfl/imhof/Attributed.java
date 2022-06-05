package ch.epfl.imhof;


public final class Attributed <T> {     // classe associant un ensemble d'attribut à une valeur de type T
	
	private final T value ;
	private final Attributes attributes ;
	
	public Attributed(T value, Attributes attributes) {
		this.value = value ;
		this.attributes = attributes ;
	}
	
	public T value() {   // retourne la valeur a laquelle les attributs sont attaches
		return value ;
	}
	
	public Attributes attributes() {   // retourne l'ensemble d'attributs attache a la valeur
		return attributes ;
	}
	
	public boolean hasAttribute(String attributeName) {   // ici, on definit des methodes similaires a celles de la classe "Attributes"
		return attributes.contains(attributeName) ;
	}
	
	public String attributeValue(String attributeName) {      // voir plus haut
		return attributes.get(attributeName) ;
	}
	
	public String attributeValue(String attributeName, String defaultValue) {          // voir plus haut
		return attributes.get(attributeName, defaultValue) ;
	}
	
	public int attributeValue(String attributeName, int defaultValue) {            // voir plus haut
		return attributes.get(attributeName, defaultValue) ;
	}

}
