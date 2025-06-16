package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.pogues.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class DynamicTableQuestionTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "DYNAMIC_LENGTH", "FIXED_LENGTH", // old ones
            "DYNAMIC", "DYNAMIC_FIXED" // new ones
    })
    void dynamicTableConversionAndMapping(String dynamic) {
        QuestionType poguesTable = new QuestionType();
        poguesTable.setQuestionType(QuestionTypeEnum.TABLE);
        poguesTable.setName("DYNAMIC_TABLE_NAME");
        poguesTable.getLabel().add("Dynamic table question.");
        ResponseStructureType responseStructure = new ResponseStructureType();
        DimensionType dimension = new DimensionType();
        responseStructure.getDimension().add(dimension);
        poguesTable.setResponseStructure(responseStructure);

        Questionnaire poguesQuestionnaire = new Questionnaire();
        SequenceType poguesSequence = new SequenceType();
        poguesSequence.setGenericName(GenericNameEnum.MODULE);
        poguesSequence.getChild().add(poguesTable);
        poguesQuestionnaire.getChild().add(poguesSequence);
        dimension.setDynamic(dynamic);

        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();

        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);

        DynamicTableQuestion enoTable = assertInstanceOf(DynamicTableQuestion.class,
                enoQuestionnaire.getMultipleResponseQuestions().getFirst());
        assertEquals("DYNAMIC_TABLE_NAME", enoTable.getName());
        assertEquals("Dynamic table question.", enoTable.getLabel().getValue());
    }

    @Test
    void integrationTest() throws PoguesDeserializationException {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                "integration/pogues/pogues-dynamic-table-size.json"));
        //
        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);
        //
        DynamicTableQuestion table1 = assertInstanceOf(DynamicTableQuestion.class,
                enoQuestionnaire.getMultipleResponseQuestions().get(0));
        DynamicTableQuestion table2 = assertInstanceOf(DynamicTableQuestion.class,
                enoQuestionnaire.getMultipleResponseQuestions().get(1));
        DynamicTableQuestion table3 = assertInstanceOf(DynamicTableQuestion.class,
                enoQuestionnaire.getMultipleResponseQuestions().get(2));
        DynamicTableQuestion table4 = assertInstanceOf(DynamicTableQuestion.class,
                enoQuestionnaire.getMultipleResponseQuestions().get(3));
        assertEquals("5", table1.getMinSizeExpression().getValue());
        assertEquals("5", table1.getMaxSizeExpression().getValue());
        assertEquals("1", table2.getMinSizeExpression().getValue());
        assertEquals("5", table2.getMaxSizeExpression().getValue());
        assertEquals("cast(HOW_MANY, integer)", table3.getMinSizeExpression().getValue());
        assertEquals("cast(HOW_MANY, integer)", table3.getMaxSizeExpression().getValue());
        assertEquals("1", table4.getMinSizeExpression().getValue());
        assertEquals("cast(HOW_MANY, integer)", table4.getMaxSizeExpression().getValue());

        Stream.of(table1, table2).forEach(table -> {
            assertEquals("number", table.getMinSizeExpression().getType());
            assertEquals("number", table.getMaxSizeExpression().getType());
        });
        Stream.of(table3, table4).forEach(table -> {
            assertEquals("VTL", table.getMinSizeExpression().getType());
            assertEquals("VTL", table.getMaxSizeExpression().getType());
        });
    }

}
