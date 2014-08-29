package org.clueminer.perspective.api;

import org.clueminer.perspective.spi.Perspective;

/**
 * Controller to manage the perspective system. A perspective is a set of panels
 * in the user interface. 'Visualize' and 'Data Laboratory' are the default
 * perspectives.
 *
 * @author Tomas Barton
 */
public interface PerspectiveController {

    /**
     * Returns the selected perspective or
     * <code>null</code> if no perspective is selected. By default the
     * 'Overview' perspective is selected.
     *
     * @return the currently selected perspective or <code>null</code>
     */
    public Perspective getSelectedPerspective();

    /**
     * Returns all perspectives installed. This is equivalent to
     * <code>Lookup.getDefault().lookupAll(Perspective.class)</code>.
     *
     * @return all installed perspectives
     */
    public Perspective[] getPerspectives();

    /**
     * Switch the current perspective to the given perspective. Only one
     * perspective can be selected at a time.
     *
     * @param perspective the perspective to select
     */
    public void selectPerspective(Perspective perspective);
}
