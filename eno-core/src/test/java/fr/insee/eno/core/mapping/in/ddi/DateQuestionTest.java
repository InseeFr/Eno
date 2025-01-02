package fr.insee.eno.core.mapping.in.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.DateQuestion;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DateQuestionTest {

    private EnoQuestionnaire enoQuestionnaire;

    @BeforeAll
    void init() throws DDIParsingException {
        enoQuestionnaire = new EnoQuestionnaire();
        new DDIMapper().mapDDI(
                DDIDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-dates.xml")).getDDIInstance(),
                enoQuestionnaire);
    }

    @Test
    void parseDayDateWithAllParams() {
        DateQuestion dateQuestion = assertInstanceOf(DateQuestion.class,
                enoQuestionnaire.getSingleResponseQuestions().getFirst());
        assertEquals("YYYY-MM-DD", dateQuestion.getFormat());
        assertEquals("2000-01-01", dateQuestion.getMinValue());
        assertEquals("2020-03-31", dateQuestion.getMaxValue());
    }

    @Test
    void parseMonthDateWithAllParams() {
        DateQuestion dateQuestion = assertInstanceOf(DateQuestion.class,
                enoQuestionnaire.getSingleResponseQuestions().get(1));
        assertEquals("YYYY-MM", dateQuestion.getFormat());
        assertEquals("2000-01", dateQuestion.getMinValue());
        assertEquals("2020-03", dateQuestion.getMaxValue());
    }

    @Test
    void parseYearDateWithAllParams() {
        DateQuestion dateQuestion = assertInstanceOf(DateQuestion.class,
                enoQuestionnaire.getSingleResponseQuestions().get(2));
        assertEquals("YYYY", dateQuestion.getFormat());
        assertEquals("2000", dateQuestion.getMinValue());
        assertEquals("2020", dateQuestion.getMaxValue());
    }

    @Test
    void parseYearDateWithNoMinMax() {
        // should retrieve data in reference date time object
        DateQuestion dateQuestion = assertInstanceOf(DateQuestion.class,
                enoQuestionnaire.getSingleResponseQuestions().get(3));
        assertEquals("YYYY-MM-DD", dateQuestion.getFormat());
        assertEquals("1900-01-01", dateQuestion.getMinValue());
        assertEquals("format-date(current-date(),'[Y0001]-[M01]-[D01]')", dateQuestion.getMaxValue());
    }

}
