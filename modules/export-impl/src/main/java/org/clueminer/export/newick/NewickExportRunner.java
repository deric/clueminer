package org.clueminer.export.newick;

import java.io.File;
import java.util.prefs.Preferences;
import org.clueminer.clustering.gui.ClusterAnalysis;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Tomas Barton
 */
public class NewickExportRunner implements Runnable {

    private final File file;
    private final ClusterAnalysis analysis;
    private final Preferences pref;
    private final ProgressHandle ph;

    public NewickExportRunner(File file, ClusterAnalysis analysis, Preferences pref, ProgressHandle ph) {
        this.file = file;
        this.analysis = analysis;
        this.pref = pref;
        this.ph = ph;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
