package org.clueminer.perspective.impl;

import org.clueminer.perspective.api.PerspectiveController;
import org.clueminer.perspective.spi.Perspective;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = PerspectiveController.class)
public class PerspectiveControllerImpl implements PerspectiveController {

    private static final String SELECTED_PERSPECTIVE_PREFERENCE = "PerspectiveControllerImpl_selectedPerspective";
    //Data
    private String selectedPerspective;
    private final Perspective[] perspectives;

    public PerspectiveControllerImpl() {
        //Load perspectives
        perspectives = Lookup.getDefault().lookupAll(Perspective.class).toArray(new Perspective[0]);

        //Find if there is a default
        String firstPerspective = perspectives.length > 0 ? perspectives[0].getName() : null;
        String defaultPerspectiveName = System.getProperty("org.clueminer.perspective.default");
        if (defaultPerspectiveName != null) {
            for (Perspective p : perspectives) {
                if (p.getName().equals(defaultPerspectiveName)) {
                    selectedPerspective = p.getName();
                    break;
                }
            }
        }
        if (selectedPerspective == null) {
            selectedPerspective = NbPreferences.root().get(SELECTED_PERSPECTIVE_PREFERENCE, firstPerspective);
        }

        //Store selected in prefs
        NbPreferences.root().put(SELECTED_PERSPECTIVE_PREFERENCE, selectedPerspective);

        Perspective selectedPerspectiveInstance = getSelectedPerspective();

        openAndCloseMembers(selectedPerspectiveInstance);

        WindowManager.getDefault().addWindowSystemListener(new PerspectiveWindowSystemListener());
    }

    @Override
    public Perspective[] getPerspectives() {
        return perspectives;
    }

    @Override
    public Perspective getSelectedPerspective() {
        for (Perspective p : perspectives) {
            if (p.getName().equals(selectedPerspective)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public void selectPerspective(Perspective perspective) {
        if (perspective.getName().equals(selectedPerspective)) {
            return;
        }

        openAndCloseMembers(perspective);

        selectedPerspective = perspective.getName();
        NbPreferences.root().put(SELECTED_PERSPECTIVE_PREFERENCE, selectedPerspective);
    }

    private void openAndCloseMembers(Perspective perspective) {
        WindowManager.getDefault().setRole(perspective.getName());
    }
}
