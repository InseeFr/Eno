package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnoAddIdentificationSectionTest {

    @Test
    void integrationTest() throws DDIParsingException {
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.BUSINESS, EnoParameters.ModeParameter.CAWI);
        enoParameters.setIdentificationQuestion(true);
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-simple.xml"),
                enoParameters);
        //
        Sequence identificationSequence = enoQuestionnaire.getSequences().get(0);
        assertEquals(EnoAddIdentificationSection.IDENTIFICATION_SEQUENCE_ID, identificationSequence.getId());
    }

    @Test
    void integrationTest_lunaticOutput() throws DDIParsingException {
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.BUSINESS, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        enoParameters.setIdentificationQuestion(true);
        //
        Questionnaire lunaticQuestionnaire = DDIToLunatic.transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-simple.xml"),
                enoParameters);
        //
        ComponentType identificationSequence = lunaticQuestionnaire.getComponents().get(0);
        assertTrue(identificationSequence instanceof fr.insee.lunatic.model.flat.Sequence);
        assertEquals(ComponentTypeEnum.SEQUENCE, identificationSequence.getComponentType());
        assertEquals(EnoAddIdentificationSection.IDENTIFICATION_SEQUENCE_ID, identificationSequence.getId());
    }

}
