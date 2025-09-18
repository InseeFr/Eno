package fr.insee.eno.core.utils.vtl;

import fr.insee.vtl.parser.VtlLexer;
import fr.insee.vtl.parser.VtlParser;
import fr.insee.vtl.parser.VtlTokens;
import lombok.NonNull;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.List;

/**
 * Utility class that provide methods for analyzing/writing VTL expressions.
 */
public class VtlSyntaxUtils {

    private VtlSyntaxUtils() {}

    // ----- VTL syntax elements used in Eno

    public static final String LEFT_JOIN_OPERATOR = getVTLTokenName(VtlTokens.LEFT_JOIN);
    public static final String USING_KEYWORD = getVTLTokenName(VtlTokens.USING);
    public static final String AND_KEYWORD = getVTLTokenName(VtlTokens.AND);

    // ----- VTL syntax methods used in Eno

    /**
     * Returns a VTL expression that concatenates both VTL string expressions given.
     * @param vtlString1 A VTL expression that returns a string value.
     * @param vtlString2 A VTL expression that returns a string value.
     * @return A VTL expression that concatenates both VTL string expressions given.
     */
    public static String concatenateStrings(String vtlString1, String vtlString2) {
        return vtlString1 + " " + getVTLTokenName(VtlTokens.CONCAT) + " " + vtlString2;
    }

    /**
     *
     * @param vtlString1
     * @param vtlString2
     * @return (vtlString1) and (vtlString2)
     */
    public static String joinByANDLogicExpression(String vtlString1, String vtlString2){
        return surroundByParenthesis(removeExtraParenthesis(vtlString1))
                + " "  + getVTLTokenName(VtlTokens.AND) + " "
                + surroundByParenthesis(removeExtraParenthesis(vtlString2));
    }

    /**
     * @param expression1
     * @param expression2
     * @return expression1 = expression2
     */
    public static String expressionEqualToOther(String expression1, String expression2){
        return expression1 + " " + getVTLTokenName(VtlTokens.EQ) + " " + expression2;
    }

    public static String expressionNotEqualToOther(String expression1, String expression2){
        return expression1 + " " +  getVTLTokenName(VtlTokens.NEQ) + " " + expression2;
    }

    /**
     *
     * @param vtlString1
     * @param vtlString2
     * @return (vtlString1) or (vtlString2)
     */
    public static String joinByORLogicExpression(String vtlString1, String vtlString2){
        return surroundByParenthesis(removeExtraParenthesis(vtlString1))
                + " "  + getVTLTokenName(VtlTokens.OR) + " "
                + surroundByParenthesis(removeExtraParenthesis(vtlString2));
    }

    /**
     *
     * @param expression
     * @return vtlExpression without parenthesis
     * example: `(nvl(TEST, "") <> "")` should return `nvl(TEST, "") <> ""`
     */
    public static String removeExtraParenthesis(String expression){ // method is package-private to be tested in isolation
        if (expression.startsWith(getVTLTokenName(VtlTokens.LPAREN)) && expression.endsWith(getVTLTokenName(VtlTokens.RPAREN)))
            return expression.substring(1, expression.length() - 1);
        return expression;
    }

    /**
     *
     * @param expression
     * @return the same expression surrounded by parenthesis
     * example: `nvl(TEST, "") <> ""` become `(nvl(TEST, "") <> "")`
     */
    public static String surroundByParenthesis(String expression){
        return getVTLTokenName(VtlTokens.LPAREN) + expression + getVTLTokenName(VtlTokens.RPAREN);
    }

    /**
     *
     * @param expression
     * @return the same expression surrounded by parenthesis
     * example: `05` become `"05"`
     */
    public static String surroundByDoubleQuotes(String expression){
        return String.format("\"%s\"", expression);
    }

    public static String nvlDefaultValue(String variableName, String defaultValue){
        return String.format("%s(%s, %s)",
                getVTLTokenName(VtlTokens.NVL),
                variableName,
                defaultValue);
    }

    /**
     * Inverts the given expression (which is supposed to be a VTL expression that returns a boolean)
     * by adding a 'not()' around it.
     * @param expression VTL boolean expression.
     * @return The inverted VTL expression.
     */
    public static String invertBooleanExpression(String expression) {
        return getVTLTokenName(VtlTokens.NOT) + surroundByParenthesis(expression);
    }

    /**
     * Replace expression by true VTL
     * @param expression VTL boolean expression.
     * @return VTL expression updated
     */
    public static String replaceByTrue(String expression, String expressionToReplace) {
        return expression.replace(expressionToReplace, "true");
    }

