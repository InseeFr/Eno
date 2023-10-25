package fr.insee.eno.core.processing.out.steps.lunatic.table;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.ComplexMultipleChoiceQuestion;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.lunatic.model.flat.Table;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ComplexMultipleChoiceQuestionProcessingTest {

    private static List<ComplexMultipleChoiceQuestion> enoMCQList;
    private Table lunaticTable;

    @BeforeAll
    static void complexMCQ_integrationTestFromDDI() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                ComplexMultipleChoiceQuestionProcessingTest.class.getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-mcq.xml"),
                EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));
        //
        enoMCQList = enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(ComplexMultipleChoiceQuestion.class::isInstance)
                .map(ComplexMultipleChoiceQuestion.class::cast)
                .toList();
    }

    @BeforeEach
    void newLunaticTable() {
        lunaticTable = new Table();
    }

    @Test
    void simpleHeaderCodeList() {
        assertDoesNotThrow(() -> {
            ComplexMultipleChoiceQuestionProcessing.process(lunaticTable, enoMCQList.get(0));
            ComplexMultipleChoiceQuestionProcessing.process(lunaticTable, enoMCQList.get(1));
        });
    }

    @Test
    @Disabled("being debugged")
    void nestedHeaderCodeList() {
        assertDoesNotThrow(() -> {
            ComplexMultipleChoiceQuestionProcessing.process(lunaticTable, enoMCQList.get(2));
            ComplexMultipleChoiceQuestionProcessing.process(lunaticTable, enoMCQList.get(3));
        });
    }
    
}
