package ch.epfl.imhof.osm;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.imhof.Attributes;


public final class OSMRelation extends OSMEntity {      // classe des relations osm

	private final List<Member> members ;
	
	public OSMRelation(long id, List<Member> members, Attributes attributes) {
		super(id, attributes) ;
		this.members = Collections.unmodifiableList(new ArrayList<Member> (members)) ;
	}
	
	public List<Member> members() {            // retourne la liste de membres qui composent la relation 
		return members ;
	}
	
	public static final class Builder extends OSMEntity.Builder {      // bâtisseur de relation
		
		private final List<Member> members ;
		
		public Builder(long id) {
			super(id) ;
			members = new ArrayList<Member> () ;
		}
		
		public void addMember(Member.Type type, String role, OSMEntity newMember) {   // ajoute un membre à la liste de membres du bâtisseur
			members.add(new Member(type, role, newMember)) ;
		}
		
		public OSMRelation build() {   // construit une relation OSM à partir de l'identifiant, la liste de membres et l'ensemble 
		                               // d'attributs courants, si l'exception de type "IllegalStateException" n'a pas été levée
			if (super.isIncomplete())
				throw new IllegalStateException() ;
			return new OSMRelation(getId(), members, getB().build()) ;
		}
		
	}
	
	public static final class Member {
		
		private final Type type ;
		private final String role ;
		private final OSMEntity membre ;
		
		public Member(Type type, String role, OSMEntity member) {
			this.type = type ;
			this.role = role ;
			membre = member ;
		}
		
		public Type type() {   // on définit ici des méthodes d'accession aux attributs privés de la classe "Member". Retourne ici le type
			return type ;
		}
		
		public String role() {        // ici le rôle
			return role ;
		}
		
		public OSMEntity member() {       // et ici le membre
			return membre ;
		}
		
		public enum Type      // décrit une énumération sur les différents types acceptés pour un membre d'une relation osm
		{NODE, WAY, RELATION}
		
	}
	
}
