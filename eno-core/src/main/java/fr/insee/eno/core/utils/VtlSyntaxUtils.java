package fr.insee.eno.core.utils;

/**
 * Utility class that provide methods for writing VTL expressions.
 */
public class VtlSyntaxUtils {

    private VtlSyntaxUtils() {}

    private static final String VTL_CONCATENATION_OPERATOR = "||";

    /**
     * Returns a VTL expression that concatenates both VTL string expressions given.
     * @param vtlString1 A VTL expression that returns a string value.
     * @param vtlString2 A VTL expression that returns a string value.
     * @return A VTL expression that concatenates both VTL string expressions given.
     */
    public static String concatenateStrings(String vtlString1, String vtlString2) {
        return vtlString1 + " " + VTL_CONCATENATION_OPERATOR + " " + vtlString2;
    }

}
