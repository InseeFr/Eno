package fr.insee.eno.core.mapping.in.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.sequence.RoundaboutSequence;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoundaboutSequenceTest {

    @Test
    void mapDDIWithRoundabout() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(
                DDIDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-roundabout.xml")),
                enoQuestionnaire);
        //
        assertEquals(1, enoQuestionnaire.getRoundaboutSequences().size());
        RoundaboutSequence roundaboutSequence = enoQuestionnaire.getRoundaboutSequences().getFirst();
        assertEquals("lxsy3t24", roundaboutSequence.getId());
        assertEquals("lxsy3t24-ROUNDABOUT_LOOP", roundaboutSequence.getLoopReference());
        assertTrue(roundaboutSequence.getLocked());
    }

}
