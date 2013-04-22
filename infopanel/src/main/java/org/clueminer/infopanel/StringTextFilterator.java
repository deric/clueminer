package org.clueminer.infopanel;

import ca.odell.glazedlists.TextFilterator;
import java.util.List;

/**
 *
 * @author Tomas Barton
 */
public class StringTextFilterator implements TextFilterator<String[]> {

    @Override
    public void getFilterStrings(List<String> baseList, String[] element) {
        baseList.add(element[0]);
        baseList.add(element[1]);
    }
}