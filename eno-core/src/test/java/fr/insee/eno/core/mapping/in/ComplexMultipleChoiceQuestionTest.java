package fr.insee.eno.core.mapping.in;

import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.InMapper;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.ComplexMultipleChoiceQuestion;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComplexMultipleChoiceQuestionTest {

    private static Stream<Arguments> mapQuestionnaireWithComplexMCQ() throws ParsingException {
        return Stream.of(
                Arguments.of(Format.DDI, new DDIMapper(), DDIDeserializer.deserialize(
                        ComplexMultipleChoiceQuestionTest.class.getClassLoader().getResourceAsStream(
                                "integration/ddi/ddi-mcq.xml")).getDDIInstance()),
                Arguments.of(Format.POGUES, new PoguesMapper(), PoguesDeserializer.deserialize(
                        ComplexMultipleChoiceQuestionTest.class.getClassLoader().getResourceAsStream(
                                "integration/pogues/pogues-mcq.json")))
        );
    }
    @ParameterizedTest
    @MethodSource
    void mapQuestionnaireWithComplexMCQ(Format inFormat, InMapper inMapper, Object inputObject) {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        inMapper.mapInputObject(inputObject, enoQuestionnaire);

        //
        List<ComplexMultipleChoiceQuestion> complexMCQList = enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(ComplexMultipleChoiceQuestion.class::isInstance)
                .map(ComplexMultipleChoiceQuestion.class::cast)
                .toList();
        assertEquals(4, complexMCQList.size());
        //
        ComplexMultipleChoiceQuestion mcq1 = complexMCQList.get(0);
        ComplexMultipleChoiceQuestion mcq2 = complexMCQList.get(1);
        ComplexMultipleChoiceQuestion mcq3 = complexMCQList.get(2);
        ComplexMultipleChoiceQuestion mcq4 = complexMCQList.get(3);
        //
        assertEquals("MCQ_CL_RADIO", mcq1.getName());
        assertEquals("MCQ_CL_DROPDOWN", mcq2.getName());
        assertEquals("MCQ_TABLE_RADIO", mcq3.getName());
        assertEquals("MCQ_TABLE_DROPDOWN", mcq4.getName());
        //
        assertEquals("lo5upwdy", mcq1.getLeftColumnCodeListReference());
        assertEquals("lo5upwdy", mcq2.getLeftColumnCodeListReference());
        assertEquals("lo5uxn2k", mcq3.getLeftColumnCodeListReference());
        assertEquals("lo5uxn2k", mcq4.getLeftColumnCodeListReference());
        //
        assertEquals(4, mcq1.getVariableNames().size());
        assertEquals(4, mcq2.getVariableNames().size());
        assertEquals(7, mcq3.getVariableNames().size());
        assertEquals(7, mcq4.getVariableNames().size());
        //
        if (inFormat == Format.DDI) {
            assertEquals(4, mcq1.getBindings().size());
            assertEquals(4, mcq2.getBindings().size());
            assertEquals(7, mcq3.getBindings().size());
            assertEquals(7, mcq4.getBindings().size());
        }
        //
        assertEquals(4, mcq1.getResponseCells().size());
        assertEquals(4, mcq2.getResponseCells().size());
        assertEquals(7, mcq3.getResponseCells().size());
        assertEquals(7, mcq4.getResponseCells().size());
    }

}
