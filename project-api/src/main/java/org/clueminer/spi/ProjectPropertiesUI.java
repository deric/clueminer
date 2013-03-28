package org.clueminer.spi;

import javax.swing.JPanel;
import org.clueminer.project.api.Project;

/**
 *
 * @author Tomas Barton
 */
public interface ProjectPropertiesUI {

    public JPanel getPanel();

    public void setup(Project project);

    public void unsetup(Project project);
}
