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

import java.util.LinkedList;
import java.util.List;
import org.netbeans.validation.api.Problem;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.ValidatorUtils;
import org.netbeans.validation.api.builtin.stringvalidation.StringValidators;

/**
 * A {@code ValidationGroup} logically groups a set of GUI-components,
 * such as text fields, lists, tables, comboboxes etc, each validated
 * by one or more validators (such as the built-in ones provided by
 * the framework in {@link StringValidators}).
 *
 * <p><i><b>Note:</b> If you are validating Swing components, you will probably
 * want to use {@link
 * org.netbeans.validation.api.ui.swing.SwingValidationGroup} which contains
 * convenience add methods for common Swing components, and performs
 * Swing-specific threading correctness checks.</i>
 *
 * <p> A {@code ValidationGroup} is typically associated with a panel
 * containing GUI-components (such as a {@code JPanel} or {@code
 * JDialog} in Swing). The idea with logically grouping GUI-components
 * (as opposed to treating them completely separately) is to <b>to
 * indicate the most severe {@link Problem} in the panel</b> (graded
 * by {@link org.netbeans.validation.api.Severity}) -- the "lead
 * problem" of the group -- to the user in various ways. Even if the
 * user happens to currently be interacting with another GUI component
 * that has no error in it, the lead problem of the panel/group will
 * be visible to the user.
 *
 * <p>If there is more than one Problem that is the most severe (they
 * have equal Severity) in the group of UI-components, then the one in
 * the UI-component that has been most recently interacted with will
 * be the lead problem.
 *
 * <p> The way for the {@code ValidationGroup} to show the lead
 * problem in the UI is by using one or more instances of the {@link
 * ValidationUI} interface. {@code ValidationUI} instances can be
 * added by client code using the {@link
 * #addUI(org.netbeans.validation.api.ui.ValidationUI) } method.
 * One {@code ValidationUI} instance could indicate the lead
 * problem for example by showing a message in some status
 * bar. Another one could disable some OK-button (which could be
 * suitable if the lead problems has {@code Severity.FATAL}) etc. All
 * these {@code ValidationUI}s are updated whenever the lead problem
 * of the group changes.
 *
 * If the ValidationGroup contains UI-components whose validity
 * depends not only on their own state but on the state of each other
 * as well, they can be said to have validity interdependencies. In
 * such cases not only the state of each component needs to be
 * validated, but the combination of components as well. This is done
 * by passing a {@code GroupValidator} object when creating the
 * ValidationGroup. For more on that, see {@link GroupValidator}.
 *
 * <p> Now, to add a GUI-component to the {@code ValidationGroup},
 * client code would wrap it (together with the desired Validators)
 * into a {@link ValidationListener} that is then added to the {@code
 * ValidationGroup} using the {@link
 * #addItem(org.netbeans.validation.api.ui.ValidationItem, boolean) } method.  Note
 * that this is the generic but perhaps inconvenient case -- <b>the
 * subclass {@link
 * org.netbeans.validation.api.ui.swing.SwingValidationGroup} contains
 * a number of {@code add}-methods for convenience, to simplify this
 * procedure for common Swing components.</b>
 *
 * <p>Each {@code ValidationListener} listens to suitable GUI-events
 * (often key-press events or mouse-events). When such an event occurs
 * the {@code ValidationListener} will perform revalidation and then
 * notify its parent {@code ValidationGroup} so that it can reevaluate
 * its lead problem.
 *
 * <p>Not only ValidationListener:s can be added to ValidationGroup,
 * but so can other ValdiationGroups as well. (This is the reason why
 * ValidationListener and ValidationGroup share a common superclass,
 * {@link ValidationItem}, and that this is the type of the argument
 * of {@code ValidationGroup#add}.) This is useful when there are two
 * separate panels with their own {@code ValidationGroup}s, and one of
 * the panels is (perhaps temporarily) to be embedded into the other.
 * Whenever there is a change in one of the added child
 * ValidationGroup's components, it will drive the UI(s) belonging to
 * the parent group, as well as (optionally) its own. (The latter can
 * be disabled by passing {@code true} as second parameter to {@link
 * #addItem(org.netbeans.validation.api.ui.ValidationItem, boolean)  }
 * instead of {@code false}
 * 
 *
 * <p> The core functionality of the {@code ValidationGroup} with
 * added {@code ValidationItem}:s works as follows:
 *
 * <ul>
 *
 * <li>When an appropriate change in a UI component occurs, a {@code
 * ValidationListener} will observe that and make sure the component
 * is validated (by invoking the {@code Validator}(s)) and then
 * decorate the UI component if there is a Problem in it. This is
 * unless the ValidationListener is suspended, in which case nothing
 * at all will happen, see {@link
 * ValidationItem#runWithValidationSuspended }</li>
 *
 * <li>The ValidationListener will notify its parent ValidationGroup
 * about the validation. If there was a fatal {@code Problem} in that
 * UI component, then this will also become the lead problem of the
 * whole group because it is the most recent of the most severe
 * problems in the groups.</li>
 *
 * <li>If not, the most recent of the most severe of the problems of
 * the UI components in the ValidationGroup will be the lead
 * problem. (This does not involve actual revalidation of these
 * components, the problem that occured when the component last
 * triggered validation is assumed to still be there).</li>
 *
 * <li>When the lead problem of the ValidationGroup has been
 * determined, the <code>showProblem</code> method of the
 * <code>ValidationUI</code>:s of the group will be called. This may
 * for example involve disabling an OK-button if the lead problem is
 * fatal (and enabling it if not) and showing an error message. This
 * is unless this ValidationGroup has been added "with disabled UI" to
 * another ValidationGroup, in which case the Problem will not be
 * shown in the ValidationUI. </li>
 * 
 * <li>If this ValidationGroup has been added to another one then the
 * parent is notified. The parent ValidationGroup will then check if
 * its lead problem needs to be updated, update its ValidationUI:s
 * accordingly (unless it has a disabled UI) and notify its parent if
 * there is one -- and so on.
 * 
 *  </ul>
 *
 * @author Tim Boudreau
 */
