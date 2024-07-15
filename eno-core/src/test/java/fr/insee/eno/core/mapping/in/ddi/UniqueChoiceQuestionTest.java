package fr.insee.eno.core.mapping.in.ddi;

import fr.insee.ddi.lifecycle33.datacollection.QuestionItemDocument;
import fr.insee.ddi.lifecycle33.datacollection.QuestionItemType;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
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
        assertEquals("codeC", enoUniqueChoiceQuestion.getDetailResponses().get(0).getValue());
        assertEquals("codeD", enoUniqueChoiceQuestion.getDetailResponses().get(1).getValue());
        assertEquals("\"Please, specify about option C:\"",
                enoUniqueChoiceQuestion.getDetailResponses().get(0).getLabel().getValue());
        assertEquals("\"Please, specify about option D:\"",
                enoUniqueChoiceQuestion.getDetailResponses().get(1).getLabel().getValue());
    }
}
