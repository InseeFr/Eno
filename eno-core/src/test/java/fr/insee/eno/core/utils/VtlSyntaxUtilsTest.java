package fr.insee.eno.core.utils;

import fr.insee.eno.core.utils.vtl.VtlSyntaxUtils;
import fr.insee.vtl.parser.VtlTokens;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VtlSyntaxUtilsTest {

    @Test
    void invertExpressions() {
        assertEquals("not(not(FOO))", VtlSyntaxUtils.invertBooleanExpression("not(FOO)"));
        assertEquals("not(FOO)", VtlSyntaxUtils.invertBooleanExpression("FOO"));
        assertEquals("not(not(not(FOO)))", VtlSyntaxUtils.invertBooleanExpression("not(not(FOO))"));
        assertEquals("not(not(FOO = 1))", VtlSyntaxUtils.invertBooleanExpression("not(FOO = 1)"));
        assertEquals("not(FOO = 1)", VtlSyntaxUtils.invertBooleanExpression("FOO = 1"));
    }
    @Test
    void getSimpleOperator(){
        assertEquals("left_join", VtlSyntaxUtils.getVTLTokenName(VtlTokens.LEFT_JOIN));
        assertEquals("using", VtlSyntaxUtils.getVTLTokenName(VtlTokens.USING));
        assertEquals("||", VtlSyntaxUtils.getVTLTokenName(VtlTokens.CONCAT));
        assertEquals("and", VtlSyntaxUtils.getVTLTokenName(VtlTokens.AND));
        assertEquals("or", VtlSyntaxUtils.getVTLTokenName(VtlTokens.OR));
        assertEquals("not", VtlSyntaxUtils.getVTLTokenName(VtlTokens.NOT));
        assertEquals("(", VtlSyntaxUtils.getVTLTokenName(VtlTokens.LPAREN));
        assertEquals(")", VtlSyntaxUtils.getVTLTokenName(VtlTokens.RPAREN));
        assertEquals(";", VtlSyntaxUtils.getVTLTokenName(VtlTokens.EOL));
        assertEquals(":=", VtlSyntaxUtils.getVTLTokenName(VtlTokens.ASSIGN));
    }

    @Test
    void testVTAggregatorFunction(){
        String expressionWithAgg = "count(PRENOM) + max(TEST) || \"bonjour\"";
        assertTrue(VtlSyntaxUtils.isAggregatorUsedInsideExpression(expressionWithAgg));
        String expressionWithoutAgg = "\"Bonjour\" || PRENOM";
        assertFalse(VtlSyntaxUtils.isAggregatorUsedInsideExpression(expressionWithoutAgg));
        String trickyExpressionWithoutAgg = "\"count(PRENOM)\" || PRENOM";
        assertFalse(VtlSyntaxUtils.isAggregatorUsedInsideExpression(trickyExpressionWithoutAgg));
    }

}