public class ValidationGroup extends ValidationItem {
    private final GroupValidator additionalGroupValidation;
    private final List<ValidationItem> validationItems = new LinkedList<ValidationItem>();
    private boolean isAncestorToSelf = false;
    

    protected ValidationGroup(GroupValidator additionalGroupValidation, ValidationUI... ui) {
        super(ui);
        this.additionalGroupValidation = additionalGroupValidation;
        if (ui == null) {
            throw new NullPointerException ("UI null");
        }
    }

    protected ValidationGroup(ValidationUI... ui) {
        this(null, ui);
    }

    /**
     * Create a new validation group
     * @param ui Zero or more initial UIs which will display errors
     * @return A validation group
     */
    public static ValidationGroup create (ValidationUI... ui) {
        return new ValidationGroup(ui);
    }

    /**
     * Create a new validation group
     * @param additionalGroupValidation
     * @param ui Zero or more initial UIs which will display errors
     * @return A validation group
     */
    public static ValidationGroup create(GroupValidator additionalGroupValidation, ValidationUI... ui) {
        return new ValidationGroup(additionalGroupValidation, ui);
    }


    /**
     *
     * @param <ComponentType>
     * @param <ValueType>
     * @param comp
     * @param validators
     */
    public final <ComponentType, ValueType> void add (ComponentType comp, Validator<ValueType>... validators) {
        this.addItem (ValidationListenerFactory.createValidationListener(comp, ValidationStrategy.DEFAULT, decorationFor(comp), ValidatorUtils.merge(validators)), false);
    }

    /**
     *
     * @param <ComponentType>
     * @param <ValueType>
     * @param comp
     * @param validator
     */
    public final <ComponentType, ValueType> void add (ComponentType comp, Validator<ValueType> validator) {
        this.addItem (ValidationListenerFactory.createValidationListener(comp, ValidationStrategy.DEFAULT, decorationFor(comp), validator), false);
    }

    protected <T> ValidationUI decorationFor(T component) {
        return ValidationUI.NO_OP;
    }

    /**
     * Add a UI which should be called on validation of this group or
     * any components within it.  
     *
     * <p/> This is useful in the case that you have multiple
     * components which are provided separately and each want to
     * respond to validation problems (for example, one UI controlling
     * a dialog's OK button, another controlling display of error
     * text).
     *
     * @param ui An implementation of ValidationUI
     */
    @Override
    public final void addUI(ValidationUI ui) {
        super.addUI(ui);
    }

