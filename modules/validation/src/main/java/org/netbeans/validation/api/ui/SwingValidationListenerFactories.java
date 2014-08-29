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

import java.awt.EventQueue;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.validation.api.Validator;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import org.netbeans.validation.api.ValidatorUtils;
import org.netbeans.validation.api.conversion.Converter;
import org.netbeans.validation.api.ui.swing.SwingValidationGroup;

/**
 * A number of factory methods for creating ValidationListeners handling validation of Swing components.
 *<p>
 * <b>Note that there are also some convenience methods for creating a  ValidationListener
 * and directly adding it to a ValidationGroup, see the {@code add} methods in class {@link SwingValidationGroup},
 * so the using the methods in this class should often not be necessary.</b>
 *
 * Note that the factory methods here could be moved to ValidationGroup, however,
 * this would cause ValidationGroup to trigger heavier classloading (at least
 * if -Xverify:none is not set for the VM).
 * 
 * @author Tim Boudreau
 * @author Hugo Heden
 */
final class SwingValidationListenerFactories {
    private SwingValidationListenerFactories(){}
    /**
     * Create a ValidationListener for a JList. The JList will be validated
     * with the passed ValidationStrategy
     * using the passed Validator<ListSelectionModel>
     * showing any problems in the passed ValidationUI
     * <p>
     * Note that there is also a convenience method for creating a  ValidationListener
     * and directly adding it to a ValidationGroup, see
     * {@link SwingValidationGroup#add(javax.swing.JList, org.netbeans.validation.api.Validator[]) }
     * 
     *
     */
    static ValidationListener createJListValidationListener(final JList component, final ValidationStrategy strategy, ValidationUI validationUI, final Validator<ListSelectionModel> validator) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        return new JListValidationListenerImpl(component, strategy, validationUI, validator);
    }

    /**
     * Create a ValidationListener for a JList. The JList will be validated
     * with the passed ValidationStrategy
     * using the passed chain of Validator<Integer[]>
     * showing any problems in the passed ValidationUI
     * <p>
     * Note that there is also a convenience method for creating a  ValidationListener
     * and directly adding it to a ValidationGroup, see
     * {@link SwingValidationGroup#add(javax.swing.JList, org.netbeans.validation.api.Validator[]) }
     *
     */
    static ValidationListener<JList> createJListValidationListenerConverted(final JList component, final ValidationStrategy strategy, ValidationUI validationUI, final Validator<Integer[]> orig) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        final Validator<ListSelectionModel> validator = Converter.find(Integer[].class, ListSelectionModel.class).convert(orig);
        return new JListValidationListenerImpl(component, strategy, validationUI, validator);
    }

    /**
     * Create a ValidationListener for a JTextComponent (such as JTextField or JTextArea). The JTextComponent will be validated
     * with the passed ValidationStrategy
     * using the passed Validator<Document>
     * showing any problems in the passed ValidationUI
     *
     * <p> Swing {@code Document}s (the model used by JTextComponent)
     * are thread-safe, and can be modified from other threads.  In
     * the case that a text component validator receives an event on
     * another thread, validation will be scheduled for later,
     * <i>on</i> the event thread.
     * <p>
     * Note that there is also a convenience method for creating a  ValidationListener
     * and directly adding it to a ValidationGroup, see
     * {@link SwingValidationGroup#add(javax.swing.text.JTextComponent, org.netbeans.validation.api.Validator[]) }
     *
     */
    static ValidationListener createJTextComponentValidationListener(final JTextComponent comp, final ValidationStrategy strategy, final ValidationUI validationUI, final Validator<Document> validator) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        return new JTextComponentValidationListenerImpl(comp, strategy, validationUI, validator);
    }

    /**
     * Create a ValidationListener for a JTextComponent (such as JTextField or JTextArea). The JTextComponent will be validated
     * (with ValidationStrategy.DEFAULT)
     * using the passed Validator<Document>
     * showing any problems in the passed ValidationUI
     *
     * <p> Swing {@code Document}s (the model used by JTextComponent)
     * are thread-safe, and can be modified from other threads. In
     * the case that a text component validator receives an event on
     * another thread, validation will be scheduled for later,
     * <i>on</i> the event thread.
     * <p>
     * Note that there is also a convenience method for creating a  ValidationListener
     * and directly adding it to a ValidationGroup, see
     * {@link SwingValidationGroup#add(javax.swing.text.JTextComponent, org.netbeans.validation.api.Validator[]) }
     *
     */
    static ValidationListener createJTextComponentValidationListener(final JTextComponent comp, final ValidationUI validationUI, final Validator<Document> validator) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        return new JTextComponentValidationListenerImpl(comp, ValidationStrategy.DEFAULT, validationUI, validator);
    }
    
    /**
     * Create a ValidationListener for a JTextComponent (such as JTextField or JTextArea). The JTextComponent will be validated
     * with the passed ValidationStrategy
     * using the passed chain of Validator<String>
     * showing any problems in the passed ValidationUI
     *
     * <p> Swing {@code Document}s (the model used by JTextComponent)
     * are thread-safe, and can be modified from other threads. In
     * the case that a text component validator receives an event on
     * another thread, validation will be scheduled for later,
     * <i>on</i> the event thread.
     * <p>
     * Note that there is also a convenience method for creating a  ValidationListener
     * and directly adding it to a ValidationGroup, see
     * {@link SwingValidationGroup#add(javax.swing.text.JTextComponent, org.netbeans.validation.api.Validator[]) }
     *
     */
    static ValidationListener createJTextComponentValidationListener(final JTextComponent comp, final ValidationStrategy strategy, final ValidationUI validationUI, final Validator<String>... validators) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        final Validator<String> merged = ValidatorUtils.merge(validators);
        final Validator<Document> validator = Converter.find(String.class, Document.class).convert(merged);
        return new JTextComponentValidationListenerImpl(comp, strategy, validationUI, validator);
    }

    /**
     * Create a ValidationListener for a JTextComponent (such as JTextField or JTextArea). The JTextComponent will be validated
     * (with ValidationStrategy.DEFAULT)
     * using the passed chain of Validator<String>
     * showing any problems in the passed ValidationUI
     *
     * <p> Swing {@code Document}s (the model used by JTextComponent)
     * are thread-safe, and can be modified from other threads. In
     * the case that a text component validator receives an event on
     * another thread, validation will be scheduled for later,
     * <i>on</i> the event thread.
     * <p>
     * Note that there is also a convenience method for creating a  ValidationListener
     * and directly adding it to a ValidationGroup, see
     * {@link SwingValidationGroup#add(javax.swing.text.JTextComponent, org.netbeans.validation.api.Validator[]) }
     *
     */
    static ValidationListener createJTextComponentValidationListener(final JTextComponent comp, final ValidationUI validationUI, final Validator<String>... validators) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        return createJTextComponentValidationListener(comp, ValidationStrategy.DEFAULT, validationUI, validators);
    }


    /**
     * Create a ValidationListener for a JComboBox. The JComboBox will be validated
     * with the passed ValidationStrategy
     * using the passed Validator<ComboBoxModel>
     * showing any problems in the passed ValidationUI
     * <p>
     * Note that there is also a convenience method for creating a  ValidationListener
     * and directly adding it to a ValidationGroup, see
     * {@link SwingValidationGroup#add(javax.swing.JComboBox, org.netbeans.validation.api.Validator[]) }
     */
    static ValidationListener createJComboBoxValidationListener(final JComboBox component, final ValidationStrategy strategy, ValidationUI validationUI, final Validator<ComboBoxModel> validator) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        return new JComboBoxValidationListenerImpl(component, strategy, validationUI, validator);
    }

    /**
     * Create a ValidationListener for a JComboBox. The JComboBox will be validated
     * (with ValidationStrategy.DEFAULT)
     * using the passed Validator<ComboBoxModel>
     * showing any problems in the passed ValidationUI
     * <p>
     * Note that there is also a convenience method for creating a  ValidationListener
     * and directly adding it to a ValidationGroup, see
     * {@link SwingValidationGroup#add(javax.swing.JComboBox, org.netbeans.validation.api.Validator[]) }
     */
    static ValidationListener createJComboBoxValidationListener(final JComboBox component, ValidationUI validationUI, final Validator<ComboBoxModel> validator) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        return new JComboBoxValidationListenerImpl(component, ValidationStrategy.DEFAULT, validationUI, validator);
    }

    /**
     * Create a ValidationListener for a JComboBox. The JComboBox will be validated
     * with the passed ValidationStrategy
     * using the passed chain of Validator<String>
     * showing any problems in the passed ValidationUI
     * <p>
     * Note that there is also a convenience method for creating a  ValidationListener
     * and directly adding it to a ValidationGroup, see
     * {@link SwingValidationGroup#add(javax.swing.JComboBox, org.netbeans.validation.api.Validator[]) }
     */
    static ValidationListener createJComboBoxValidationListener(final JComboBox component, final ValidationStrategy strategy, ValidationUI validationUI, final Validator<String>... validators) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        final Validator<String> merged = ValidatorUtils.merge(validators);
        final Validator<ComboBoxModel> validator = Converter.find(String.class, ComboBoxModel.class).convert(merged);
        return new JComboBoxValidationListenerImpl(component, strategy, validationUI, validator);
    }

    /**
     * Create a ValidationListener for a JComboBox. The JComboBox will be validated
     * (with ValidationStrategy.DEFAULT)
     * using the passed chain of Validator<String>
     * showing any problems in the passed ValidationUI
     * <p>
     * Note that there is also a convenience method for creating a  ValidationListener
     * and directly adding it to a ValidationGroup, see
     * {@link SwingValidationGroup#add(javax.swing.JComboBox, org.netbeans.validation.api.Validator[]) }
     */
    static ValidationListener createJComboBoxValidationListener(final JComboBox component, ValidationUI validationUI, final Validator<String>... validator) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        return createJComboBoxValidationListener(component, ValidationStrategy.DEFAULT, validationUI, validator);
    }



    /**
     * Create a ValidationListener for a AbstractButton[] (such as a number of JCheckBoxes and JRadioButtons). The AbstractButton[] will be validated
     * using the passed Validator<ButtonModel[]>
     * showing any problems in the passed ValidationUI
     *
     * <p>
     * Note that there is also a convenience method for creating a  ValidationListener
     * and directly adding it to a ValidationGroup, see
     * {@link SwingValidationGroup#add(javax.swing.AbstractButton[], org.netbeans.validation.api.Validator[]) }
     */
    static ValidationListener createButtonsValidationListener(final AbstractButton[] buttons, ValidationUI validationUI, final Validator<ButtonModel[]> validator) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        return new ButtonsValidationListenerImpl(buttons, validationUI, validator);
    }


    /**
     * Create a ValidationListener for a AbstractButton[] (such as a number of JCheckBoxes and JRadioButtons). The AbstractButton[] will be validated
     * using the passed chain of Validator<Integer[]>
     * showing any problems in the passed ValidationUI
     * <p>
     * Note that there is also a convenience method for creating a  ValidationListener
     * and directly adding it to a ValidationGroup, see
     * {@link SwingValidationGroup#add(javax.swing.AbstractButton[], org.netbeans.validation.api.Validator[]) }
     *
     */
    static ValidationListener createButtonsValidationListener(final AbstractButton[] buttons, ValidationUI validationUI, final Validator<Integer[]>... validators) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        final Validator<Integer[]> merged = ValidatorUtils.merge(validators);
        final Validator<ButtonModel[]> validator = Converter.find(Integer[].class, ButtonModel[].class).convert(merged);
        return new ButtonsValidationListenerImpl(buttons, validationUI, validator);
    }

}
