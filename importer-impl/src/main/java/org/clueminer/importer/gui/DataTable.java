package org.clueminer.importer.gui;

import javax.swing.table.AbstractTableModel;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.ContainerLoader;
import org.clueminer.io.importer.api.InstanceDraft;

/**
 *
 * @author Tomas Barton
 */
public class DataTable extends AbstractTableModel {

    private ContainerLoader container;

    public DataTable() {

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
            return container.getInstance(rowIndex).getValue(rowIndex);
        }
        return null;
    }

    public ContainerLoader getContainer() {
        return container;
    }

    public void setContainer(ContainerLoader container) {
        this.container = container;
        updateData();
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
                j++;
            }
        }
    }

}
