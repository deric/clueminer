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

import java.awt.Component;
import org.netbeans.validation.api.ui.*;
import java.awt.EventQueue;
import java.awt.Point;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.Popup;
import javax.swing.text.JTextComponent;
import org.netbeans.validation.api.Problem;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.ValidatorUtils;

/**
 * {@link ValidationGroup} subclass specialized for handling Swing
 * components.  This subclass has {@code add}-methods for adding
 * GUI-components for common Swing cases. There are also a method for
 * getting the {@link SwingComponentDecorationFactory} used by this
 * SwingValidationGroup to create decorations for the separate
 * GUI-components added to the group. A custom {@code SwingComponentDecorationFactory}
 * can be specified when creating the {@code SwingValidationGroup}.
 *
 * <p> For components this library supports out-of-the-box such as
 * <code>JTextField</code>s or <code>JComboBox</code>es, simply call
 * one of the <code>add()</code> methods with your component and
 * validators.  For validating your own components or ones this class
 * doesn't have methods for, you implement {@link ValidationListener}s, and add them
 * to the {@code ValidationGroup} using the the method
 * {@link ValidationGroup#addItem(org.netbeans.validation.api.ui.ValidationItem, boolean)  }
 */
public final class SwingValidationGroup extends ValidationGroup {
    private final SwingComponentDecorationFactory decorator;

    private SwingValidationGroup(GroupValidator additionalGroupValidation, SwingComponentDecorationFactory decorator, ValidationUI... ui) {
        super(additionalGroupValidation, ui);
        if (ui == null) {
            throw new NullPointerException();
        }
        this.decorator = ( decorator!=null ? decorator : SwingComponentDecorationFactory.getDefault() );
    }

