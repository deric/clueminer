package org.clueminer.chart.overlay;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.PropertySupport;

/**
 *
 * @author Tomas Barton
 */
public class InterpolatorPropertyEditor extends PropertySupport.ReadWrite<String> {

    private InterpolateNode nd = null;
    private PropertyEditor editor;
    private InterpolatorOverlay overlay;

    public InterpolatorPropertyEditor(String name, Class<String> type, String displayName, String shortDescription) {
        super(name, type, displayName, shortDescription);
    }

    public InterpolatorPropertyEditor(InterpolateNode nd, InterpolatorOverlay overlay) throws NoSuchMethodException {
        super("interpName", String.class, "Interpolator", null);
        this.nd = nd;
        this.overlay = overlay;
    }

    @Override
    public String getValue() throws IllegalAccessException, InvocationTargetException {
        return editor.getAsText();
    }

    @Override
    public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (val != null) {
            //this value is for some reason outdated
            // overlay.setInterpolatorName(val);
        }
    }

    public void setInterpolator(String interp) {
        if (interp != null) {
            overlay.setInterpolatorName(interp);
        }
    }

    public String getCurrent() {
        return overlay.getInterpolatorName();
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        if (editor != null) {
            return editor;
        }
        return editor = new TagEditor(this, overlay.getInterpolators());
    }

    private class TagEditor extends PropertyEditorSupport {

        private String current;
        private InterpolatorPropertyEditor ed;
        private final String[] values;

        public TagEditor(InterpolatorPropertyEditor ed, String[] ary) {
            this.values = ary;
            this.ed = ed;
            current = ed.getCurrent();
        }

        @Override
        public String[] getTags() {
            return values;
        }

        @Override
        public String getAsText() {
            return current;
        }

        @Override
        public void setAsText(String curr) {
            this.current = curr;
            ed.setInterpolator(curr);
        }

    }

}
