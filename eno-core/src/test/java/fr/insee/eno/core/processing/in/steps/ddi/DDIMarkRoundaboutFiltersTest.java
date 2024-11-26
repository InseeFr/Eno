package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DDIMarkRoundaboutFiltersTest {

    @Test
    void mapDDIWithRoundabout() throws DDIParsingException {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(
                DDIDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-roundabout.xml")),
                enoQuestionnaire);
        // When
        new DDIMarkRoundaboutFilters().apply(enoQuestionnaire);
        // Then
        // this questionnaire has no filters except the "occurrence" filter added for the roundabout
        assertEquals(1, enoQuestionnaire.getFilters().size());
        Filter enoFilter = enoQuestionnaire.getFilters().getFirst();
        assertTrue(enoFilter.isRoundaboutFilter());
        assertTrue(enoFilter.getExpression().getValue().startsWith("not"));
    }

}
