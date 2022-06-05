package ch.epfl.imhof.painting;


public class LineStyle {        // classe des styles de ligne
    
    private final float width;
    private final Color color;
    private final Cap cap;
    private final Join join;
    private final float[] dashingPattern;
    
    public LineStyle(float x, Color c, Cap cap, Join join, float[] tabF) {      
        if (x < 0.)
            throw new IllegalArgumentException();
        for (float v : tabF)
            if (v <= 0.f)
                throw new IllegalArgumentException();
        width = x;
        color = c;
        this.cap = cap;
        this.join = join;
        dashingPattern = tabF;
    }
    
    public LineStyle(float x, Color c) {    
        this(x, c, Cap.BUTT, Join.MITER, new float[0]);
    }
    
    public LineStyle withWidth(float x) {       // retourne une nouvelle ligne de mêmes caractéristiques que le style courant, sauf son épaisseur 
        return new LineStyle(x, color, cap, join, dashingPattern);  // qui est égale au float entré.
    }
    
    public LineStyle withColor(Color c) {       // idem avec une nouvelle couleur
        return new LineStyle(width, c, cap, join, dashingPattern);
    }
    
    public LineStyle withCap(Cap cap) {     // idem avec une nouvelle terminaison de ligne
        return new LineStyle(width, color, cap, join, dashingPattern);
    }
    
    public LineStyle withJoin(Join join) {      // idem avec une nouvelle jointure de segment
        return new LineStyle(width, color, cap, join, dashingPattern);
    }
    
    public LineStyle withDashingPattern(float[] tabF) {     // idem avec une nouvelle alternance des segments opaques et transparents
        return new LineStyle(width, color, cap, join, tabF);
    }
    
    
    public enum Cap {BUTT, ROUND, SQUARE}       // énumération des différentes terminaisons de ligne
    
    public enum Join {BEVEL, MITER, ROUND}      // idem pour les différentes jointures de segment

    public float getWidth() {       // getters pour les 5 caractéristiques
        return width;
    } 

    public Color getColor() {
        return color;
    }

    public Cap getCap() {
    	return cap ;
    }

    public Join getJoin() {
    	return join ;
    }

    public float[] getDashingPattern() {
        return dashingPattern;
    }
    
}
