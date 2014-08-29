package org.clueminer.events;

import org.clueminer.events.LogEvent;
import java.util.EventListener;

/**
 *
 * @author Tomas Barton
 */

public interface LogListener extends EventListener {

    public void fire(LogEvent evt);

}
