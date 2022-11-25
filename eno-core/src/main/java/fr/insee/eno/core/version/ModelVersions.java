package fr.insee.eno.core.version;

public class ModelVersions {

    /* TODO: These methods won't work outside dev environment
        -> use gradle to copy versions in application.properties,
        then use the latter in code.
     */

    /** Return the Eno version. */
    public static String enoVersion() {
        return "TODO"; // TODO: see above
    }

    /** Return the Lunatic-Model version used in Eno. */
    public static String lunaticModelVersion() {
        return "TODO"; // TODO: see above
    }

}
