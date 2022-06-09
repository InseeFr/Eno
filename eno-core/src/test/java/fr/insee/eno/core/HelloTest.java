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
