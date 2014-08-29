package org.clueminer.branding.actions;

import java.awt.event.ActionEvent;
import org.clueminer.project.api.ProjectControllerUI;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Tomas Barton
 */
public final class NewProject extends SystemAction {

    private static final long serialVersionUID = -5644338153996509293L;

    @Override
    public String getName() {
        return NbBundle.getMessage(NewProject.class, "CTL_NewProject");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        Lookup.getDefault().lookup(ProjectControllerUI.class).newProject();
    }

    @Override
    public boolean isEnabled() {
        return Lookup.getDefault().lookup(ProjectControllerUI.class).canNewProject();
    }
}
