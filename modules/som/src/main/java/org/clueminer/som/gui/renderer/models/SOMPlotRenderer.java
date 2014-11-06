package org.clueminer.som.gui.renderer.models;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.rapidminer.datatable.DataTable;
import com.rapidminer.datatable.SimpleDataTable;
import com.rapidminer.datatable.SimpleDataTableRow;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.AttributeRole;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.gui.plotter.Plotter;
import com.rapidminer.gui.plotter.PlotterAdapter;
import com.rapidminer.gui.plotter.PlotterConfigurationModel;
import com.rapidminer.gui.plotter.settings.ListeningJComboBox;
import com.rapidminer.gui.renderer.AbstractRenderer;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeAttribute;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.report.Reportable;
import org.clueminer.som.gui.plotter.charts.SOMChartPlotter;
import org.clueminer.som.operator.features.transformation.som.SOMModel;

/**
 * A renderer for the graph view of the Self-Organizing Map.
 * It draws the controls for the plotting.
 *
 * @author Jan Motl
 */
public class SOMPlotRenderer extends AbstractRenderer {

    public static final String PARAMETER_VISUALIZATION_METHOD = "visualization_method";
    public static final String PARAMETER_LABEL_NAME = "label_name";
    public static final String PARAMETER_COLOR_SCHEMA = "color_schema";
    public static final String PARAMETER_SHOW_CLUSTERS = "show_clusters"; // so far hidden since not implemented

    public String getName() {
        return "Graph View";
    }

    // define setting parameters
    public Reportable createReportable(Object renderable, IOContainer ioContainer, int width, int height) {
        SOMModel somModel = (SOMModel) renderable;

        String attributeName = "";

        DataTable table = getDataTable(somModel);
        PlotterConfigurationModel settings = new PlotterConfigurationModel(PlotterConfigurationModel.COMPLETE_PLOTTER_SELECTION, table);
        Plotter plotter = new SOMChartPlotter(settings, somModel);
        settings.setPlotter(plotter);
        settings.setParameterAsString(PlotterAdapter.PARAMETER_PLOT_COLUMN, attributeName);
        plotter.getRenderComponent().setSize(width, height);
        return plotter;

    }

