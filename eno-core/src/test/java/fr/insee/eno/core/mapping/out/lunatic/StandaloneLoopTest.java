package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.lunatic.model.flat.Loop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StandaloneLoopTest {

    private StandaloneLoop enoLoop;
    private Loop lunaticLoop;

    @BeforeEach
    void loopObjects() {
        enoLoop = new StandaloneLoop();
        lunaticLoop = new Loop();
    }

    @Test
    void lunaticLoopDependencies() {
        //
        enoLoop.setLoopIterations(new StandaloneLoop.LoopIterations());
        CalculatedExpression minIteration = new CalculatedExpression();
        minIteration.getBindingReferences().add(new BindingReference("foo-id", "FOO"));
        enoLoop.getLoopIterations().setMinIteration(minIteration);
        CalculatedExpression maxIteration = new CalculatedExpression();
        maxIteration.getBindingReferences().add(new BindingReference("bar-id", "BAR"));
        enoLoop.getLoopIterations().setMaxIteration(maxIteration);
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoLoop, lunaticLoop);
        //
        assertThat(lunaticLoop.getLoopDependencies()).containsExactlyInAnyOrderElementsOf(Set.of("FOO", "BAR"));
    }

    @Test
    void lunaticLoopDependencies_shouldBeNoDuplicate() {
        //
        enoLoop.setLoopIterations(new StandaloneLoop.LoopIterations());
        CalculatedExpression minIteration = new CalculatedExpression();
        minIteration.getBindingReferences().add(new BindingReference("foo-ref1", "FOO"));
        enoLoop.getLoopIterations().setMinIteration(minIteration);
        CalculatedExpression maxIteration = new CalculatedExpression();
        maxIteration.getBindingReferences().add(new BindingReference("foo-ref2", "FOO"));
        enoLoop.getLoopIterations().setMaxIteration(maxIteration);
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoLoop, lunaticLoop);
        //
        assertEquals(List.of("FOO"), lunaticLoop.getLoopDependencies());
    }

}
