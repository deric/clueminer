/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.validation.api;

/**
 * Convenience base class for validators.
 *
 * @author Tim Boudreau
 */
public abstract class AbstractValidator<T> implements Validator<T> {
    private final Class<T> type;
    protected AbstractValidator(Class<T> type) {
        this.type = type;
    }

    /**
     * Model type for this validator - the type of argument it validates.
     * @return The model type
     */
    @Override
    public final Class<T> modelType() {
        return type;
    }
}
