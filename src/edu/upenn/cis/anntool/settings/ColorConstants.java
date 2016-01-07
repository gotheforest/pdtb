/*
 * ColorConstants.java
 *
 * Created on December 7, 2005, 3:38 PM
 */

package edu.upenn.cis.anntool.settings;

import java.awt.Color;

/**
 * Definitions of colors used in the various (sub)components of the PDTBBrowser.
 *
 * @author  nikhild, geraud
 */
public interface ColorConstants { 
    
    /**
     * Explicit connective and AltLex highlight
     * (red)
     */
    public static final Color ExplicitConnColor = new Color((float)1.0, (float)0.1, (float)0.1, (float)0.6);
    
    /**
     * Explicit connective and AltLex Attribution highlight
     * (pink)
     */
    public static final Color ExplicitConnAttribColor = new Color((float)1.0, (float)0.75, (float)0.8, (float)0.6); 
    
    /**
     * Arg1 highlight
     * (yellow)
     */
    public static final Color Arg1Color = new Color((float)1.0, (float)1.0, (float)0.0, (float)0.6);
    
    /**
     * Arg1 Attribution highlight
     * (orange)
     */
    public static final Color Arg1AtrribColor = new Color((float)1.0, (float)0.65, (float)0.0, (float)0.6);
        
    /**
     * Arg2 highlight
     * (blue)
     */
    public static final Color Arg2Color = new Color((float)0.2, (float)0.6, (float)1.0, (float)0.6);
    
    /**
     * Arg2 Attribution highlight
     * (purple)
     */
    public static final Color Arg2AtrribColor = new Color((float)0.5, (float)0.0, (float)0.5, (float)0.6);
    
    /**
     * Sup1 highlight
     * (grey)
     */
    public static final Color Sup1Color = new Color((float)0.7, (float)0.7, (float)0.7, (float)0.6);
    
    /**
     * Sup2 highlight
     * (green)
     */
    public static final Color Sup2Color = new Color((float)0.0, (float)1.0, (float)0.0, (float)0.6);
    
    /**
     * Default background color
     */
    public static final Color DefaultBackground = Color.WHITE;
    
    /**
     * Selection highlight
     */
    //TODO: make the selection color translucent
    public static final Color SelectionColor = new Color((float)0.5, (float)0.5, (float)0.6, (float)0.6);
    
	// brown
//	public static final Color searchColor = new Color((float) 0.80,
//			(float) 0.52, (float) 0.25, (float) 0.6);
    /**
     * blue green
     */
    public static final Color searchColor = new Color((float)0.0, (float)1.0, (float)0.8, (float)0.6);

    /**
     * black
     */
    public static final Color BreakColor = Color.BLACK;
    
}
