package org.clueminer.scatter;

import java.awt.BorderLayout;
import java.util.Random;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;

public class ScatterDemo extends ExamplePanel {

    /**
     * Version id for serialization.
     */
    private static final long serialVersionUID = -412699430625953887L;

    private static final int SAMPLE_COUNT = 100000;
    /**
     * Instance to generate random data values.
     */
    private static final Random random = new Random();

    @SuppressWarnings("unchecked")
    public ScatterDemo() {
        // Generate 100,000 data points
        DataTable data = new DataTable(Double.class, Double.class);
        for (int i = 0; i <= SAMPLE_COUNT; i++) {
            data.add(random.nextGaussian() * 2.0, random.nextGaussian() * 2.0);
        }

        // Create a new xy-plot
        XYPlot plot = new XYPlot(data);

        // Format plot
        plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
        plot.getTitle().setText(getDescription());

        // Format points
        plot.getPointRenderer(data).setColor(COLOR1);

        // Add plot to Swing component
        add(new InteractivePanel(plot), BorderLayout.CENTER);
    }

    @Override
    public String getTitle() {
        return "Scatter plot";
    }

    @Override
    public String getDescription() {
        return String.format("Scatter plot with %d data points", SAMPLE_COUNT);
    }

    public static void main(String[] args) {
        new ScatterDemo().showInFrame();
    }

}
