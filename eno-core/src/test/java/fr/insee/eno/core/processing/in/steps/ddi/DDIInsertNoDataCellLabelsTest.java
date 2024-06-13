package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.EnoTable;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DDIInsertNoDataCellLabelsTest {

    @Test
    void integrationTest() throws DDIParsingException {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(
                DDIDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-no-data-cell.xml")),
                enoQuestionnaire);
        // When
        new DDIInsertNoDataCellLabels().apply(enoQuestionnaire);
        // Then
        // (all multiple response questions in this questionnaire are tables)
        List<EnoTable> tableQuestions = enoQuestionnaire.getMultipleResponseQuestions().stream()
                .map(EnoTable.class::cast).toList();
        //
        tableQuestions.forEach(tableQuestion -> assertTrue(tableQuestion.getCellLabels().isEmpty()));
        assertEquals("20", tableQuestions.get(0).getNoDataCells().get(0).getCellLabel().getValue());
        assertNotNull(tableQuestions.get(0).getNoDataCells().get(1).getCellLabel().getValue());
        assertEquals("\"Foo\"", tableQuestions.get(1).getNoDataCells().get(0).getCellLabel().getValue());
        assertNotNull(tableQuestions.get(1).getNoDataCells().get(1).getCellLabel().getValue());
        assertEquals("\"Bar\"", tableQuestions.get(2).getNoDataCells().get(0).getCellLabel().getValue());
        assertNotNull(tableQuestions.get(2).getNoDataCells().get(1).getCellLabel().getValue());
    }

}