    // draw the panel with the settings
    public Component getVisualizationComponent(Object renderable, IOContainer ioContainer) {
        SOMModel somModel = (SOMModel) renderable;

        JPanel graphPanel = new JPanel(new BorderLayout());

        DataTable table = getDataTable(somModel);
        final PlotterConfigurationModel settings = new PlotterConfigurationModel(PlotterConfigurationModel.COMPLETE_PLOTTER_SELECTION, table);
        final SOMChartPlotter plotter = new SOMChartPlotter(settings, somModel); // originally defined as plotter
        settings.setPlotter(plotter);

        graphPanel.add(plotter.getPlotter(), BorderLayout.CENTER);

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.0d;
        c.weightx = 1.0d;
        c.insets = new Insets(4, 4, 4, 4);
        c.gridwidth = GridBagConstraints.REMAINDER;
        JPanel boxPanel = new JPanel(layout);

        // Visualization comboBox (U-matrix, P-matrix,...)
        final JLabel labelVisualization = new JLabel("Visualization Style:");
        layout.setConstraints(labelVisualization, c);
        boxPanel.add(labelVisualization);

        String[] style = {
            "U-Matrix",
            "U-Matrix with neighbours",
            "P-Matrix",
            "U*-Matrix"
        };
        style = concat(style, somModel.getAttributeNames());

        final ListeningJComboBox comboVisualization = new ListeningJComboBox(settings, PARAMETER_VISUALIZATION_METHOD, style);
        layout.setConstraints(comboVisualization, c);
        boxPanel.add(comboVisualization);

        // Label comboBox (None, Histogram, Special Attributes, Common Attributes)
        final JLabel labelLabel = new JLabel("Label:");
        layout.setConstraints(labelLabel, c);
        boxPanel.add(labelLabel);

        String[] none = {"None", "Histogram"};
        String[] labels = concat(none, somModel.getAttributeNames());
        labels = concat(labels, somModel.getSpecialAttributeNames());

        final ListeningJComboBox comboLabel = new ListeningJComboBox(settings, PARAMETER_LABEL_NAME, labels);
        layout.setConstraints(comboLabel, c);
        boxPanel.add(comboLabel);

        // Color Schema comboBox (black & white, blue-red)
        final JLabel labelColorSchema = new JLabel("Color Schema:");
        layout.setConstraints(labelColorSchema, c);
        boxPanel.add(labelColorSchema);

        String[] schemas = {"Blue & Red", "Black & White"};

        final ListeningJComboBox comboColorSchema = new ListeningJComboBox(settings, PARAMETER_COLOR_SCHEMA, schemas);
        layout.setConstraints(comboColorSchema, c);
        boxPanel.add(comboColorSchema);

        // Cluster checkBox (hidden)
        final JCheckBox showClusters = new JCheckBox("Show Clusters", false);
        layout.setConstraints(showClusters, c);
		//boxPanel.add(showClusters); // hidden... uncomment to show it

        // Free space
        c.weighty = 1.0d;
        JPanel fillPanel = new JPanel();
        layout.setConstraints(fillPanel, c);
        boxPanel.add(fillPanel);

        // Visualization description
        final String[] description = {
            "<html><b>Description:</b> <br> U-Matrix shows the average <br> Euclidean distance to the <br> adjacent hexagons.</html>",
            "<html><b>Description:</b> <br> U-Matrix with neighbours <br> shows the average Euclidean <br> distance to the adjacent <br> hexagons like U-Matrix but <br>it adds extra hexagons <br>depicting the distance<br> between the two adjacent <br> hexagons.</html>",
            "<html><b>Description:</b> <br> P-Matrix shows the number <br> of samples in the hexagon's <br> neighborhood.</html>",
            "<html><b>Description:</b> <br> U*-Matrix is a combination <br> of U and P matrices.</html>",
            "<html><b>Description:</b> <br> An attribute in the dataset.</html>"
        };
        final JLabel labelDescription = new JLabel(description[0]); // declare the variable
        layout.setConstraints(labelDescription, c);
        boxPanel.add(labelDescription);

        // plot the whole left panel
        graphPanel.add(boxPanel, BorderLayout.WEST);

        // Listeners
        comboVisualization.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                plotter.setVisualizationStyle(comboVisualization.getSelectedItem().toString());
                plotter.setPlotColumn(comboVisualization.getSelectedIndex() - 4, true); // we want to index the attributes from 0, hence -4
                if (comboVisualization.getSelectedIndex() > 3) {
                    labelDescription.setText(description[4]);		// an attribute is selected
                } else {
                    labelDescription.setText(description[comboVisualization.getSelectedIndex()]);			// a matrix is selected
                }
                plotter.repaintAll();
            }
        });

        comboLabel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                plotter.setLabelColumn(comboLabel.getSelectedIndex());
            }
        });

        comboColorSchema.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                plotter.setColorSchema(comboColorSchema.getSelectedItem().toString());
            }
        });

        showClusters.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                plotter.setShowClusters(showClusters.isSelected());
            }
        });

        // default values
        comboVisualization.setSelectedIndex(0);
        comboLabel.setSelectedIndex(1);
        comboColorSchema.setSelectedIndex(0);

        return graphPanel;
    }

    // Tooltip text (which doesn't show on MAC), default values and range. But default values are set above...
    @Override
    public List<ParameterType> getParameterTypes(InputPort inputPort) {
        List<ParameterType> types = super.getParameterTypes(inputPort);
        if (inputPort != null) {
            types.add(new ParameterTypeAttribute(PARAMETER_VISUALIZATION_METHOD, "Indicates for which attribute the distribution should be plotted.", inputPort, false));
        } else {
            types.add(new ParameterTypeString(PARAMETER_VISUALIZATION_METHOD, "Indicates for which attribute the distribution should be plotted.", false));
        }
        types.add(new ParameterTypeString(PARAMETER_LABEL_NAME, "Which additional information to show.", false));
        types.add(new ParameterTypeBoolean(PARAMETER_SHOW_CLUSTERS, "Indicates if clusters should be highlighted.", false));
        return types;
    }

    // convert data from ExampleSet into DataTable
    private DataTable getDataTable(SOMModel somModel) {
        // create dataTable
        DataTable table = new SimpleDataTable("Dummy", concat(somModel.getAttributeNames(), somModel.getSpecialAttributeNames()));

        // retrieve data (ugly exampleSet -> dataTable)
        ExampleSet exampleSet = somModel.getData();

        // create a temporary storage used to transfer data from exampleSet -> dataTable
        double[] value = new double[somModel.getAttributeNames().length + somModel.getSpecialAttributeNames().length];

        // store data
        for (Example example : exampleSet) {
            int counter = 0;

            // common attributes as double (both numerical and nominal)
            for (Attribute attribute : exampleSet.getAttributes()) {
                value[counter] = example.getValue(attribute);
                counter++;
            }

            // special attributes as double (class names)
            Iterator<AttributeRole> it = exampleSet.getAttributes().specialAttributes();
            while (it.hasNext()) {
                AttributeRole role = it.next();
                Attribute specialAttribute = role.getAttribute();
                value[counter] = example.getValue(specialAttribute);
                counter++;
            }

            // add it into the table
            table.add(new SimpleDataTableRow(value.clone()));
        }

        // Mark special attributes as special
        return table;
    }

    // support function: connect two string arrays together. Do not confuse with string connection.
    public static String[] concat(String[] first, String[] second) {
        String[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
