package org.clueminer.som.gui.plotter.charts;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import com.rapidminer.datatable.DataTable;
import com.rapidminer.gui.plotter.PlotterAdapter;
import com.rapidminer.gui.plotter.PlotterConfigurationModel;
import com.rapidminer.tools.Tools;

import cz.ctu.rapidminer.operator.features.transformation.som.Hexagon;
import cz.ctu.rapidminer.operator.features.transformation.som.Net;
import cz.ctu.rapidminer.operator.features.transformation.som.SOMModel;

/**
 * This class plots SOM map
 *
 * @author Jan Motl
 */
public class SOMChartPlotter extends PlotterAdapter {

    private static final long serialVersionUID = 1511167115976161350L;

    private static final int MARGIN = 30;

	//public static final String PARAMETER_SHOW_CLUSTERS="show_clusters";
	//private final ListeningJCheckBox showClusters;
    private Net network;							// output data

    private transient DataTable dataTable;			// input data (imputed for legend)

    private int plotColumn = -1;					// which dimension to use to color the hexagons

    private Color[] collectionOfColors;				// colors for histogram

    private int labelColumn = -1;					// which label to on top of the hexagon

    private String visualizationStyle = "";			// how to represent the output (dimensions, u matrix, p matrix,...)

    private String selectedColorSchema;				// which color space to use to draw SOM

    private SOMColor colorSchema;					// the color provider

    private boolean showClusters = false;				// switch visibility of the clusters in SOM

    private String currentToolTip = null;

    private double toolTipX = 0.0d;

    private double toolTipY = 0.0d;

    // constructor: setup mouse listener
    public SOMChartPlotter(final PlotterConfigurationModel settings) {
        super(settings);

        this.setDoubleBuffered(true);
        //addMouseListener(this);
    }

    // store input data (I think it's not used)
    public SOMChartPlotter(PlotterConfigurationModel settings, DataTable dataTable) {
        this(settings);
        setDataTable(dataTable);
    }

    // constructor for call from SOMPlotRenderer
    public SOMChartPlotter(PlotterConfigurationModel settings, SOMModel model) {
        this(settings);
        dataTable = settings.getDataTable();
        network = model.network;
    }

    @Override
    public void paint(Graphics graphics) {
        graphics.clearRect(0, 0, getWidth(), getHeight());
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D g = (Graphics2D) graphics;

		// set antialiasing
        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int pixWidth = getWidth() - 2 * MARGIN;
        int pixHeight = getHeight() - 2 * MARGIN;

        // translate to ignore margins
        Graphics2D translated = (Graphics2D) g.create();
        translated.translate(MARGIN, MARGIN);

        // prepare colors for histogram (assign unique value from 0..1 for each class)
        collectionOfColors = new Color[network.at(0, 0).histogram.length];
        for (int i = 0; i < network.at(0, 0).histogram.length; i++) {
            collectionOfColors[i] = getColorProvider().getPointColor((double) i / (network.at(0, 0).histogram.length - 1));	// get values from 0..1
        }

        // tooltip initialization
        drawToolTip(g);

        // draw nominal/numerical legend (the dots/bargraph in the top left corner)
        if (labelColumn == 1 && network.allClasses != null && network.labelIsNominal) {	// show only when histogram is selected and label column is present
            // Names
            String[] names = network.allClasses.keySet().toArray(new String[network.allClasses.size()]);

            // Points
            PointStyle[] pointStyles = new PointStyle[network.at(0, 0).histogram.length];
            for (int i = 0; i < pointStyles.length; i++) {
                pointStyles[i] = ELLIPSOID_POINT_STYLE;
            }

            // Draw the nominal legend
            drawGenericNominalLegend(g, names, pointStyles, collectionOfColors, 0, 255);
        } else if (labelColumn == 1 && network.allClasses != null) {
            double min = Double.parseDouble(network.allClasses.firstKey());
            double max = Double.parseDouble(network.allClasses.lastKey());
            drawSimpleNumericalLegend(g, 5, 15, "Histogram", Tools.formatNumber(min), Tools.formatNumber(max));
        }

        // draw legend in the top middle
        if ("Blue & Red".equals(selectedColorSchema)) {
            drawLegend(g, pixWidth);
        }

        // draw quantization error in the right bottom
        drawExplainedVariance(g, pixWidth, pixHeight);

        // draw net size in the left bottom
        g.drawString("Net size: " + network.sizeX + " \u00D7 " + network.sizeY, 20, (int) (pixHeight + 1.5 * MARGIN));

        // color initialization
        colorSchema = new SOMColor(selectedColorSchema);	// initialize color colorSchema

		// paint ToolTip
        //drawToolTip((Graphics2D) g);
        // draw the hexagons
        draw(translated, pixWidth, pixHeight);
        translated.dispose();
    }

