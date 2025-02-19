package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.PoguesToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Duration;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DurationQuestionTest {

    private EnoQuestionnaire enoQuestionnaire;

    @Test
    void mapFromDDI() throws DDIParsingException {
        enoQuestionnaire = DDIToEno.fromInputStream(this.getClass().getClassLoader().getResourceAsStream(
                "integration/ddi/ddi-durations-2.xml"))
                .transform(EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI));
        assertNotNull(enoQuestionnaire);
    }

    @Test
    void mapFromPogues() throws PoguesDeserializationException {
        enoQuestionnaire = PoguesToEno.fromInputStream(this.getClass().getClassLoader().getResourceAsStream(
                "integration/pogues/pogues-durations-2.json"))
                .transform(EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI));
        assertNotNull(enoQuestionnaire);
    }

    @AfterEach
    void testLunaticMapping() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        //
        List<Duration> durationComponents = lunaticQuestionnaire.getComponents().stream()
                .filter(Duration.class::isInstance).map(Duration.class::cast).toList();
        assertEquals(2, durationComponents.size());
        durationComponents.forEach(duration ->
                assertEquals(ComponentTypeEnum.DURATION, duration.getComponentType()));
        assertEquals("PnYnM", durationComponents.get(0).getFormat().value());
        assertEquals("PTnHnM", durationComponents.get(1).getFormat().value());
    }

}
