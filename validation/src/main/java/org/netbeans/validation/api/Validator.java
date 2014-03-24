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
package org.netbeans.validation.api;

import org.netbeans.validation.api.builtin.stringvalidation.StringValidators;

/**
 * Validator that can validate some aspect of a component's model, and
 * indicate problems to the user.
 *
 * <p> Note that the enum {@link StringValidators} provides many
 * built-in validators to perform common tasks.
 *
 * @author Tim Boudreau
 */
public interface Validator<T> {
    /**
     * Validate the passed model.  If the component is invalid, this
     * method shall add problems to the passed list.
     *
     * @param problems A list of problems.
     * @param compName The name of the component in question (may be null in some cases)
     * @param model The model in question
     */
    void validate (Problems problems, String compName, T model);
    /**
     * The type of the model object which can be validated.  Necessary due
     * to limitations of the Java implementation of generics, so that
     * model conversions can be done at runtime, and declaratively registered
     * ValidationListeners can be matched with Validator types.
     * <p/>
     * The return value of this method is expected to remain constant
     * throughout the life of this validator.
     * 
     * @return The type of the model object expected.  Note that a validator
     * may be passed a subclass of this type.
     */
    Class<T> modelType();
}
