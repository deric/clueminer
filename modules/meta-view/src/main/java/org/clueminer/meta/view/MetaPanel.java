package org.clueminer.meta.view;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.factory.EvaluationFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.meta.api.MetaFlag;
import org.clueminer.meta.api.MetaResult;
import org.clueminer.meta.api.MetaStorage;
import org.clueminer.project.api.ProjectController;
import org.clueminer.project.api.Workspace;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class MetaPanel extends JPanel {

    private static final long serialVersionUID = 6800384383501394578L;
    private JTable instaceJTable;
    private JScrollPane instanceListScrollPane;
    private final EventList<MetaResult> resultsList;
    private Dataset<? extends Instance> dataset;
    private MetaStorage storage;
    private JComboBox<String> evolutions;
    private JComboBox<String> evaluators;
    private ClusterEvaluation evaluator;
    private JLabel lbResults;
    private Collection<? extends Clustering> clusterings;
    private static final Logger logger = Logger.getLogger(MetaPanel.class.getName());
    private Object2ObjectMap<String, MetaResult> map;
    private Int2ObjectOpenHashMap<MetaResult> chash;
    private int matched;

    public MetaPanel() {
        this.resultsList = new BasicEventList<>();
        initialize();
    }

    private void initialize() {
        setLayout(new GridBagLayout());
        evolutions = new JComboBox<>();
        evolutions.addActionListener(new QueryReloader());
        evaluators = new JComboBox<>(initEvaluators());
        evaluators.addActionListener(new QueryReloader());

        // lock while creating the transformed models
        resultsList.getReadWriteLock().readLock().lock();
        try {
            SortedList<MetaResult> sortedItems = new SortedList<>(resultsList, new ElementComparator(this));

            //FilterList<String[]> textFilteredIssues = new FilterList<>(propertieList, new TextComponentMatcherEditor<>(filterEdit, new StringTextFilterator()));
            DefaultEventTableModel<MetaResult> infoListModel = new DefaultEventTableModel<>(sortedItems, new MetaTableFormat());
            instaceJTable = new JTable(infoListModel);
            instaceJTable.setDefaultRenderer(MetaResult.class, new ColorCellRenderer(sortedItems));
            TableComparatorChooser tableSorter = TableComparatorChooser.install(instaceJTable, sortedItems, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE);
        } finally {
            resultsList.getReadWriteLock().readLock().unlock();
        }

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.BOTH;

        add(new JLabel("Source: "), c);
        c.gridy = 1;
        add(new JLabel("Evaluation: "), c);
        c.gridy = 0;
        c.insets = new Insets(5, 55, 5, 5);
        c.weightx = 0.15;
        add(evolutions, c);
        c.gridy = 1;
        c.insets = new Insets(5, 70, 5, 5);
        add(evaluators, c);
        lbResults = new JLabel("Meta score");
        c.gridx = 0;
        c.gridy = 2;
        add(lbResults, c);
        instanceListScrollPane = new JScrollPane(instaceJTable);
        add(instanceListScrollPane, new GridBagConstraints(0, 3, 1, 4, 0.85, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    }

    private String[] initEvaluators() {
        EvaluationFactory ef = EvaluationFactory.getInstance();
        List<String> list = ef.getProviders();
        return list.toArray(new String[list.size()]);
    }

    public void updateDataset(Dataset<? extends Instance> d) {
        this.dataset = d;
        updateResult();
    }

    protected String currentEvolution() {
        return (String) evolutions.getSelectedItem();
    }

    public ClusterEvaluation getEvaluator() {
        if (evaluator == null) {
            evaluator = currentEvaluator();
        }
        return evaluator;
    }

    protected ClusterEvaluation currentEvaluator() {
        EvaluationFactory ef = EvaluationFactory.getInstance();
        return ef.getProvider((String) evaluators.getSelectedItem());
    }

    protected void updateResult() {
        if (storage != null) {
            String evo = currentEvolution();
            evaluator = currentEvaluator();
            if (evo != null && evaluator != null && getDataset() != null) {
                final Collection<MetaResult> col = storage.findResults(getDataset(), evo, evaluator);

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        updateData(col);
                    }
                });

            } else {
                logger.log(Level.SEVERE, "missing data, evo: {0}, eval: {1}, dataset: {2}", new Object[]{evo, evaluator, getDataset()});
            }
        }
    }

    private Dataset<? extends Instance> getDataset() {
        if (dataset == null) {
            ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
            Workspace w = pc.getCurrentWorkspace();
            //when no project is opened, workspace is null
            if (w != null) {
                dataset = pc.getCurrentWorkspace().getLookup().lookup(Dataset.class);
            }
        }
        return dataset;
    }

    /**
     * Removes all items from the browser
     */
    public void clear() {
        resultsList.clear();
    }

    public void updateData(Collection<MetaResult> col) {
        resultsList.clear();
        map = new Object2ObjectOpenHashMap<>(col.size());
        chash = new Int2ObjectOpenHashMap<>(col.size());
        for (MetaResult res : col) {
            resultsList.add(res);
            map.put(res.getTemplate(), res);
            chash.put(res.getHash(), res);
        }
    }

    public void setStorage(MetaStorage storage) {
        this.storage = storage;
        if (storage != null) {
            Collection<String> algs = storage.getEvolutionaryAlgorithms();
            if (algs.size() > 0) {
                evolutions.setModel(new DefaultComboBoxModel<>(algs.toArray(new String[algs.size()])));
            }
        }
    }

    public void setReferenceClustering(Collection<? extends Clustering> res) {
        clusterings = res;
        MetaResult m;
        matched = 0;
        int hashMatch = 0;
        String templ;
        int hash;
        //compare with meta database
        for (Clustering c : res) {
            if (c != null) {
                templ = c.getParams().toString();
                if (map.containsKey(templ)) {
                    m = map.get(templ);
                    m.setFlag(MetaFlag.MATCHED);
                    matched++;
                }
                hash = c.hashCode();
                if (chash.containsKey(hash)) {
                    m = chash.get(hash);
                    m.setFlag(MetaFlag.HASH);
                    hashMatch++;
                }
            }
        }
        lbResults.setText("matched " + matched + " / " + map.size() + " hash: " + hashMatch);
        logger.log(Level.INFO, "clustering matched {0} records in meta-db", matched);
    }

    private class QueryReloader implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            updateResult();
        }
    }

}
