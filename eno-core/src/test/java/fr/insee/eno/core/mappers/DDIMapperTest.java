package fr.insee.eno.core.mappers;

import datacollection33.SequenceType;
import fr.insee.eno.core.model.EnoQuestionnaire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DDIMapperTest {

    @Test
    void mapIncompatibleTypes_throwsException() {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        SequenceType ddiSequence = SequenceType.Factory.newInstance();
        // When + Then
        DDIMapper ddiMapper = new DDIMapper();
        assertThrows(IllegalArgumentException.class, () ->
                ddiMapper.mapDDIObject(ddiSequence, enoQuestionnaire));
    }

}
