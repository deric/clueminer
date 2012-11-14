package org.clueminer.events;

import java.util.EventListener;

/**
 *
 * @author Tomas Barton
 */
public interface ProjectListener extends EventListener {
    
   
    public void projectClosed();

    public void projectOpened(ProjectEvent evt);
}
