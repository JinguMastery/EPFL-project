package ch.epfl.imhof.painting;


public class Color {                // classe des couleurs
	
    public final static Color RED = new Color(1, 0, 0), BLUE = new Color(0, 1, 0), GREEN = new Color(0, 0, 1), 
            BLACK = new Color(0, 0, 0), WHITE = new Color(1, 1, 1);
    private final double r, g, b;
    
    private Color(double x, double y, double z) {   
        r = x;
        g = y;
        b = z;
    }
    
    public static Color gray(double x) {    // crée une couleur dont les valeurs r g et b sont les mêmes, donc des couleurs allant du noir au blanc
        if ( x < 0. || x > 1.)
            throw new IllegalArgumentException();
        return new Color(x, x, x);
    }
    
    public static Color rgb(double x, double y, double z) {     // crée une couleur à partir des 3 valeurs entrées, et vérifie qu'elles sont toutes comprises entre 0 et 1
        if ( x < 0. || x > 1. || y < 0. || y > 1. || z < 0. || z > 1.)
            throw new IllegalArgumentException(); 
        return new Color(x, y, z);
    }
    
    public static Color rgb(int n) {    // accède aux 3 parties de l'entier qui correspondent aux 3 couleurs, puis construit la couleur associée
        double x, y, z;
        x = (n >> 24 & 0xFF) /255. ;
        y = (n >> 16 & 0xFF) /255. ;
        z = (n >> 8 & 0xFF) /255. ;
        return new Color(x, y, z);
    }
    
    public static Color multiply(Color c1, Color c2) {      // multiplie les 3 paires de couleurs correspondantes et retourne la couleur obtenue
        return rgb(c1.r*c2.r, c1.g*c2.g, c1.b*c2.b);
    }
    
    public static java.awt.Color convert(Color c) {     // convertit un objet de type "Color" en type "java.awt.Color" à partir de 3 "float"
        return new java.awt.Color((float)c.r, (float)c.g, (float)c.b);
    }

    public double getR() {      // 3 getters pour chacune des couleurs
       return r;
    }

    public double getG() {
        return g;
    }

    public double getB() {
        return b;
    }
}
