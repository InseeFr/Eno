package fr.insee.eno.core.processing.out.steps.lunatic.table;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.PoguesToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.ComplexMultipleChoiceQuestion;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ComplexMultipleChoiceQuestionProcessingTest {

    private List<Table> lunaticTableList;

    @BeforeAll
    void complexMCQ_integrationTestFromDDI() throws ParsingException {
        // Given
        EnoQuestionnaire enoQuestionnaire = mapQuestionnaire();
        List<ComplexMultipleChoiceQuestion> enoMCQList =  enoQuestionnaire.getMultipleResponseQuestions().stream()
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

    abstract EnoQuestionnaire mapQuestionnaire() throws ParsingException;

    static class DDITest extends ComplexMultipleChoiceQuestionProcessingTest {
        @Override
        EnoQuestionnaire mapQuestionnaire() throws DDIParsingException {
            return new DDIToEno().transform(
                    ComplexMultipleChoiceQuestionProcessingTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-mcq.xml"),
                    EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));
        }
    }

    static class PoguesTest extends ComplexMultipleChoiceQuestionProcessingTest {
        @Override
        EnoQuestionnaire mapQuestionnaire() throws PoguesDeserializationException {
            return new PoguesToEno().transform(
                    ComplexMultipleChoiceQuestionProcessingTest.class.getClassLoader().getResourceAsStream(
                            "integration/pogues/pogues-mcq.json"),
                    EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void simpleLeftColumnCodeList(int i) {
        //
        Table lunaticTable = lunaticTableList.get(i);
        // (deeper testing in tests of the more complex case)
        assertTrue(lunaticTable.getHeader().isEmpty());
        assertEquals(4, lunaticTable.getBodyLines().size());
        lunaticTable.getBodyLines().forEach(bodyLine -> {
            assertEquals(2, bodyLine.getBodyCells().size());
            assertNotNull(bodyLine.getBodyCells().getFirst().getValue());
            assertNotNull(bodyLine.getBodyCells().getFirst().getLabel().getValue());
            assertEquals(5, bodyLine.getBodyCells().get(1).getOptions().size());
        });
    }

    @Test
    void simpleLeftColumn_radio() {
        //
        Table lunaticTable = lunaticTableList.getFirst();
        //
        lunaticTable.getBodyLines().forEach(bodyLine -> {
            assertEquals(2, bodyLine.getBodyCells().size());
            assertEquals(ComponentTypeEnum.RADIO, bodyLine.getBodyCells().get(1).getComponentType());
            assertEquals(Orientation.HORIZONTAL, bodyLine.getBodyCells().get(1).getOrientation());
            assertEquals(5, bodyLine.getBodyCells().get(1).getOptions().size());
        });
    }

    @Test
    void simpleLeftColumn_dropdown() {
        //
        Table lunaticTable = lunaticTableList.get(1);
        //
        lunaticTable.getBodyLines().forEach(bodyLine ->
                assertEquals(ComponentTypeEnum.DROPDOWN, bodyLine.getBodyCells().get(1).getComponentType()));
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
                assertNotNull(bodyLine.getBodyCells().getFirst().getLabel().getValue()));
        // Closer look (first line)
        assertEquals("c1", lunaticTable.getBodyLines().getFirst().getBodyCells().getFirst().getValue());
        assertEquals("\"Code 1\"",
                lunaticTable.getBodyLines().getFirst().getBodyCells().getFirst().getLabel().getValue());
        assertEquals(LabelTypeEnum.VTL_MD,
                lunaticTable.getBodyLines().getFirst().getBodyCells().getFirst().getLabel().getType());
        // Only look at the modality value for the others
        assertEquals("c2", lunaticTable.getBodyLines().get(1).getBodyCells().getFirst().getValue());
        assertEquals("c21", lunaticTable.getBodyLines().get(1).getBodyCells().get(1).getValue());
        assertEquals("c22", lunaticTable.getBodyLines().get(2).getBodyCells().getFirst().getValue());
        assertEquals("c3", lunaticTable.getBodyLines().get(3).getBodyCells().getFirst().getValue());
        assertEquals("c31", lunaticTable.getBodyLines().get(3).getBodyCells().get(1).getValue());
        assertEquals("c311", lunaticTable.getBodyLines().get(3).getBodyCells().get(2).getValue());
        assertEquals("c312", lunaticTable.getBodyLines().get(4).getBodyCells().getFirst().getValue());
        assertEquals("c313", lunaticTable.getBodyLines().get(5).getBodyCells().getFirst().getValue());
        assertEquals("c32", lunaticTable.getBodyLines().get(6).getBodyCells().getFirst().getValue());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void nestedLeftColumnCodeList_colspanValues(int i) {
        //
        Table lunaticTable = lunaticTableList.get(i);
        //
        assertEquals(BigInteger.valueOf(3), lunaticTable.getBodyLines().getFirst().getBodyCells().getFirst().getColspan());
        assertNull(lunaticTable.getBodyLines().get(1).getBodyCells().getFirst().getColspan());
        assertEquals(BigInteger.valueOf(2), lunaticTable.getBodyLines().get(1).getBodyCells().get(1).getColspan());
        assertEquals(BigInteger.valueOf(2), lunaticTable.getBodyLines().get(2).getBodyCells().getFirst().getColspan());
        assertNull(lunaticTable.getBodyLines().get(3).getBodyCells().getFirst().getColspan());
        assertNull(lunaticTable.getBodyLines().get(3).getBodyCells().get(1).getColspan());
        assertNull(lunaticTable.getBodyLines().get(3).getBodyCells().get(2).getColspan());
        assertNull(lunaticTable.getBodyLines().get(4).getBodyCells().getFirst().getColspan());
        assertNull(lunaticTable.getBodyLines().get(5).getBodyCells().getFirst().getColspan());
        assertEquals(BigInteger.valueOf(2), lunaticTable.getBodyLines().get(6).getBodyCells().getFirst().getColspan());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void nestedLeftColumnCodeList_rowspanValues(int i) {
        //
        Table lunaticTable = lunaticTableList.get(i);
        //
        assertNull(lunaticTable.getBodyLines().getFirst().getBodyCells().getFirst().getRowspan());
        assertEquals(BigInteger.valueOf(2), lunaticTable.getBodyLines().get(1).getBodyCells().getFirst().getRowspan());
        assertNull(lunaticTable.getBodyLines().get(1).getBodyCells().get(1).getRowspan());
        assertNull(lunaticTable.getBodyLines().get(2).getBodyCells().getFirst().getRowspan());
        assertEquals(BigInteger.valueOf(4), lunaticTable.getBodyLines().get(3).getBodyCells().getFirst().getRowspan());
        assertEquals(BigInteger.valueOf(3), lunaticTable.getBodyLines().get(3).getBodyCells().get(1).getRowspan());
        assertNull(lunaticTable.getBodyLines().get(3).getBodyCells().get(2).getRowspan());
        assertNull(lunaticTable.getBodyLines().get(4).getBodyCells().getFirst().getRowspan());
        assertNull(lunaticTable.getBodyLines().get(5).getBodyCells().getFirst().getRowspan());
        assertNull(lunaticTable.getBodyLines().get(6).getBodyCells().getFirst().getRowspan());
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
            assertThat(responseCell.getOptions().stream().map(Option::getValue).toList())
                    .containsExactlyElementsOf(expectedResponseCellValues);
        });
    }

}
