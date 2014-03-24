/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.validation.api.ui.swing;

import javax.swing.JComponent;
import org.netbeans.validation.api.Problem;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.ui.ValidationListener;
import org.netbeans.validation.api.ui.ValidationUI;


/**
 * Base class for ValidationListeners.
 *
 * @author Tim Boudreau
 */
public abstract class AbstractValidationListener<CompType extends JComponent, T>
        extends ValidationListener<CompType> {
    private final Validator<T> validator;
    /**
     * Create a new AbstractValidationListener for the single component
     * passed here as an argument.  If the component is not expected to
     * live after the validator is detached, you can add this object as a
     * listener to the component in the constructor (but remember that
     * this means the component will reference this validator forever).
     * @param comp
     */
    public AbstractValidationListener(Class<CompType> type, CompType comp, ValidationUI ui, Validator<T> validator) {
        super(type, ui, comp);
        this.validator = validator;
    }

    /**
     * Get the name of the component which should be passed to
     * validate.  The default implementation delegates to
     * <code>nameForComponent</code> which will either return the
     * client-property based name or the result of getName() on
     * the component.
     *
     * @param comp The component
     * @return A localized name
     */
    protected String findComponentName (CompType comp) {
        return SwingValidationGroup.nameForComponent(comp);
    }

    /**
     * Get the model object that will be passed to validate
     * @param comp The component
     * @return The model object
     */
    protected abstract T getModelObject(CompType comp);

    /**
     * Called when validation runs.  The default implementation does nothing;
     * some validators may want to change the visual appearance of the component
     * to indicate an error.
     *
     * @param component The component
     * @param validationResult The result of validation
     */
    protected void onValidate(CompType component, Problem validationResult){}

    @Override
    protected final void performValidation(Problems ps) {
        CompType comp = getTarget();
        if (!comp.isEnabled()) {
            return;
        }
        Problems problems = new Problems();
        validator.validate(problems, SwingValidationGroup.nameForComponent(comp),
                getModelObject(comp)); //XXX generics quirk
        onValidate(getTarget(), problems.getLeadProblem());
    }


}
