package org.clueminer.clustering.api;

/**
 * Determines whether dataset will be clustered by rows, columns or both
 *
 * @see {@link HierarchicalResult}
 *
 * @author Tomas Bruna
 */
public enum ClusteringType {

    ROWS_CLUSTERING,
    COLUMNS_CLUSTERING,
    BOTH;

    public static ClusteringType parse(Object o) {
        if (o instanceof ClusteringType) {
            return (ClusteringType) o;
        }
        if (o instanceof String) {
            return ClusteringType.valueOf(o.toString());
        }
        throw new RuntimeException("could not parse ClusteringType: " + o);
    }
}
