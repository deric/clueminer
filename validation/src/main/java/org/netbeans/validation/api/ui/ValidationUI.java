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
import org.netbeans.validation.api.Problem;

/**
 * User interface controller which can show the user one problem. This
 * could involve showing an icon and some textual info in a status
 * bar, and/or disabling some OK-button (if the problem is of {@code
 * Severity.FATAL}) etc.
 *
 * <p> For one {@link ValidationGroup} (a group of UI-components
 * validated together), typically one or a few {@code ValidationUI}
 * instances might be used, but one instance of {@code ValidationUI}
 * should only be used with at most one {@code ValidationGroup} -
 * otherwise a new {@code Problem} in one {@code ValidationGroup} will
 * hide any {@code Problem}s in others.
 *
 * <p> Also, typically a {@code ValidationUI} instance is also used
 * for decorating each separate GUI-component that has a Problem. The
 * {@link org.netbeans.validation.api.ui.swing.SwingComponentDecorationFactory} is
 * a factory class creating such {@code ValidationUI} instances for decorating
 * Swing components when there is a validation problem in them.
 *
 * @author Tim Boudreau
 */
public interface ValidationUI {
    /**
     * Sets the {@link Problem} to be displayed to the user. Depending on the
     * severity of the problem, the user interface may want to block the
     * user from continuing until it is fixed (for example, disabling the
     * Next button in a wizard or the OK button in a dialog).
     * @param problem A problem that the user should be shown, which may
     * affect the state of the UI as a whole.  Should never be null.
     */
    public void showProblem(final Problem problem);
    /**
     * Clear the problem shown in this UI.
     */
    public void clearProblem();

    /**
     * Access a ValidationUI instance that does nothing.
     */
    public static final ValidationUI NO_OP = new ValidationUI(){
        @Override
        public void showProblem(Problem problem) {}
        public void clearProblem() {}
    };

}
