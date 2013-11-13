package org.clueminer.magic;

import com.google.common.base.CharMatcher;
import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 * @author Tomas Barton
 */
public class TxtDetect implements Detector {

    @Override
    public DatasetProperties detect(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            int count = CharMatcher.is(',').countIn(line);
            // System.out.println(line);
            // System.out.println("comma cnt: " + count);
            //
        }
        return null;
    }
}
