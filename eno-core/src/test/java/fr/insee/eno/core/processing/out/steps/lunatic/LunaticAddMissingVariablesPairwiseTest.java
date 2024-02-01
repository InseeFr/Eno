package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.model.lunatic.MissingBlock;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import java.util.List;
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
        pairwiseLinks.setComponentType(ComponentTypeEnum.PAIRWISE_LINKS);
        Dropdown dropdown = new Dropdown();
        dropdown.setComponentType(ComponentTypeEnum.DROPDOWN);
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
        Optional<IVariableType> pairwiseMissingVariable = lunaticQuestionnaire.getVariables().stream()
                .filter(variable -> "FOO_LINKS_MISSING".equals(variable.getName()))
                .findAny();
        assertTrue(pairwiseMissingVariable.isPresent());
        //
        List<MissingBlock> missingEntries = lunaticQuestionnaire.getMissingBlock().getAny().stream()
                .map(MissingBlock.class::cast).toList();
        assertEquals(2, missingEntries.size());
        //
        Optional<MissingBlock> pairwiseMissingBlock = missingEntries.stream()
                .filter(missingBlock -> "FOO_LINKS_MISSING".equals(missingBlock.getMissingName()))
                .findAny();
        assertTrue(pairwiseMissingBlock.isPresent());
        assertEquals(1, pairwiseMissingBlock.get().getNames().size());
        assertEquals("FOO_LINKS", pairwiseMissingBlock.get().getNames().get(0));
        //
        Optional<MissingBlock> pairwiseReverseMissingBlock = missingEntries.stream()
                .filter(missingBlock -> "FOO_LINKS".equals(missingBlock.getMissingName()))
                .findAny();
        assertTrue(pairwiseReverseMissingBlock.isPresent());
        assertEquals(1, pairwiseReverseMissingBlock.get().getNames().size());
        assertEquals("FOO_LINKS_MISSING", pairwiseReverseMissingBlock.get().getNames().get(0));
    }

}
