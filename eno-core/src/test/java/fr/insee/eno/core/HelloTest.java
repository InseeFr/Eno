package fr.insee.eno.core;

import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.Variable;
import fr.insee.eno.core.model.VariableGroup;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.lunatic.model.flat.IVariableType;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.VariableType;
import instance33.DDIInstanceDocument;
import org.apache.xmlbeans.XmlException;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import reusable33.IDType;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HelloTest {

    @Test
    public void hello() {
        //
        DDIInstanceDocument newInstance = DDIInstanceDocument.Factory.newInstance();
    }

    @Test
    public void parserDDITest() throws XmlException, IOException {
        //
        DDIInstanceDocument ddiInstance = DDIInstanceDocument.Factory.parse(
                HelloTest.class.getClassLoader().getResourceAsStream("l10xmg2l.xml"));
        //
        assertNotNull(ddiInstance);
        //

    }

    @Test
    public void xmlBeansAndDDI(){
        IDType idType = IDType.Factory.newInstance();
        String stringId = "foo";
        idType.setStringValue(stringId);
        assertEquals(stringId, idType.getStringValue());
    }

    @Test
    public void ddiMappingTest() throws XmlException, IOException {
        DDIInstanceDocument ddiInstanceDocument = DDIParser.parse(
                HelloTest.class.getClassLoader().getResourceAsStream("l10xmg2l.xml"));
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        DDIMapper.map(enoQuestionnaire, ddiInstanceDocument);
        //
        assertEquals("INSEE-l10xmg2l", enoQuestionnaire.getId());
        assertEquals("COCHECASE", enoQuestionnaire.getFirstVariableName());
        assertEquals("COCHECASE", enoQuestionnaire.getFirstVariable().getName());
        assertTrue(enoQuestionnaire.getVariables().stream().map(Variable::getName)
                .anyMatch(name -> name.equals("COCHECASE")));
        assertTrue(enoQuestionnaire.getVariableGroups().stream().map(VariableGroup::getName)
                .anyMatch(name -> name.equals("DOCSIMPLE")));
    }

    }
}
