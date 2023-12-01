package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnoCleanTooltipsTest {

    @Test
    void integrationTestFromDDI() throws DDIParsingException {
        // Given + When
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        enoParameters.setSequenceNumbering(false);
        enoParameters.setQuestionNumberingMode(EnoParameters.QuestionNumberingMode.NONE);
        enoParameters.setArrowCharInQuestions(false);
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-tooltips.xml"),
                enoParameters);

        // Then
        // NB: pay attention to the difference between simple quote and apostrophe
        assertEquals("\"Sequence [label](. 'There is a tooltip on the label.')\"",
                enoQuestionnaire.getSequences().get(0).getLabel().getValue());
        assertEquals("\"Declaration with a tooltip [here](. 'Tooltip’s content.').\"",
                enoQuestionnaire.getSequences().get(0).getInstructions().get(0).getLabel().getValue());
        //
        String expectedQuestionLabel = "\"Question label with a [tooltip](. " +
                "'The tooltip, also known as infotip or hint, is a common graphical user interface element in which, " +
                "when hovering over a screen element or component, a text box displays information about that element." +
                "').\"";
        assertEquals(expectedQuestionLabel, enoQuestionnaire.getSingleResponseQuestions().get(0).getLabel().getValue());
        assertEquals("\"Before question [label](. 'Some text').\"",
                enoQuestionnaire.getSingleResponseQuestions().get(0).getDeclarations().get(0).getLabel().getValue());
        assertEquals("\"After question [label](. 'Some text').\"",
                enoQuestionnaire.getSingleResponseQuestions().get(0).getInstructions().get(0).getLabel().getValue());
        assertEquals("\"Error message with a [tooltip](. 'Some text')\"",
                enoQuestionnaire.getSingleResponseQuestions().get(0).getControls().get(0).getMessage().getValue());
        //
        assertEquals("\"[Code 1](. 'Tooltip text of code 1')\"",
                enoQuestionnaire.getCodeLists().get(0).getCodeItems().get(0).getLabel().getValue());
        assertEquals("\"[Code 2](. 'Tooltip text of code 2')\"",
                enoQuestionnaire.getCodeLists().get(0).getCodeItems().get(1).getLabel().getValue());
        //
        SimpleMultipleChoiceQuestion simpleMCQ = (SimpleMultipleChoiceQuestion)
                enoQuestionnaire.getMultipleResponseQuestions().get(0);
        assertEquals("\"[Code 1](. 'Tooltip text of code 1')\"",
                simpleMCQ.getCodeResponses().get(0).getLabel().getValue());
        assertEquals("\"[Code 2](. 'Tooltip text of code 2')\"",
                simpleMCQ.getCodeResponses().get(1).getLabel().getValue());
    }

}
