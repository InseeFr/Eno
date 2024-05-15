package fr.insee.eno.core.mapping.in.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.suggester.SuggesterConfigurationDTO;
import fr.insee.eno.core.processing.in.steps.ddi.DDIDeserializeSuggesterConfiguration;
import fr.insee.eno.core.serialize.DDIDeserializer;
import instance33.DDIInstanceDocument;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Code lists configurations are stored in code list objects in DDI.
 * This class tests the mapping of these between DDI and the Eno model.
 * Note: few tests on the suggester configuration object here, would be redundant with DDISuggesterDeserializerTest.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CodeListSuggesterTest {

    private EnoQuestionnaire enoQuestionnaire;

    @BeforeAll
    void mapDDI() throws DDIParsingException {
        // Given a DDI with suggester code lists
        DDIInstanceDocument ddiInstanceDocument = DDIDeserializer.deserialize(
                CodeListSuggesterTest.class.getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-suggester.xml"));
        // When mapping it to Eno and deserializing xml suggester configuration
        DDIMapper ddiMapper = new DDIMapper();
        enoQuestionnaire = new EnoQuestionnaire();
        ddiMapper.mapDDI(ddiInstanceDocument, enoQuestionnaire);
        new DDIDeserializeSuggesterConfiguration().apply(enoQuestionnaire);
        // Then -> tests
    }

    @Test
    void activityCodeList() {
        CodeList codeList = enoQuestionnaire.getCodeLists().stream()
                .filter(codeList1 -> "L_ACTIVITES-1-0-0".equals(codeList1.getName()))
                .findAny().orElse(null);
        assertNotNull(codeList);
        assertEquals("activité", codeList.getLabel());
    }

    @Test
    void cityCodeList() {
        CodeList codeList = enoQuestionnaire.getCodeLists().stream()
                .filter(codeList1 -> "L_COMMUNEPASSEE-1-2-0".equals(codeList1.getName()))
                .findAny().orElse(null);
        assertNotNull(codeList);
        assertEquals("commune passée", codeList.getLabel());
        SuggesterConfigurationDTO suggesterConfiguration = codeList.getSuggesterConfiguration();
        assertEquals(1, suggesterConfiguration.getFields().size());
        assertNotNull(suggesterConfiguration.getQueryParser());
    }

    @Test
    void departmentCodeList() {
        CodeList codeList = enoQuestionnaire.getCodeLists().stream()
                .filter(codeList1 -> "L_DEPNAIS-1-1-0".equals(codeList1.getName()))
                .findAny().orElse(null);
        assertNotNull(codeList);
        assertEquals("département de naissance", codeList.getLabel());
        SuggesterConfigurationDTO suggesterConfiguration = codeList.getSuggesterConfiguration();
        assertEquals(2, suggesterConfiguration.getFields().size());
        assertNotNull(suggesterConfiguration.getOrder());
        assertNotNull(suggesterConfiguration.getQueryParser());
    }

    @Test
    void countryCodeList() {
        CodeList codeList = enoQuestionnaire.getCodeLists().stream()
                .filter(codeList1 -> "L_PAYS-1-2-0".equals(codeList1.getName()))
                .findAny().orElse(null);
        assertNotNull(codeList);
        assertEquals("pays", codeList.getLabel());
        SuggesterConfigurationDTO suggesterConfiguration = codeList.getSuggesterConfiguration();
        assertEquals(1, suggesterConfiguration.getFields().size());
        assertNotNull(suggesterConfiguration.getQueryParser());
    }

    @Test
    void nationalityCodeList() {
        CodeList codeList = enoQuestionnaire.getCodeLists().stream()
                .filter(codeList1 -> "L_NATIONALITE-1-2-0".equals(codeList1.getName()))
                .findAny().orElse(null);
        assertNotNull(codeList);
        assertEquals("nationalité", codeList.getLabel());
        SuggesterConfigurationDTO suggesterConfiguration = codeList.getSuggesterConfiguration();
        assertEquals(1, suggesterConfiguration.getFields().size());
        assertNotNull(suggesterConfiguration.getQueryParser());
    }

    @Test
    void professionMasculineFormCodeList() {
        CodeList codeList = enoQuestionnaire.getCodeLists().stream()
                .filter(codeList1 -> "L_PCS_HOMMES-1-5-0".equals(codeList1.getName()))
                .findAny().orElse(null);
        assertNotNull(codeList);
        assertEquals("PCS hommes", codeList.getLabel());
        SuggesterConfigurationDTO suggesterConfiguration = codeList.getSuggesterConfiguration();
        assertEquals(1, suggesterConfiguration.getFields().size());
        assertEquals(23, suggesterConfiguration.getStopWords().size());
        assertNotNull(suggesterConfiguration.getQueryParser());
    }

    @Test
    void professionFeminineFormCodeList() {
        CodeList codeList = enoQuestionnaire.getCodeLists().stream()
                .filter(codeList1 -> "L_PCS_FEMMES-1-5-0".equals(codeList1.getName()))
                .findAny().orElse(null);
        assertNotNull(codeList);
        assertEquals("PCS femmes", codeList.getLabel());
        SuggesterConfigurationDTO suggesterConfiguration = codeList.getSuggesterConfiguration();
        assertEquals(1, suggesterConfiguration.getFields().size());
        assertEquals(23, suggesterConfiguration.getStopWords().size());
        assertNotNull(suggesterConfiguration.getQueryParser());
    }

    @Test
    void diplomaCodeList() {
        CodeList codeList = enoQuestionnaire.getCodeLists().stream()
                .filter(codeList1 -> "L_DIPLOMES-1-0-0".equals(codeList1.getName()))
                .findAny().orElse(null);
        assertNotNull(codeList);
        assertEquals("diplomes", codeList.getLabel());
        SuggesterConfigurationDTO suggesterConfiguration = codeList.getSuggesterConfiguration();
        assertEquals(2, suggesterConfiguration.getFields().size());
        assertNotNull(suggesterConfiguration.getQueryParser());
    }

}
