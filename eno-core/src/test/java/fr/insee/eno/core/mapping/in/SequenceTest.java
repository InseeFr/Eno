package fr.insee.eno.core.mapping.in;

import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.InMapper;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SequenceTest {

    private static Stream<Arguments> integrationTest1() throws ParsingException {
        return Stream.of(
                Arguments.of(
                        new PoguesMapper(),
                        PoguesDeserializer.deserialize(SequenceTest.class.getClassLoader().getResourceAsStream(
                                "integration/pogues/pogues-simple.json"))),
                Arguments.of(
                        new DDIMapper(),
                        DDIDeserializer.deserialize(SequenceTest.class.getClassLoader().getResourceAsStream(
                                "integration/ddi/ddi-simple.xml")).getDDIInstance())
        );
    }
    @ParameterizedTest
    @MethodSource
    void integrationTest1(InMapper inMapper, Object inputObject) {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        inMapper.mapInputObject(inputObject, enoQuestionnaire);
        //
        assertEquals("S1", enoQuestionnaire.getSequences().getFirst().getName());
        assertEquals("\"Unique sequence\"", enoQuestionnaire.getSequences().getFirst().getLabel().getValue());
    }

    private static Stream<Arguments> integrationTest2() throws ParsingException {
        return Stream.of(
                Arguments.of(
                        new PoguesMapper(),
                        PoguesDeserializer.deserialize(SequenceTest.class.getClassLoader().getResourceAsStream(
                                "integration/pogues/pogues-subsequences.json"))),
                Arguments.of(
                        new DDIMapper(),
                        DDIDeserializer.deserialize(SequenceTest.class.getClassLoader().getResourceAsStream(
                                "integration/ddi/ddi-subsequences.xml")).getDDIInstance())
        );
    }
    @ParameterizedTest
    @MethodSource
    void integrationTest2(InMapper inMapper, Object inputObject) {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        inMapper.mapInputObject(inputObject, enoQuestionnaire);
        //
        assertEquals(2, enoQuestionnaire.getSequences().size());
        Sequence sequence1 = enoQuestionnaire.getSequences().get(0);
        Sequence sequence2 = enoQuestionnaire.getSequences().get(1);
        assertEquals("S1", sequence1.getName());
        assertEquals("S2", sequence2.getName());
        assertEquals("\"Sequence 1\"", sequence1.getLabel().getValue());
        assertEquals("\"Sequence 2\"", sequence2.getLabel().getValue());
    }

}
