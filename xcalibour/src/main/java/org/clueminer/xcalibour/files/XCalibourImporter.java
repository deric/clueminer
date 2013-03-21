package org.clueminer.xcalibour.files;

import java.io.File;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.utils.progress.ProgressTicket;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Tomas Barton
 */
public class XCalibourImporter implements LongTask, Runnable {
    
    private ProgressHandle ph;
    
    public XCalibourImporter(File file){
        if(!file.exists()){
            throw new RuntimeException("file "+file.getAbsolutePath()+" not found");
        }
    }
    
    public void setProgressHandle(ProgressHandle ph){
        this.ph = ph;
    }

    @Override
    public boolean cancel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        System.out.println("importing data");
    }

}
