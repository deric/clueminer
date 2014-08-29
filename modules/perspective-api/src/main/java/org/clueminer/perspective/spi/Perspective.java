package org.clueminer.perspective.spi;

import javax.swing.Icon;

/**
 *
 * @author Tomas Barton
 */
public interface Perspective {

    /**
     * Return the name to display in the user interface.
     *
     * @return the perspective display name
     */
    public String getDisplayName();

    /**
     * Return a unique identifier for this perspective.
     *
     * @return the name of the perspective
     */
    public String getName();

    /**
     * Return the icon of the perspective.
     *
     * @return the perspective's icon, or <code>null</code>
     */
    public Icon getIcon();
}
