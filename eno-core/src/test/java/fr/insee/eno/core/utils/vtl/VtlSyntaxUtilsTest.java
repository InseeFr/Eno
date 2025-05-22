package fr.insee.eno.core.utils.vtl;

import fr.insee.vtl.parser.VtlTokens;
import org.junit.jupiter.api.Test;

import static fr.insee.eno.core.utils.vtl.VtlSyntaxUtils.countVariable;
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
        assertEquals("<>", VtlSyntaxUtils.getVTLTokenName(VtlTokens.NEQ));
    }

    @Test
    void testVTAggregatorFunction(){
        String expressionWithAgg = "count(FIRST_NAME) + max(TEST) || \"bonjour\"";
        assertTrue(VtlSyntaxUtils.isAggregatorUsedInsideExpression(expressionWithAgg));
        String expressionWithoutAgg = "\"Bonjour\" || FIRST_NAME";
        assertFalse(VtlSyntaxUtils.isAggregatorUsedInsideExpression(expressionWithoutAgg));
        String trickyExpressionWithoutAgg = "\"count(FIRST_NAME)\" || FIRST_NAME"; // here 'count' is not an operator since it is in a string
        assertFalse(VtlSyntaxUtils.isAggregatorUsedInsideExpression(trickyExpressionWithoutAgg));
    }

    @Test
    void testSurroundByParenthesis(){
        assertEquals("(FIRST_NAME = \"Laurent\")", VtlSyntaxUtils.surroundByParenthesis("FIRST_NAME = \"Laurent\""));
        assertEquals("((NB >= 15))", VtlSyntaxUtils.surroundByParenthesis("(NB >= 15)"));
    }

    @Test
    void testSurroundByDoubleQuotes(){
        assertEquals("\"FIRST_NAME\"", VtlSyntaxUtils.surroundByDoubleQuotes("FIRST_NAME"));
    }

    @Test
    void testJoinByORLogicExpression(){
        String expectedExpression = "(FIRST_NAME = \"Laurent\") or (NB >= 15)";
        assertEquals(expectedExpression, VtlSyntaxUtils.joinByORLogicExpression("FIRST_NAME = \"Laurent\"", "NB >= 15"));
    }

    @Test
    void testJoinByANDLogicExpression_should_addAndVtlBetweenExpression(){
        String expectedExpression = "(FIRST_NAME = \"Laurent\") and (NB >= 15)";
        assertEquals(expectedExpression, VtlSyntaxUtils.joinByANDLogicExpression("FIRST_NAME = \"Laurent\"", "NB >= 15"));
    }

    @Test
    void testJoinByANDLogicExpression_should_not_addExtraParen_when_AddAndVtlBetweenExpression(){
        String expectedExpression = "(FIRST_NAME = \"Laurent\") and (NB >= 15)";
        assertEquals(expectedExpression, VtlSyntaxUtils.joinByANDLogicExpression(
                VtlSyntaxUtils.surroundByParenthesis("FIRST_NAME = \"Laurent\""),
                VtlSyntaxUtils.surroundByParenthesis("NB >= 15")));
    }

    @Test
    void testCountVariable(){
        String variableName = "AGE";
        assertEquals("count(AGE)", countVariable(variableName));
    }

}
