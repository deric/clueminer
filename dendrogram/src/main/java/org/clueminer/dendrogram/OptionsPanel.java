package org.clueminer.dendrogram;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringFactory;

/**
 *
 * @author Tomas Barton
 */
public class OptionsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 7332498789632451008L;
    private final DendroPanel panel;
    private JComboBox algBox;
    private ClusteringFactory cf;

    /**
     * Creates new form OptionsPanel
     * @param panel
     */
    public OptionsPanel(DendroPanel panel) {
        this.panel = panel;
        initComponents();
    }

    private void initComponents() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        algBox = new JComboBox();

        cf = ClusteringFactory.getInstance();
        for (ClusteringAlgorithm a : cf.getAll()) {
            algBox.addItem(a.getName());
        }
        algBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.setAlgorithm((AgglomerativeClustering) cf.getProvider((String) algBox.getSelectedItem()));
                panel.execute();
            }
        });
        add(algBox);
    }
}
