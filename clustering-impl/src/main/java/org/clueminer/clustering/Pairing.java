package org.clueminer.clustering;

/**
 * A utility structure for holding the assignment of a cluster to another
 * cluster by means of a high similarity.
 */
public class Pairing implements Comparable<Pairing> {

    /**
     * The similarity of the other cluster to the cluster indicated by
     * {@code pairedIndex}
     */
    public final double similarity;
    /**
     * The index of the cluster that is paired
     */
    public final int pairedIndex;

    public Pairing(double similarity, int pairedIndex) {
        this.similarity = similarity;
        this.pairedIndex = pairedIndex;
    }

    @Override
    public int compareTo(Pairing p) {
        return (int) ((p.similarity - similarity) * Integer.MAX_VALUE);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Pairing) && ((Pairing) o).pairedIndex == pairedIndex;
    }

    @Override
    public int hashCode() {
        return pairedIndex;
    }

    @Override
    public String toString() {
        return "Pair [ " + similarity + ", " + pairedIndex + "]";
    }
}