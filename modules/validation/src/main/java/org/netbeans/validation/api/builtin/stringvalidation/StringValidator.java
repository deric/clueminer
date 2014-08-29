package org.netbeans.validation.api.builtin.stringvalidation;

import org.netbeans.validation.api.AbstractValidator;

/**
 *
 * @author Tim Boudreau
 */
abstract class StringValidator extends AbstractValidator<String> {
    protected StringValidator() {
        super (String.class);
    }
}
