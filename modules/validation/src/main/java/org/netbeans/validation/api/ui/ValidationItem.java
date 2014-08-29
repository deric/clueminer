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
 * Common superclass for {@code ValidationGroup} and {@code ValidationListener}, providing a common API
 * for forcing revalidation of the managed UI component or components, see {@link #performValidation()},
 * and for temporarily suspending validation, see
 * {@link #runWithValidationSuspended(java.lang.Runnable)}
 * 
 * <p>{@code ValidationItem}s can be added to a ValidationGroup using
 * {@link ValidationGroup#addItem(org.netbeans.validation.api.ui.ValidationItem, boolean) }.
 *
 * @author Tim Boudreau
 * @author Hugo Heden
 */
public abstract class ValidationItem {
    private Problem currentLeadProblem = null;
    private Problem currentProblemInUI = null;
    private ValidationGroup parentValidationGroup = null;
    private int suspendCount = 0;
    private boolean uiEnabled = true;
    private final MulticastValidationUI multicastValidationUI = new MulticastValidationUI();


    ValidationItem(ValidationUI... uis) {  // Package visibility to make class "final" outside of package
        for (ValidationUI ui : uis) {
            // Dont call addUI. This is superclass constructor, the subclass is not finished with creation yet, so this is too early.
            // Instead, subclass is responsible for updating UI:s 
            multicastValidationUI.add(ui); 
        }
    }

    void addUI(ValidationUI ui) {
        if (!containsUI(ui)) {
            multicastValidationUI.add(ui);
            if(uiEnabled){
                if (currentProblemInUI == null) {
                    ui.clearProblem();
                } else {
                    ui.showProblem(currentProblemInUI);
                }
            }
        }
    }

    void removeUI(ValidationUI ui) {
        if (containsUI(ui)) {
            this.multicastValidationUI.remove(ui);
            ui.clearProblem();
        }
    }

    final void showIfUIEnabled( Problem problem ){
        if( uiEnabled &&
                (
                    (currentProblemInUI != null && !currentProblemInUI.equals(problem) )
                    ||
                    ( currentProblemInUI == null && problem != null )
                )
            ){
            currentProblemInUI = problem;
            if ( problem == null) {
                multicastValidationUI.clearProblem();
            } else {
                multicastValidationUI.showProblem( problem );
            }
        }
    }

    private boolean containsUI(ValidationUI ui) {
        return this.multicastValidationUI.contains(ui);
    }

    /**
     * <p>A request (to the simple-validation infrastructure) for revalidation of
     * the component(s) managed by this ValidationListener/ValidationGroup, updating
     * the {@code ValidationUI} as necessary.
     * 
     * <p>This method <i>can</i> be called by client code (i.e non-subclasses), to manually
     * request revalidation -- a "refresh". This can be useful if custom ValidationListeners are used that rely
     * not only on the state of the managed component, but also on outside state
     * that the listener does not know about
     * 
     * <p><b>More typically</b>, this method is called by {@code ValidationListener} subclasses
     * to let the simple-validation infrastructure know
     * that the user has interacted with the UI-component in a way that makes revalidation
     * needed.
     *
     * <p>This will initiate the validation logic (unless the validation is suspended, see
     * {@link ValidationItem#runWithValidationSuspended(java.lang.Runnable)}:
     * A call to {@link ValidationListener#performValidation(org.netbeans.validation.api.Problems)}
     * will occur.
     *
     * <p>If this results in a {@link Problem},  the {@link ValidationUI}
     * managed by this {@code ValidationListener} (such as an error icon
     * decorating the UI-component) will be activated, indicating the {@code Problem}
     * to the user.
     *
     * <p>If this ValidationListener has been added to a {@link ValidationGroup},
     * the latter will update its {@code ValidationUI}:s as well (unless there happens
     * to be a more severe {@code Problem} somewhere else within that {@code ValidationGroup})
     * 
     * @return The lead problem of the ValidationItem, null if there is no Problem
     *
     *
     */
    public final Problem performValidation() {
        if (isSuspended()) {
            return this.getCurrentLeadProblem();
        }
        subtreeRevalidation();
        if( getParentValidationGroup() != null ) {
            getParentValidationGroup().validationTriggered(this);
        } else {
            showIfUIEnabled(getCurrentLeadProblem());
        }
        return getCurrentLeadProblem();
    }


    // Package private method. Intented to be a helper method for implementing performValidation().
    // If the latter is called on a ValidationGroup, the whole subtree should revalidate
    // recursively down to the leafs, with help from this method.
    // So, if this is a ValidationGroup, this
    // method should pass the call recursively down the tree of ValidationItem:s to
    // the leafs (typically ValidationListener:s). A ValidationListener should
    // perform revalidation, store the new lead problem (if any) and then
    // just return -- i.e they should not call validationTriggered or update
    // the UI. The calling parent ValidationGroup should then perform GroupValidation
    // (if any) and then update the UI of each child as appropriate, and then
    // return -- i.e it should not update its own UI.
    abstract void subtreeRevalidation();
    
    /**
     * The current problem leading problem (if any) with this ValidationItem
     * @return The current problem
     * @return null if there currently is no problem.
     */
    
    final Problem getCurrentLeadProblem() {
        return this.currentLeadProblem;
    }
    final void setCurrentLeadProblem(Problem problem){
        this.currentLeadProblem = problem;
    }

    /**
     * Indicates whether this ValidationGroup is currently suspended.
     * @see #runWithValidationSuspended(java.lang.Runnable)
     * @return true if this ValidationGroup is currently suspended, false otherwise
     */
    final boolean isSuspended() {
        return suspendCount > 0 || (getParentValidationGroup() != null && ((ValidationItem)getParentValidationGroup()).isSuspended());
    }

    /**
     * Disable validation and invoke a runnable.  This method is useful
     * in UIs where a change in one component can trigger changes in
     * another component, and you do not want validation to be triggered
     * because a component was programmatically updated.
     * <p>
     * For example, say you have a dialog that lets you create a new
     * Servlet source file.  As the user types the servlet name, web.xml
     * entries are updated to match, and these are also in fields in the same
     * dialog.  Since the updated web.xml entries are being programmatically
     * (and presumably correctly) generated, those changes should not
     * trigger a useless validation run.  Wrap such generation code in
     * a Runnable and pass it to this method when making programmatic
     * changes to the contents of the UI.
     * <p>
     * The runnable is run synchronously, but no changes made to components
     * while the runnable is running will trigger validation.
     * <p>
     * When the last runnable exits,
     * validateAll(null) will be called to run validation
     * against the entire newly updated UI.
     * <p>
     * This method is reentrant - a call to updateComponents can trigger
     * another call to updateComponents without triggering multiple
     * calls to validateAll() on each Runnable's exit.
     *
     * @param run A runnable which makes changes to the contents of one
     * or more components in the UI which should not trigger validation
     */
    public final void runWithValidationSuspended(Runnable run) {
        suspendCount++;
        try {
            run.run();
        } finally {
            suspendCount--;
            if (!isSuspended()) {
                performValidation();
            }
        }
    }

    /**
     * @return null if this ValidationItem is not added to a ValidationGroup
     */
    final ValidationGroup getParentValidationGroup() {
        return parentValidationGroup;
    }

    /**
     * @param parentGroup null allowed
     */
    final void setParentValidationGroup(ValidationGroup parentGroup, boolean setUIEnabled) {
        this.parentValidationGroup = parentGroup;
        this.uiEnabled = setUIEnabled;
    }
}
