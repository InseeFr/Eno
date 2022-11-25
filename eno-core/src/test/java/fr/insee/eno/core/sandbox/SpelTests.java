package fr.insee.eno.core.sandbox;

import fr.insee.eno.core.exceptions.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapperTest;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.eno.core.reference.DDIIndex;
import fr.insee.lunatic.model.flat.PairwiseLinks;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SpelTests {

    @Test
    public void useSpelToSetValue() {
        // Given a Eno questionnaire
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        enoQuestionnaire.setId("hello");

        // Put the Eno questionnaire id in a Lunatic questionnaire using Spring expression language
        Object value = enoQuestionnaire.getId();
        Questionnaire lunaticQuestionnaire = new Questionnaire();

        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("param", value);

        (new SpelExpressionParser().parseExpression("setId(#param)")).getValue(context, lunaticQuestionnaire);

        //
        assertEquals("hello", lunaticQuestionnaire.getId());
    }

    @Test
    public void spelInlineVariable() {
        String fooString = new SpelExpressionParser().parseExpression("true ? 'hello' : 'goodbye'").getValue(String.class);
        assertEquals("hello", fooString);
    }

    @Test
    public void chainedSpelExpression() {
        Variable variable = new Variable();
        variable.setName("hello");
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        String hello = spelExpressionParser.parseExpression("getName()").getValue(variable, String.class);
        assertNotNull(hello);
        assertEquals(hello, "hello");
        spelExpressionParser.parseExpression("setName(\"foo\")").getValue(variable);
        assertEquals("foo", variable.getName());
        spelExpressionParser.parseExpression("setName(\"bar\")").getValue(variable);
        assertEquals("bar", variable.getName());
    }

    @Test
    public void modifyListContentWithSpel() {
        // Idea : convert a list of something (e.g. Variable) into a list of something else (e.g. String)
        //
        Variable v1 = new Variable();
        v1.setName("foo");
        Variable v2 = new Variable();
        v2.setName("bar");
        List<Variable> variableList = new ArrayList<>();
        variableList.add(v1);
        variableList.add(v2);
        // desired output
        List<String> stringList1 = variableList.stream().map(Variable::getName).toList();
        // do it with spel
        String stringExpression = "![getName()]";
        @SuppressWarnings("unchecked")
        List<String> stringList = new SpelExpressionParser().parseExpression(stringExpression)
                .getValue(variableList, List.class);
        //
        assertNotNull(stringList);
        assertEquals(stringList1, stringList);
    }

    @Test
    public void usingIndexOnListWithSpel() {
        // Idea : convert a list of something (e.g. Variable) into a list of something else (e.g. String)
        //
        Variable v1 = new Variable();
        v1.setName("foo");
        Variable v2 = new Variable();
        v2.setName("bar");
        //
        Map<String, Variable> indexMap = new HashMap<>();
        indexMap.put("id1", v1);
        indexMap.put("id2", v2);
        //
        Variable variableReference1 = new Variable();
        variableReference1.setName("id1");
        Variable variableReference2 = new Variable();
        variableReference2.setName("id2");
        List<Variable> referenceList = new ArrayList<>();
        referenceList.add(variableReference1);
        referenceList.add(variableReference2);
        //
        List<Variable> expected = referenceList.stream().map(referenceVariable -> indexMap.get(referenceVariable.getName())).toList();
        //
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("index", indexMap);
        String stringExpression = "![#index.get(#this.getName())]";
        @SuppressWarnings("unchecked")
        List<Variable> result = new SpelExpressionParser().parseExpression(stringExpression)
                .getValue(context, referenceList, List.class);
        //
        assertNotNull(result);
        assertEquals(expected, result);
    }

    //@Test
    public void getDDIIndexUsingSpel() throws IOException, DDIParsingException {
        //
        DDIIndex ddiIndex = new DDIIndex();
        ddiIndex.indexDDI(DDIParser.parse(
                DDIMapperTest.class.getClassLoader().getResource("l10xmg2l.xml")));
        //
        Expression expression = new SpelExpressionParser()
                .parseExpression("#index.get(\"kzwoti00\")");
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("index", ddiIndex);
        //
        logicalproduct33.VariableType ddiVariable = expression.getValue(context, logicalproduct33.VariableType.class);
        assertNotNull(ddiVariable);
        assertEquals("COCHECASE",
                ddiVariable.getVariableNameArray(0).getStringArray(0).getStringValue());
    }

    @Test
    public void useStaticMethodInLunaticMapper() {
        //
        PairwiseLinks lunaticPairwiseLinks = new PairwiseLinks();
        String fooString = "FOO";
        //
        Expression expression = new SpelExpressionParser().parseExpression(
                "T(fr.insee.eno.core.model.question.PairwiseQuestion).computeLunaticAxes(#this, #param)");
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("param", fooString);
        expression.getValue(context, lunaticPairwiseLinks);
        //
        assertEquals("count(FOO)", lunaticPairwiseLinks.getXAxisIterations().getValue());
        assertEquals("VTL", lunaticPairwiseLinks.getXAxisIterations().getType());
    }

}
