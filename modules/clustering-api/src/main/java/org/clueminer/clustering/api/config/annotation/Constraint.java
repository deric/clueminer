package org.clueminer.clustering.api.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.clueminer.clustering.api.config.ConstraintValidator;

/**
 *
 * @author Tomas Barton
 */
@Documented
@Target(value = {ElementType.ANNOTATION_TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Constraint {

    public Class<? extends ConstraintValidator<?, ?>>[] validatedBy();
}
