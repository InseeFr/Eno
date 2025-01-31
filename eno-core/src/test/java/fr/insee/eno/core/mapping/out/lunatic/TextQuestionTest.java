package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.InToEno;
import fr.insee.eno.core.PoguesDDIToEno;
import fr.insee.eno.core.PoguesToEno;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Input;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class TextQuestionTest {

    @Test
    void shortText_lunaticComponentType() {
        // With the current implementation, this can only be tested starting at the questionnaire level.
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        TextQuestion enoTextQuestion = new TextQuestion();
        enoTextQuestion.setLengthType(TextQuestion.LengthType.SHORT);
        enoQuestionnaire.getSingleResponseQuestions().add(enoTextQuestion);
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoQuestionnaire, lunaticQuestionnaire);
        //
        assertEquals(ComponentTypeEnum.INPUT, lunaticQuestionnaire.getComponents().getFirst().getComponentType());
    }

    @Test
    void largeText_lunaticComponentType() {
        // With the current implementation, this can only be tested starting at the questionnaire level.
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        TextQuestion enoTextQuestion = new TextQuestion();
        enoTextQuestion.setLengthType(TextQuestion.LengthType.LONG);
        enoQuestionnaire.getSingleResponseQuestions().add(enoTextQuestion);
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoQuestionnaire, lunaticQuestionnaire);
        //
        assertEquals(ComponentTypeEnum.TEXTAREA, lunaticQuestionnaire.getComponents().getFirst().getComponentType());
    }

    private static Stream<Arguments> integrationTest() throws ParsingException {
        ClassLoader classLoader = TextQuestionTest.class.getClassLoader();
        DDIInstanceDocument ddiQuestionnaire = DDIDeserializer.deserialize(
                classLoader.getResourceAsStream("integration/ddi/ddi-simple.xml"));
        fr.insee.pogues.model.Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(
                classLoader.getResourceAsStream("integration/pogues/pogues-simple.json"));
        return Stream.of(
                Arguments.of(DDIToEno.fromObject(ddiQuestionnaire)),
                Arguments.of(PoguesDDIToEno.fromObjects(poguesQuestionnaire, ddiQuestionnaire)),
                Arguments.of(PoguesToEno.fromObject(poguesQuestionnaire))
        );
    }
    @ParameterizedTest
    @MethodSource
    void integrationTest(InToEno inToEno) {
        //
        EnoQuestionnaire enoQuestionnaire = inToEno.transform(EnoParameters.of(Context.HOUSEHOLD, ModeParameter.CAWI, Format.LUNATIC));
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        new LunaticMapper().mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        //
        Input lunaticInput = assertInstanceOf(Input.class, lunaticQuestionnaire.getComponents().get(1));
        assertEquals("lmyo3e0y", lunaticInput.getId());
        assertEquals("\"Unique question\"", lunaticInput.getLabel().getValue());
        assertEquals(LabelTypeEnum.VTL_MD, lunaticInput.getLabel().getType());
        assertEquals(BigInteger.valueOf(249), lunaticInput.getMaxLength());
        assertEquals("Q1", lunaticInput.getResponse().getName());
    }

}
