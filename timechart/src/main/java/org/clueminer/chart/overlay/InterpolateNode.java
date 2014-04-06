package org.clueminer.chart.overlay;

import org.openide.ErrorManager;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;

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

                set.put(interp);
            } catch (NoSuchMethodException ex) {
                ErrorManager.getDefault();
            }
        }
        sheet.put(set);
        return sheet;
    }

}
