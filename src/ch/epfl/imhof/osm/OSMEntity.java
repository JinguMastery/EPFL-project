package ch.epfl.imhof.osm;

import ch.epfl.imhof.Attributes;


public abstract class OSMEntity {       // classe abstraite des entités OSM quelconques

	private final long id ;
	private final Attributes attributes ;
	
	public OSMEntity(long id, Attributes attributes) {
		this.id = id ;
		this.attributes = attributes ;
	}
	
	public long id() {   // simple methode de retour pour un attribut de la classe
		return id ;
	}
	
	public Attributes attributes() {   // idem
		return attributes ;
	}
	
	public boolean hasAttribute(String key) {   // ici, on fait appel a une methode de la classe Attributes
		return attributes.contains(key) ;
	}
	
	public String attributeValue(String key) {   // idem
		return attributes.get(key) ;
	}
	
	public static abstract class Builder {     // bâtisseur d'entités OSM
		
		private final long id ;
		private final Attributes.Builder b ;
		private boolean isIncomplete ;
		
		public Builder(long id) {
			this.id = id ;
			b = new Attributes.Builder() ;
		}
		
		public void setAttribute(String key, String value) {   // on appelle la méthode "put" du Builder de la classe "Attributes"                                               
			b.put(key, value) ;
		}
		
		public void setIncomplete() {         // on indique que l'attribut est incomplet (par exemple valeur inexistante d'une clé, ou invalide)
			isIncomplete = true ;
		}
		
		public boolean isIncomplete() {       // retourne vrai si la méthode ci-dessus a été appelée au moins une fois (donc si incomplet)
			return isIncomplete ;
		}

		public long getId() {
			return id;
		}

		public Attributes.Builder getB() {
			return b;
		}
		
	}
		
}
