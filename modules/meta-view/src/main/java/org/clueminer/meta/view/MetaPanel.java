package org.clueminer.meta.view;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
class MetaPanel extends JPanel {

    private static final long serialVersionUID = 6800384383501394578L;
    private JTable instaceJTable;
    private JScrollPane instanceListScrollPane;
    private final EventList<String[]> resultsList;
    private Dataset<? extends Instance> dataset;

    public MetaPanel() {
        this.resultsList = new BasicEventList<>();
        initialize();
    }

    private void initialize() {
        setLayout(new GridBagLayout());
        JTextField filterEdit = new JTextField(10);

        // lock while creating the transformed models
        resultsList.getReadWriteLock().readLock().lock();
        try {
            SortedList<String[]> sortedItems = new SortedList<>(resultsList, new ElementComparator());

            //FilterList<String[]> textFilteredIssues = new FilterList<>(propertieList, new TextComponentMatcherEditor<>(filterEdit, new StringTextFilterator()));
            DefaultEventTableModel<String[]> infoListModel = new DefaultEventTableModel<>(sortedItems, new InfoTableFormat());
            instaceJTable = new JTable(infoListModel);
            TableComparatorChooser tableSorter = TableComparatorChooser.install(instaceJTable, sortedItems, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE);
        } finally {
            resultsList.getReadWriteLock().readLock().unlock();
        }

        add(new JLabel("Filter: "), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        add(filterEdit, new GridBagConstraints(0, 0, 1, 1, 0.15, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 55, 5, 5), 0, 0));
        instanceListScrollPane = new JScrollPane(instaceJTable);
        add(instanceListScrollPane, new GridBagConstraints(0, 1, 1, 4, 0.85, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    }

    public void updateDataset(Dataset<? extends Instance> d) {
        this.dataset = d;
    }

    /**
     * Removes all items from the browser
     */
    public void clear() {
        resultsList.clear();
    }

    public void setData(Object[][] data) {
        for (Object[] line : data) {
            //convert to String
            String[] stringArray = Arrays.copyOf(line, line.length, String[].class);
            resultsList.add(stringArray);
        }
    }

}
