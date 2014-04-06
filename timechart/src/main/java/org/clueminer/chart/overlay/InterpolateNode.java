package org.clueminer.chart.overlay;

import org.openide.ErrorManager;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class InterpolateNode extends OverlayNode {

    private OverlayProperties properties;
    private InterpolatorOverlay overlay;

    public InterpolateNode(Children children) {
        super(children);

    }

    public InterpolateNode(OverlayProperties properties) {
        super(Children.LEAF);
        this.properties = properties;
    }

    public InterpolateNode(OverlayProperties properties, InterpolatorOverlay overlay) {
        super(Children.LEAF);
        this.properties = properties;
        this.overlay = overlay;
    }

    public InterpolatorOverlay getOverlay() {
        return overlay;
    }

    public void setOverlay(InterpolatorOverlay overlay) {
        this.overlay = overlay;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setDisplayName("Interpolation properties");
        if (overlay != null) {
            try {
                Property interp = new InterpolatorPropertyEditor(this, overlay);
                Property inc = new PropertySupport.Reflection(overlay, int.class, "getSteps", "setSteps");
                inc.setName("steps between two points");
                Property gc = new PropertySupport.Reflection<Boolean>(overlay, boolean.class, "isGlobalCache", "setGlobalCache");
                gc.setName("precompute whole curve");

                set.put(interp);
                set.put(inc);
                set.put(gc);
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
                ErrorManager.getDefault();
            }
        }
        sheet.put(set);
        return sheet;
    }

}
