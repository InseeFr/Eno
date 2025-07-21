package fr.insee.eno.core.processing.in.steps.pogues;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.model.sequence.StructureItemReference.StructureItemType;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PoguesSortLoopsTest {

    @Test
    @DisplayName("Loop on sequence")
    void test01() {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();

        Sequence sequence1 = new Sequence();
        sequence1.setId("sequence-1");
        sequence1.getSequenceStructure().add(
                StructureItemReference.builder().id("question-1").type(StructureItemType.QUESTION).build());
        Sequence sequence2 = new Sequence();
        sequence2.setId("sequence-2");
        sequence1.getSequenceStructure().add(
                StructureItemReference.builder().id("question-2").type(StructureItemType.QUESTION).build());
        // Sequences are in the right order
        enoQuestionnaire.getSequences().add(sequence1);
        enoQuestionnaire.getSequences().add(sequence2);

        Loop loop1 = new StandaloneLoop();
        loop1.setPoguesStartReference("sequence-1");
        Loop loop2 = new LinkedLoop();
        loop2.setPoguesStartReference("sequence-2");
        // Loops are not in the right order
        enoQuestionnaire.getLoops().add(loop2);
        enoQuestionnaire.getLoops().add(loop1);

        // When
        new PoguesSortLoops().apply(enoQuestionnaire);

        // Then
        assertEquals(loop1, enoQuestionnaire.getLoops().get(0));
        assertEquals(loop2, enoQuestionnaire.getLoops().get(1));
    }

    @Test
    void test02() {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();

        Sequence sequence1 = new Sequence();
        sequence1.setId("sequence-1");
        sequence1.getSequenceStructure().add(
                StructureItemReference.builder().id("subsequence-1").type(StructureItemType.SUBSEQUENCE).build());
        sequence1.getSequenceStructure().add(
                StructureItemReference.builder().id("subsequence-2").type(StructureItemType.SUBSEQUENCE).build());
        enoQuestionnaire.getSequences().add(sequence1);

        Loop loop1 = new StandaloneLoop();
        loop1.setPoguesStartReference("subsequence-1");
        Loop loop2 = new LinkedLoop();
        loop2.setPoguesStartReference("subsequence-2");
        // Loops are not in the right order
        enoQuestionnaire.getLoops().add(loop2);
        enoQuestionnaire.getLoops().add(loop1);

        // When
        new PoguesSortLoops().apply(enoQuestionnaire);

        // Then
        assertEquals(loop1, enoQuestionnaire.getLoops().get(0));
        assertEquals(loop2, enoQuestionnaire.getLoops().get(1));
    }

    @Test
    void nestedSequences() {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();

        Sequence sequence1 = new Sequence();
        sequence1.setId("sequence-1");
        sequence1.getSequenceStructure().add(
                StructureItemReference.builder().id("sequence-2").type(StructureItemType.SEQUENCE).build());
        enoQuestionnaire.getSequences().add(sequence1);

        Loop loop1 = new StandaloneLoop();
        loop1.setPoguesStartReference("sequence-1");
        Loop loop2 = new LinkedLoop();
        loop2.setPoguesStartReference("sequence-2");
        enoQuestionnaire.getLoops().add(loop2);
        enoQuestionnaire.getLoops().add(loop1);

        // When + Then
        var processing = new PoguesSortLoops();
        assertThrows(MappingException.class, () -> processing.apply(enoQuestionnaire));
    }

    @Test
    void integrationTest() throws PoguesDeserializationException {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new PoguesMapper().mapPoguesQuestionnaire(
                PoguesDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "functional/pogues/pogues-l20g2ba7.json")), // this questionnaire have two loops in the wrong order
                enoQuestionnaire);

        // At this point, loops are in the wrong order:
        assertEquals("BOUCLEINDIV", enoQuestionnaire.getLoops().get(0).getName());
        assertEquals("BOUCLETHL", enoQuestionnaire.getLoops().get(1).getName());

        // When
        new PoguesSortLoops().apply(enoQuestionnaire);
        // Then
        assertEquals("BOUCLETHL", enoQuestionnaire.getLoops().get(0).getName());
        assertEquals("BOUCLEINDIV", enoQuestionnaire.getLoops().get(1).getName());
    }

}
