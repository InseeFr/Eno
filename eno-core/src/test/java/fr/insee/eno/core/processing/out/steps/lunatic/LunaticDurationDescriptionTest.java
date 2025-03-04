package fr.insee.eno.core.processing.out.steps.lunatic;

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


}