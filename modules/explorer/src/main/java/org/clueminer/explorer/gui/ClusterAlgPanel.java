package org.clueminer.explorer.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringFactory;
import org.clueminer.clustering.gui.ClusteringDialog;
import org.clueminer.clustering.gui.ClusteringDialogFactory;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public class ClusterAlgPanel extends JPanel {

    private static final long serialVersionUID = 3764607764760405449L;

    private JComboBox<String> cbType;
    private JPanel optPanel;
    private String selected = null;
    private ClusteringDialog dialog = null;

    public ClusterAlgPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        ClusteringFactory factory = ClusteringFactory.getInstance();
        cbType = new JComboBox<>(factory.getProvidersArray());
        if (selected != null) {
            cbType.setSelectedItem(selected);
        }
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 1;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.NONE;
        add(cbType, c);
        cbType.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selected = (String) cbType.getSelectedItem();
                removeAll();
                initComponents();
                repaint();
                revalidate();
            }
        });

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        String expName = (String) cbType.getSelectedItem();
        ClusteringAlgorithm alg = factory.getProvider(expName);
        optPanel = getUI(alg);
        add(optPanel, c);
    }

    private JPanel getUI(ClusteringAlgorithm alg) {
        for (ClusteringDialog dlg : ClusteringDialogFactory.getInstance().getAll()) {
            if (dlg.isUIfor(alg)) {
                dialog = dlg;
                return dlg.getPanel();
            }
        }
        //last resort
        return new JPanel();
    }

    public ClusteringAlgorithm getAlgorithm() {
        String algName = (String) cbType.getSelectedItem();
        ClusteringAlgorithm algorithm = ClusteringFactory.getInstance().getProvider(algName);
        return algorithm;
    }

    public Props getProps() {
        if (dialog != null) {
            return dialog.getParams();
        } else {
            throw new RuntimeException("missing dialog");
        }
    }

}
