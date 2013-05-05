package com.panayotis.gnuplot.style;

import java.util.ArrayList;
import org.clueminer.gnuplot.ColorGenerator;


/**
 *
 * @author Tomas Barton
 */
public class ColorPalette {

    private static ArrayList<String> palet = new ArrayList<String>(15);

    private static void init() {
        palet.add("red");
        palet.add("blue");
        palet.add("#32CD32");//limegreen
        palet.add("violet");
        palet.add("orange");
        palet.add("cyan");
        palet.add("skyblue");
        palet.add("#FFA07A");
    }

    public static String getColor(int i) {
        if(palet.isEmpty()){
            init();
        }
        if (i >= palet.size()) {
            palet.add(ColorGenerator.getHexColor());
        }
        return palet.get(i);
    }

    public static int size() {
        return palet.size();
    }
}
