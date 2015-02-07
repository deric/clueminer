package org.clueminer.meta.view;

import ca.odell.glazedlists.SortedList;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.clueminer.meta.api.MetaFlag;
import org.clueminer.meta.api.MetaResult;

/**
 *
 * @author Tomas Barton
 */
public class ColorCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 2576852057802686200L;
    private final SortedList<MetaResult> sortedItems;

    public ColorCellRenderer(SortedList<MetaResult> sortedItems) {
        this.sortedItems = sortedItems;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!hasFocus) {
            MetaResult m = sortedItems.get(row);
            if (m.getFlag() == MetaFlag.MATCHED) {
                c.setBackground(Color.green);
            }
        }
        return c;
    }

}
