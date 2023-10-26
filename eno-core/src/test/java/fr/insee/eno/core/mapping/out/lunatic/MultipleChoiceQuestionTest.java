package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.Constant;
import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.ComplexMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.lunatic.model.flat.CheckboxGroup;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Table;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MultipleChoiceQuestionTest {

    @Test
    void simpleMCQ_lunaticComponentType() {
        //
        SimpleMultipleChoiceQuestion enoMultipleChoiceQuestion = new SimpleMultipleChoiceQuestion();
        CheckboxGroup lunaticCheckboxGroup = new CheckboxGroup();
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoMultipleChoiceQuestion, lunaticCheckboxGroup);
        //
        assertEquals(ComponentTypeEnum.CHECKBOX_GROUP, lunaticCheckboxGroup.getComponentType());
    }

    @Test
    void simpleMCQ_integrationTestFromDDI() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-mcq.xml"),
                EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));
        //
        SimpleMultipleChoiceQuestion simpleMultipleChoiceQuestion = (SimpleMultipleChoiceQuestion)
                enoQuestionnaire.getMultipleResponseQuestions().get(0);
        CheckboxGroup lunaticCheckboxGroup = new CheckboxGroup();

        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(simpleMultipleChoiceQuestion, lunaticCheckboxGroup);

        //
        assertEquals(4, lunaticCheckboxGroup.getResponses().size());
        //
        assertEquals("\"Code A\"", lunaticCheckboxGroup.getResponses().get(0).getLabel().getValue());
        assertEquals("\"Code B\"", lunaticCheckboxGroup.getResponses().get(1).getLabel().getValue());
        assertEquals("\"Code C\"", lunaticCheckboxGroup.getResponses().get(2).getLabel().getValue());
        assertEquals("\"Code D\"", lunaticCheckboxGroup.getResponses().get(3).getLabel().getValue());
        //
        lunaticCheckboxGroup.getResponses().forEach(responsesCheckboxGroup ->
                assertEquals(Constant.LUNATIC_LABEL_VTL_MD, responsesCheckboxGroup.getLabel().getType()));
        //
        assertEquals("MCQ_BOOL1", lunaticCheckboxGroup.getResponses().get(0).getResponse().getName());
        assertEquals("MCQ_BOOL2", lunaticCheckboxGroup.getResponses().get(1).getResponse().getName());
        assertEquals("MCQ_BOOL3", lunaticCheckboxGroup.getResponses().get(2).getResponse().getName());
        assertEquals("MCQ_BOOL4", lunaticCheckboxGroup.getResponses().get(3).getResponse().getName());
    }

    @Test
    void complexMCQ_lunaticComponentType() {
        //
        ComplexMultipleChoiceQuestion enoMultipleChoiceQuestion = new ComplexMultipleChoiceQuestion();
        Table lunaticTable = new Table();
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoMultipleChoiceQuestion, lunaticTable);
        //
        assertEquals(ComponentTypeEnum.TABLE, lunaticTable.getComponentType());
    }

}
