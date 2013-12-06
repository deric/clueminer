package org.clueminer.magic;

import com.google.common.base.CharMatcher;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author Tomas Barton
 */
public class TxtDetect implements Detector {

    private LinkedList<Separator> separators = new LinkedList<Separator>();

    public TxtDetect() {
        separators.add(new Separator(','));
        separators.add(new Separator(';'));
        separators.add(new Separator('.'));
        separators.add(new Separator('\t'));
    }

    @Override
    public DatasetProperties detect(BufferedReader br) throws IOException {
        String line;
        int count, i;
        while ((line = br.readLine()) != null) {
            for (Separator s : separators) {
                count = CharMatcher.is(s.getSymbol()).countIn(line);
                if (count == 0) {
                    //    separators.remove(s);
                } else {
                    System.out.println("symbol " + s.getSymbol() + " cnt: " + count);
                }
                System.out.println(line);

            }
            System.out.println("total sep: " + separators.size());

            //
        }
        return null;
    }

    public void addSeparator(Separator sep) {
        separators.add(sep);
    }
}
