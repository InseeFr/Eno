package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.PoguesToEno;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.DateQuestion;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LunaticDateDescriptionTest {

    @Test
    void minAndMaxYearMonthDay() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        Datepicker datepicker = new Datepicker();
        datepicker.setDateFormat(DateQuestion.YEAR_MONTH_DAY_FORMAT);
        datepicker.setMin("2021-12-02");
        datepicker.setMax("2025-06-04");
        lunaticQuestionnaire.getComponents().add(datepicker);
        //
        EnoParameters enoParameters = EnoParameters.emptyValues();
        enoParameters.setLanguage(EnoParameters.Language.FR);
        //
        LunaticDateDescription processing = new LunaticDateDescription(enoParameters);
        processing.apply(lunaticQuestionnaire);
        //
        LabelType description = lunaticQuestionnaire.getComponents().getFirst().getDescription();
        assertEquals("Entre 02/12/2021 et 04/06/2025.", description.getValue());
        assertEquals(LabelTypeEnum.TXT, description.getType());
    }

    @Test
    void minAndNoMaxYearMonthDay() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        Datepicker datepicker = new Datepicker();
        datepicker.setDateFormat(DateQuestion.YEAR_MONTH_DAY_FORMAT);
        datepicker.setMin("2021-12-02");
        lunaticQuestionnaire.getComponents().add(datepicker);
        //
        EnoParameters enoParameters = EnoParameters.emptyValues();
        enoParameters.setLanguage(EnoParameters.Language.FR);
        //
        LunaticDateDescription processing = new LunaticDateDescription(enoParameters);
        processing.apply(lunaticQuestionnaire);
        //
        LabelType description = lunaticQuestionnaire.getComponents().getFirst().getDescription();
        assertEquals("Après 02/12/2021.", description.getValue());
        assertEquals(LabelTypeEnum.TXT, description.getType());
    }

    @Test
    void maxAndNoMinYearMonthDay() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        Datepicker datepicker = new Datepicker();
        datepicker.setDateFormat(DateQuestion.YEAR_MONTH_DAY_FORMAT);
        datepicker.setMax("2025-06-04");
        lunaticQuestionnaire.getComponents().add(datepicker);
        //
        EnoParameters enoParameters = EnoParameters.emptyValues();
        enoParameters.setLanguage(EnoParameters.Language.FR);
        //
        LunaticDateDescription processing = new LunaticDateDescription(enoParameters);
        processing.apply(lunaticQuestionnaire);
        //
        LabelType description = lunaticQuestionnaire.getComponents().getFirst().getDescription();
        assertEquals("Avant 04/06/2025.", description.getValue());
        assertEquals(LabelTypeEnum.TXT, description.getType());
    }

    @Test
    void noMinAndNoMaxYearMonthDay() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        Datepicker datepicker = new Datepicker();
        datepicker.setDateFormat(DateQuestion.YEAR_MONTH_DAY_FORMAT);
        lunaticQuestionnaire.getComponents().add(datepicker);
        //
        EnoParameters enoParameters = EnoParameters.emptyValues();
        enoParameters.setLanguage(EnoParameters.Language.FR);
        //
        LunaticDateDescription processing = new LunaticDateDescription(enoParameters);
        processing.apply(lunaticQuestionnaire);
        //
        LabelType description = lunaticQuestionnaire.getComponents().getFirst().getDescription();
        assertNull(description.getValue());
        assertEquals(LabelTypeEnum.TXT, description.getType());
    }

    @Test
    void minAndMaxYearMonth() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        Datepicker datepicker = new Datepicker();
        datepicker.setDateFormat(DateQuestion.YEAR_MONTH_FORMAT);
        datepicker.setMin("2021-12");
        datepicker.setMax("2025-06");
        lunaticQuestionnaire.getComponents().add(datepicker);
        //
        EnoParameters enoParameters = EnoParameters.emptyValues();
        enoParameters.setLanguage(EnoParameters.Language.FR);
        //
        LunaticDateDescription processing = new LunaticDateDescription(enoParameters);
        processing.apply(lunaticQuestionnaire);
        //
        LabelType description = lunaticQuestionnaire.getComponents().getFirst().getDescription();
        assertEquals("Entre 12/2021 et 06/2025.", description.getValue());
        assertEquals(LabelTypeEnum.TXT, description.getType());
    }

    @Test
    void minAndMaxYear() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        Datepicker datepicker = new Datepicker();
        datepicker.setDateFormat(DateQuestion.YEAR_FORMAT);
        datepicker.setMin("2021");
        datepicker.setMax("2025");
        lunaticQuestionnaire.getComponents().add(datepicker);
        //
        EnoParameters enoParameters = EnoParameters.emptyValues();
        enoParameters.setLanguage(EnoParameters.Language.FR);
        //
        LunaticDateDescription processing = new LunaticDateDescription(enoParameters);
        processing.apply(lunaticQuestionnaire);
        //
        LabelType description = lunaticQuestionnaire.getComponents().getFirst().getDescription();
        assertEquals("Entre 2021 et 2025.", description.getValue());
        assertEquals(LabelTypeEnum.TXT, description.getType());
    }

    @Test
    void integrationTest() throws ParsingException {
        // Given
        EnoQuestionnaire enoQuestionnaire = PoguesToEno.fromInputStream(
                        this.getClass().getClassLoader().getResourceAsStream("integration/pogues/pogues-dates-2.json"))
                .transform(EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        new LunaticMapper().mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticTableProcessing(enoQuestionnaire).apply(lunaticQuestionnaire);

        // When
        EnoParameters enoParameters = EnoParameters.emptyValues();
        enoParameters.setLanguage(EnoParameters.Language.FR);
        new LunaticDateDescription(enoParameters).apply(lunaticQuestionnaire);

        // Then
        Datepicker date0 = (Datepicker) lunaticQuestionnaire.getComponents().get(3);
        Datepicker date1 = (Datepicker) lunaticQuestionnaire.getComponents().get(4);
        Datepicker date2 = (Datepicker) lunaticQuestionnaire.getComponents().get(5);
        assertEquals("Entre 01/01/1950 et 01/01/2050.",
                date0.getDescription().getValue());
        assertEquals("Entre 01/1950 et 01/2050.",
                date1.getDescription().getValue());
        assertEquals("Entre 1950 et 2050.",
                date2.getDescription().getValue());
        assertEquals(LabelTypeEnum.TXT, date0.getDescription().getType());
        assertEquals(LabelTypeEnum.TXT, date1.getDescription().getType());
        assertEquals(LabelTypeEnum.TXT, date2.getDescription().getType());
    }

}