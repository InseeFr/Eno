package fr.insee.eno.core.mapping.in.ddi;

import datacollection33.QuestionItemDocument;
import datacollection33.QuestionItemType;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.eno.core.model.response.DetailResponse;
import org.apache.xmlbeans.XmlException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UniqueChoiceQuestionTest {

    @Test
    void mapUniqueChoiceQuestionWithDetailResponses() throws XmlException, IOException {
        //
        QuestionItemType ddiUniqueChoiceQuestion = QuestionItemDocument.Factory.parse(
                this.getClass().getClassLoader().getResourceAsStream(
                        "unit/ddi/ddi-unique-choice-other-specify.xml")).getQuestionItem();
        UniqueChoiceQuestion enoUniqueChoiceQuestion = new UniqueChoiceQuestion();

        //
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDIObject(ddiUniqueChoiceQuestion, enoUniqueChoiceQuestion);

        //
        assertEquals("lutkfklf", enoUniqueChoiceQuestion.getCodeListReference());
        assertEquals(UniqueChoiceQuestion.DisplayFormat.RADIO, enoUniqueChoiceQuestion.getDisplayFormat());
        //
        assertEquals(3, enoUniqueChoiceQuestion.getDdiBindings().size());
        assertEquals(3, enoUniqueChoiceQuestion.getDdiResponses().size());
        //
        assertEquals(2, enoUniqueChoiceQuestion.getDetailResponses().size());
    }
}
