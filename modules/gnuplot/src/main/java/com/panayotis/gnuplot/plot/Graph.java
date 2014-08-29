package com.panayotis.gnuplot.plot;

import com.panayotis.gnuplot.GNUPlotParameters;
import static com.panayotis.gnuplot.GNUPlotParameters.ERRORTAG;
import static com.panayotis.gnuplot.GNUPlotParameters.ERROR_VAR;
import com.panayotis.gnuplot.layout.LayoutMetrics;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Graph objects are parts of a multi-plot drawing. Each graph contains other plots which
 * share the same axis. All gnuplot objects have at least one graph object.
 * <p> For single plots, better have a look at Plot objects and GNUPlot.addPlot() command
 * @author teras
 */
public class Graph extends ArrayList<Plot> {

    protected static final String NL = System.getProperty("line.separator");
    private HashMap<String, Axis> axis;
    private LayoutMetrics metrics;

    /**
     * Create a new graph object
     */
    public Graph() {
        axis = new HashMap<String, Axis>();
        axis.put("x", new Axis("x"));
        axis.put("y", new Axis("y"));
        axis.put("z", new Axis("z"));
        metrics = null;
    }

    /**
     *  Get one of the available Axis, in orde to set some parameters on it.
     * @param axisname The name of the Axis. It is usually "x", "y", "z"
     * @return The desired Axis
     */
    public Axis getAxis(String axisname) {
        if (axisname == null) {
            return null;
        }
        return axis.get(axisname.toLowerCase());
    }

    /**
     * Add a new plot to this plotgroup.
     * At least one plot is needed to produce visual results.
     * @param plot The given plot.
     */
    public void addPlot(Plot plot) {
        if (plot != null) {
            add(plot);
        }
    }

    /**
     * Get gnuplot commands for this graph.
     * @param bf
     */
    void retrieveData(StringBuffer bf) {
        /* Do not append anything, if this graph is empty */
        if (size() == 0)
            return;

        /* Set various axis parameters */
        for (Axis ax : axis.values()) {
            ax.appendProperties(bf);
        }

        /* Create data plots */
        bf.append(ERROR_VAR).append(" = 1").append(NL);  // Set error parameter
        bf.append(getPlotCommand());    // Use the corresponding plot command
        /* Add plot definitions */
        for (Plot p : this) {
            bf.append(' ');
            p.retrieveDefinition(bf);
            bf.append(',');
        }
        bf.deleteCharAt(bf.length() - 1);
        bf.append(GNUPlotParameters.NOERROR_COMMAND).append(NL);    // Reset error parameter. if everything OK
        /* Add plot data (if any) */
        for (Plot p : this) {
            p.retrieveData(bf);
        }

        /* Print error check */
        bf.append("if (").append(ERROR_VAR).append(" == 1) print '").append(ERRORTAG).append('\'').append(NL);
    }

    /**
     * Set the position and size of the graph object, relative to a 0,0-1,1 page
     *
     * @param x horizontal position
     * @param y vertical position
     * @param width width of this graph
     * @param height of this graph
     */
    public void setMetrics(float x, float y, float width, float height) {
        metrics = new LayoutMetrics(x, y, width, height);
    }

    /**
     * Get the positioning and size of this graph object
     * @return The metrics of this object
     */
    public LayoutMetrics getMetrics() {
        return metrics;
    }
    
    /**
     * Get the actual gnuplot command to initiate the plot.
     * @return This method always returns "plot"
     */
    protected String getPlotCommand() {
        return "plot";
    }

    public void setMetrics(int x, int y, double width, double height) {
        metrics = new LayoutMetrics((float)x, (float)y, (float)width, (float)height);
    }
}
