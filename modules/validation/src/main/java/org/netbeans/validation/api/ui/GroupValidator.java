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

import org.netbeans.validation.api.Problems;

/**
 * Encapsulates validation of the <i>combination of several</i> UI
 * components within a {@link ValidationGroup}.
 *
 * <p>UI-components whose validity depends not only on their own state
 * but on the state of each other as well, can be said to have
 * validity interdependencies. In such cases not only the state of
 * each component needs to be validated as a singular, but the
 * combination of components needs to be validated as well.
 * 
 * <p>The following items outline what needs to be done to achieve this.
 *
 * <ul>
 *
 * <li> The UI components with interdependencies need to be added to
 * the same {@code ValidationGroup}
 *
 * <li> This {@code ValidationGroup} needs to be prepared at creation time
 * with an instance of {@code GroupValidator}.
 *
 * <li> In this {@code GroupValidator}, the method {@link
 * #performGroupValidation(org.netbeans.validation.api.Problems) }
 * needs to be overridden to perform the custom interdependency
 * validation.
 *
 * </ul>
 * 
 * <p>When a UI component is changed (either programmatically or by
 * having been interacted with by the user) the following will happen:
 * 
 * <ul>
 *
 * <li> As usual, the UI component will be revalidated using its
 * connected validators.
 *
 * <li> Now, only if there is no fatal {@link org.netbeans.validation.api.Problem}
 * in any of the UI components within the {@code ValidationGroup}, the validation in
 * the {@code GroupValidator} will be invoked as well.
 * 
 * <li> If it turns out that the latter yields a {@code Problem} more
 * severe (i.e strictly worse) than any other {@code Problem} in the
 * {@code ValidationGroup}, then this {@code Problem} will become the
 * lead problem in the group.
 * 
 * <li> The lead problem of the group (whichever it may be) is shown
 * as usual in the  {@link ValidationUI}(s) of the  {@code ValidationGroup}.
 *
 * <li>If the lead {@code Problem} happens to be the one caused by the
 * {@code GroupValidator}, then the default behavior is that this
 * {@code Problem} will cause all UI components within the
 * ValidationGroup to be decorated. This behavior can however be
 * disabled by passing {@code false} to the constructor {@link
 * #GroupValidator(boolean) }
 * 
 * </ul>
 *
 * <p> The following code example illustrates how this class can be
 * used.
 * 
<style type="text/css">
pre {color: #000000; background-color: #ffffff; font-family: Monospaced}
table {color: #000000; background-color: #e9e8e2; font-family: Monospaced}
.comment {color: #969696}
.character {color: #ce7b00}
.keyword-directive {color: #0000e6}
</style>
<pre>
        <span class="comment">// Given three text fields, aField, bField and cField, this class validates</span>
        <span class="comment">// that the sum of the numbers in them equals a number given in a combo box.</span>
        <span class="keyword-directive">class</span> SumValidation <span class="keyword-directive">extends</span> GroupValidator {
            SumValidation() {
                <span class="comment">// The boolean specifies whether a Problem generated by the</span>
                <span class="comment">// Gro</span><span class="comment">upValidator should cause the UI-components in the</span>
                <span class="comment">// ValidationGroup to be decorated or not</span>
                <span class="keyword-directive">super</span>(<span class="keyword-directive">true</span>);
            }
            &#64;Override
            <span class="keyword-directive">protected</span> <span class="keyword-directive">void</span> performGroupValidation(Problems problems) {
                <span class="keyword-directive">try</span> {
                    <span class="keyword-directive">int</span> desiredSum = Integer.parseInt(sumComboBox.getModel().getSelectedItem().toString());
                    <span class="keyword-directive">int</span> val1 = Integer.parseInt(aField.getText());
                    <span class="keyword-directive">int</span> val2 = Integer.parseInt(bField.getText());
                    <span class="keyword-directive">int</span> val3 = Integer.parseInt(cField.getText());
                    <span class="keyword-directive">int</span> sum = val1 + val2 + val3;
                    <span class="keyword-directive">if</span> (sum != desiredSum) {
                        problems.add( <span class="keyword-directive">new</span> Problem (val1 + <span class="character">&quot;</span><span class="character">+</span><span class="character">&quot;</span> + val2 + <span class="character">&quot;</span><span class="character">+</span><span class="character">&quot;</span> + val3 +
                                <span class="character">&quot;</span><span class="character"> equals </span><span class="character">&quot;</span> + sum + <span class="character">&quot;</span><span class="character">, not </span><span class="character">&quot;</span> + desiredSum, Severity.FATAL));
                    } <span class="keyword-directive">else</span> <span class="keyword-directive">if</span> (val1 == desiredSum || val2 == desiredSum || val3 == desiredSum) {
                        problems.add( <span class="keyword-directive">new</span> Problem (<span class="character">&quot;</span><span class="character">Hey...that&#39;s cheating!</span><span class="character">&quot;</span>,
                                Severity.WARNING) );
                    }
                } <span class="keyword-directive">catch</span> (NumberFormatException e) {
                    <span class="comment">//do nothing, the other validators would have taken care of the bad entry</span>
                }
            }
        }

        <span class="comment">// The GroupValidator can be used as follows:</span>

        <span class="comment">// Create ValidationGr</span><span class="comment">oup that will contain UI component with validity</span>
        <span class="comment">// interdependencies. Pass a GroupValidator -- SumValidation -- to the</span>
        <span class="comment">// ValidationGroup creator.</span>
        SwingValidationGroup bunch = SwingValidationGroup.create(<span class="keyword-directive">new</span> SumValidation());

        <span class="comment">// Create a Validator that can be reused for individual validation of</span>
        <span class="comment">// the three text fields</span>
        Validator&lt;String&gt; fieldValidator =
                StringValidators.trimString(StringValidators.REQUIRE_NON_EMPTY_STRING,
                StringValidators.NO_WHITESPACE,
                StringValidators.REQUIRE_VALID_NUMBER,
                StringValidators.REQUIRE_VALID_INTEGER,
                StringValidators.REQUIRE_NON_NEGATIVE_NUMBER);

        bunch.add(aField, fieldValidator);
        bunch.add(bField, fieldValidator);
        bunch.add(cField, fieldValidator);

        <span class="comment">// Add the combo box as well so that the additional group</span>
        <span class="comment">// validation is triggered whenever the combo box is </span><span class="comment">interacted with. Note</span>
        <span class="comment">// that there are no validators added for the combo box alone. Also, ValidationUI.NoOp.get()</span>
        <span class="comment">// is passed,</span><span class="comment"> so that the combo box will not be decorated when there&#39;s a problem.</span>
        bunch.add(SwingValidationListenerFactory.createJComboBoxValidationListener(sumComboBox, ValidationUI.NoOp.get()));
 *
 *
 * </pre>
 * @author Hugo Heden
 */
