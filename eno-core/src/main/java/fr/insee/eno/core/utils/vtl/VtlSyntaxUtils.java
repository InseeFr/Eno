package fr.insee.eno.core.utils.vtl;

import fr.insee.vtl.parser.VtlLexer;
import fr.insee.vtl.parser.VtlParser;
import fr.insee.vtl.parser.VtlTokens;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.List;

/**
 * Utility class that provide methods for writing VTL expressions.
 */
public class VtlSyntaxUtils {

    // See: http://www.trevas.info/fr/user-guide/coverage/aggregate-operators
    private static final List<Integer> VTL_AGR_FUNCTIONS_ID = List.of(
            VtlTokens.COUNT, VtlTokens.MIN,  VtlTokens.MAX, VtlTokens.MEDIAN,
            VtlTokens.SUM,  VtlTokens.AVG,
            VtlTokens.STDDEV_POP, VtlTokens.STDDEV_SAMP,
            VtlTokens.VAR_POP,  VtlTokens.VAR_SAMP);

    private static String DUMMY_AFFECTATION = "d"+getVTLTokenName(VtlTokens.ASSIGN);

    public static String getVTLTokenName(int tokenId){
        String token = VtlTokens.VOCABULARY.getLiteralName(tokenId);
        if (token.startsWith("'") && token.endsWith("'")) return token.substring(1, token.length() - 1);
        return token;
    }

    private static String wrapIntoValidVTLExpression(String expression){
        return DUMMY_AFFECTATION + expression + getVTLTokenName(VtlTokens.EOL);
    }

    private static List<Integer> getVTLTokenIdUsedInExpression(String expression){
        String validExpression = wrapIntoValidVTLExpression(expression);
        VtlParser parser = lexeAndParse(validExpression);
        Listener listener = new Listener();
        new ParseTreeWalker().walk(listener, parser.start());
        return listener.getTokenIdInExpressions();
    }

    public static boolean isAggregatorUsedInsideExpression(String expression){
        List<Integer> tokensInExpression = getVTLTokenIdUsedInExpression(expression);
        if(tokensInExpression == null || tokensInExpression.isEmpty()) return false;
        return VTL_AGR_FUNCTIONS_ID.stream().anyMatch(tokensInExpression::contains);
    }

    public static final String LEFT_JOIN_OPERATOR = getVTLTokenName(VtlTokens.LEFT_JOIN);
    public static final String USING_KEYWORD = getVTLTokenName(VtlTokens.USING);

    private VtlSyntaxUtils() {}

    private static final String VTL_CONCATENATION_OPERATOR = VtlSyntaxUtils.getVTLTokenName(VtlTokens.CONCAT);

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
     * by adding a 'not()' around it.
     * @param expression VTL boolean expression.
     * @return The inverted VTL expression.
     */
    public static String invertBooleanExpression(String expression) {
        return getVTLTokenName(VtlTokens.NOT) +
                    getVTLTokenName(VtlTokens.LPAREN) +
                        expression +
                    getVTLTokenName(VtlTokens.RPAREN);
    }

    private static VtlParser lexeAndParse(String expression) {
        CodePointCharStream stream = CharStreams.fromString(expression);
        VtlLexer lexer = new VtlLexer(stream);
        return new VtlParser(new CommonTokenStream(lexer));
    }

}
