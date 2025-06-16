package fr.insee.eno.core.processing.out.steps.lunatic.table;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.PoguesDDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import fr.insee.lunatic.model.flat.RosterForLoop;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DynamicTableQuestionProcessingTest {

    @Test
    void integrationTestFromDDI() throws DDIParsingException {
        // Given
        EnoQuestionnaire enoQuestionnaire = DDIToEno.fromInputStream(
                DynamicTableQuestionProcessingTest.class.getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-dynamic-table.xml"))
                .transform(EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI));
        //
        Optional<DynamicTableQuestion> enoDynamicTable = enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(DynamicTableQuestion.class::isInstance)
                .map(DynamicTableQuestion.class::cast)
                .findAny();
        assertTrue(enoDynamicTable.isPresent());

        // When
        RosterForLoop lunaticDynamicTable = new RosterForLoop();
        DynamicTableQuestionProcessing.process(lunaticDynamicTable, enoDynamicTable.get());

        // Then
        // Min and max
        assertEquals("1", lunaticDynamicTable.getLines().getMin().getValue());
        assertEquals("5", lunaticDynamicTable.getLines().getMax().getValue());
        assertEquals(LabelTypeEnum.VTL, lunaticDynamicTable.getLines().getMin().getType());
        assertEquals(LabelTypeEnum.VTL, lunaticDynamicTable.getLines().getMax().getType());
        // Header
        assertEquals(3, lunaticDynamicTable.getHeader().size());
        // Cells
        assertEquals(3, lunaticDynamicTable.getComponents().size());
        assertEquals(ComponentTypeEnum.INPUT, lunaticDynamicTable.getComponents().get(0).getComponentType());
        assertEquals(ComponentTypeEnum.INPUT_NUMBER, lunaticDynamicTable.getComponents().get(1).getComponentType());
        assertEquals(ComponentTypeEnum.RADIO, lunaticDynamicTable.getComponents().get(2).getComponentType());
        // Iterations
        assertEquals(LabelTypeEnum.VTL, lunaticDynamicTable.getIterations().getType());
        assertEquals("count(DYNAMIC_TABLE1)", lunaticDynamicTable.getIterations().getValue());

    }

    @Test
    void integrationTestFromPoguesDDI_sizeExpression() throws ParsingException {
        // Given
        ClassLoader classLoader = DynamicTableQuestionProcessingTest.class.getClassLoader();
        EnoQuestionnaire enoQuestionnaire = PoguesDDIToEno
                .fromInputStreams(
                        classLoader.getResourceAsStream("integration/pogues/pogues-dynamic-table-size.json"),
                        classLoader.getResourceAsStream("integration/ddi/ddi-dynamic-table-size.xml"))
                .transform(EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI));
        //
        List<DynamicTableQuestion> enoDynamicTable = enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(DynamicTableQuestion.class::isInstance)
                .map(DynamicTableQuestion.class::cast)
                .toList();
        assertEquals(4, enoDynamicTable.size());

        // When
        RosterForLoop lunaticDynamicTable1 = new RosterForLoop();
        RosterForLoop lunaticDynamicTable2 = new RosterForLoop();
        RosterForLoop lunaticDynamicTable3 = new RosterForLoop();
        RosterForLoop lunaticDynamicTable4 = new RosterForLoop();
        DynamicTableQuestionProcessing.process(lunaticDynamicTable1, enoDynamicTable.get(0));
        DynamicTableQuestionProcessing.process(lunaticDynamicTable2, enoDynamicTable.get(1));
        DynamicTableQuestionProcessing.process(lunaticDynamicTable3, enoDynamicTable.get(2));
        DynamicTableQuestionProcessing.process(lunaticDynamicTable4, enoDynamicTable.get(3));

        // Then
        assertEquals("5", lunaticDynamicTable1.getLines().getMin().getValue());
        assertEquals("5", lunaticDynamicTable1.getLines().getMax().getValue());
        assertEquals("1", lunaticDynamicTable2.getLines().getMin().getValue());
        assertEquals("5", lunaticDynamicTable2.getLines().getMax().getValue());
        assertEquals("cast(HOW_MANY, integer)", lunaticDynamicTable3.getLines().getMin().getValue());
        assertEquals("cast(HOW_MANY, integer)", lunaticDynamicTable3.getLines().getMax().getValue());
        assertEquals("1", lunaticDynamicTable4.getLines().getMin().getValue());
        assertEquals("cast(HOW_MANY, integer)", lunaticDynamicTable4.getLines().getMax().getValue());
    }

}