public abstract class GroupValidator {
    private final boolean shallShowProblemInChildrenUIs;
    private boolean isCurrentlyLeadingProblem = false;

    /**
     * Default constructor, calls {@code this(true)}
     */
    protected GroupValidator() {
        this( true );
    }
    
    /**
     * @param shallShowProblemInChildrenUIs specifies whether a
     * Problem generated by the {@code GroupValidator} (if it happens
     * to be the lead {@code Problem}) should cause the UI-components
     * in the {@code ValidationGroup} to be decorated (showing the
     * {@code Problem}) or not
     */
    protected GroupValidator(boolean shallShowProblemInChildrenUIs ) {
        this.shallShowProblemInChildrenUIs = shallShowProblemInChildrenUIs;
    }

    final boolean isCurrentlyLeadingProblem() {
        return isCurrentlyLeadingProblem;
    }

    final void setIsCurrentlyLeadingProblem(boolean isCurrentlyLeadingProblem) {
        this.isCurrentlyLeadingProblem = isCurrentlyLeadingProblem;
    }

    final boolean shallShowProblemInChildrenUIs() {
        return shallShowProblemInChildrenUIs;
    }

    /**
     * Validate the state of the combination of the UI components
     * within the ValidationGroup. If invalid
     * this method shall add one or more {@code Problem}s to
     * the passed list.
     *
     * @param problems A list of problems.
     */
    protected abstract void performGroupValidation(Problems problems);

}
