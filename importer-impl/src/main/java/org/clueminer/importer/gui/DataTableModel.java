package org.clueminer.importer.gui;

import javax.swing.table.AbstractTableModel;
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
                if (rowIndex < draft.size()) {
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
        System.out.println("got new container: " + container);
        System.out.println("attr: " + container.getAttributeCount() + " lines: " + container.getInstanceCount());
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
            fireTableStructureChanged();
        }
    }

    @Override
    public void analysisFinished(ContainerLoader container) {
        setContainer(container);
    }

}
