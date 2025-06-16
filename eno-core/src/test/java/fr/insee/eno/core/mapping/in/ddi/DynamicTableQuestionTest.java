package fr.insee.eno.core.mapping.in.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DynamicTableQuestionTest {

    @Test
    void integrationTest_tableSize() throws DDIParsingException {
        // Given + When
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new DDIMapper().mapDDI(
                DDIDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-dynamic-table-size.xml")),
                enoQuestionnaire);

        // Then
        List<DynamicTableQuestion> dynamicTableQuestions = enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(DynamicTableQuestion.class::isInstance).map(DynamicTableQuestion.class::cast).toList();
        assertEquals(4, dynamicTableQuestions.size());
        DynamicTableQuestion dynamicTableQuestion1 = dynamicTableQuestions.get(0);
        DynamicTableQuestion dynamicTableQuestion2 = dynamicTableQuestions.get(1);
        DynamicTableQuestion dynamicTableQuestion3 = dynamicTableQuestions.get(2);
        DynamicTableQuestion dynamicTableQuestion4 = dynamicTableQuestions.get(3);
        //
        assertEquals(BigInteger.valueOf(5), dynamicTableQuestion1.getMinLines());
        assertEquals(BigInteger.valueOf(5), dynamicTableQuestion1.getMaxLines());
        assertNull(dynamicTableQuestion1.getMinSizeExpression());
        assertNull(dynamicTableQuestion1.getMaxSizeExpression());
        assertEquals(BigInteger.valueOf(1), dynamicTableQuestion2.getMinLines());
        assertEquals(BigInteger.valueOf(5), dynamicTableQuestion2.getMaxLines());
        assertNull(dynamicTableQuestion2.getMinSizeExpression());
        assertNull(dynamicTableQuestion2.getMaxSizeExpression());
        //
        assertEquals(BigInteger.valueOf(1), dynamicTableQuestion3.getMinLines());
        assertNull(dynamicTableQuestion3.getMaxLines());
        assertNotNull(dynamicTableQuestion3.getMaxSizeExpression());
        assertEquals(BigInteger.valueOf(1), dynamicTableQuestion4.getMinLines());
        assertNull(dynamicTableQuestion4.getMaxLines());
        assertNotNull(dynamicTableQuestion4.getMaxSizeExpression());
    }

}
