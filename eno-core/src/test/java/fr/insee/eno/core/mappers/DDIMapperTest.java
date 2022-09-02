package fr.insee.eno.core.mappers;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.Variable;
import fr.insee.eno.core.model.VariableGroup;
import fr.insee.eno.core.model.question.BooleanQuestion;
import fr.insee.eno.core.model.question.SingleResponseQuestion;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.eno.core.reference.EnoIndex;
import instance33.DDIInstanceDocument;
import instance33.DDIInstanceType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import reusable33.IDType;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DDIMapperTest {

    // TODO: more unit tests (one per DDI annotation in the model classes)

    // Note: it may be pertinent to do one unit test class per model class

    // Note: it is also possible to read portions of DDI xml files to do unit testing of annotations

    @Test
    public void mapDDIInstanceId() {
        //
        String expectedId = "FOO-ID";
        // Given
        DDIInstanceType ddiInstanceType = DDIInstanceType.Factory.newInstance();
        ddiInstanceType.getIDList().add(IDType.Factory.newInstance());
        ddiInstanceType.getIDList().get(0).setStringValue(expectedId);
        // When
        DDIMapper mapper = new DDIMapper();
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        BeanWrapper beanWrapper = new BeanWrapperImpl(enoQuestionnaire);
        PropertyDescriptor propertyDescriptor = beanWrapper.getPropertyDescriptor("id");
        EvaluationContext context = new StandardEvaluationContext();
        mapper.propertyMapping(ddiInstanceType, enoQuestionnaire, beanWrapper, propertyDescriptor,context);
        // Then
        assertEquals(expectedId, enoQuestionnaire.getId());
    }

    @Test
    public void ddiMappingFunctionalTest() throws IOException {
        //
        DDIInstanceDocument ddiInstanceDocument = DDIParser.parse(
                DDIMapperTest.class.getClassLoader().getResource("in/ddi/l10xmg2l.xml"));
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(ddiInstanceDocument, enoQuestionnaire);

        // Questionnaire id
        assertEquals("INSEE-l10xmg2l", enoQuestionnaire.getId());
        // Variable
        assertEquals("COCHECASE", enoQuestionnaire.getFirstVariableName());
        assertEquals("COCHECASE", enoQuestionnaire.getFirstVariable().getName());
        assertTrue(enoQuestionnaire.getVariables().stream().map(Variable::getName)
                .anyMatch(name -> name.equals("COCHECASE")));
        Variable testedVariable = enoQuestionnaire.getVariables().stream()
                .filter(variable1 -> variable1.getName().equals("COCHECASE"))
                .findAny().orElse(null);
        assertNotNull(testedVariable);
        // Group
        assertTrue(enoQuestionnaire.getVariableGroups().stream().map(VariableGroup::getName)
                .anyMatch(name -> name.equals("DOCSIMPLE")));
        // Variables in a group
        VariableGroup testedVariableGroup = enoQuestionnaire.getVariableGroups().stream()
                .filter(variableGroup -> variableGroup.getName().equals("DOCSIMPLE"))
                .findAny().orElse(null);
        assertNotNull(testedVariableGroup);
        assertTrue(testedVariableGroup.getVariables().stream().map(Variable::getName)
                .anyMatch(name -> name.equals("COCHECASE")));
        // SingleResponseQuestion
        assertNotNull(testedVariable.getQuestionReference());
        assertEquals("jfazk91m", testedVariable.getQuestionReference());
        // Sequences
        assertEquals(2, enoQuestionnaire.getSequences().size());
        assertEquals("jfaz9kv9", enoQuestionnaire.getSequences().get(0).getId());
        // Subsequences
        assertEquals(6, enoQuestionnaire.getSubsequences().size());
        // SingleResponseQuestions
        Map<String, SingleResponseQuestion> singleResponseQuestionsMap = new HashMap<>();
        enoQuestionnaire.getSingleResponseQuestions().forEach(singleResponseQuestion ->
                singleResponseQuestionsMap.put(singleResponseQuestion.getName(), singleResponseQuestion));
        assertTrue(singleResponseQuestionsMap.get("COCHECASE") instanceof BooleanQuestion);
    }

}
