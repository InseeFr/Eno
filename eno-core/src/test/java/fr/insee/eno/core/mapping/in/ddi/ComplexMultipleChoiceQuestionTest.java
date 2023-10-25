package fr.insee.eno.core.mapping.in.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.ComplexMultipleChoiceQuestion;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComplexMultipleChoiceQuestionTest {

    @Test
    void mapQuestionnaireWithComplexMCQ() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(
                DDIDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-mcq.xml")),
                enoQuestionnaire);

        //
        List<ComplexMultipleChoiceQuestion> complexMCQList = enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(ComplexMultipleChoiceQuestion.class::isInstance)
                .map(ComplexMultipleChoiceQuestion.class::cast)
                .toList();
        assertEquals(4, complexMCQList.size());
        //
        assertEquals("MCQ_CL_RADIO", complexMCQList.get(0).getName());
        assertEquals("lo5upwdy", complexMCQList.get(0).getHeaderCodeListReference());
        assertEquals(4, complexMCQList.get(0).getVariableNames().size());
        assertEquals(4, complexMCQList.get(0).getBindings().size());
        assertEquals(4, complexMCQList.get(0).getTableCells().size());
        //
        assertEquals("MCQ_CL_DROPDOWN", complexMCQList.get(1).getName());
        assertEquals("lo5upwdy", complexMCQList.get(1).getHeaderCodeListReference());
        assertEquals(4, complexMCQList.get(1).getVariableNames().size());
        assertEquals(4, complexMCQList.get(1).getBindings().size());
        assertEquals(4, complexMCQList.get(1).getTableCells().size());
        //
        assertEquals("MCQ_TABLE_RADIO", complexMCQList.get(2).getName());
        assertEquals("lo5uxn2k", complexMCQList.get(2).getHeaderCodeListReference());
        assertEquals(7, complexMCQList.get(2).getVariableNames().size());
        assertEquals(7, complexMCQList.get(2).getBindings().size());
        assertEquals(7, complexMCQList.get(2).getTableCells().size());
        //
        assertEquals("MCQ_TABLE_DROPDOWN", complexMCQList.get(3).getName());
        assertEquals("lo5uxn2k", complexMCQList.get(3).getHeaderCodeListReference());
        assertEquals(7, complexMCQList.get(3).getVariableNames().size());
        assertEquals(7, complexMCQList.get(3).getBindings().size());
        assertEquals(7, complexMCQList.get(3).getTableCells().size());
    }

}
