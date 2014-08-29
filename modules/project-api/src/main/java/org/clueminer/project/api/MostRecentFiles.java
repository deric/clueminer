package org.clueminer.project.api;

import java.util.List;

/**
 *
 * @author Tomas Barton
 */
public interface MostRecentFiles {

    public void addFile(String absolutePath);

    public List<String> getMRUFileList();
}