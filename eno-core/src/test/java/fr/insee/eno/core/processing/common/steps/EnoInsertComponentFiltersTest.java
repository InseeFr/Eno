package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.model.sequence.StructureItemReference.StructureItemType;
import fr.insee.eno.core.processing.common.steps.EnoInsertComponentFilters;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EnoInsertComponentFiltersTest {

    private final static String SEQUENCE_ID = "sequence-id";
    private final static String SUBSEQUENCE_ID = "subsequence-id";
    private final static String QUESTION_ID = "question-id";

    @Test
    void unitTest_sequence() {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        Sequence sequence = new Sequence();
        sequence.setId(SEQUENCE_ID);
        enoQuestionnaire.getSequences().add(sequence);
        //
        EnoIndex enoIndex = new EnoIndex();
        enoIndex.put(SEQUENCE_ID, sequence);
        enoQuestionnaire.setIndex(enoIndex);
        //
        Filter filter = new Filter();
        filter.setExpression(new CalculatedExpression());
        filter.getExpression().setValue("FOO_VARIABLE = 1");
        filter.getFilterScope().add(
                StructureItemReference.builder().id(SEQUENCE_ID).type(StructureItemType.SEQUENCE).build());
        enoQuestionnaire.getFilters().add(filter);

        // When
        EnoInsertComponentFilters processing = new EnoInsertComponentFilters();
        processing.apply(enoQuestionnaire);

        // Then
        assertNotNull(enoQuestionnaire.getSequences().get(0).getComponentFilter());
        assertEquals("(FOO_VARIABLE = 1)",
                enoQuestionnaire.getSequences().get(0).getComponentFilter().getValue());
    }

    @Test
    void loopWithExcept() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(
                DDIDeserializer.deserialize(
                        this.getClass().getClassLoader().getResourceAsStream(
                                "integration/ddi/ddi-loop-except.xml")),
                enoQuestionnaire);
        //
        new EnoInsertComponentFilters().apply(enoQuestionnaire);
        //
        assertNotNull(enoQuestionnaire.getSequences().get(2).getComponentFilter());
    }

}
