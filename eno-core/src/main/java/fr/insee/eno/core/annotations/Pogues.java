package fr.insee.eno.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mapping annotation for the Pogues input format.
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Pogues {

    /** SpEL expression to be applied on the DDI object to get the adapted value
     * on the model attribute on which the annotation is placed. */
    String value();

    /** In some objects, there can be lists that may be null in some instances for valid reasons.
     * The mapper will not throw an exception if the property is set to true. */
    boolean allowNullList() default true;

    /** To activate or not debug logging for this annotation. */
    boolean debug() default false;

}
