package org.clueminer.export.sorting;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.prefs.Preferences;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 *
 * @author Tomas Barton
 */
public class SortingOptions extends JPanel {

    private static final long serialVersionUID = -2124372034270507212L;
    public static final String INCLUDE_HEADER = "include_header";
    private JCheckBox chckIncludeHeader;

    public SortingOptions() {
        initComponets();
    }

    void updatePreferences(Preferences p) {
        p.putBoolean(INCLUDE_HEADER, chckIncludeHeader.isSelected());
    }

    private void initComponets() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;

        chckIncludeHeader = new JCheckBox("include header");
        chckIncludeHeader.setSelected(true);
        add(chckIncludeHeader, c);

    }

}
