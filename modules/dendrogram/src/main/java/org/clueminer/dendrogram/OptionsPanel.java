package org.clueminer.dendrogram;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.ClusteringFactory;
import org.clueminer.clustering.api.LinkageFactory;

/**
 *
 * @author Tomas Barton
 */
public class OptionsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 7332498789632451008L;
    private final DendroPanel panel;
    private JComboBox algBox;
    private JComboBox dataBox;
    private JComboBox linkageBox;
    private ClusteringFactory cf;

    /**
     * Creates new form OptionsPanel
     *
     * @param panel
     */
    public OptionsPanel(DendroPanel panel) {
        this.panel = panel;
        initComponents();
    }

    private void initComponents() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        cf = ClusteringFactory.getInstance();
        algBox = new JComboBox(cf.getProvidersArray());

        algBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.setAlgorithm((AgglomerativeClustering) cf.getProvider((String) algBox.getSelectedItem()));
                panel.execute();
            }
        });
        add(algBox);

        dataBox = new JComboBox();
        dataBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.dataChanged((String) dataBox.getSelectedItem());
            }
        });
        add(dataBox);

        linkageBox = new JComboBox(LinkageFactory.getInstance().getProvidersArray());
        linkageBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.linkageChanged((String) linkageBox.getSelectedItem());
            }
        });
        add(linkageBox);
    }

    public void setDatasets(String[] datasets) {
        for (String str : datasets) {
            dataBox.addItem(str);
        }
    }
}
