package fr.insee.eno.core.mapping.in.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.DateQuestion;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DateQuestionTest {

    private EnoIndex index;

    @BeforeAll
    void init() throws DDIParsingException {
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new DDIMapper().mapDDI(
                DDIDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-dates.xml")).getDDIInstance(),
                enoQuestionnaire);
        index = enoQuestionnaire.getIndex();
    }

    @Test
    void parseDayDateWithAllParams() {
        DateQuestion dateQuestion = assertInstanceOf(DateQuestion.class, index.get("jfjfckyw"));
        assertEquals("YYYY-MM-DD", dateQuestion.getFormat());
        assertEquals("2000-01-01", dateQuestion.getMinValue());
        assertEquals("2020-03-31", dateQuestion.getMaxValue());
    }

    @Test
    void parseMonthDateWithAllParams() {
        DateQuestion dateQuestion = assertInstanceOf(DateQuestion.class, index.get("k6c1guqb"));
        assertEquals("YYYY-MM", dateQuestion.getFormat());
        assertEquals("2000-01", dateQuestion.getMinValue());
        assertEquals("2020-03", dateQuestion.getMaxValue());
    }

    @Test
    void parseYearDateWithAllParams() {
        DateQuestion dateQuestion = assertInstanceOf(DateQuestion.class, index.get("k6c1che6"));
        assertEquals("YYYY", dateQuestion.getFormat());
        assertEquals("2000", dateQuestion.getMinValue());
        assertEquals("2020", dateQuestion.getMaxValue());
    }

    @Test
    void parseYearDateWithNoMinMax() {
        // should retrieve data in reference date time object
        DateQuestion dateQuestion = assertInstanceOf(DateQuestion.class, index.get("ljwv6q99"));
        assertEquals("YYYY-MM-DD", dateQuestion.getFormat());
        assertEquals("1900-01-01", dateQuestion.getMinValue());
        assertEquals("format-date(current-date(),'[Y0001]-[M01]-[D01]')", dateQuestion.getMaxValue());
    }

}
