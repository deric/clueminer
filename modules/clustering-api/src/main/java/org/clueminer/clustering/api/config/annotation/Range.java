package org.clueminer.clustering.api.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.clueminer.clustering.api.config.InRangeValidator;

/**
 * Annotation for restricting property to a certain range
 *
 * @author Tomas Barton
 */
@Constraint(validatedBy = InRangeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Range {

    /**
     * The lower bound of the range.
     *
     * @return double value representing the lower bound
     */
    double from();

    /**
     * The upper bound of the range.
     *
     * @return double value representing the upper bound
     */
    double to();

    Class<?>[] groups() default {};

    String message() default "property not in range <{from},{to}>";

}