    public static SwingValidationGroup create(ValidationUI... ui) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        return new SwingValidationGroup(null, null, ui);
    }

    /**
     * Creates a {@code SwingValidationGroup}.
     *
     * Will use a {@code SwingComponentDecorationFactory} returned by {@link SwingComponentDecorationFactory#getDefault() } to modify the appearance of
     * subsequently added components (to show that there is a problem with a
     * component's content). To instead use a custom {@code SwingComponentDecorationFactory}, call
     * {@link #create(org.netbeans.validation.api.ui.GroupValidator, org.netbeans.validation.api.ui.swing.SwingComponentDecorationFactory, org.netbeans.validation.api.ui.ValidationUI[]) }
     *
     * @param ui Zero or more {@code ValidationUI}:s. Will be used by the {@code SwingValidationGroup} to show the leading problem (if any)
     */
    public static SwingValidationGroup create(GroupValidator additionalGroupValidation, ValidationUI... ui) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        return new SwingValidationGroup(additionalGroupValidation, null, ui);
    }

    /**
     * Creates a {@code SwingValidationGroup}.
     * @param additionalGroupValidation may be null
     * @param ui Zero or more {@code ValidationUI}:s. Will all be used by the
     * {@code SwingValidationGroup} to show the leading problem (if any)
     * @param decorator A decorator to be used to modify the appearance of 
     * subsequently added components (to show that there is a problem with a
     * component's content).
     */
    public static SwingValidationGroup create(GroupValidator additionalGroupValidation, SwingComponentDecorationFactory decorator, ValidationUI... ui) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        return new SwingValidationGroup(additionalGroupValidation, decorator, ui);
    }

    /**
     * Gets the currently set component decorator used to modify
     * components appearance (to show that there is a problem with a
     * component's content).
     * @return decorator A decorator. May not be null.
     */
    final SwingComponentDecorationFactory getComponentDecorationFactory() {
        return decorator;
    }

    @Override
    protected final <T> ValidationUI decorationFor (T comp) {
        ValidationUI dec = comp instanceof JComponent ? 
            this.getComponentDecorationFactory().decorationFor((JComponent) comp) :
            ValidationUI.NO_OP;
        return dec;
    }

    /**
     * Add a text component to be validated using the passed validators.
     *
     * <p> When a problem occurs, the created ValidationListener will
     * use a {@link ValidationUI} created by this {@code ValidationGroup} to decorate
     * the component.
     *
     * <p> <b>Note:</b> All methods in this class must be called from
     * the AWT Event Dispatch thread, or assertion errors will be
     * thrown.  Manipulating components on other threads is not safe.
     *
     * <p> Swing {@code Document}s (the model used by JTextComponent)
     * are thread-safe, and can be modified from other threads.  In
     * the case that a text component validator receives an event on
     * another thread, validation will be scheduled for later,
     * <i>on</i> the event thread.
     *
     * @param comp A text component such as a <code>JTextField</code>
     * @param validators One or more Validators
     */
    public final void add(JTextComponent comp, Validator<String>... validators) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        assert validators.length > 0 : "Empty validator array";
        Validator<String> merged = ValidatorUtils.merge(validators);
        ValidationListener<JTextComponent> vl = ValidationListenerFactory.createValidationListener(comp,
                ValidationStrategy.DEFAULT,
                this.getComponentDecorationFactory().decorationFor(comp),
                merged);
        this.addItem (vl, false);
    }


    /**
     * Add a text component to be validated using the passed validator.
     *
     * <p> When a problem occurs, the created ValidationListener will
     * use a {@link ValidationUI} created by this {@code ValidationGroup} to decorate
     * the component.
     *
     * <p> <b>Note:</b> All methods in this class must be called from
     * the AWT Event Dispatch thread, or assertion errors will be
     * thrown.  Manipulating components on other threads is not safe.
     *
     * <p> Swing {@code Document}s (the model used by JTextComponent)
     * are thread-safe, and can be modified from other threads.  In
     * the case that a text component validator receives an event on
     * another thread, validation will be scheduled for later,
     * <i>on</i> the event thread.
     * <p>Unlike {@link #add(JTextComponent,Validator...)}, calling this method does not trigger warnings under {@code -Xlint:unchecked}.
     * If you wish to add more than one validator, simply add the result of {@link ValidatorUtils#merge(Validator,Validator)}.
     * @param comp A text component such as a <code>JTextField</code>
     * @param validator a validator
     */
    public final void add(JTextComponent comp, Validator<String> validator) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        ValidationListener<JTextComponent> vl = ValidationListenerFactory.createValidationListener(comp,
                ValidationStrategy.DEFAULT,
                this.getComponentDecorationFactory().decorationFor(comp),
                validator);
        this.addItem (vl, false);
    }

    /**
     * Add a combo box to be validated using the passed validators
     *
     * <p> When a problem occurs, the created {@link ValidationListener} will
     * use a {@link ValidationUI} created by this {@code ValidationGroup} to decorate
     * the component.
     *
     * <p> <b>Note:</b> All methods in this class must be called from
     * the AWT Event Dispatch thread, or assertion errors will be
     * thrown.  Manipulating components on other threads is not safe.
     *
     * @param box A combo box component
     * @param validators One or more Validators
     */
    public final void add(JComboBox box, Validator<String>... validators) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        this.addItem(ValidationListenerFactory.createValidationListener(box, ValidationStrategy.DEFAULT, ValidationUI.NO_OP, ValidatorUtils.<String>merge(validators)), false);
    }

    /**
     * Add a combo box to be validated using the passed validator.
     *
     * <p> When a problem occurs, the created {@link ValidationListener} will
     * use a {@link ValidationUI} created by this {@code ValidationGroup} to decorate
     * the component.
     *
     * <p> <b>Note:</b> All methods in this class must be called from
     * the AWT Event Dispatch thread, or assertion errors will be
     * thrown.  Manipulating components on other threads is not safe.
     * <p>Unlike {@link #add(JComboBox,Validator...)}, calling this method does not trigger warnings under {@code -Xlint:unchecked}.
     * If you wish to add more than one validator, simply add the result of {@link ValidatorUtils#merge(Validator,Validator)}.
     * @param box A combo box component
     * @param validator a validator
     */
    public final void add(JComboBox box, Validator<String> validator) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        this.addItem(ValidationListenerFactory.createValidationListener(box, ValidationStrategy.DEFAULT, ValidationUI.NO_OP, validator), false);
    }

    /**
     * Add a JList to be validated using the passed validators
     *
     * <p> When a problem occurs, the created {@link ValidationListener} will
     * use a {@link ValidationUI} created by this {@code ValidationGroup} to decorate
     * the component.
     *
     * <p> <b>Note:</b> All methods in this class must be called from
     * the AWT Event Dispatch thread, or assertion errors will be
     * thrown.  Manipulating components on other threads is not safe.
     *
     * @param list A JList component
     * @param validators One or more Validators
     */
    public final void add(JList list, Validator<Integer[]>... validators) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        this.addItem(ValidationListenerFactory.createValidationListener(list, ValidationStrategy.DEFAULT, this.getComponentDecorationFactory().decorationFor(list), ValidatorUtils.merge(validators)), false);
    }

    /**
     * Add a JList to be validated using the passed validator.
     *
     * <p> When a problem occurs, the created {@link ValidationListener} will
     * use a {@link ValidationUI} created by this {@code ValidationGroup} to decorate
     * the component.
     *
     * <p> <b>Note:</b> All methods in this class must be called from
     * the AWT Event Dispatch thread, or assertion errors will be
     * thrown.  Manipulating components on other threads is not safe.
     * <p>Unlike {@link #add(JList,Validator...)}, calling this method does not trigger warnings under {@code -Xlint:unchecked}.
     * If you wish to add more than one validator, simply add the result of {@link ValidatorUtils#merge(Validator,Validator)}.
     * @param list A JList component
     * @param validator a validator
     */
    public final void add(JList list, Validator<Integer[]> validator) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        this.addItem(ValidationListenerFactory.createValidationListener(list, ValidationStrategy.DEFAULT, this.getComponentDecorationFactory().decorationFor(list), validator), false);
    }


    /**
     * Add a validator of button models - typically to see if any are selected.
     *
     * <p> <b>Note:</b> All methods in this class must be called from
     * the AWT Event Dispatch thread, or assertion errors will be
     * thrown.  Manipulating components on other threads is not safe.
     *
     * @param buttons The buttons
     * @param validators A number of Validators
     */
    public final void add(final AbstractButton[] buttons, Validator<Integer[]>... validators) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        this.addItem(ValidationListenerFactory.createValidationListener(buttons, ValidationStrategy.DEFAULT, ValidationUI.NO_OP, ValidatorUtils.merge(validators)), false);
    }

    /**
     * Add a validator of button models - typically to see if any are selected.
     *
     * <p> <b>Note:</b> All methods in this class must be called from
     * the AWT Event Dispatch thread, or assertion errors will be
     * thrown.  Manipulating components on other threads is not safe.
     * <p>Unlike {@link #add(AbstractButton[],Validator...)}, calling this method does not trigger warnings under {@code -Xlint:unchecked}.
     * If you wish to add more than one validator, simply add the result of {@link ValidatorUtils#merge(Validator,Validator)}.
     * @param buttons The buttons
     * @param validator a validator
     */
    public final void add(final AbstractButton[] buttons, Validator<Integer[]> validator) {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        this.addItem(ValidationListenerFactory.createValidationListener(buttons, ValidationStrategy.DEFAULT, ValidationUI.NO_OP, validator), false);
    }


    /**
     * Create a label which will show the current problem if any, which
     * can be added to a panel that uses validation
     *
     * @return A JLabel
     */
    public final JComponent createProblemLabel() {
        assert EventQueue.isDispatchThread() : "Must be called on event thread";
        final MultilineLabel result = new MultilineLabel();
        addUI(result.createUI());
        return result;
    }

    /**
     * Create a Popup which can be shown over a component to display what the
     * problem is.  The resulting popup will be word-wrapped and effort will be
     * made to ensure it fits on-screen in the case of lengthy error messages.
     *
     * @param problem The problem to show
     * @param target The target component
     * @param relativeLocation The coordinates where the popup should appear,
     * <i>in the coordinate space of the target component, not the screen</i>.
     * @return A popup.  Generally, use the returned popup once and get a new
     * one if you want to show a message again.  The returned popup will take
     * care of hiding itself on component hierarchy changes.
     */
    static Popup createProblemPopup (Problem problem, Component target, Point relativeLocation) {
        return MultilineLabelUI.showPopup(problem, target, relativeLocation.x, relativeLocation.y);
    }

    /**
     * Client property which can be set to provide a component's name
     * for use in validation messages.  If not set, the component's
     * <code>getName()</code> method is used.
     */
    private static final String CLIENT_PROP_NAME = "_name";

    /**
     * Get a string name for a component using the following strategy:
     * <ol>
     * <li>Check <code>jc.getClientProperty(CLIENT_PROP_NAME)</code></li>
     * <li>If that returned null, call <code>jc.getName()</code>
     * </ol>
     * @param jc The component
     * @return its name, if any, or null
     */
    public static String nameForComponent(JComponent jc) {
        String result = (String) jc.getClientProperty(CLIENT_PROP_NAME);
        if (result == null) {
            result = jc.getName();
        }
        return result;
    }

    public static void setComponentName(JComponent comp, String localizedName) {
        comp.putClientProperty (CLIENT_PROP_NAME, localizedName);
    }
}
