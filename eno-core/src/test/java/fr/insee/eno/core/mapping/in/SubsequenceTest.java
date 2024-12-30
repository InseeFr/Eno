package fr.insee.eno.core.mapping.in;

import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.InMapper;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubsequenceTest {

    private static Stream<Arguments> integrationTest() throws ParsingException {
        return Stream.of(
                Arguments.of(
                        new PoguesMapper(),
                        PoguesDeserializer.deserialize(SubsequenceTest.class.getClassLoader().getResourceAsStream(
                                "integration/pogues/pogues-subsequences.json"))),
                Arguments.of(
                        new DDIMapper(),
                        DDIDeserializer.deserialize(SubsequenceTest.class.getClassLoader().getResourceAsStream(
                                "integration/ddi/ddi-subsequences.xml")).getDDIInstance())
        );
    }
    @ParameterizedTest
    @MethodSource
    void integrationTest(InMapper inMapper, Object inputObject) {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        inMapper.mapInputObject(inputObject, enoQuestionnaire);
        //
        assertEquals(3, enoQuestionnaire.getSubsequences().size());
        Subsequence subsequence1 = enoQuestionnaire.getSubsequences().get(0);
        Subsequence subsequence21 = enoQuestionnaire.getSubsequences().get(1);
        Subsequence subsequence22 = enoQuestionnaire.getSubsequences().get(2);
        assertEquals("SS1", subsequence1.getName());
        assertEquals("SS21", subsequence21.getName());
        assertEquals("SS22", subsequence22.getName());
        assertEquals("\"Subsequence 1\"", subsequence1.getLabel().getValue());
        assertEquals("\"Subsequence 2.1\"", subsequence21.getLabel().getValue());
        assertEquals("\"Subsequence 2.2\"", subsequence22.getLabel().getValue());
    }

}
