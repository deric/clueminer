/*
 * Copyright (C) 2011-2018 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.importer.gui;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.spi.AnalysisListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public class DataTableModel<E extends InstanceDraft> extends AbstractTableModel implements AnalysisListener {

    private static final long serialVersionUID = 8958158241016938460L;

    private Container<E> container;
    private JTable table;
    private static final Logger LOG = LoggerFactory.getLogger(DataTableModel.class);

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
                    return draft.getObject(columnIndex);
                }
            }
        }
        return null;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(final Container loader) {
        if (loader != null) {
            LOG.info("setting container with {} rows ", loader.getInstanceCount());
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                container = loader;
                updateAttributes();
                updateData();
                fireTableStructureChanged();
                table.revalidate();
                table.repaint();
            }
        });
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        AttributeDraft attr = container.getAttribute(columnIndex);
        if (attr != null) {
            return attr.getJavaType().getClass();
        }
        return null;
    }

    private void updateData() {
        if (container != null) {
            int j = 0;
            for (InstanceDraft draft : container.getInstances()) {
                for (int i = 0; i < draft.size(); i++) {
                    setValueAt(draft.getObject(i), j, i);
                }
                fireTableChanged(new TableModelEvent(this, j));
                j++;
            }
            fireTableDataChanged();
        }
    }

    @Override
    public void analysisFinished(Container container) {
        LOG.info("analysis finished");
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
            for (AttributeDraft attr : container.getAttrIter()) {
                index = attr.getIndex();
                if (index < tcm.getColumnCount()) {
                    tc = tcm.getColumn(attr.getIndex());
                } else {
                    tc = new TableColumn(index);
                    tcm.addColumn(tc);
                }
                tc.setHeaderValue(attr.getName());
                LOG.info("setting header: {} type: {}, role: {}", attr.getName(), attr.getJavaType(), attr.getRole());
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

    @Override
    public void attributeChanged(AttributeDraft attr, Object property) {
        JTableHeader th = table.getTableHeader();
        TableColumnModel tcm = th.getColumnModel();
        TableColumn tc;
        int index = attr.getIndex();
        if (index < tcm.getColumnCount()) {
            tc = tcm.getColumn(attr.getIndex());
        } else {
            tc = new TableColumn(index);
            tcm.addColumn(tc);
        }
        tc.setHeaderValue(attr.getName());
    }

}
