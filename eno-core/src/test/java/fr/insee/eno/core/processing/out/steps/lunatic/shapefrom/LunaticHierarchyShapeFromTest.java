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
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import static fr.insee.eno.core.utils.LunaticUtils.findComponentById;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LunaticHierarchyShapeFromTest {

    @Test
    void integrationTest_fromDDI() throws DDIParsingException {
        //
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
        new LunaticHierarchyShapeFrom(enoQuestionnaire).apply(lunaticQuestionnaire);
        //
        assertNotNull(lunaticQuestionnaire);
        ComponentType sequenceWithoutShapeFrom = findComponentById(lunaticQuestionnaire, "lw4zc9jk").get();
        assertNull(sequenceWithoutShapeFrom.getConditionFilter().getShapeFrom());

        ComponentType sequenceShapeFrom = findComponentById(lunaticQuestionnaire, "lw4zsbvk").get();
        assertEquals("Q21", sequenceShapeFrom.getConditionFilter().getShapeFrom());
        assertEquals("Q21", sequenceShapeFrom.getLabel().getShapeFrom());

        ComponentType subsequenceShapeFrom = findComponentById(lunaticQuestionnaire, "lw4zph3u").get();
        assertEquals("Q21", subsequenceShapeFrom.getConditionFilter().getShapeFrom());
        assertEquals("Q21", subsequenceShapeFrom.getLabel().getShapeFrom());

        ComponentType loopShapeFrom = findComponentById(lunaticQuestionnaire, "lw4zypq0").get();
        //assertEquals("Q21", loopShapeFrom.getConditionFilter().getShapeFrom()); // FIXME

        ComponentType subSequenceShapeFromOtherScope = findComponentById(lunaticQuestionnaire, "lw50fbep").get();
        assertEquals("Q311", subSequenceShapeFromOtherScope.getConditionFilter().getShapeFrom());
        assertEquals("Q311", subSequenceShapeFromOtherScope.getLabel().getShapeFrom());

    }
}
