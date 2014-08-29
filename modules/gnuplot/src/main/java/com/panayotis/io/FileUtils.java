/*
 * FileUtils.java
 *
 * Created on October 14, 2007, 5:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.panayotis.io;

import java.io.File;
import java.util.StringTokenizer;

/**
 * This object is used to serch for an executable in the current $PATH.
 * @author teras
 */
public class FileUtils {
    
    /**
     * Check if the specified file path is a valid file.
     * @param path The file path to check if it is valid.
     * @return Pointer to this file, if this file is valid. Otherwise return null.
     */
    public final static File getExec(String path) {
        File file = new File(path);
        if (file.isFile() && file.canRead()) return file;
        file = new File(path+".exe");
        if (file.isFile() && file.canRead()) return file;
        return null;
    }
    
    /**
     * This method browses current path to search for a file. Typically this should be
     * an executable, but it is impossible under Java to check if this file has the execution
     * bit on. Apart from the user defined $PATH variable, common bin-directory places
     * are searched.
     * @param prog The file to check if it exists in the system $PATH
     * @return The path of the specified program. If it is not found, the program name is returned.
     */
    public final static String findPathExec(String prog) {
        String [] xtrapath = {"/bin", "/usr/bin", "/usr/local/bin",
        "/sbin", "/usr/sbin", "/usr/local/sbin",
        "/opt/bin", "/opt/local/bin",
        "/opt/sbin", "/opt/local/sbin",
        "/sw/bin", "c:\\cygwin\\bin", "."};
        
        String pathsep = System.getProperty("path.separator");
        String fileexec = System.getProperty("file.separator") + prog;
        
        /* Create enriched path */
        StringBuffer path = new StringBuffer();
        path.append(System.getenv("PATH"));
        for(int i=0 ; i < xtrapath.length ; i++) {
            path.append(pathsep).append(xtrapath[i]);
        }
        
        StringTokenizer st = new StringTokenizer(path.toString(), pathsep);
        File file;
        while (st.hasMoreTokens()) {
            file = getExec(st.nextToken()+fileexec);
            if (file!=null) return file.getPath();
        }
        return prog;
    }
    
}