    /** List of Trevas token ids for VTL aggregation operators. */
    private static final List<Integer> VTL_AGR_FUNCTIONS_ID = List.of(
            // Simple aggregator functions
            VtlTokens.COUNT, VtlTokens.MIN,  VtlTokens.MAX, VtlTokens.SUM,
            // Statistical functions
            VtlTokens.AVG, VtlTokens.MEDIAN,
            VtlTokens.STDDEV_POP, VtlTokens.STDDEV_SAMP,
            VtlTokens.VAR_POP,  VtlTokens.VAR_SAMP,
            // "in" operator is used to check in value is present in vector, returns boolean
            // example: VARIABLE in VECTOR
            VtlTokens.IN,
            // operators to get a scalar value from a vector
            // example: first_value(FIRST_NAME over())
            VtlTokens.FIRST_VALUE, VtlTokens.LAST_VALUE);

    /**
     * Parses the given VTL expression to determine if a VTL aggregation is used.
     * See:
     * <ul>
     *   <li><a href="https://www.trevas.info/fr/user-guide/coverage/aggregate-operators">
     *       Trevas docs</a></li>
     *   <li><a href="https://inseefr.github.io/Bowie/1._Pogues/Le%20VTL%20dans%20Pogues/vtl/">
     *       Pogues VTL docs</a></li>
     *   <li><a href="https://inseefr.github.io/Bowie/1._Pogues/Le%20VTL%20dans%20Pogues/fonctions-vtl/">
     *       Pogues VTL functions docs</a></li>
     * </ul> .
     * @param expression VTL expression.
     * @return True if the VTL expression uses an aggregation operator.
     */
    public static boolean isAggregatorUsedInsideExpression(@NonNull String expression){
        List<Integer> tokensInExpression = getVTLTokenIdUsedInExpression(expression);
        if (tokensInExpression == null || tokensInExpression.isEmpty())
            return false;
        return VTL_AGR_FUNCTIONS_ID.stream().anyMatch(tokensInExpression::contains);
    }



    // ----- Private methods for this class implementation

    /**
     * Returns the VTL token that corresponds to the given Trevas id.
     * To be used with <code>VtlTokens<code/> values.
     * @param tokenId Identifier of the VTL token in the Trevas lib.
     * @return String VTL token.
     * @throws IllegalArgumentException if there is no token registered for the given id.
     */
    static String getVTLTokenName(int tokenId){ // method is package-private to be tested in isolation
        String token = VtlTokens.VOCABULARY.getLiteralName(tokenId);
        if (token == null)
            throw new IllegalArgumentException("Invalid VTL token id: " + tokenId);
        if (token.startsWith("'") && token.endsWith("'"))
            return token.substring(1, token.length() - 1);
        return token;
    }

    /**
     * Returns the list of VTL tokens present in the given expression.
     * @param expression VTL expression.
     * @return Trevas VTL token ids.
     */
    private static List<Integer> getVTLTokenIdUsedInExpression(String expression){
        String validExpression = wrapIntoVTLStatement(expression);
        VtlParser parser = lexAndParse(validExpression);
        VtlTokensListener listener = new VtlTokensListener();
        new ParseTreeWalker().walk(listener, parser.start());
        return listener.getTokenIdInExpressions();
    }

    /**
     * Wraps the given VTL expression in a valid statement (by assigning it to an inline variable).
     * @param expression VTL expression (e.g. "1 + 1").
     * @return VTL statement (e.g. "d := 1 + 1;").
     */
    private static String wrapIntoVTLStatement(String expression){
        return "d"+getVTLTokenName(VtlTokens.ASSIGN) + expression + getVTLTokenName(VtlTokens.EOL);
    }

    /**
     * Calls Trevas lexer to parse the given VTL statement.
     * Note: The given string must be a valid VTL statement (not an expression,
     * see the <code>wrapIntoVTLStatement</code> method).
     * @param vtlStatement VTL statement.
     * @return Trevas VTL parser for the given statement.
     */
    private static VtlParser lexAndParse(String vtlStatement) {
        CodePointCharStream stream = CharStreams.fromString(vtlStatement);
        VtlLexer lexer = new VtlLexer(stream);
        return new VtlParser(new CommonTokenStream(lexer));
    }

    /**
     * @return count(variableName)
     */
    public static String countVariable(String variableName){
        return getVTLTokenName(VtlTokens.COUNT) + getVTLTokenName(VtlTokens.LPAREN) + variableName + getVTLTokenName(VtlTokens.RPAREN);
    }

}
