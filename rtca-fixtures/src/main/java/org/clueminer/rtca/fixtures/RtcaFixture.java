package org.clueminer.rtca.fixtures;

import java.io.File;
import java.io.IOException;
import org.clueminer.fixtures.AbstractFixture;

/**
 *
 * @author Tomas Barton
 */
public class RtcaFixture  extends AbstractFixture {

    /**
     * Pretty empty (but valid) RTCA file
     *
     * @return
     * @throws IOException
     */
    public File rtcaTest() throws IOException {        
        return resource("RTCA/scan_plate_data.plt");
    }

    /**
     * Sample RTCA data from a real experiment
     *
     * @return RTCA file (an MS Access database)
     * @throws IOException
     */
    public File rtcaData() throws IOException {
        return resource("RTCA/shHT29_H+T.plt");
    }

    public File rtcaTextFile() throws IOException {
        return resource("RTCA/0424061749P1.rtca");
    }

    public File sdfTest() throws IOException {
        return resource("RTCA/plate.sdf");
    }
    
    public File rtcaHts() throws IOException {
        return resource("RTCA/1309051100HT1_A113710.plt");
    }
}
