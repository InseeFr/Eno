package fr.insee.eno.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(FIELD)
public @interface DDI {

    /** Property to be removed (see Context annotation). */
    Class<?>[] contextType();

    /** SpEL expression to be applied on the DDI object to get the adapted value
     * on the model attribute on which the annotation is placed. */
    String field();

    /** In some objects, there can be lists that may be null in some instances for valid reasons.
     * The mapper will not throw an exception if the property is set to true. */
    boolean allowNullList() default true;

    /** In some objects, there can be lists that may be null in some instances for valid reasons.
     * The mapper will not throw an exception if this annotation is placed. */
    @Retention(RUNTIME)
    @Target(FIELD)
    @interface AllowNullList {}

}
