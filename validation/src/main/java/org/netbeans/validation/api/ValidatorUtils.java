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
package org.netbeans.validation.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.netbeans.validation.api.conversion.Converter;

/**
 * Some utilities for manipulating and creating new Validators, useful in many situations.
 *
 * @author Tim Boudreau
 */
public class ValidatorUtils {

    /**
     * Merge together a chain of validators (all working which work against the same
     * type), using logical {@code AND}. The resulting validator reports success if <b>all</b> the merged validators
     * report success. (If <b>one</b> or more of the merged validators report failure, the resulting
     * merged validator reports failure.)
     *
     * @param <T> The type of model (Document, String, etc.) you want to
     * work with
     * @param validators A chain of validators which should be logically
     * AND'd together, merged into a single validator
     * @return a single validator which delegates to all of the passed ones
     */
    public static <T> Validator<T> merge (Validator<T>... validators) {
        if (validators == null) {
            throw new NullPointerException();
        }
        if (validators.length == 1) {
            return validators[0];
        }
        if (validators.length == 0) {
            throw new IllegalArgumentException ("Merging empty array of " +
                    "validators");
        }
        return new AndValidator<T>(validators[0].modelType(), Arrays.asList(validators));
    }

    /**
     * Merge together two validators (both of which work against the same type), using logical {@code AND}.
     * The resulting validator reports success if <b>both</b> the merged validators
     * report success. (If <b>one</b> or both of the merged validators report failure, the resulting
     * merged validator reports failure.)
     * <p>Unlike {@link #merge(Validator...)}, calling this method does not trigger warnings under {@code -Xlint:unchecked}.
     * If you wish to merge more than two validators, simply merge the result of merging two with the next one.
     * @param <T> The type of model (Document, String, etc.) you want to
     * work with
     * @param validator1 one validator
     * @param validator2 another validator of the same type
     * @return a single validator which delegates to both of the passed ones
     */
    public static <T> Validator<T> merge(Validator<T> validator1, Validator<T> validator2) {
        // XXX could check if one or both are instanceof AndValidator and unpack them, but probably overkill
        List<Validator<T>> validators = new ArrayList<Validator<T>>(2);
        validators.add(validator1);
        validators.add(validator2);
        return new AndValidator<T>(validator1.modelType(), validators);
    }

    /**
     * Wrapper one or more validators in a validator which imposes a limit on
     * the severity of the validators in use.  This means that while the
     * validators you pass in here will work normally, all Problems encountered
     * which are of greater severity than the maximum passed here will be reset
     * to the level you pass here.
     * <p/>
     * This makes it possible to use validators from the StringValidators enum, which
     * mostly produce FATAL errors, for cases where only a warning or info message
     * is actually to be displayed.
     * @param <T> The type
     * @param maximum
     * @param validators
     */
    public static <T> Validator<T> limitSeverity (Severity maximum, Validator<T>... validators) {
        assert maximum != null : "Maximum null";
        assert validators != null : "Validators null";
        assert validators.length > 0 : "Empty validators array";
        return new CustomLevelValidator<T>(maximum, merge(validators));
    }

    /**
     * Wraps a validator in a validator which imposes a limit on the severity of the validator in use.
     * This means that while the
     * validator you pass in here will work normally, all Problems encountered
     * which are of greater severity than the maximum passed here will be reset
     * to the level you pass here.
     * <p/>
     * This makes it possible to use validators from the StringValidators enum, which
     * mostly produce FATAL errors, for cases where only a warning or info message
     * is actually to be displayed.
     * <p>Unlike {@link #limitSeverity(Severity,Validator...)}, calling this method does not trigger warnings under {@code -Xlint:unchecked}.
     * If you wish to limit more than one validator, simply limit the result of {@link #merge(Validator,Validator)}.
     * @param <T> The type
     * @param maximum
     * @param validator
     */
    public static <T> Validator<T> limitSeverity(Severity maximum, Validator<T> validator) {
        assert maximum != null : "Maximum null";
        assert validator != null : "Validator null";
        return new CustomLevelValidator<T>(maximum, validator);
    }

    /**
     * Get a validator for a validator of a possibly unknown type.  The resulting
     * validator simply uses other.modelType().cast(model) and passes it on;
     * this will result in a ClassCastException if the passed type argument is
     * not the same as or a subtype of the passed Validator's
     * <code>modelType()</code>.
     * <p/>
     * This method exists principally to avoid compile-time warnings for cases
     * where a validator type is not actually known at compile time.  Most
     * client code will use a specific type;  this technique is mainly
     * used for cases of factories for validators against multiple types,
     * whose types cannot be determined at compile-time.
     * <p/>
     *
     * @param <T> The type parameter of the desired validator
     * @param <R> The type of the actual validator
     * @param type The model type of the desired validator
     * @param other A validator whose type should be a supertype or the same as
     * the passed <code>type</code> argument
     * @return A validator of the desired type
     */
    public static <T, R> Validator<T> cast (Class<T> type, Validator<R> other) {
        return new CastValidator<T, R> (type, other);
    }

    /**
     * Will do the following:
     * <ul>
     * <li>Check if <code>t</code> is equal to or assignable to this Validator's
     * model type.  If so, will return an adapter which casts this Validator
     * as that type.
     * </li>
     * <li>Check if there is a registered <code>Converter</code> that acts as
     * a factory for Validators of the passed type from Validators of this type
     * (e.g. produce a Validator for a <code>javax.swing.text.Document</code> from a
     * Validator for a <code>java.lang.String</code>).
     * If one is found, use that to manufacture a wrapper validator for this
     * one, which converts arguments appropriately.
     * @param <T>
     * @param t
     * @throws IllegalArgumentException if no way is known to either cast
     * or adapt this validator's model type to the requested one
     * @return A validator
     */
    public final <T,R> Validator<T> as (Class<T> t, Validator<R> v) {
        Class<R> type = v.modelType();
        if (t.equals(type) || type.isAssignableFrom(t)) {
            return ValidatorUtils.cast(t, v);
        }
        return Converter.find(type, t).convert(v);
    }

    private static final class CastValidator<T, R> extends AbstractValidator<T> {
        private final Validator<R> wrapped;
        private Exception e;
        CastValidator(Class<T> type, Validator<R> wrapped) {
            super (type);
            this.wrapped = wrapped;
            if (!type.isAssignableFrom(wrapped.modelType())) {
                throw new ClassCastException("Not assignable: " + type.getName() + " from " + wrapped.modelType().getName());
            }
            assert (e = new Exception()) != null;
        }

        public void validate(Problems problems, String compName, T model) {
            try {
                wrapped.validate(problems, compName, wrapped.modelType().cast(model));
            } catch (ClassCastException cce) {
                if (e != null) {
                    cce.initCause(e);
                }
                throw cce;
            }
        }

        @Override
        public String toString() {
            return super.toString() + "[" + wrapped + "]";
        }
    }
}
