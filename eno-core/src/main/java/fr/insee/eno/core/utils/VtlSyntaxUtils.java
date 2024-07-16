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

    /**
     * Inverts the given expression (which is supposed to be a VTL expression that returns a boolean)
     * by adding a 'not()' around it, or eventually removing one if the entire expression is in a "not".
     * @param expression VTL boolean expression.
     * @return The inverted VTL expression.
     */
    public static String invertBooleanExpression(String expression) {
        String trimmed = expression.trim();
        // If the whole expression is within a not(), remove it
        if (trimmed.startsWith("not(") && trimmed.endsWith(")"))
            return trimmed.substring(4, trimmed.length() - 1);
        // Otherwise, add a not() around the expression
        return "not(" + trimmed + ")";
    }

}
