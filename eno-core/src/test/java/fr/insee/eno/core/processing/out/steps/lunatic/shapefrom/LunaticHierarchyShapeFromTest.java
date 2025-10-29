package fr.insee.eno.core.processing.out.steps.lunatic.shapefrom;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticLoopResolution;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticSortComponents;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticVariablesDimension;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
import fr.insee.eno.core.utils.LunaticUtils;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LunaticHierarchyShapeFromTest {

    @Test
    void integrationTest_fromDDI() throws DDIParsingException {
        // Given
        EnoQuestionnaire enoQuestionnaire = DDIToEno.fromInputStream(
                        this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-dimensions.xml"))
                .transform(EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC));
        LunaticMapper lunaticMapper = new LunaticMapper();
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticLoopResolution(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticTableProcessing(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticVariablesDimension(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticShapeFrom().apply(lunaticQuestionnaire);

        // When
        new LunaticHierarchyShapeFrom(enoQuestionnaire).apply(lunaticQuestionnaire);

        // Then
        ComponentType sequenceWithoutShapeFrom = getLunaticComponent(lunaticQuestionnaire, "lw4zc9jk");
        assertNull(sequenceWithoutShapeFrom.getConditionFilter().getShapeFrom());

        ComponentType sequenceShapeFrom = getLunaticComponent(lunaticQuestionnaire, "lw4zsbvk");
        assertEquals("Q21", sequenceShapeFrom.getConditionFilter().getShapeFrom());
        assertEquals("Q21", sequenceShapeFrom.getLabel().getShapeFrom());

        ComponentType subsequenceShapeFrom = getLunaticComponent(lunaticQuestionnaire, "lw4zph3u");
        assertEquals("Q21", subsequenceShapeFrom.getConditionFilter().getShapeFrom());
        assertEquals("Q21", subsequenceShapeFrom.getLabel().getShapeFrom());

        ComponentType loopShapeFrom = getLunaticComponent(lunaticQuestionnaire, "lw4zypq0");
        assertNull(loopShapeFrom.getConditionFilter().getShapeFrom());

        ComponentType subSequenceShapeFromOtherScope = getLunaticComponent(lunaticQuestionnaire, "lw50fbep");
        assertEquals("Q311", subSequenceShapeFromOtherScope.getConditionFilter().getShapeFrom());
        assertEquals("Q311", subSequenceShapeFromOtherScope.getLabel().getShapeFrom());
    }

    private static ComponentType getLunaticComponent(Questionnaire lunaticQuestionnaire, String id) {
        Optional<ComponentType> lunaticComponent = LunaticUtils.findComponentById(lunaticQuestionnaire, id);
        assertTrue(lunaticComponent.isPresent());
        return lunaticComponent.get();
    }

}
