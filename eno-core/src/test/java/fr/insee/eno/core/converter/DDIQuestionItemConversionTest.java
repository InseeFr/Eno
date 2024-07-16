package fr.insee.eno.core.converter;

import fr.insee.ddi.lifecycle33.datacollection.QuestionItemDocument;
import fr.insee.ddi.lifecycle33.datacollection.QuestionItemType;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import org.apache.xmlbeans.XmlException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class DDIQuestionItemConversionTest {

    @Test
    void convertUniqueChoiceQuestionWithDetailResponses() throws XmlException, IOException {
        //
        QuestionItemType questionItemType = QuestionItemDocument.Factory.parse(
                this.getClass().getClassLoader().getResourceAsStream(
                        "unit/ddi/ddi-unique-choice-other-specify.xml")).getQuestionItem();
        //
        assertInstanceOf(UniqueChoiceQuestion.class,
                DDIQuestionItemConversion.instantiateFrom(questionItemType, null));
    }

}
