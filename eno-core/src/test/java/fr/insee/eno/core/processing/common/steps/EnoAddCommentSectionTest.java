package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EnoAddCommentSectionTest {

    @Test
    void integrationTest() throws DDIParsingException {
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI);
        enoParameters.setCommentSection(true);
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-simple.xml"),
                enoParameters);
        //
        Optional<Sequence> commentSequence = enoQuestionnaire.getSequences().stream()
                .filter(sequence -> EnoAddCommentSection.COMMENT_SEQUENCE_ID.equals(sequence.getId()))
                .findAny();
        assertTrue(commentSequence.isPresent());
    }

    @Test
    void integrationTest_lunaticOutput() throws DDIParsingException {
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        enoParameters.setCommentSection(true);
        //
        Questionnaire lunaticQuestionnaire = DDIToLunatic.transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-simple.xml"),
                enoParameters);
        //
        Optional<fr.insee.lunatic.model.flat.Sequence> commentSequence = lunaticQuestionnaire.getComponents().stream()
                .filter(fr.insee.lunatic.model.flat.Sequence.class::isInstance)
                .map(fr.insee.lunatic.model.flat.Sequence.class::cast)
                .filter(component -> EnoAddCommentSection.COMMENT_SEQUENCE_ID.equals(component.getId()))
                .findAny();
        assertTrue(commentSequence.isPresent());
    }

}
