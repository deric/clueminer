package org.clueminer.chart;

import java.awt.Color;
import java.awt.Font;
import java.util.logging.Level;
import org.clueminer.chart.api.ChartProperties;
import org.clueminer.chart.base.AbstractPropertiesNode;
import org.clueminer.chart.base.ChartPropertiesImpl;
import org.openide.nodes.Sheet;

/**
 *
 * @author Tomas Barton
 */
public class ChartNode extends AbstractPropertiesNode {

    private static final long serialVersionUID = 1L;

    private ChartProperties prop;

    public ChartNode() {
        super("Chart Properties");
        prop = new ChartPropertiesImpl();
    }

    public ChartNode(ChartProperties chartProperties) {
        super("Chart Properties", chartProperties);
        this.prop = chartProperties;
    }

    @SuppressWarnings("unchecked")
    protected @Override
    Sheet createSheet() {
        Sheet sheet = new Sheet();

        for (Sheet.Set set : getSets()) {
            sheet.put(set);
        }

        return sheet;
    }

    public @Override
    Sheet.Set[] getSets() {
        Sheet.Set[] sets = new Sheet.Set[4];

        Sheet.Set window = getPropertiesSet(
                "Window Properties", // properties set name
                "Window Properties" // properties set description
        );
        sets[0] = window;

        Sheet.Set axis = getPropertiesSet(
                "Axis Properties", // properties set name
                "Axis Properties" // properties set description
        );
        sets[1] = axis;

        Sheet.Set data = getPropertiesSet(
                "Data Properties", // properties set name
                "Data Properties" // properties set description
        );
        sets[2] = data;

        Sheet.Set grid = getPropertiesSet(
                "Grid Properties", // properties set name
                "Grid Properties" // properties set description
        );
        sets[3] = grid;

        try {
            // Window Properties

            // Background Color
            window.put(getProperty(
                    "Background Color", // property name
                    "Sets the background color", // property description
                    ChartProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getBackgroundColor", // get method name
                    "setBackgroundColor", // set method name
                    prop.getBackgroundColor() // default property value
            ));
            // Font
            window.put(getProperty(
                    "Font", // property name
                    "Sets the font", // property description
                    ChartProperties.class, // properties class
                    Font.class, // property class
                    null, // property editor class (null if none)
                    "getFont", // get method name
                    "setFont", // set method name
                    prop.getFont() // default property value
            ));
            // Font Color
            window.put(getProperty(
                    "Font Color", // property name
                    "Sets the font color", // property description
                    ChartProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getFontColor", // get method name
                    "setFontColor", // set method name
                    prop.getFontColor() // default property value
            ));

        } catch (NoSuchMethodException ex) {
            LOG.log(Level.SEVERE, "[ChartNode] : Method does not exist.", ex);
        }

        return sets;
    }
}
