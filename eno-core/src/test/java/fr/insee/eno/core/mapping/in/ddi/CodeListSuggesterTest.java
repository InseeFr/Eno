package fr.insee.eno.core.mapping.in.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.serialize.DDIDeserializer;
import instance33.DDIInstanceDocument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Code lists configurations are stored in code list objects in DDI.
 * This class tests the mapping of these between DDI and the Eno model. */
class CodeListSuggesterTest {

    private static EnoQuestionnaire enoQuestionnaire;

    @BeforeAll
    static void mapDDI() throws DDIParsingException {
        // Given a DDI with suggester code lists
        DDIInstanceDocument ddiInstanceDocument = DDIDeserializer.deserialize(
                CodeListSuggesterTest.class.getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-suggester.xml"));
        // When mapping it to Eno
        DDIMapper ddiMapper = new DDIMapper();
        enoQuestionnaire = new EnoQuestionnaire();
        ddiMapper.mapDDI(ddiInstanceDocument, enoQuestionnaire);
        // Then -> tests
    }

    @Test
    void renameMe() {
        //
        Optional<CodeList> searchedCodeList = enoQuestionnaire.getCodeLists().stream()
                .filter(codeList -> "L_ACTIVITES-1-0-0".equals(codeList.getName())).findAny();
        assertTrue(searchedCodeList.isPresent());
        CodeList codeList = searchedCodeList.get();
        assertEquals("activité", codeList.getLabel());
    }

}
