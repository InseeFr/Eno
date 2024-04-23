package fr.insee.eno.core.converter;

import datacollection33.QuestionGridDocument;
import datacollection33.QuestionGridType;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import org.apache.xmlbeans.XmlException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class DDIQuestionGridConversionTest {

    @Test
    void convertMultipleChoiceQuestion() throws XmlException, IOException {
        //
        QuestionGridType questionGridType = QuestionGridDocument.Factory.parse(
                this.getClass().getClassLoader().getResourceAsStream(
                        "unit/ddi/ddi-multiple-choice-question.xml")).getQuestionGrid();
        //
        assertInstanceOf(SimpleMultipleChoiceQuestion.class,
                DDIQuestionGridConversion.instantiateFrom(questionGridType));
    }

    @Test
    void convertMultipleChoiceQuestionWithDetailResponses() throws XmlException, IOException {
        //
        QuestionGridType questionGridType = QuestionGridDocument.Factory.parse(
                this.getClass().getClassLoader().getResourceAsStream(
                        "unit/ddi/ddi-multiple-choice-other-specify.xml")).getQuestionGrid();
        //
        assertInstanceOf(SimpleMultipleChoiceQuestion.class,
                DDIQuestionGridConversion.instantiateFrom(questionGridType));
    }

}
