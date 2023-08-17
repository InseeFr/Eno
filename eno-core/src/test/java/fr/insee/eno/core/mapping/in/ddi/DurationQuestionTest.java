package fr.insee.eno.core.mapping.in.ddi;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.DurationQuestion;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.EnoIndex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DurationQuestionTest {

    private EnoIndex index;

    @BeforeAll
    void init() throws DDIParsingException {
        InputStream ddiStream = this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-durations.xml");
        EnoQuestionnaire questionnaire = DDIToEno.transform(ddiStream,
                EnoParameters.of(Context.DEFAULT, ModeParameter.PROCESS));
        index = questionnaire.getIndex();
    }

    @Test
    void parseHourMinuteDurationWithMaxParam() {
        EnoObject dateObject = index.get("k6c0ysf3");
        assertTrue(dateObject instanceof DurationQuestion);

        DurationQuestion question = (DurationQuestion) dateObject;
        assertEquals("PTnHnM", question.getFormat());
        assertNull(question.getMinValue());
        assertEquals("PT200H59M", question.getMaxValue());
    }

    @Test
    void parseYearMonthDurationWithMinParam() {
        EnoObject dateObject = index.get("k6c10exe");
        assertTrue(dateObject instanceof DurationQuestion);

        DurationQuestion question = (DurationQuestion) dateObject;
        assertEquals("PnYnM", question.getFormat());
        assertEquals("P1Y1M", question.getMinValue());
        assertNull(question.getMaxValue());
    }

    @Test
    void parseYearDateWithAllParams() {
        EnoObject dateObject = index.get("ljwweb92");
        assertTrue(dateObject instanceof DurationQuestion);

        DurationQuestion question = (DurationQuestion) dateObject;
        assertEquals("HH:CH", question.getFormat());
        assertEquals("00:00", question.getMinValue());
        assertEquals("99:99", question.getMaxValue());
    }

    @Test
    void parseHourMinuteDurationWithAllParams() {
        EnoObject dateObject = index.get("ljwwfchy");

        assertTrue(dateObject instanceof DurationQuestion);

        // should retrieve data in reference date time object
        DurationQuestion question = (DurationQuestion) dateObject;
        assertEquals("PTnHnM", question.getFormat());
        assertEquals("PT1H1M", question.getMinValue());
        assertEquals("PT2H2M", question.getMaxValue());
    }
}
