package fr.insee.eno.core.processing.out.steps.lunatic.table;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import fr.insee.lunatic.model.flat.RosterForLoop;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DynamicTableQuestionProcessingTest {

    private static RosterForLoop lunaticDynamicTable;

    @BeforeAll
    static void complexMCQ_integrationTestFromDDI() throws DDIParsingException {
        // Given
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                DynamicTableQuestionProcessingTest.class.getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-dynamic-table.xml"),
                EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));
        //
        Optional<DynamicTableQuestion> enoDynamicTable = enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(DynamicTableQuestion.class::isInstance)
                .map(DynamicTableQuestion.class::cast)
                .findAny();
        assertTrue(enoDynamicTable.isPresent());

        // When
        lunaticDynamicTable = new RosterForLoop();
        DynamicTableQuestionProcessing.process(lunaticDynamicTable, enoDynamicTable.get());

        // Then
        // -> tests
    }

    @Test
    void minAndMaxIterations() {
        assertEquals("1", lunaticDynamicTable.getLines().getMin().getValue());
        assertEquals("5", lunaticDynamicTable.getLines().getMax().getValue());
        assertEquals(LabelTypeEnum.VTL, lunaticDynamicTable.getLines().getMin().getType());
        assertEquals(LabelTypeEnum.VTL, lunaticDynamicTable.getLines().getMax().getType());
    }

    @Test
    void dynamicTableHeader() {
        assertEquals(3, lunaticDynamicTable.getHeader().size());
    }

    @Test
    void dynamicTableCells() {
        assertEquals(3, lunaticDynamicTable.getComponents().size());
        assertEquals(ComponentTypeEnum.INPUT, lunaticDynamicTable.getComponents().get(0).getComponentType());
        assertEquals(ComponentTypeEnum.INPUT_NUMBER, lunaticDynamicTable.getComponents().get(1).getComponentType());
        assertEquals(ComponentTypeEnum.RADIO, lunaticDynamicTable.getComponents().get(2).getComponentType());
    }

}
