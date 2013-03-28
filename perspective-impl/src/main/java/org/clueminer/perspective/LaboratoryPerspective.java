package org.clueminer.perspective;


import javax.swing.Icon;
import org.clueminer.perspective.spi.Perspective;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Perspective.class, position = 200)
public class LaboratoryPerspective implements Perspective {

    
    @Override
    public Icon getIcon() {
        return ImageUtilities.loadImageIcon("org/clueminer/perspective/resources/laboratory.png", false);
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(LaboratoryPerspective.class, "LaboratoryPerspective.name");
    }

    @Override
    public String getName() {
        return "datalab";
    }
}
