package fr.insee.eno.core.version;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ModelVersions {

    /* TODO: These methods won't work outside dev environment
        -> use gradle to copy gradle.properties in application.properties,
        then use the latter in code.
     */

    /** Return the Eno version. */
    public static String EnoVersion() {
        try {
            Properties enoProperties = new Properties();
            enoProperties.load(new FileInputStream("../gradle.properties"));
            return enoProperties.getProperty("version.eno");
        } catch (IOException e) {
            throw new RuntimeException("Unable to read Eno version.");
        }
    }

    /** Return the Lunatic-Model version used in Eno. */
    public static String lunaticModelVersion() {
        try {
            Properties enoCoreProperties = new Properties();
            enoCoreProperties.load(new FileInputStream("gradle.properties"));
            return enoCoreProperties.getProperty("version.lunatic-model");
        } catch (IOException e) {
            throw new RuntimeException("Unable to read Lunatic-Model version.");
        }
    }

}
