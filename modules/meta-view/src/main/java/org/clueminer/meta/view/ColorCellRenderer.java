/*
 * Copyright (C) 2011-2017 clueminer.org
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
            c.setBackground(Color.RED);
        }
        return c;
    }

}
