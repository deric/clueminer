/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.validation.api.builtin.stringvalidation;

import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Validator;
import org.openide.util.NbBundle;

/**
 * Does not allow a string to terminate with a particular character
 *
 * @author Tim Boudreau
 */
final class MayNotEndWithValidator extends StringValidator {
    private final char c;
    public MayNotEndWithValidator(char c) {
        this.c = c;
    }

    @Override
    public void validate(Problems problems, String compName, String model) {
        if (model != null && model.charAt(model.length() - 1) == c) {
            problems.add(NbBundle.getMessage(MayNotEndWithValidator.class,
                    "MAY_NOT_END_WITH", compName, new String(new char[] { c })));
        }
    }

}
