package org.clueminer.meta.view;

import java.util.Comparator;
import org.clueminer.meta.api.MetaResult;

/**
 *
 * @author Tomas Barton
 */
public class ElementComparator implements Comparator<MetaResult> {

    private final MetaPanel panel;

    public ElementComparator(MetaPanel panel) {
        this.panel = panel;
    }

    @Override
    public int compare(MetaResult o1, MetaResult o2) {
        if (panel.getEvaluator().isMaximized()) {
            if (o1.getScore() < o2.getScore()) {
                return 1;
            }
            if (o1.getScore() > o2.getScore()) {
                return -1;
            }
            return 0;
        } else {
            if (o1.getScore() < o2.getScore()) {
                return -1;
            }
            if (o1.getScore() > o2.getScore()) {
                return 1;
            }
            return 0;
        }
    }

}
