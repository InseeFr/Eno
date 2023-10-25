package fr.insee.eno.core.processing.out.steps.lunatic.table;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.ComplexMultipleChoiceQuestion;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Table;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @ParameterizedTest
    @ValueSource(ints = {0, 1}) // First two MCQ are identical except first has radio format, second dropdown
    void simpleHeaderCodeList(int i) {
        //
        ComplexMultipleChoiceQuestionProcessing.process(lunaticTable, enoMCQList.get(i));
        //
        assertTrue(lunaticTable.getHeader().isEmpty());
        assertEquals(4, lunaticTable.getBodyLines().size());
        lunaticTable.getBodyLines().forEach(bodyLine -> {
            assertEquals(2, bodyLine.getBodyCells().size());
            assertNotNull(bodyLine.getBodyCells().get(0).getValue());
            assertNotNull(bodyLine.getBodyCells().get(0).getLabel());
            assertEquals(i == 0 ? ComponentTypeEnum.RADIO : ComponentTypeEnum.DROPDOWN,
                    bodyLine.getBodyCells().get(1).getComponentType());
            assertEquals(5, bodyLine.getBodyCells().get(1).getOptions().size());
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3}) // same idea as other test
    @Disabled("being debugged")
    void nestedHeaderCodeList(int i) {
        //
        ComplexMultipleChoiceQuestionProcessing.process(lunaticTable, enoMCQList.get(i));
        //
        assertTrue(lunaticTable.getHeader().isEmpty());
        assertEquals(7, lunaticTable.getBodyLines().size());
    }
    
}
