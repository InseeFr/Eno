package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.model.sequence.RoundaboutSequence;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RoundaboutSequenceTest {

    private final ClassLoader classLoader = this.getClass().getClassLoader();

    @Test
    void integrationTest_roundaboutOnSequence() throws PoguesDeserializationException {
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new PoguesMapper().mapPoguesQuestionnaire(
                PoguesDeserializer.deserialize(classLoader.getResourceAsStream(
                        "integration/pogues/pogues-roundabout.json")),
                enoQuestionnaire);

        assertEquals(1, enoQuestionnaire.getRoundaboutSequences().size());
        RoundaboutSequence roundaboutSequence = enoQuestionnaire.getRoundaboutSequences().getFirst();
        assertEquals("lxsy3t24", roundaboutSequence.getId());
        assertNotNull(roundaboutSequence.getInnerLoop());
        LinkedLoop linkedLoop = roundaboutSequence.getInnerLoop();
        assertEquals("ROUNDABOUT_LOOP", linkedLoop.getName());
        assertEquals("lxsxyttl", linkedLoop.getPoguesStartReference());
        assertEquals("lxsxyttl", linkedLoop.getPoguesEndReference());
    }

    @Test
    void integrationTest_roundaboutOnSubsequence() throws PoguesDeserializationException {
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new PoguesMapper().mapPoguesQuestionnaire(
                PoguesDeserializer.deserialize(classLoader.getResourceAsStream(
                        "integration/pogues/pogues-roundabout-subsequence.json")),
                enoQuestionnaire);

        assertEquals(1, enoQuestionnaire.getRoundaboutSequences().size());
        RoundaboutSequence roundaboutSequence = enoQuestionnaire.getRoundaboutSequences().getFirst();
        assertEquals("m2c2ozay", roundaboutSequence.getId());
        assertNotNull(roundaboutSequence.getInnerLoop());
        LinkedLoop linkedLoop = roundaboutSequence.getInnerLoop();
        assertEquals("LOOP_SS2", linkedLoop.getName());
        assertEquals("m2c27pvz", linkedLoop.getPoguesStartReference());
        assertEquals("m2c27pvz", linkedLoop.getPoguesEndReference());
    }
}
