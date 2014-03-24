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
package org.netbeans.validation.api.ui;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.validation.api.Problem;

/**
 *
 * @author Tim Boudreau
 */
final class MulticastValidationUI implements ValidationUI {

    private final Set<ValidationUI> real;

    MulticastValidationUI(ValidationUI... real) {
        this.real = new HashSet<ValidationUI>(Arrays.asList(real));
        assert validUIs(real);
    }

    public void add(ValidationUI ui) {
        if (ui == null) {
            throw new NullPointerException();
        }
        assert !contains(ui) : "Already a member: " + ui;
        real.add (ui);
    }

    public void remove(ValidationUI ui) {
        if (ui == null) {
            throw new NullPointerException();
        }
        assert contains(ui) : "Not a member: " + ui;
        real.remove(ui);
    }

    public boolean contains(ValidationUI check) {
        boolean result = real.contains(check);
        if (!result) {
            for (ValidationUI ui : real) {
                if (ui instanceof MulticastValidationUI) {
                    if (result = ((MulticastValidationUI) ui).contains(check)) {
                        break;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void showProblem(Problem problem) {
        assert real != null;
        for (ValidationUI ui : real) {
            ui.showProblem(problem);
        }
    }

    public boolean validUIs(ValidationUI[] uis) {
        for (int i = 0; i < uis.length; i++) {
            ValidationUI ui = uis[i];
            if (ui == null) {
                throw new NullPointerException("Element " + i + " of ui " +
                        "array is null");
            }
        }
        return true;
    }

    public void clearProblem() {
        assert real != null;
        for (ValidationUI ui : real) {
            ui.clearProblem();
        }
    }

}

