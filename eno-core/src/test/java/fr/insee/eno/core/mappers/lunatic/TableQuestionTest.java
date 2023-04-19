package fr.insee.eno.core.mappers.lunatic;

import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TableQuestionTest {

    private TableQuestion enoTableQuestion;
    private Table lunaticTable;

    @BeforeEach
    void tableQuestionObjects() {
        enoTableQuestion = new TableQuestion();
        lunaticTable = new Table();
    }

    @Test
    void lunaticComponentType() {
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoTableQuestion, lunaticTable);
        //
        assertEquals(ComponentTypeEnum.TABLE, lunaticTable.getComponentType());
    }

}