    private void draw(Graphics g, int pixWidth, int pixHeight) {
        // Plot dense matrix of hexagons (for U matrix)
        if (visualizationStyle.equals("U-Matrix with neighbours")) {
            drawUMatrix(g, pixWidth, pixHeight);
        } // Plot common matrix of hexagons
        else {
            drawCommonMatrix(g, pixWidth, pixHeight);
        }
    }

    // draw Common Matrix
    public void drawCommonMatrix(Graphics g, int pixWidth, int pixHeight) {

        // create a hexagon
        Hexagon hexagon = new Hexagon(g, pixWidth, pixHeight, network.sizeX, network.sizeY);

        // align the hexagon to to center
        hexagon.allignToCenter(pixWidth, pixHeight, network.sizeX, network.sizeY);

        // get P-Matrix if needed
        if (visualizationStyle.equals("P-Matrix")) {
            network.updatePDistances(dataTable); // calculate P-Matrix
        }
        // get U*-Matrix if needed
        if (visualizationStyle.equals("U*-Matrix")) {
            network.updateUStarDistances(dataTable); // calculate U*-Matrix
        }
        // draw matrix of hexagons
        for (int x = 0; x < network.sizeX; x++) {
            for (int y = 0; y < network.sizeY; y++) {

                // get node value based on the visualization style (either dimension, P-Matrix or U*-Matrix)
                double colorValue = 0;
                if (visualizationStyle.equals("U-Matrix")) {
                    colorValue = network.at(x, y).uDistance; // get P-Matrix color
                } else if (visualizationStyle.equals("P-Matrix")) {
                    colorValue = network.at(x, y).pDistance; // get P-Matrix color
                } else if (visualizationStyle.equals("U*-Matrix")) {
                    colorValue = network.at(x, y).uStarDistance; // get U*-Matrix color
                } else if (plotColumn >= 0) {
                    colorValue = network.at(x, y).normWeights[plotColumn]; //get dimension color
                }
                // draw the node
                if (labelColumn == 1) { // Histogram is selected (the first items in the menu are "None" and "Histogram")
                    hexagon.drawHexagonWithHistogram(x, y, colorSchema.getColor(colorValue), network.at(x, y).histogram, collectionOfColors);
                } else if (labelColumn >= 2) { // Some attribute is selected (I want to refer them from 0, hence I then subtract 2)
                    hexagon.drawHexagonWithLabel(x, y, colorSchema.getColor(colorValue), network.at(x, y).labelList.get(labelColumn - 2));
                } else {
                    hexagon.drawHexagon(x, y, colorSchema.getColor(colorValue));
                }
            }
        }

        // Plot bold borders
        if (showClusters) {
            network.updateClusters();
            hexagon.drawBoldBorders(network.sizeX, network.sizeY, network);
        }
    }

    // draw Dense Matrix (U-Matrix)
    public void drawUMatrix(Graphics g, int pixWidth, int pixHeight) {
        // adjust net
        int countX = 2 * network.sizeX - 1;
        int countY = 2 * network.sizeY - 1;

        // create a small hexagon
        Hexagon smallHexagon = new Hexagon(g, pixWidth, pixHeight, countX, countY);

        // move the hexagon to the center
        smallHexagon.allignToCenter(pixWidth, pixHeight, countX, countY);

        // draw U Matrix
        for (int x = 0; x < network.sizeX; x++) {
            for (int y = 0; y < network.sizeY; y++) {
                // shift some rows right
                int shift = 0;
                if (y % 2 == 1) {
                    shift++;
                }

                // draw hexagon with the selected setting
                double colorValue = network.at(x, y).uDistanceOwn; //get color
                smallHexagon.drawHexagon(2 * x + shift, 2 * y, colorSchema.getColor(colorValue)); // draw the hexagon
                if (labelColumn == 1) {
                    smallHexagon.drawHexagonWithHistogram(2 * x + shift, 2 * y, colorSchema.getColor(colorValue), network.at(x, y).histogram, collectionOfColors);
                } else if (labelColumn >= 2) {
                    smallHexagon.drawHexagonWithLabel(2 * x + shift, 2 * y, colorSchema.getColor(colorValue), network.at(x, y).labelList.get(labelColumn - 2));
                }

                // on the right
                if (network.at(x, y).nRight != null) {
                    smallHexagon.drawHexagon(2 * x + 1 + shift, 2 * y, colorSchema.getColor(network.at(x, y).uDistanceRight));
                }
                // on the bottom right
                if (network.at(x, y).nBottomRight != null) {
                    smallHexagon.drawHexagon(2 * x + shift, 2 * y + 1, colorSchema.getColor(network.at(x, y).uDistanceBottomRight));
                }
                // on the bottom left
                if (network.at(x, y).nBottomLeft != null) {
                    smallHexagon.drawHexagon(2 * x - 1 + shift, 2 * y + 1, colorSchema.getColor(network.at(x, y).uDistanceBottomLeft));
                }
            }
        }

        // Plot bold borders
        if (showClusters) {
            network.updateClusters();
            smallHexagon.drawBoldBorders(network.sizeX, network.sizeY, network);
        }
    }

