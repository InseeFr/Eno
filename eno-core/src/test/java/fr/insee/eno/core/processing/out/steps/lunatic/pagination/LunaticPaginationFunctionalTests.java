package fr.insee.eno.core.processing.out.steps.lunatic.pagination;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticAddHierarchy;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticLoopResolution;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticSortComponents;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Loop;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.Subsequence;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LunaticPaginationFunctionalTests {

    private Questionnaire mapDDIAndApplyPagination(String id) throws DDIParsingException {
        // Given
        EnoParameters enoParameters = EnoParameters.of(EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                LunaticPaginationFunctionalTests.class.getClassLoader().getResourceAsStream(
                        "functional/ddi/pagination/ddi-"+id+".xml"),
                enoParameters);
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoQuestionnaire, lunaticQuestionnaire);
        new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticLoopResolution(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticTableProcessing(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticAddHierarchy().apply(lunaticQuestionnaire);

        // When
        new LunaticAddPageNumbers(enoParameters.getLunaticParameters().getLunaticPaginationMode())
                .apply(lunaticQuestionnaire);

        return lunaticQuestionnaire;
    }

    @Test
    void functionalTest1() throws DDIParsingException {
        Questionnaire lunaticQuestionnaire = mapDDIAndApplyPagination("llxh9g6g");

        // Then
        assertNotNull(lunaticQuestionnaire.getMaxPage());
        // ...
    }

    @Test
    void functionalTest2() throws DDIParsingException {
        Questionnaire lunaticQuestionnaire = mapDDIAndApplyPagination("lnycjn6n");

        // Then
        assertEquals("3", lunaticQuestionnaire.getMaxPage());

        //
        assertEquals(ComponentTypeEnum.LOOP, lunaticQuestionnaire.getComponents().get(0).getComponentType());
        Loop loop1 = (Loop) lunaticQuestionnaire.getComponents().get(0);
        assertEquals("1", loop1.getPage());
        assertNull(loop1.getMaxPage());
        assertEquals(2, loop1.getComponents().size());
        loop1.getComponents().forEach(component ->
                assertEquals("1", component.getPage()));

        //
        assertEquals(ComponentTypeEnum.LOOP, lunaticQuestionnaire.getComponents().get(1).getComponentType());
        Loop loop2 = (Loop) lunaticQuestionnaire.getComponents().get(1);
        assertEquals("2", loop2.getPage());
        assertEquals("492", loop2.getMaxPage());
        assertEquals("2.1", loop2.getComponents().get(0).getPage());
        assertEquals("2.2", loop2.getComponents().get(1).getPage());
        assertEquals("2.3", loop2.getComponents().get(2).getPage());
        // ...
        assertEquals("2.7", loop2.getComponents().get(6).getPage());
        assertEquals(ComponentTypeEnum.SUBSEQUENCE, loop2.getComponents().get(7).getComponentType());
        assertEquals("2.8", loop2.getComponents().get(7).getPage());
        assertEquals("2.8", ((Subsequence) loop2.getComponents().get(7)).getGoToPage());
        assertEquals("2.9", loop2.getComponents().get(8).getPage());
        // ...

        //
        assertEquals(ComponentTypeEnum.SEQUENCE, lunaticQuestionnaire.getComponents().get(2).getComponentType());
        assertEquals("3", lunaticQuestionnaire.getComponents().get(2).getPage());
    }

}
