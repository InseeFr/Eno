package fr.insee.eno.core.sandbox;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.variable.CollectedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Sandbox tests on SpEL. */
class SpelTests {

    @Test
    void useSpelToSetValue() {
        // Given: a Eno questionnaire with id, and an Lunatic questionnaire with no id
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        enoQuestionnaire.setId("hello");
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        // When: Put the Eno questionnaire id in a Lunatic questionnaire using Spring expression language
        Object value = enoQuestionnaire.getId();
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("param", value);
        (new SpelExpressionParser().parseExpression("setId(#param)"))
                .getValue(context, lunaticQuestionnaire);
        // Then: the Lunatic questionnaire should have the id
        assertEquals("hello", lunaticQuestionnaire.getId());
    }

    @Test
    void ternaryOperator() {
        String fooString = new SpelExpressionParser().parseExpression("true ? 'hello' : 'goodbye'")
                .getValue(String.class);
        assertEquals("hello", fooString);
    }

    @Test
    void safeNavigationOperator() {
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        Object result = spelExpressionParser.parseExpression("#this?.getBindingReferences()?.clear()")
                .getValue((Object) null);
        assertNull(result);
    }

    @Test
    void chainedSpelExpression() {
        Variable variable = new CollectedVariable();
        variable.setName("hello");
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        String hello = spelExpressionParser.parseExpression("getName()").getValue(variable, String.class);
        assertNotNull(hello);
        assertEquals("hello", hello);
        spelExpressionParser.parseExpression("setName(\"foo\")").getValue(variable);
        assertEquals("foo", variable.getName());
        spelExpressionParser.parseExpression("setName(\"bar\")").getValue(variable);
        assertEquals("bar", variable.getName());
    }

    /** Idea: convert a list of something (e.g. Variable) into a list of something else (e.g. String) */
    @Test
    void spelProjectionOperator() {
        // Given: a list of variables
        Variable v1 = new CollectedVariable();
        v1.setName("foo");
        Variable v2 = new CollectedVariable();
        v2.setName("bar");
        List<Variable> variableList = List.of(v1, v2);
        // When: use SpEL projection operator
        @SuppressWarnings("unchecked")
        List<String> result = new SpelExpressionParser().parseExpression("![getName()]")
                .getValue(variableList, List.class);
        // Then: check the result is as expected
        List<String> expected = variableList.stream().map(Variable::getName).toList();
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void callMethodOnProjection() {
        List<String> stringList = List.of("a", "b", "c");
        Boolean result = new SpelExpressionParser().parseExpression("#root.?[#this == 'c'].size() > 0")
                .getValue(stringList, Boolean.class);
        assertNotNull(result);
        assertTrue(result);
    }

}
