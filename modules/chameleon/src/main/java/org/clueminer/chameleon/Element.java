package org.clueminer.chameleon;

/**
 *
 * @author Tomas Bruna
 */
public class Element implements Comparable {

    public double value;
    public int firstCluster;
    public int secondCluster;

    Element(double value, int firstCluster, int secondCluster) {
        this.value = value;
        this.firstCluster = firstCluster;
        this.secondCluster = secondCluster;
    }

    @Override
    public int compareTo(Object o) {
        Element e = (Element) o;
        if (value > e.value) {
            return -1;
        } else if (value < e.value) {
            return 1;
        }
        return 0;
    }

}
