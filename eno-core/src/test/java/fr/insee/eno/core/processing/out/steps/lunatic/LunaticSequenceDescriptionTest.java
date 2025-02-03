package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.Question;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.Sequence;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LunaticSequenceDescriptionTest {

    @Test
    void testFromDDI() throws DDIParsingException {
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        enoParameters.getLunaticParameters().setDsfr(true);
        Questionnaire lunaticQuestionnaire = DDIToLunatic.fromInputStream(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-declarations.xml"))
                .transform(enoParameters);
        //
        Sequence sequence = (Sequence) lunaticQuestionnaire.getComponents().getFirst();
        assertTrue(sequence.getDeclarations().isEmpty());
        assertEquals("\"Static label of type 'Aide' in Pogues\"",
                sequence.getDescription().getValue());
        //
        Question input = (Question) lunaticQuestionnaire.getComponents().get(1);
        assertFalse(input.getDeclarations().isEmpty());
        assertNull(input.getDescription());
    }

}
