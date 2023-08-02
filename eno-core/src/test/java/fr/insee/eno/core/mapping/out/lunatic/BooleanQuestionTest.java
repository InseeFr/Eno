package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.question.BooleanQuestion;
import fr.insee.lunatic.model.flat.CheckboxBoolean;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BooleanQuestionTest {

    private BooleanQuestion booleanQuestion;
    private CheckboxBoolean checkboxBoolean;

    @BeforeEach
    void booleanQuestionObjects() {
        booleanQuestion = new BooleanQuestion();
        checkboxBoolean = new CheckboxBoolean();
    }

    @Test
    void lunaticComponentType() {
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(booleanQuestion, checkboxBoolean);
        //
        assertEquals(ComponentTypeEnum.CHECKBOX_BOOLEAN, checkboxBoolean.getComponentType());
    }

}
