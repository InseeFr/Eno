package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.PoguesToEno;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LunaticDurationDescriptionTest {

    @Test
    void minAndMaxYearMonth() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        Duration duration = new Duration();
        duration.setFormat(DurationFormat.YEARS_MONTHS);
        duration.setMin("P1Y3M");
        duration.setMax("P3Y4M");
        lunaticQuestionnaire.getComponents().add(duration);
        //
        LunaticDurationDescription processing = new LunaticDurationDescription();
        processing.apply(lunaticQuestionnaire);
        //
        LabelType description = lunaticQuestionnaire.getComponents().getFirst().getDescription();
        assertEquals("De 1 an et 3 mois à 3 ans et 4 mois", description.getValue());
        assertEquals(LabelTypeEnum.TXT, description.getType());
    }

    @Test
    void minAndMaxHourMinute() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        Duration duration = new Duration();
        duration.setFormat(DurationFormat.HOURS_MINUTES);
        duration.setMin("PT0H3M");
        duration.setMax("PT5H4M");
        lunaticQuestionnaire.getComponents().add(duration);
        //
        LunaticDurationDescription processing = new LunaticDurationDescription();
        processing.apply(lunaticQuestionnaire);
        //
        LabelType description = lunaticQuestionnaire.getComponents().getFirst().getDescription();
        assertEquals("De 3 minutes à 5 heures et 4 minutes", description.getValue());
        assertEquals(LabelTypeEnum.TXT, description.getType());
    }

    @Test
    void withoutMinYearMonth() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        Duration duration = new Duration();
        duration.setFormat(DurationFormat.YEARS_MONTHS);
        duration.setMin("P0Y0M");
        duration.setMax("P3Y4M");
        lunaticQuestionnaire.getComponents().add(duration);
        //
        LunaticDurationDescription processing = new LunaticDurationDescription();
        processing.apply(lunaticQuestionnaire);
        //
        LabelType description = lunaticQuestionnaire.getComponents().getFirst().getDescription();
        assertEquals("Jusqu'à 3 ans et 4 mois", description.getValue());
        assertEquals(LabelTypeEnum.TXT, description.getType());
    }

    @Test
    void withoutMinHourMinute() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        Duration duration = new Duration();
        duration.setFormat(DurationFormat.HOURS_MINUTES);
        duration.setMin("PT0H0M");
        duration.setMax("PT1H1M");
        lunaticQuestionnaire.getComponents().add(duration);
        //
        LunaticDurationDescription processing = new LunaticDurationDescription();
        processing.apply(lunaticQuestionnaire);
        //
        LabelType description = lunaticQuestionnaire.getComponents().getFirst().getDescription();
        assertEquals("Jusqu'à 1 heure et 1 minute", description.getValue());
        assertEquals(LabelTypeEnum.TXT, description.getType());
    }

    @Test
    void integrationTest() throws ParsingException {
        // Given
        EnoQuestionnaire enoQuestionnaire = PoguesToEno.fromInputStream(
                        this.getClass().getClassLoader().getResourceAsStream("integration/pogues/pogues-durations-2.json"))
                .transform(EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        new LunaticMapper().mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticTableProcessing(enoQuestionnaire).apply(lunaticQuestionnaire);

        // When
        new LunaticDurationDescription().apply(lunaticQuestionnaire);

        // Then
        Duration duration0 = (Duration) lunaticQuestionnaire.getComponents().get(1);
        Duration duration1 = (Duration) lunaticQuestionnaire.getComponents().get(2);
        assertEquals("Jusqu'à 2 ans et 6 mois",
                duration0.getDescription().getValue());
        assertEquals("Jusqu'à 1 heure et 30 minutes",
                duration1.getDescription().getValue());
        assertEquals(LabelTypeEnum.TXT, duration0.getDescription().getType());
        assertEquals(LabelTypeEnum.TXT, duration1.getDescription().getType());
    }

}