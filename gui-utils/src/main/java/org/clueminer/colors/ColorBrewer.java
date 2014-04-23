package org.clueminer.colors;

import java.awt.Color;
import org.clueminer.dataset.api.ColorGenerator;

/**
 * A set of fixed colors
 *
 * @author Tomas Barton
 */
public class ColorBrewer implements ColorGenerator {

    private static final long serialVersionUID = -4190241089099580601L;

    private static final Color[] scheme1 = new Color[]{
        new Color(178, 24, 43), new Color(33, 102, 172), new Color(161, 215, 106),
        new Color(5, 48, 97), new Color(244, 165, 130), new Color(153, 112, 171),
        new Color(90, 174, 97), new Color(208, 28, 139)};
    private final RandomColorsGenerator rg = new RandomColorsGenerator();
    private int cnt;

    public ColorBrewer() {
        cnt = 0;
    }

    @Override
    public Color next() {
        if (cnt < scheme1.length) {
            return scheme1[cnt++];
        }
        return rg.next();

    }

    @Override
    public Color next(Color base) {
        return rg.next();
    }

}
