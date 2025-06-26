package fr.insee.eno.core.processing.in.steps.pogues;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PoguesRoundaboutLoopsTest {

    private final ClassLoader classLoader = this.getClass().getClassLoader();

    @Test
    void integrationTest_roundaboutOnSequence() throws PoguesDeserializationException {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new PoguesMapper().mapPoguesQuestionnaire(
                PoguesDeserializer.deserialize(classLoader.getResourceAsStream(
                        "integration/pogues/pogues-roundabout.json")),
                enoQuestionnaire);
        // When
        new PoguesRoundaboutLoops().apply(enoQuestionnaire);
        // Then
        List<Loop> linkedLoops = enoQuestionnaire.getLoops().stream().filter(LinkedLoop.class::isInstance).toList();
        assertEquals(1, linkedLoops.size());
        assertEquals("lxsy3t24-ROUNDABOUT_LOOP", linkedLoops.getFirst().getId());
    }

    @Test
    void integrationTest_roundaboutOnSubsequence() throws PoguesDeserializationException {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new PoguesMapper().mapPoguesQuestionnaire(
                PoguesDeserializer.deserialize(classLoader.getResourceAsStream(
                        "integration/pogues/pogues-roundabout-subsequence.json")),
                enoQuestionnaire);
        // When
        new PoguesRoundaboutLoops().apply(enoQuestionnaire);
        // Then
        List<Loop> linkedLoops = enoQuestionnaire.getLoops().stream().filter(LinkedLoop.class::isInstance).toList();
        assertEquals(1, linkedLoops.size());
        assertEquals("m2c2ozay-ROUNDABOUT_LOOP", linkedLoops.getFirst().getId());
    }

}