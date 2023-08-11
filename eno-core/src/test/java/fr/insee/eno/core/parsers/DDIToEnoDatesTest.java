package fr.insee.eno.core.parsers;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.DateQuestion;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.EnoIndex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DDIToEnoDatesTest {

    private EnoIndex index;

    @BeforeAll
    void init() throws DDIParsingException {
        InputStream ddiStream = this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-dates.xml");
        EnoQuestionnaire questionnaire = DDIToEno.transform(ddiStream,
                EnoParameters.of(Context.DEFAULT, Format.LUNATIC, ModeParameter.PROCESS));
        index = questionnaire.getIndex();
    }

    @Test
    void parseDayDateWithAllParams() {
        EnoObject dateObject = index.get("jfjfckyw");
        assertTrue(dateObject instanceof DateQuestion);

        DateQuestion dateQuestion = (DateQuestion) dateObject;
        assertEquals("YYYY-MM-DD", dateQuestion.getFormat());
        assertEquals("2000-01-01", dateQuestion.getMinValue());
        assertEquals("2020-03-31", dateQuestion.getMaxValue());
    }

    @Test
    void parseMonthDateWithAllParams() {
        EnoObject dateObject = index.get("k6c1guqb");
        assertTrue(dateObject instanceof DateQuestion);

        DateQuestion dateQuestion = (DateQuestion) dateObject;
        assertEquals("YYYY-MM", dateQuestion.getFormat());
        assertEquals("2000-01", dateQuestion.getMinValue());
        assertEquals("2020-03", dateQuestion.getMaxValue());
    }

    @Test
    void parseYearDateWithAllParams() {
        EnoObject dateObject = index.get("k6c1che6");
        assertTrue(dateObject instanceof DateQuestion);

        DateQuestion dateQuestion = (DateQuestion) dateObject;
        assertEquals("YYYY", dateQuestion.getFormat());
        assertEquals("2000", dateQuestion.getMinValue());
        assertEquals("2020", dateQuestion.getMaxValue());
    }

    @Test
    void parseYearDateWithNoMinMax() {
        EnoObject dateObject = index.get("ljwv6q99");
        assertTrue(dateObject instanceof DateQuestion);

        // should retrieve data in reference date time object
        DateQuestion dateQuestion = (DateQuestion) dateObject;
        assertEquals("YYYY-MM-DD", dateQuestion.getFormat());
        assertEquals("1900-01-01", dateQuestion.getMinValue());
        assertEquals("format-date(current-date(),'[Y0001]-[M01]-[D01]')", dateQuestion.getMaxValue());
    }
}
