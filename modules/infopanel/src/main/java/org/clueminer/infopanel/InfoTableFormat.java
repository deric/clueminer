package org.clueminer.infopanel;

import ca.odell.glazedlists.gui.TableFormat;

/**
 *
 * @author Tomas Barton
 */
public class InfoTableFormat implements TableFormat<String[]> {

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "Key";
        } else if (column == 1) {
            return "Value";
        }

        throw new IllegalStateException();
    }

    @Override
    public Object getColumnValue(String[] baseObject, int column) {
        if (column == 0) {
            return baseObject[0];
        } else if (column == 1) {
            return baseObject[1];
        }
        throw new IllegalStateException();
    }
}
