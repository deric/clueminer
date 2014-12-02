package org.clueminer.clustering.api.config.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *
 * @author Tomas Barton
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({FIELD, METHOD})
public @interface Param {

    /**
     * Human readable name of this parameter.
     *
     * @return a human readable name of this property
     */
    String name() default "";

    /**
     * Human readable description of this parameter.
     *
     * @return string description
     */
    String description() default "";

    /**
     * Whether this option is required.
     * @return
     */
    boolean required() default false;
}
