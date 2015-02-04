package org.clueminer.explorer;

import java.util.List;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.evolution.api.Evolution;
import org.openide.nodes.ChildFactory;

/**
 *
 * @author Tomas Barton
 */
public class ClustChildFactory extends ChildFactory<Clustering> {

    @Override
    protected boolean createKeys(List<Clustering> toPopulate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Evolution> getChildren() {
        return null;
        //return Collections.unmodifiableList(children);
    }

}
