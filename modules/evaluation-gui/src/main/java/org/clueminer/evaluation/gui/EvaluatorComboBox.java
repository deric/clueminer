package org.clueminer.evaluation.gui;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 *
 * @author Tomas Barton
 */
public class EvaluatorComboBox extends AbstractListModel<String> implements ComboBoxModel<String> {

    private static final long serialVersionUID = 4980257827981287952L;
    private final String[] providers;
    private String selection = null;

    public EvaluatorComboBox(String[] providers) {
        this.providers = providers;
    }

    @Override
    public int getSize() {
        return providers.length;
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selection = (String) anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selection;
    }

    @Override
    public String getElementAt(int index) {
        return providers[index];
    }
}
