package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.Textarea;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnoAddIdentificationSectionTest {

    @Test
    void integrationTest() throws DDIParsingException {
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.BUSINESS, EnoParameters.ModeParameter.CAWI);
        enoParameters.setIdentificationQuestion(true);
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.fromInputStream(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-simple.xml"))
                .transform(enoParameters);
        //
        Sequence identificationSequence = enoQuestionnaire.getSequences().getFirst();
        assertEquals(EnoAddIdentificationSection.IDENTIFICATION_SEQUENCE_ID, identificationSequence.getId());
    }

    @Test
    void integrationTest_lunaticOutput() throws DDIParsingException {
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.BUSINESS, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        enoParameters.setIdentificationQuestion(true);
        //
        Questionnaire lunaticQuestionnaire = DDIToLunatic.fromInputStream(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-simple.xml"))
                .transform(enoParameters);
        //
        ComponentType identificationSequence = lunaticQuestionnaire.getComponents().getFirst();
        assertInstanceOf(fr.insee.lunatic.model.flat.Sequence.class, identificationSequence);
        assertEquals(ComponentTypeEnum.SEQUENCE, identificationSequence.getComponentType());
        assertEquals(EnoAddIdentificationSection.IDENTIFICATION_SEQUENCE_ID, identificationSequence.getId());
        //
        Textarea identificationQuestion = lunaticQuestionnaire.getComponents().stream()
                .filter(Textarea.class::isInstance).map(Textarea.class::cast)
                .filter(component -> "COMMENT-UE-QUESTION".equals(component.getId())).findAny().orElse(null);
        assertNotNull(identificationQuestion);
        assertEquals("COMMENT_UE", identificationQuestion.getResponse().getName());
        //
        assertTrue(lunaticQuestionnaire.getVariables().stream()
                .anyMatch(variable -> variable.getName().equals("COMMENT_UE")));
    }

}
