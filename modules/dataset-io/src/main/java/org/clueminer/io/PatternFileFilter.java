package org.clueminer.io;

import java.io.File;
import java.io.FileFilter;

public class PatternFileFilter implements FileFilter {

    private String pattern;
    
    public PatternFileFilter(String pattern){
        this.pattern=pattern;
        
    }

    public boolean accept(File arg0) {
        return arg0.getName().matches(pattern);
    }
    
    
}
