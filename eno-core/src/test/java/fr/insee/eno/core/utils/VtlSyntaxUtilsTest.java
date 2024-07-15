package fr.insee.eno.core.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VtlSyntaxUtilsTest {

    @Test
    void invertExpressions() {
        assertEquals("FOO", VtlSyntaxUtils.invertBooleanExpression("not(FOO)"));
        assertEquals("not(FOO)", VtlSyntaxUtils.invertBooleanExpression("FOO"));
        assertEquals("not(FOO)", VtlSyntaxUtils.invertBooleanExpression("not(not(FOO))"));
        assertEquals("FOO = 1", VtlSyntaxUtils.invertBooleanExpression("not(FOO = 1)"));
        assertEquals("not(FOO = 1)", VtlSyntaxUtils.invertBooleanExpression("FOO = 1"));
    }

}
