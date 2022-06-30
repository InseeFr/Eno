package fr.insee.eno.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Lunatic {

    Class<?>[] contextType();

    String field();

    Class<?> instanceType() default Object.class;
}
