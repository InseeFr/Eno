package fr.insee.eno.core.mappers;

import fr.insee.eno.core.HelloTest;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.Variable;
import fr.insee.eno.core.model.VariableGroup;
import fr.insee.eno.core.parsers.DDIParser;
import instance33.DDIInstanceDocument;
import org.apache.xmlbeans.XmlException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DDIMapperTest {

    @Test
    public void ddiMappingTest() throws XmlException, IOException {
        DDIInstanceDocument ddiInstanceDocument = DDIParser.parse(
                HelloTest.class.getClassLoader().getResourceAsStream("l10xmg2l.xml"));
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        DDIMapper ddiMapper = new DDIMapper(ddiInstanceDocument);
        ddiMapper.mapDDI(enoQuestionnaire);
        assertEquals("INSEE-l10xmg2l", enoQuestionnaire.getId());
        assertEquals("COCHECASE", enoQuestionnaire.getFirstVariableName());
        assertEquals("COCHECASE", enoQuestionnaire.getFirstVariable().getName());
        assertTrue(enoQuestionnaire.getVariables().stream().map(Variable::getName)
                .anyMatch(name -> name.equals("COCHECASE")));
        assertTrue(enoQuestionnaire.getVariableGroups().stream().map(VariableGroup::getName)
                .anyMatch(name -> name.equals("DOCSIMPLE")));
    }

}
