package org.clueminer.export.newick;

import java.util.prefs.Preferences;
import javax.swing.JPanel;
import org.clueminer.export.impl.AbstractExporter;

/**
 *
 * @author Tomas Barton
 */
public class NewickExporter extends AbstractExporter {

    private static final String title = "Export to Newick";

    @Override
    public JPanel getOptions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void updatePreferences(Preferences p) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void export(Preferences p) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
