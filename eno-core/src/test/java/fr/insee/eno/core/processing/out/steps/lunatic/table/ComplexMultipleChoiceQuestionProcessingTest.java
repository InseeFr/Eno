package fr.insee.eno.core.processing.out.steps.lunatic.table;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.ComplexMultipleChoiceQuestion;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ComplexMultipleChoiceQuestionProcessingTest {

    private static List<Table> lunaticTableList;

    @BeforeAll
    static void complexMCQ_integrationTestFromDDI() throws DDIParsingException {
        // Given
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                ComplexMultipleChoiceQuestionProcessingTest.class.getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-mcq.xml"),
                EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));
        //
        List<ComplexMultipleChoiceQuestion> enoMCQList = enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(ComplexMultipleChoiceQuestion.class::isInstance)
                .map(ComplexMultipleChoiceQuestion.class::cast)
                .toList();
        lunaticTableList = new ArrayList<>();

        // When
        enoMCQList.forEach(enoMCQ -> {
            Table lunaticTable = new Table();
            ComplexMultipleChoiceQuestionProcessing.process(lunaticTable, enoMCQ);
            lunaticTableList.add(lunaticTable);
        });

        // Then
        // -> tests
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1}) // First two MCQ are identical except first has radio format, second dropdown
    void simpleLeftColumnCodeList(int i) {
        //
        Table lunaticTable = lunaticTableList.get(i);
        // (deeper testing in tests of the more complex case)
        assertTrue(lunaticTable.getHeader().isEmpty());
        assertEquals(4, lunaticTable.getBodyLines().size());
        lunaticTable.getBodyLines().forEach(bodyLine -> {
            assertEquals(2, bodyLine.getBodyCells().size());
            assertNotNull(bodyLine.getBodyCells().get(0).getValue());
            assertNotNull(bodyLine.getBodyCells().get(0).getLabel().getValue());
            assertEquals(i == 0 ? ComponentTypeEnum.RADIO : ComponentTypeEnum.DROPDOWN,
                    bodyLine.getBodyCells().get(1).getComponentType());
            assertEquals(5, bodyLine.getBodyCells().get(1).getOptions().size());
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3}) // same idea as other test
    void nestedLeftColumnCodeList_numberOfBodyLines(int i) {
        //
        Table lunaticTable = lunaticTableList.get(i);
        //
        assertEquals(7, lunaticTable.getBodyLines().size());

    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void nestedLeftColumnCodeList_headerShouldBeEmpty(int i) {
        //
        Table lunaticTable = lunaticTableList.get(i);
        //
        assertTrue(lunaticTable.getHeader().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void nestedLeftColumnCodeList_codeListValues(int i) {
        //
        Table lunaticTable = lunaticTableList.get(i);
        // Code list labels are present
        lunaticTable.getBodyLines().forEach(bodyLine ->
                assertNotNull(bodyLine.getBodyCells().get(0).getLabel().getValue()));
        // Closer look (first line)
        assertEquals("c1", lunaticTable.getBodyLines().get(0).getBodyCells().get(0).getValue());
        assertEquals("\"Code 1\"",
                lunaticTable.getBodyLines().get(0).getBodyCells().get(0).getLabel().getValue());
        assertEquals(LabelTypeEnum.VTL_MD,
                lunaticTable.getBodyLines().get(0).getBodyCells().get(0).getLabel().getTypeEnum());
        // Only look at the modality value for the others
        assertEquals("c2", lunaticTable.getBodyLines().get(1).getBodyCells().get(0).getValue());
        assertEquals("c21", lunaticTable.getBodyLines().get(1).getBodyCells().get(1).getValue());
        assertEquals("c22", lunaticTable.getBodyLines().get(2).getBodyCells().get(0).getValue());
        assertEquals("c3", lunaticTable.getBodyLines().get(3).getBodyCells().get(0).getValue());
        assertEquals("c31", lunaticTable.getBodyLines().get(3).getBodyCells().get(1).getValue());
        assertEquals("c311", lunaticTable.getBodyLines().get(3).getBodyCells().get(2).getValue());
        assertEquals("c312", lunaticTable.getBodyLines().get(4).getBodyCells().get(0).getValue());
        assertEquals("c313", lunaticTable.getBodyLines().get(5).getBodyCells().get(0).getValue());
        assertEquals("c32", lunaticTable.getBodyLines().get(6).getBodyCells().get(0).getValue());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void nestedLeftColumnCodeList_colspanValues(int i) {
        //
        Table lunaticTable = lunaticTableList.get(i);
        //
        assertEquals(BigInteger.valueOf(3), lunaticTable.getBodyLines().get(0).getBodyCells().get(0).getColspan());
        assertNull(lunaticTable.getBodyLines().get(1).getBodyCells().get(0).getColspan());
        assertEquals(BigInteger.valueOf(2), lunaticTable.getBodyLines().get(1).getBodyCells().get(1).getColspan());
        assertEquals(BigInteger.valueOf(2), lunaticTable.getBodyLines().get(2).getBodyCells().get(0).getColspan());
        assertNull(lunaticTable.getBodyLines().get(3).getBodyCells().get(0).getColspan());
        assertNull(lunaticTable.getBodyLines().get(3).getBodyCells().get(1).getColspan());
        assertNull(lunaticTable.getBodyLines().get(3).getBodyCells().get(2).getColspan());
        assertNull(lunaticTable.getBodyLines().get(4).getBodyCells().get(0).getColspan());
        assertNull(lunaticTable.getBodyLines().get(5).getBodyCells().get(0).getColspan());
        assertEquals(BigInteger.valueOf(2), lunaticTable.getBodyLines().get(6).getBodyCells().get(0).getColspan());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void nestedLeftColumnCodeList_rowspanValues(int i) {
        //
        Table lunaticTable = lunaticTableList.get(i);
        //
        assertNull(lunaticTable.getBodyLines().get(0).getBodyCells().get(0).getRowspan());
        assertEquals(BigInteger.valueOf(2), lunaticTable.getBodyLines().get(1).getBodyCells().get(0).getRowspan());
        assertNull(lunaticTable.getBodyLines().get(1).getBodyCells().get(1).getRowspan());
        assertNull(lunaticTable.getBodyLines().get(2).getBodyCells().get(0).getRowspan());
        assertEquals(BigInteger.valueOf(4), lunaticTable.getBodyLines().get(3).getBodyCells().get(0).getRowspan());
        assertEquals(BigInteger.valueOf(3), lunaticTable.getBodyLines().get(3).getBodyCells().get(1).getRowspan());
        assertNull(lunaticTable.getBodyLines().get(3).getBodyCells().get(2).getRowspan());
        assertNull(lunaticTable.getBodyLines().get(4).getBodyCells().get(0).getRowspan());
        assertNull(lunaticTable.getBodyLines().get(5).getBodyCells().get(0).getRowspan());
        assertNull(lunaticTable.getBodyLines().get(6).getBodyCells().get(0).getRowspan());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void nestedLeftColumnCodeList_cellOptions(int i) {
        //
        Table lunaticTable = lunaticTableList.get(i);
        //
        List<String> expectedResponseCellValues = List.of("answerA", "answerB", "answerC", "answerD", "answerE");
        lunaticTable.getBodyLines().forEach(bodyLine -> {
            int responseCellIndex = bodyLine.getBodyCells().size() - 1;
            BodyCell responseCell = bodyLine.getBodyCells().get(responseCellIndex);
            assertEquals(5, responseCell.getOptions().size());
            assertThat(responseCell.getOptions().stream().map(Options::getValue).toList())
                    .containsExactlyElementsOf(expectedResponseCellValues);
        });
    }
    
}
