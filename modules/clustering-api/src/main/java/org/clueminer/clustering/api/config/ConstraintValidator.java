
package org.clueminer.clustering.api.config;

import java.lang.annotation.Annotation;

/**
 *
 * @author Tomas Barton
 * @param <A>
 * @param <T>
 */
public interface ConstraintValidator<A extends Annotation, T extends Object> {

    public void initialize(A a);

    public boolean isValid(T t);
}
