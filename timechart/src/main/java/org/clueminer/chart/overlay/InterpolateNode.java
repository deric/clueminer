package org.clueminer.chart.overlay;

import java.lang.reflect.InvocationTargetException;
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
                //Property inc = new PropertySupport.Reflection(overlay, Integer.class, "getSteps", "setSteps");
                Property inc = new PropertySupport.ReadWrite<Integer>("steps", Integer.class, "steps between points", "steps") {

                    @Override
                    public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                        return overlay.getSteps();
                    }

                    @Override
                    public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                        overlay.setSteps(val);
                    }
                };

                //inc.setName("steps between points");
                set.put(interp);
                set.put(inc);
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
                ErrorManager.getDefault();
            }
        }
        sheet.put(set);
        return sheet;
    }

}