    /**
     * Remove a delegate UI which is being controlled by this
     * validation item. Will clear the UI from any Problem as well.
     *
     * @param ui The UI
     */
    @Override
    public final void removeUI(ValidationUI ui) {
        super.removeUI(ui);
    }

    /**
     * Adds a ValidationItem (i.e a ValidationListener or another
     * ValidationGroup) to this ValidationGroup.
     *
     * <p> The purpose of being able to disable a ValidationUI is so
     * you can compose together reusable panels which have their own
     * ValidationUI, but only let the outer ValidationUI show the lead
     * problem when one is inside another. Typically, without the
     * ability to turn off the inner panel's ValidationUI, when
     * there's a problem in the inner panel the error message is going
     * to be shown twice -- once at the bottom of the dialog and one
     * in the middle of the screen inside the inner panel -- which would look bad.
     *
     * <p> Note that the subclass {@link
     * org.netbeans.validation.api.ui.swing.SwingValidationGroup}
     * contains a number of {@code add}-methods for convenience, to
     * simplify this procedure for common Swing components
     * @param validationItem item to add
     * @param disableUI indicates that the validation UI of the parent panel
     * should not be notified
     *
     */
     public final void addItem(ValidationItem validationItem, boolean disableUI){
        if( validationItem.getParentValidationGroup() != null ){
            throw new IllegalArgumentException("Added item already has parent group"); //NOI18N
        }
        validationItem.setParentValidationGroup(this, !disableUI);
        // Add first in list to make this the most recent one,
        // as if the user would have interacted with it.. (not sure about this,
        // but shouldn't be a big deal..?)
        validationItems.add(0, validationItem);
        // A check to make sure a child validation group cannot have an
        // ancestor of itself added to itself - i.e. preemptively avoid endless loops:
        if( this.detectAncestryToSelf() ){
            validationItems.remove(0);
            validationItem.setParentValidationGroup(null, true);
            throw new IllegalArgumentException("Ancestry to self"); //NOI18N
        }

        // This is like validationTriggered just happened for the added child.
        // This is probably reasonable, because when a child that has a problem is added to a group,
        // and this problem happens to be the worst problem in the group, the group UI should be updated.
         if (!isSuspended()) {
             /* Don't pass the child to update, because we do not want to update its UI (it has problably already done so itself)*/
             update(false, null); 
             if (getParentValidationGroup() != null) {
                 getParentValidationGroup().validationTriggered(this);
             } else {
                 showIfUIEnabled(this.getCurrentLeadProblem());
             }
        }
     }

    /**
     * Intended to be used by {@link ValidationGroup#add(org.netbeans.validation.api.ui.ValidationItem) }
     * to detect self-ancestry (fail-fast-detection of infinite loop problems)
     * @return true if detected ancestry to self -- i.e bad news.
     * @return false if good news
     */
    boolean detectAncestryToSelf() {
        if( isAncestorToSelf ) {
            return true;
        }
        isAncestorToSelf = true;
        boolean badNews = false;
        if(getParentValidationGroup() != null){
            badNews = getParentValidationGroup().detectAncestryToSelf();
        }
        isAncestorToSelf = false;
        return badNews;
    }

    
    /**
     * Removes a previously added ValidationItem (i.e a
     * ValidationListener or another ValidationGroup) to this
     * ValidationGroup.
     *
     * <p> If the ValidationItem to be removed has the current lead
     * problem of the ValidationGroup, the ValidationGroup will remove
     * that and evaluate a new lead problem
     * 
     * @param validationItem item to remove
     */
    public final void remove(ValidationItem validationItem) {
        if( validationItems.remove(validationItem) ) {
            validationItem.setParentValidationGroup(null, true);
            if( this.getCurrentLeadProblem() != null && this.getCurrentLeadProblem() == validationItem.getCurrentLeadProblem() ){
                if( !isSuspended() ) {
                    this.update( false, null );
                    if( getParentValidationGroup() != null ) {
                        getParentValidationGroup().validationTriggered(this);
                    } else {
                        showIfUIEnabled(this.getCurrentLeadProblem());
                    }
                }
            }
        }
    }

