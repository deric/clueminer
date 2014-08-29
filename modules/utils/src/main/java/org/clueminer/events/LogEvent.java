package org.clueminer.events;

import java.util.EventObject;

/**
 *
 * @author Tomas Barton
 */
public class LogEvent extends EventObject {
    private static final long serialVersionUID = -5558613470893259258L;

    public LogEvent(Object source) {
        super(source);
    }
}
