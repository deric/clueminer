package org.clueminer.evolution;

import it.unimi.dsi.fastutil.Hash;
import org.clueminer.clustering.api.Clustering;

/**
 *
 * @author Tomas Barton
 * @param <K>
 */
public class ClustHash<K extends Clustering> implements Hash.Strategy<K> {

    @Override
    public int hashCode(K o) {
        return o.hashCode();
    }

    @Override
    public boolean equals(K a, K b) {
        return a.equals(b);
    }

}
