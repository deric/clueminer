package org.clueminer.dendrogram;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.dendrogram.DendroViewer;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dendrogram.gui.SettingsPanel;
import org.clueminer.utils.Props;

/**
 * Simple dendrogram panel for demo and testing purposes
 *
 * @author deric
 */
public abstract class DendroPanel extends JPanel {

    private static final long serialVersionUID = -8394261467235786115L;
    protected AgglomerativeClustering algorithm;
    protected DendroViewer viewer;
    //original dataset
    private Dataset<? extends Instance> dataset;
    private SettingsPanel panel;
    protected OptionsPanel options;
    protected Props properties;

    public DendroPanel() {
        initComponents();
    }

    /**
     * A template method
     */
    public abstract void initViewer();

    private void initComponents() {
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints c = new GridBagConstraints();
        initViewer();
        panel = new SettingsPanel(viewer);
        options = new OptionsPanel(this);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = c.weighty = 0.2; //no fill while resize
        gbl.setConstraints(panel, c);
        add(panel, c);

        c.gridy = 1;
        gbl.setConstraints(options, c);
        add(options, c);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 2;
        c.gridheight = 1;
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTHEAST;
        c.weightx = c.weighty = 8.0; //ratio for filling the frame space
        gbl.setConstraints((Component) viewer, c);
        this.add((Component) viewer, c);
        setVisible(true);
    }

    public ClusteringAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(ClusteringAlgorithm algorithm) {
        this.algorithm = (AgglomerativeClustering) algorithm;
    }

    public Dataset<? extends Instance> getDataset() {
        return dataset;
    }

    public void setDataset(Dataset<? extends Instance> dataset) {
        this.dataset = dataset;
    }

    public abstract DendrogramMapping execute();

    public DendroViewer getViewer() {
        return viewer;
    }

    public abstract void dataChanged(String datasetName);

    public abstract String[] getDatasets();

    public void fireTreeUpdated() {

    }

    public void setProperties(Props props) {
        this.properties = props;
    }

    public Props getProperties() {
        return properties;
    }

}
