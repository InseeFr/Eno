package fr.insee.eno.core.mappers;

import fr.insee.eno.core.model.*;
import fr.insee.eno.core.model.question.BooleanQuestion;
import fr.insee.eno.core.model.question.SingleResponseQuestion;
import fr.insee.eno.core.parsers.DDIParser;
import instance33.DDIInstanceDocument;
import instance33.DDIInstanceType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reusable33.IDType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DDIMapperTest {

    private static EnoQuestionnaire enoQuestionnaire;

    @BeforeAll
    public static void mapDDI() throws IOException {
        //
        DDIInstanceDocument ddiInstanceDocument = DDIParser.parse(
                DDIMapperTest.class.getClassLoader().getResource("l10xmg2l.xml"));
        //
        enoQuestionnaire = new EnoQuestionnaire();
        //
        DDIMapper ddiMapper = new DDIMapper(ddiInstanceDocument);
        ddiMapper.mapDDI(enoQuestionnaire);


        /* TODO: unit testing of annotations like this (also possible to read portions of DDI xml files
        String expectedId = "TOTO-ID";
        //
        DDIInstanceType ddiInstanceType = DDIInstanceType.Factory.newInstance();
        ddiInstanceType.getIDList().add(IDType.Factory.newInstance());
        ddiInstanceType.getIDList().get(0).setStringValue(expectedId);
        //
        DDIMapper mapper = new DDIMapper(ddiInstanceType);
        EnoQuestionnaire enoQuestionnaire1 = new EnoQuestionnaire();
        mapper.mapDDI(enoQuestionnaire1, ddiInstanceType);
        //
        assertEquals(expectedId, enoQuestionnaire1.getId());
        */
    }

    @Test
    public void ddiMappingTest() {
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
        assertEquals(5, enoQuestionnaire.getSubsequences().size());
        // SingleResponseQuestions
        Map<String, SingleResponseQuestion> singleResponseQuestionsMap = new HashMap<>();
        enoQuestionnaire.getSingleResponseQuestions().forEach(singleResponseQuestion ->
                singleResponseQuestionsMap.put(singleResponseQuestion.getName(), singleResponseQuestion));
        assertTrue(singleResponseQuestionsMap.get("COCHECASE") instanceof BooleanQuestion);
    }

}