    @Override
    final void subtreeRevalidation(){
        if (isSuspended()) {
            return ;
        }
        update( true, null );
    }
    

    // Intended to be called from child ValidationItem that has triggered validation.
    final void validationTriggered(final ValidationItem triggeringChild) {
        // Never "touch" the trigger (do not call performValidation on it). The trigger takes care of itself.
        assert triggeringChild != null;
        assert !isSuspended(); // child should have noticed.
        // Put trigger first in list, because it's the most recent trigger.
        // This way, if there are more than one problem with
        // equal severity, we ensure that the lead problem of this
        // ValidationGroup (see update()) is always the more recent one (rather than an arbitary
        // one)
        validationItems.remove(triggeringChild);
        validationItems.add(0, triggeringChild);
        update( false, triggeringChild );

        if( getParentValidationGroup() != null ) {
            getParentValidationGroup().validationTriggered(this);
        } else {
            showIfUIEnabled(this.getCurrentLeadProblem());
        }
    }

    private void update(final boolean childrenShallPerformValidation, final ValidationItem triggerThatHasAlreadyPerformedValidation) {
        assert !isSuspended();
        assert ! ( childrenShallPerformValidation && triggerThatHasAlreadyPerformedValidation!=null ); // This would be unexpected
        final Problems ps = new Problems();
        // Iterate from first to last, so that the most recent problems will be
        // added first to the Problems. Problems.getLeadProblem() will return:
        // (1) the most severe problem,
        // (2) the problem *added* first (the one triggered most recently)
        if (childrenShallPerformValidation) {
            for (ValidationItem vi : validationItems) {
                if (vi != triggerThatHasAlreadyPerformedValidation) {
                    vi.subtreeRevalidation();
                }
                ps.add(vi.getCurrentLeadProblem());
            }
        } else {
            for (ValidationItem vi : validationItems) {
                final Problem p = vi.getCurrentLeadProblem();
                ps.add(p);
                if( p != null && p.isFatal() ){
                    break; // Optimization: We already found a fatal problem, no need to keep looking.
                }
            }
        }
        
        Problem leadProblem = ps.getLeadProblem();
        boolean haveUpdatedRelevantChildrenUI = false;
        if (additionalGroupValidation != null) {
            boolean theAdditionalProblemIsLeading = false;
            if (leadProblem == null || !leadProblem.isFatal()) {
                additionalGroupValidation.performGroupValidation(ps);
                final Problem nue = ps.getLeadProblem();
                if (nue != null && !nue.equals(leadProblem)) {
                    theAdditionalProblemIsLeading = true;
                    leadProblem = nue;
                }
            }
            if (additionalGroupValidation.shallShowProblemInChildrenUIs()) {
                if (theAdditionalProblemIsLeading) {
                    for (ValidationItem vi : validationItems) {
                        vi.showIfUIEnabled(leadProblem);
                    }
                    haveUpdatedRelevantChildrenUI = true;
                } else if(additionalGroupValidation.isCurrentlyLeadingProblem()) {
                    // The "additional" problem of this group has been showing
                    // in the children UI:s (and it's time to overwrite it with
                    // each childs own respective Problem)
                    for (ValidationItem vi : validationItems) {
                        vi.showIfUIEnabled(vi.getCurrentLeadProblem());
                    }
                    haveUpdatedRelevantChildrenUI = true;
                } 
                additionalGroupValidation.setIsCurrentlyLeadingProblem(theAdditionalProblemIsLeading);
            } 
        } 
        if ( !haveUpdatedRelevantChildrenUI ){
            if( triggerThatHasAlreadyPerformedValidation != null ) {
                triggerThatHasAlreadyPerformedValidation.showIfUIEnabled(triggerThatHasAlreadyPerformedValidation.getCurrentLeadProblem());
            } else if (childrenShallPerformValidation) {
                for (ValidationItem vi : validationItems) {
                    vi.showIfUIEnabled(vi.getCurrentLeadProblem());
                }
            }
        }
        super.setCurrentLeadProblem(leadProblem);
    }
}
