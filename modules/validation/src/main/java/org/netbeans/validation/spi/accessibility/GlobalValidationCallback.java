/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.validation.spi.accessibility;

import java.util.Collection;
import org.netbeans.validation.api.Problem;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 */
public abstract class GlobalValidationCallback {
    private static final Object LOCK = new Object();
    private static GlobalValidationCallback INSTANCE;
    static GlobalValidationCallback getDefault() {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = new ProxyGlobalCallback();
            }
        }
        return INSTANCE;
    }

    public void onValidationTrigger (Object source, Object triggeringEvent) {

    }

    public void onValidationFinished (Object source, Object triggeringEvent) {

    }

    public abstract void onProblem (Object component, Problem problem);
    public abstract void onProblemCleared (Object component, Problem problem);

    private static final class ProxyGlobalCallback extends GlobalValidationCallback {
        private ProxyGlobalCallback() {

        }

        Collection<? extends GlobalValidationCallback> registered() {
            return Lookup.getDefault().lookupAll(GlobalValidationCallback.class);
        }

        @Override
        public void onProblem(Object component, Problem problem) {
            for (GlobalValidationCallback cb : registered()) {
                try {
                    cb.onProblem(component, problem);
                } catch (Exception e) {
                    Exceptions.printStackTrace(e);
                }
            }
        }

        @Override
        public void onProblemCleared(Object component, Problem problem) {
            for (GlobalValidationCallback cb : registered()) {
                try {
                    cb.onProblemCleared(component, problem);
                } catch (Exception e) {
                    Exceptions.printStackTrace(e);
                }
            }
        }
    }
}
