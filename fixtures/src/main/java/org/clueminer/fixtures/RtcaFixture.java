package org.clueminer.fixtures;

import java.io.File;
import java.io.IOException;

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
        String config = "scan_plate_data.plt";
        return new File(localPath + "/RTCA/" + config);
    }

    /**
     * Sample RTCA data from a real experiment
     *
     * @return RTCA file (an MS Access database)
     * @throws IOException
     */
    public File rtcaData() throws IOException {
        String config = "shHT29_H+T.plt";
        return new File(localPath + "/RTCA/" + config);
    }

    public File rtcaTextFile() throws IOException {
        String config = "0424061749P1.rtca";
        return new File(localPath + "/RTCA/" + config);
    }

    public File sdfTest() throws IOException {
        String config = "plate.sdf";
        return new File(localPath + "/RTCA/" + config);
    }
}
