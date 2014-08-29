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

import javax.swing.event.ListSelectionEvent;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.ui.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventListener;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.ui.swing.SwingValidationGroup;
import org.netbeans.validation.api.ui.swing.SwingValidationGroup;

/**
 * 
 * @author Hugo Heden
 */
class JListValidationListenerImpl extends ValidationListener<JList>
        implements EventListener, ListSelectionListener, FocusListener {
    private Validator<ListSelectionModel> validator;
    private boolean hasFatalProblem = false;

    public JListValidationListenerImpl(JList component,
            ValidationStrategy strategy,
            ValidationUI validationUI,
            Validator<ListSelectionModel> validator
            ) {
        super(JList.class, validationUI, component);
        this.validator = validator;
        if (strategy == null) {
            throw new NullPointerException("strategy null");
        }
        getTarget().addPropertyChangeListener("enabled", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                performValidation();
            }
        });

        switch (strategy) {
            case DEFAULT:
            case ON_CHANGE_OR_ACTION:
                component.addListSelectionListener(this);
                break;
            case ON_FOCUS_LOSS:
                component.addFocusListener(this);
                break;
            case INPUT_VERIFIER:
                component.setInputVerifier( new InputVerifier() {
                    @Override
                    public boolean verify(JComponent input) {
                        performValidation();
                        return !hasFatalProblem;
                    }
                });
                break;
            default:
                throw new AssertionError();
        }
        performValidation(); // Make sure any initial errors are discovered immediately.
    }

    @Override
    protected void performValidation(Problems ps){
        JList component = getTarget();
        if (!getTarget().isEnabled()) {
            return;
        }
        validator.validate(ps, SwingValidationGroup.nameForComponent(component), component.getSelectionModel());
        hasFatalProblem = ps.hasFatal();
    }

    @Override
    public void focusLost(FocusEvent e) {
        performValidation();
    }

    public void valueChanged(ListSelectionEvent lse) {
        performValidation();
    }

    @Override
    public void focusGained(FocusEvent e) {
    }


}
