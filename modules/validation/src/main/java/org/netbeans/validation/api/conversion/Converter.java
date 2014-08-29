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
package org.netbeans.validation.api.conversion;

import java.util.Collection;
import org.netbeans.validation.api.*;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.validation.api.ValidatorUtils;
import org.openide.util.Lookup;

/**
 * Converts validators. Can convert a {@link Validator} working on one type (such as {@code String})
 * to a {@code Validator} of a different type, such as <code>javax.swing.text.Document</code>.
 * In this way, it is possible to write only validators
 * for {@code String}s, but use them against <code>javax.swing.text.Document</code>s (for validating
 * {@code JTextField} and {@code JTextArea} components), etc.
 *
 * @author Tim Boudreau
 */
public abstract class Converter<From,To> {
    private static Set <Converter<?,?>> registry = new HashSet<Converter<?,?>>();
    private final Class<From> from;
    private final Class<To> to;
    protected Converter (Class<From> from, Class<To> to) {
        this.from = from;
        this.to = to;
    }

    public final Class<From> from() {
        return from;
    }

    public final Class<To> to() {
        return to;
    }

    /**
     * Create a {@link Validator} for type {@code To} from a validator for type
     * {@code From}. For example, a converter that is a factory for Validators
     * of {@code javax.swing.text.Document}s from a validator
     * that only handles Strings may be created. (Convert would simply return a
     * Validator<Document> that wraps the Validator<String>. At validation time
     * it will first call Document.getText(), and then pass the result to
     * the wrapped Validator<String>).
     * @param from The original validator.
     * @return A validator of the type requested
     */
    public abstract Validator<To> convert (Validator<From> from);

    /**
     * Will merge the chain of passed validators to one, and then convert it to 
     * the requested type. See {@link #convert(org.netbeans.validation.api.Validator) }
     * 
     * @param froms A chain of validator to convert.
     * @return A validator of the type requested
     */
    public final Validator<To> convert (Validator<From>... froms) {
        return (convert(ValidatorUtils.merge(froms)));
    }

    /**
     * Register a converter
     * @param <From>
     * @param <To>
     * @param from
     * @param to
     * @param converter
     */
    public static void register (Converter<?,?> converter) {
        registry.add (converter);
    }

    static {
        Converter.register (new StringToDocumentConverter());
        Converter.register (new StringToComboBoxModelConverter());
        Converter.register (new SelectedIndicesToListSelectionModelConverter());
        Converter.register (new SelectedIndicesToButtonModelArrayConverter());
    }

    /**
     * Find a converter to create validators for one type from validators for
     * another type.
     * @param <From> The type of object we get from a component, such as a
     * <code>javax.swing.text.Document</code>
     * @param <To> The type of object we want to process, such as a
     * <code>java.lang.String</code>
     * @param from A class, such as <code>Document.class</code>
     * @param to A class such as <code>String.class</code>
     * @return An object which can take validators for type <code>From</code>
     * and produce validators for type <code>To</code>
     */
    public static<From, To> Converter<From, To> find (Class<From> from, Class<To> to) {
        Collection<? extends Converter> converters = Lookup.getDefault().lookupAll(Converter.class);
        for (Converter<?,?> c : converters) {
            if (c.match(from,to)) {
                return c.as(from, to);
            }
        }
        for (Converter<?,?> c : registry) {
            if (c.match(from, to)) {
                return c.as(from, to);
            }
        }
        throw new IllegalArgumentException ("No registered converter from " +
                from.getName() + " to " + to.getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Converter<?, ?> other = (Converter<?, ?>) obj;
        if (this.from != other.from && (this.from == null || !this.from.equals(other.from))) {
            return false;
        }
        if (this.to != other.to && (this.to == null || !this.to.equals(other.to))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.from != null ? this.from.hashCode() : 0);
        hash = 11 * hash + (this.to != null ? this.to.hashCode() : 0);
        return hash;
    }

    private boolean match (Class<?> from, Class<?> to) {
//        return from().isAssignableFrom(from) && to().isAssignableFrom(to);
        return from().equals(from) && to().equals(to);
    }
    
    <T, R> Converter<T,R> as (Class<T> t, Class<R> r) {
        return new Wrap<T,R,From,To>(t, r, this);
    }


    static final class Wrap<A, B, T, R> extends Converter<A, B> {
        final Converter<T,R> other;
        private final Exception ex;
        Wrap (Class<A> a, Class<B> b, Converter<T,R> other) {
            super (a,b);
            this.other = other;
            ex = new Exception();
        }

        @Override
        public Validator<B> convert(Validator<A> from) {
            Validator<T> tv = ValidatorUtils.cast (other.from, from);
            Validator<R> cvt = other.convert(tv);
            return ValidatorUtils.cast(to(), cvt);
        }
    }
//    private static final class DepthComparator implements Comparator<Converter<?,?>> {
//        int classDepth(Class<?> type) {
//            int depth = 0;
//            Set<Class<?>> set = new HashSet<Class<?>>();
//            for (;type != null; type = type.getSuperclass()) {
//                set.addAll(Arrays.asList(type.getInterfaces()));
//                depth++;
//            }
//            return depth + set.size();
//        }
//
//        public int compare(Converter<?, ?> o1, Converter<?, ?> o2) {
//            return classDepth(o1.from()) - classDepth(o2.from());
//        }
//    }
}
