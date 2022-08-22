package fr.insee.eno.core.annotations;

public @interface TargetType {

    /** Object type that will be instantiated by the mapper if the condition evaluation returns true. */
    Class<?> type();

    /** A SpEL expression that must return a boolean value.
     * The expression will be evaluated on the Eno object. */
    String condition();

}
