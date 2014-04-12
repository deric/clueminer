package org.clueminer.importer.gui;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.ContainerLoader;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.spi.AnalysisListener;

/**
 *
 * @author Tomas Barton
 */
public class DataTableModel extends AbstractTableModel implements AnalysisListener {

    private ContainerLoader container;
    private JTable table;

    public DataTableModel() {

    }

    @Override
    public int getRowCount() {
        if (container == null) {
            return 0;
        }
        return container.getInstanceCount();
    }

    @Override
    public int getColumnCount() {
        if (container == null) {
            return 0;
        }
        return container.getAttributeCount();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (container != null) {
            if (rowIndex < container.getInstanceCount()) {
                InstanceDraft draft = container.getInstance(rowIndex);
                if (columnIndex < draft.size()) {
                    return draft.getValue(columnIndex);
                }
            }
        }
        return null;
    }

    public ContainerLoader getContainer() {
        return container;
    }

    public void setContainer(ContainerLoader container) {
        this.container = container;
        updateAttributes();
        updateData();
        fireTableStructureChanged();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        AttributeDraft attr = container.getAttribute(columnIndex);
        return attr.getType().getClass();
    }

    private void updateData() {
        if (container != null) {
            int j = 0;
            for (InstanceDraft draft : container.getInstances()) {
                for (int i = 0; i < draft.size(); i++) {
                    setValueAt(draft.getValue(i), j, i);
                }
                fireTableChanged(new TableModelEvent(this, j));
                j++;
            }
            fireTableDataChanged();
        }
    }

    @Override
    public void analysisFinished(ContainerLoader container) {
        setContainer(container);
    }

    /**
     * Updates table header
     */
    private void updateAttributes() {
        if (container != null && table != null) {
            JTableHeader th = table.getTableHeader();
            TableColumnModel tcm = th.getColumnModel();
            TableColumn tc;
            int index;
            for (AttributeDraft attr : container.getAttributes()) {
                index = attr.getIndex();
                if (index < tcm.getColumnCount()) {
                    tc = tcm.getColumn(attr.getIndex());
                } else {
                    tc = new TableColumn(index);
                    tcm.addColumn(tc);
                }
                tc.setHeaderValue(attr.getName());
                System.out.println("setting header: " + attr.getName());
            }
            th.repaint();
        }
    }

    /**
     * We need reference to JTable in order to work with table header
     *
     * @param table
     */
    public void setTable(JTable table) {
        this.table = table;
    }

}
