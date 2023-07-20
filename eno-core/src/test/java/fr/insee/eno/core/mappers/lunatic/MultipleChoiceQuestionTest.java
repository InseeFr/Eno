package fr.insee.eno.core.mappers.lunatic;

import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.question.ComplexMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
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
