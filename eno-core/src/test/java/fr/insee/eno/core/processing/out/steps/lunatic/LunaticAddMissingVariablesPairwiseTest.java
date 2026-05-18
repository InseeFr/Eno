package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.CollectedVariableType;
import fr.insee.lunatic.model.flat.variable.CollectedVariableValues;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests on the Lunatic "missing variables" processing focused on the pairwise links component case.
 * */
class LunaticAddMissingVariablesPairwiseTest {

    @Test
    void pairwiseMissingVariable_unitTest() {
        // Given
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        PairwiseLinks pairwiseLinks = new PairwiseLinks();
        Dropdown dropdown = new Dropdown();
        ResponseType responseType = new ResponseType();
        responseType.setName("FOO_LINKS");
        dropdown.setResponse(responseType);
        pairwiseLinks.getComponents().add(dropdown);
        lunaticQuestionnaire.getComponents().add(pairwiseLinks);

        // When (no need of the Eno catalog in this case)
        new LunaticAddMissingVariables(null, true).apply(lunaticQuestionnaire);

        // Then
        assertNotNull(dropdown.getMissingResponse());
        assertEquals("FOO_LINKS_MISSING", dropdown.getMissingResponse().getName());
        //
        Optional<CollectedVariableType> pairwiseMissingVariable = lunaticQuestionnaire.getVariables().stream()
                .filter(CollectedVariableType.class::isInstance)
                .map(CollectedVariableType.class::cast)
                .filter(variable -> "FOO_LINKS_MISSING".equals(variable.getName()))
                .findAny();
        assertTrue(pairwiseMissingVariable.isPresent());
        assertInstanceOf(CollectedVariableValues.DoubleArray.class, pairwiseMissingVariable.get().getValues());
        //
        MissingType missingType = lunaticQuestionnaire.getMissingBlock();
        assertEquals(2, missingType.countMissingEntries());
        //
        MissingEntry pairwiseMissingEntry = missingType.getMissingEntry("FOO_LINKS_MISSING");
        assertNotNull(pairwiseMissingEntry);
        assertEquals(1, pairwiseMissingEntry.getCorrespondingVariables().size());
        assertEquals("FOO_LINKS", pairwiseMissingEntry.getCorrespondingVariables().getFirst());
        //
        MissingEntry pairwiseReverseMissingEntry = missingType.getMissingEntry("FOO_LINKS");
        assertNotNull(pairwiseReverseMissingEntry);
        assertEquals(1, pairwiseReverseMissingEntry.getCorrespondingVariables().size());
        assertEquals("FOO_LINKS_MISSING", pairwiseReverseMissingEntry.getCorrespondingVariables().getFirst());
    }

}
