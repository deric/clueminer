package org.clueminer.infopanel;

import java.util.Comparator;

/**
 *
 * @author Tomas Barton
 */
public class ElementComparator implements Comparator<String[]> {

    @Override
    public int compare(String[] o1, String[] o2) {
        return o1[0].compareTo(o2[0]);
    }

}
