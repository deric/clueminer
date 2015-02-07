package org.clueminer.meta.view;

import ca.odell.glazedlists.gui.TableFormat;

/**
 *
 * @author Tomas Barton
 */
public class MetaTableFormat implements TableFormat<String[]> {

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "k";
            case 1:
                return "score";
            case 2:
                return "template";
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public Object getColumnValue(String[] baseObject, int column) {
        if (column < 0 || column > 2) {
            throw new IllegalStateException();
        }
        return baseObject[column];
    }
}
