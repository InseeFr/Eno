package fr.insee.eno.core.annotations;

import fr.insee.eno.core.parameter.Format;

import java.lang.annotation.*;

/**
 * Control annotation to specify the context type in the mapped objects that corresponds to the annotated class
 * for different formats.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Contexts {

    Context[] value();

    /**
     * Control annotation to specify the context type in the mapped object that corresponds to the annotated class
     * for a certain format.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Repeatable(value = Contexts.class)
    @interface Context {

        /** Concerned format. */
        Format format();

        /** Allowed context class types in the mapped object. */
        Class<?>[] type();
    }
}
