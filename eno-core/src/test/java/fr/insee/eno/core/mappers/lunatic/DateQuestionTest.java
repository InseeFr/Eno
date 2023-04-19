package fr.insee.eno.core.mappers.lunatic;

import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.question.DateQuestion;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Datepicker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateQuestionTest {

    private DateQuestion enoDateQuestion;
    private Datepicker lunaticDatepicker;

    @BeforeEach
    void booleanQuestionObjects() {
        enoDateQuestion = new DateQuestion();
        lunaticDatepicker = new Datepicker();
    }

    @Test
    void lunaticComponentType() {
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoDateQuestion, lunaticDatepicker);
        //
        assertEquals(ComponentTypeEnum.DATEPICKER, lunaticDatepicker.getComponentType());
    }

}
