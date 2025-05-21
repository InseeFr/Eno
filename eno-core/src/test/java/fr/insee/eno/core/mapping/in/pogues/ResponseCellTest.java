package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.question.table.ResponseCell;
import fr.insee.eno.core.model.question.table.TextCell;
import fr.insee.pogues.model.ResponseType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResponseCellTest {

    @Test
    void unitTest() {
        ResponseType poguesResponse = new ResponseType();
        poguesResponse.setConditionReadOnly("<some expression>");
        ResponseCell enoResponseCell = new TextCell();

        new PoguesMapper().mapInputObject(poguesResponse, enoResponseCell);

        assertEquals("<some expression>", enoResponseCell.getComponentReadOnly().getValue());
    }
}
