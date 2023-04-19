package fr.insee.eno.core.mappers.lunatic;

import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoopTest {

    private Loop enoLoop;
    private fr.insee.lunatic.model.flat.Loop lunaticLoop;

    @BeforeEach
    void loopObjects() {
        enoLoop = new Loop();
        lunaticLoop = new fr.insee.lunatic.model.flat.Loop();
    }

    @Test
    void lunaticComponentType() {
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoLoop, lunaticLoop);
        //
        assertEquals(ComponentTypeEnum.LOOP, lunaticLoop.getComponentType());
    }

}
