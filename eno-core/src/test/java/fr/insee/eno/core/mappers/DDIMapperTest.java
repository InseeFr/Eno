package fr.insee.eno.core.mappers;

import datacollection33.SequenceType;
import fr.insee.eno.core.HelloTest;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.Variable;
import fr.insee.eno.core.model.VariableGroup;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.eno.core.reference.DDIIndex;
import instance33.DDIInstanceDocument;
import logicalproduct33.VariableType;
import org.apache.xmlbeans.XmlException;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DDIMapperTest {

    @Test
    public void getDDIIndexUsingSpel() throws IOException {
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
        VariableType ddiVariable = expression.getValue(context, VariableType.class);
        assertNotNull(ddiVariable);
        assertEquals("COCHECASE",
                ddiVariable.getVariableNameArray(0).getStringArray(0).getStringValue());
    }

    @Test
    public void ddiMappingTest() throws IOException {
        //
        DDIInstanceDocument ddiInstanceDocument = DDIParser.parse(
                DDIMapperTest.class.getClassLoader().getResource("l10xmg2l.xml"));
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        DDIMapper ddiMapper = new DDIMapper(ddiInstanceDocument);
        ddiMapper.mapDDI(enoQuestionnaire);

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
        assertTrue(testedVariableGroup.getGroupVariables().stream().map(Variable::getName)
                .anyMatch(name -> name.equals("COCHECASE")));
        // Question
        assertNotNull(testedVariable.getQuestion());
        assertEquals("COCHECASE", testedVariable.getQuestion().getName());
        // Sequences
        assertEquals(2, enoQuestionnaire.getSequences().size());
        assertEquals("jfaz9kv9", enoQuestionnaire.getSequences().get(0).getId());
        // Subsequences
        assertEquals(5, enoQuestionnaire.getSubsequences().size());
    }

}
