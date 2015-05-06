package org.clueminer.dendrogram;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.ClusteringFactory;
import org.clueminer.clustering.api.factory.CutoffStrategyFactory;
import org.clueminer.clustering.api.factory.LinkageFactory;

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
    private JComboBox cutoffBox;
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
                String alg = (String) algBox.getSelectedItem();
                //if algorithm was really changed, trigger execution
                if (!alg.equals(panel.getAlgorithm().getName())) {
                    panel.setAlgorithm((AgglomerativeClustering) cf.getProvider(alg));
                    panel.execute();
                }
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
        linkageBox.setSelectedItem(AgglParams.DEFAULT_LINKAGE);
        linkageBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.linkageChanged((String) linkageBox.getSelectedItem());
            }
        });
        add(linkageBox);

        cutoffBox = new JComboBox(CutoffStrategyFactory.getInstance().getProvidersArray());
        cutoffBox.setSelectedItem(CutoffStrategyFactory.getInstance().getDefault().getName());
        cutoffBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.cutoffChanged((String) cutoffBox.getSelectedItem());
            }
        });
        add(cutoffBox);
    }

    public void setDatasets(String[] datasets) {
        for (String str : datasets) {
            dataBox.addItem(str);
        }
    }

    public void selectAlgorithm(String algorithm) {
        algBox.setSelectedItem(algorithm);
    }
}
