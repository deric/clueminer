package com.panayotis.gnuplot.plot;

import com.panayotis.gnuplot.layout.AutoGraphLayout;
import com.panayotis.gnuplot.layout.GraphLayout;
import com.panayotis.gnuplot.layout.LayoutMetrics;
import java.util.ArrayList;

/**
 * The data representation of a whole graph page
 * @author teras
 */
public class Page extends ArrayList<Graph> {
    private static final long serialVersionUID = 6485013309125515984L;

    protected static final String NL = System.getProperty("line.separator");

    private String pagetitle;
    private GraphLayout layout;

    /**
     * Contruct a new blank page with one graph inside
     */
    public Page() {
        this(false);
    }

    /**
     * Contruct a new blank page with one graph inside
     * @param isGraph3D true, if this graph is a 3D plot
     */
    public Page(boolean isGraph3D) {
        if(isGraph3D) {
            add(new Graph3D());
        } else {
            add(new Graph());
        }
        pagetitle = "";
        layout = new AutoGraphLayout();
    }
    
    /**
     * Get a reference for this page layout
     * @return the layout used in this page
     */
    public GraphLayout getLayout() {
        return layout;
    }

    public void setLayout(GraphLayout layout) {
        this.layout = layout;
    }

    /**
     * Get the title of this page
     * @return page title
     */
    public Object getTitle() {
        return pagetitle;
    }

    /**
     * Set the title of this page
     * @param title the new page title
     */
    public void setTitle(String title) {
        if (title == null)
            title = "";
        pagetitle = title;
    }

    /**
     * Append the GNUPlot program which will construct this page, to a buffer.
     * @param bf Buffer to store the gnuplot program
     */
    public void getGNUPlotPage(StringBuffer bf) {
        if (size() > 1) {
            /* This is a multiplot */

            bf.append("set multiplot");
            if (!pagetitle.equals("")) {
                bf.append(" title \"").append(pagetitle).append('"');
            }
            layout.setDefinition(this, bf);
            bf.append(NL);

            LayoutMetrics metrics;
            for (Graph gr : this) {
                metrics = gr.getMetrics();
                if (metrics != null) {
                    bf.append("set origin ").append(metrics.getX()).append(',').append(metrics.getY()).append(NL);
                    bf.append("set size ").append(metrics.getWidth()).append(',').append(metrics.getHeight()).append(NL);
                }
                gr.retrieveData(bf);
            }

            bf.append("unset multiplot").append(NL);
        } else {
            get(0).retrieveData(bf);
        }

    }

}
