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

import org.netbeans.validation.api.ui.*;
import java.awt.EventQueue;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.ui.swing.SwingValidationGroup;
import org.netbeans.validation.api.ui.swing.SwingValidationGroup;

/**
 *
 * @author Tim Boudreau
 */
class JTextComponentValidationListenerImpl extends ValidationListener<JTextComponent>
        implements DocumentListener, FocusListener, Runnable {
    private Validator<Document> validator;
    private boolean hasFatalProblem = false;

    public JTextComponentValidationListenerImpl(JTextComponent component,
            ValidationStrategy strategy,
            ValidationUI validationUI,
            Validator<Document> validator
            ) {
        super(JTextComponent.class, validationUI, component);
        this.validator = validator;
        if (strategy == null) {
            throw new NullPointerException("strategy null");
        }
        component.addPropertyChangeListener("enabled", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                performValidation();
            }
        });
        switch (strategy) {
            case DEFAULT:
            case ON_CHANGE_OR_ACTION:
                component.getDocument().addDocumentListener(this);
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
            case ON_FOCUS_LOSS:
                component.addFocusListener(this);
                break;
        }
        performValidation(); // Make sure any initial errors are discovered immediately.
  }


    @Override
    protected final void performValidation(Problems ps){
        JTextComponent component = getTarget();
        if (!component.isEnabled()) {
            return;
        }
        validator.validate(ps, SwingValidationGroup.nameForComponent(component), component.getDocument());
        hasFatalProblem = ps.hasFatal();
    }


    @Override
    public void focusLost(FocusEvent e) {
        performValidation();
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        removeUpdate(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        //Documents can be legally updated from another thread,
        //but we will not run validation outside the EDT
        if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(this);
        } else {
            performValidation();
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        removeUpdate(e);
    }

    // See removeUpdate..
    @Override
    public void run() {
        performValidation();
    }
}
