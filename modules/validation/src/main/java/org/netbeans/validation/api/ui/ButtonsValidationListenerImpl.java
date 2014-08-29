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

import java.awt.Color;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.ui.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JColorChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.validation.api.Problems;

/**
 * THIS CLASS AND EVERYTHING ELSE IN THIS PACKAGE IS NOT API.  DO NOT CALL
 * OR INSTANTIATE DIRECTLY.
 *
 * @author Hugo Heden
 */
class ButtonsValidationListenerImpl extends ValidationListener<AbstractButton[]> implements ItemListener, ChangeListener{

    private final Validator<ButtonModel[]> validator;
    private final AbstractButton[] buttons;
    public ButtonsValidationListenerImpl(AbstractButton[] buttons, ValidationUI validationUI, Validator<ButtonModel[]> validator) {
        super(AbstractButton[].class, validationUI, buttons);
        this.validator = validator;
        this.buttons = buttons;
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].getModel().addChangeListener(this);
            buttons[i].getModel().addItemListener(this);
        }
        performValidation(); // Make sure any initial errors are discovered immediately.
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        performValidation();
    }
    @Override
    public void stateChanged(ChangeEvent pce) {
        performValidation();
    }

    @Override
    protected void performValidation(Problems ps) {
        boolean theyreAllDisabled = true;
        List<ButtonModel> selectedButtons = new ArrayList<ButtonModel>();
        for (AbstractButton button : buttons) {
            if (button.getModel().isEnabled()) {
                theyreAllDisabled = false;
                if (button.getModel().isSelected()) {
                    selectedButtons.add(button.getModel());
                }
            }
        }
        if (!theyreAllDisabled) {
            validator.validate(ps, null, selectedButtons.toArray(new ButtonModel[selectedButtons.size()]));
        }
    }

}



