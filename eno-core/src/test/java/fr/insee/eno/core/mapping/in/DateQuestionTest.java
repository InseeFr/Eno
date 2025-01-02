package fr.insee.eno.core.mapping.in;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.DateQuestion;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class DateQuestionTest {

    EnoQuestionnaire enoQuestionnaire;

    @BeforeAll
    void init() throws ParsingException {
        enoQuestionnaire = mapQuestionnaire();
    }

    abstract EnoQuestionnaire mapQuestionnaire() throws ParsingException;

    static class DateQuestionDDITest extends DateQuestionTest {
        @Override
        EnoQuestionnaire mapQuestionnaire() throws DDIParsingException {
            EnoQuestionnaire enoQuestionnaire1 = new EnoQuestionnaire();
            new DDIMapper().mapDDI(
                    DDIDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-dates-2.xml")),
                    enoQuestionnaire1);
            return enoQuestionnaire1;
        }
    }

    static class DateQuestionPoguesTest extends DateQuestionTest {
        @Override
        EnoQuestionnaire mapQuestionnaire() throws PoguesDeserializationException {
            EnoQuestionnaire enoQuestionnaire1 = new EnoQuestionnaire();
            new PoguesMapper().mapPoguesQuestionnaire(
                    PoguesDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                            "integration/pogues/pogues-dates-2.json")),
                    enoQuestionnaire1);
            return enoQuestionnaire1;
        }
    }

    @Test
    void yearMonthDay_minAndMax() {
        DateQuestion dateQuestion = assertInstanceOf(DateQuestion.class,
                enoQuestionnaire.getSingleResponseQuestions().get(10));
        assertEquals("1950-01-01", dateQuestion.getMinValue());
        assertEquals("2050-01-01", dateQuestion.getMaxValue());
        assertEquals("YYYY-MM-DD", dateQuestion.getFormat());
    }
    @Test
    void yearMonth_minAndMax() {
        DateQuestion dateQuestion = assertInstanceOf(DateQuestion.class,
                enoQuestionnaire.getSingleResponseQuestions().get(11));
        assertEquals("1950-01", dateQuestion.getMinValue());
        assertEquals("2050-01", dateQuestion.getMaxValue());
        assertEquals("YYYY-MM", dateQuestion.getFormat());
    }
    @Test
    void year_minAndMax() {
        DateQuestion dateQuestion = assertInstanceOf(DateQuestion.class,
                enoQuestionnaire.getSingleResponseQuestions().get(12));
        assertEquals("1950", dateQuestion.getMinValue());
        assertEquals("2050", dateQuestion.getMaxValue());
        assertEquals("YYYY", dateQuestion.getFormat());
    }

}
