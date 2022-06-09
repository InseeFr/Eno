package fr.insee.eno.core;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.Variable;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.lunatic.model.flat.*;
import instance33.DDIInstanceDocument;
import logicalproduct33.VariableGroupType;
import logicalproduct33.impl.VariableGroupTypeImpl;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import reusable33.IDType;
import reusable33.ReferenceType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Sandbox
 */
public class HelloTest {

    @Test
    public void hello() {
        //
        DDIInstanceDocument newInstance = DDIInstanceDocument.Factory.newInstance();
        System.out.println("Hello !");
    }

    @Test
    public void ddiObjects() throws IOException {
        //
        DDIInstanceDocument ddiInstanceDocument = DDIParser.parse(
                this.getClass().getClassLoader().getResource("l10xmg2l.xml"));
        //
        VariableGroupType firstVariableGroupType = ddiInstanceDocument.getDDIInstance().getResourcePackageArray(0)
                .getVariableSchemeArray(0).getVariableGroupArray(0);
        List<ReferenceType> referenceList = firstVariableGroupType.getVariableGroupReferenceList();
        referenceList.get(0).getIDArray(0).getStringValue();
    }

    @Test
    public void xmlBeansAndDDI() {
        IDType idType = IDType.Factory.newInstance();
        String stringId = "foo";
        idType.setStringValue(stringId);
        assertEquals(stringId, idType.getStringValue());
    }

    @Test
    public void helloLunaticQuestionnaire() {
        // New Lunatic questionnaire
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        // Variables list
        List<IVariableType> lunaticVariables = lunaticQuestionnaire.getVariables();
        // Add a variable
        IVariableType lunaticVariable = new VariableType();
        lunaticVariable.setName("foo");
        lunaticVariables.add(lunaticVariable);
        //
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        ComponentType componentType;

        //
        assertEquals(1, lunaticQuestionnaire.getVariables().size());
        assertEquals("foo", lunaticQuestionnaire.getVariables().get(0).getName());
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

    @Test
    public void helloSpel() {
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
}
