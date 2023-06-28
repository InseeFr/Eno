package fr.insee.eno.core.mappers.lunatic;

import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.question.NumericQuestion;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.InputNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NumericQuestionTest {

    private NumericQuestion enoNumericQuestion;
    private InputNumber lunaticInputNumber;

    @BeforeEach
    void numericQuestionObjects() {
        enoNumericQuestion = new NumericQuestion();
        lunaticInputNumber = new InputNumber();
    }

    @Test
    void lunaticComponentType() {
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoNumericQuestion, lunaticInputNumber);
        //
        assertEquals(ComponentTypeEnum.INPUT_NUMBER, lunaticInputNumber.getComponentType());
    }

}
