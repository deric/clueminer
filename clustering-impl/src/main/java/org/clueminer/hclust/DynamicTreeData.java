package org.clueminer.hclust;

import org.clueminer.clustering.api.dendrogram.DendroData;
import org.clueminer.clustering.api.dendrogram.DendroNode;

/**
 *
 * @author Tomas Barton
 */
public class DynamicTreeData implements DendroData {
    
    private DendroNode root;
    
    public DynamicTreeData(){
        
    }

    @Override
    public int numLeaves() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int treeLevels() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int numNodes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DendroNode getRoot() {
        return root;
    }
    
    

}
