package ch.epfl.imhof.painting;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import ch.epfl.imhof.Attributed;


public final class Filters {        // classe des filtres
    
    private Filters() {
    }
    
    public static Predicate<Attributed<?>> tagged(String str) {     // prédicat retournant vrai lorsque l'ensemble d'attributs concerné contient un attribut de nom "str"
        return p -> p.hasAttribute(str);
    }
    
    public static Predicate<Attributed<?>> tagged(String str, String... args) {   // idem, mais vérifie en plus que la valeur associée à l'attribut de nom "str" est contenu dans les arguments donnés
        if (args.length == 0)
            throw new IllegalArgumentException();
        List<String> listArgs = Arrays.asList(args);
        return p -> p.hasAttribute(str) && listArgs.contains(p.attributeValue(str));
    }
    
    public static Predicate<Attributed<?>> onLayer(int n) {     // prédicat retournant vrai lorsque la valeur de l'attribut "layer" de l'entité concernée est égale à n.
        return p -> Integer.parseInt(p.attributeValue("layer", "0")) == n;
    }
    
}
