package org.clueminer.wellmap;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.hts.api.HtsPlate;
import org.clueminer.project.api.Project;
import org.clueminer.project.api.Workspace;
import org.clueminer.project.api.WorkspaceListener;
import static org.clueminer.wellmap.WellMapTopComponent.project;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class WellMapLookupListener implements WorkspaceListener {

    private WellMapTopComponent component;
    private static final Logger logger = Logger.getLogger(WellMapLookupListener.class.getName());
    private Lookup.Result<HtsPlate> htsResult;

    public WellMapLookupListener(WellMapTopComponent component, Lookup.Result<HtsPlate> result) {
        this.component = component;
        this.htsResult = result;
    }

    @Override
    public void initialize(Workspace workspace) {
        logger.log(Level.INFO, "wellmap listener initialized");
    }

    @Override
    public void select(Workspace workspace) {
        logger.log(Level.INFO, "workspace: {0}", workspace.toString());
        logger.log(Level.INFO, "selected");
        logger.log(Level.INFO, "workspace selected: got result (plate)");
        htsResult = workspace.getLookup().lookupResult(HtsPlate.class);
        htsResult.addLookupListener(component);
        logger.log(Level.INFO, "lookup res= {0}", htsResult.toString());

        HtsPlate plt = workspace.getLookup().lookup(HtsPlate.class);
        logger.log(Level.INFO, "got plate, size: {0}", plt);
        component.update(plt);
    }

    @Override
    public void unselect(Workspace workspace) {
        logger.log(Level.INFO, "component unselected");
    }

    @Override
    public void close(Workspace workspace) {
        logger.log(Level.INFO, "component closed");
        if (htsResult != null) {
            htsResult.removeLookupListener(component);
        }
    }

    @Override
    public void disable() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void projectActivated(Project proj) {
        project = proj;
        component.projectChanged();
    }
}
