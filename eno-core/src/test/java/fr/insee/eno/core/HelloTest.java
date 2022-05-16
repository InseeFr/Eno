package fr.insee.eno.core;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.lunatic.model.flat.IVariableType;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.VariableType;
import instance33.DDIInstanceDocument;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import reusable33.IDType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(1, lunaticQuestionnaire.getVariables().size());
        assertEquals("foo", lunaticQuestionnaire.getVariables().get(0).getName());
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
