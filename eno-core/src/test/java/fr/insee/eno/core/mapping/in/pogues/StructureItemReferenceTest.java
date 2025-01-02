package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.StructureItemReference.StructureItemType;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StructureItemReferenceTest {

    @Test
    void integrationTest_sequence() throws PoguesDeserializationException {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new PoguesMapper().mapInputObject(
                PoguesDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/pogues/pogues-simple.json")),
                enoQuestionnaire);
        //
        Sequence sequence = enoQuestionnaire.getSequences().getFirst();
        assertEquals(1, sequence.getSequenceStructure().size());
        assertEquals(StructureItemType.QUESTION, sequence.getSequenceStructure().getFirst().getType());
    }

    @Test
    void integrationTest_subsequences() throws PoguesDeserializationException {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new PoguesMapper().mapInputObject(
                PoguesDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/pogues/pogues-subsequences.json")),
                enoQuestionnaire);

        //
        Sequence sequence1 = enoQuestionnaire.getSequences().get(0);
        Sequence sequence2 = enoQuestionnaire.getSequences().get(1);
        Subsequence subsequence1 = enoQuestionnaire.getSubsequences().get(0);
        Subsequence subsequence21 = enoQuestionnaire.getSubsequences().get(1);
        Subsequence subsequence22 = enoQuestionnaire.getSubsequences().get(2);

        assertEquals(1, sequence1.getSequenceStructure().size());
        assertEquals(StructureItemType.SUBSEQUENCE, sequence1.getSequenceStructure().getFirst().getType());
        assertEquals(1, subsequence1.getSequenceStructure().size());
        assertEquals(StructureItemType.QUESTION, subsequence1.getSequenceStructure().getFirst().getType());
        assertEquals(2, sequence2.getSequenceStructure().size());
        assertEquals(StructureItemType.SUBSEQUENCE, sequence2.getSequenceStructure().get(0).getType());
        assertEquals(StructureItemType.SUBSEQUENCE, sequence2.getSequenceStructure().get(1).getType());
        assertEquals(1, subsequence21.getSequenceStructure().size());
        assertEquals(StructureItemType.QUESTION, subsequence21.getSequenceStructure().getFirst().getType());
        assertEquals(1, subsequence22.getSequenceStructure().size());
        assertEquals(StructureItemType.QUESTION, subsequence22.getSequenceStructure().getFirst().getType());
    }

}