    // draw Legends at the top
    public void drawLegend(Graphics g, int pixWidth) {
        // draw numerical legend (the line in the middle above the hexmap)
        if (plotColumn >= 0) { 	// draw only when an attribute is selected
            FontMetrics fm = g.getFontMetrics(g.getFont());
            int shift = (pixWidth / 2 - 59 - fm.stringWidth(dataTable.getColumnName(plotColumn)));
            drawSimpleNumericalLegend(g, shift, 15, dataTable.getColumnName(plotColumn), Tools.formatNumber(network.minValue[plotColumn]), Tools.formatNumber(network.maxValue[plotColumn]));
        }

        // draw legend for Matrices
        if (visualizationStyle.equals("P-Matrix")) {
            drawSimpleNumericalLegend(g, (pixWidth / 2 - 65), 15, "", "Scarce", "Dense");
        }
        if (visualizationStyle.equals("U-Matrix") || visualizationStyle.equals("U-Matrix with neighbours") || visualizationStyle.equals("U*-Matrix")) {
            drawSimpleNumericalLegend(g, (int) (pixWidth / 2 - 66), 15, "", "Similar", "Different");
        }
    }

    // draw error value in the right bottom corner
    public void drawExplainedVariance(Graphics g, int pixWidth, int pixHeight) {
        // compose the string
        String quantizationErrorString = "Explained variance: " + String.format("%.2f%%", 100 * network.explainedVariance);

        // draw it gray
        g.setColor(Color.GRAY);
        g.drawString(quantizationErrorString, pixWidth - 120, (int) (pixHeight + 1.5 * MARGIN));
    }

    ////////////////// Tooltip //////////////
    /**
     * Sets the mouse position in the shown data space.
     */
    /**
     * Sets the mouse position in the shown data space.
     */
    @Override
    public void setMousePosInDataSpace(int x, int y) {
        String text = "adfsd";
        System.out.println("setMouse Position " + x + " " + y);
        double pixWidth = (getWidth() - 2 * MARGIN);
        double pixHeight = (getHeight() - 2 * MARGIN);
        double xPos = 600;//* pixWidth;
        double yPos = 600;//pixHeight - ((point.y - min[Y_AXIS]) / (max[Y_AXIS] - min[Y_AXIS]) * pixHeight);
        setToolTip(text, xPos, yPos);

    }

    private void setToolTip(String toolTip, double x, double y) {
        System.out.println("SetToolTip");
        this.currentToolTip = toolTip;
        this.toolTipX = x;
        this.toolTipY = y;
        repaint();
    }

    private void drawToolTip(Graphics2D g) {
        System.out.println("ok once");
        if (currentToolTip != null) {
            System.out.println("drawToolTip");
            g.setFont(LABEL_FONT);
            Rectangle2D stringBounds = LABEL_FONT.getStringBounds(currentToolTip, g.getFontRenderContext());
            g.setColor(TOOLTIP_COLOR);
            Rectangle2D bg = new Rectangle2D.Double(toolTipX - stringBounds.getWidth() / 2 - 4, toolTipY - stringBounds.getHeight() / 2 - 2, stringBounds.getWidth() + 5, Math.abs(stringBounds.getHeight()) + 3);
            g.fill(bg);
            g.setColor(Color.black);
            g.draw(bg);
            g.drawString(currentToolTip, (float) (toolTipX - stringBounds.getWidth() / 2) - 2, (float) (toolTipY + 3));
        }
    }

    public void mouseClicked(MouseEvent event) {
        String name = "holla";
        setToolTip(name, event.getX(), event.getY());
    }

    public void mouseReleased(MouseEvent event) {
        currentToolTip = null;
    }

    @Override
    public boolean isProvidingCoordinates() {
        return true;
    }

	////////////////// Setters //////////////
    // set the visualization style (dimensions, u matrix, p matrix,...)
    public void setVisualizationStyle(String style) {
        visualizationStyle = style;
        repaint();
    }

    public void setLabelColumn(int column) {
        this.labelColumn = column;
        repaint();
    }

    public void setColorSchema(String selectedColorSchema) {
        this.selectedColorSchema = selectedColorSchema;
        repaint();
    }

    public void setShowClusters(boolean showClusters) {
        this.showClusters = showClusters;
        repaint();
    }

    public void repaintAll() {
        repaint();
    }

    ////////////////// Required for this class by RapidMiner //////////////
    @Override
    public void setPlotColumn(int column, boolean plot) {
        this.plotColumn = column;
        repaint();
    }

    @Override
    public boolean getPlotColumn(int column) {
        return (column == this.plotColumn);
    }

    @Override
    public String getPlotName() {
        return "Plot Column";
    }

    @Override
    public String getPlotterName() {
        return "SOM";
    }
}
