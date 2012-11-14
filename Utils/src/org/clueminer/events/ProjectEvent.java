package org.clueminer.events;

import java.util.EventObject;

/**
 *
 * @author Tomas Barton
 */
public class ProjectEvent extends EventObject {
    private static final long serialVersionUID = -1461721026362147694L;
    
    public ProjectEvent(Object source){
        super(source);
    }
}
