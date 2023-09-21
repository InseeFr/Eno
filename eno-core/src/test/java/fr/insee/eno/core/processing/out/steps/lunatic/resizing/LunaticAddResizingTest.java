package fr.insee.eno.core.processing.out.steps.lunatic.resizing;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticLoopResolution;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticSortComponents;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

class LunaticAddResizingTest {

    private static List<Object> resizingList;

    @BeforeAll
    static void init() throws DDIParsingException {

        // Given
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                LunaticAddResizingTest.class.getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-resizing.xml"),
                EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticLoopResolution(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticTableProcessing(enoQuestionnaire).apply(lunaticQuestionnaire);

        // When
        new LunaticAddResizing(enoQuestionnaire).apply(lunaticQuestionnaire);

        // Then
        resizingList = lunaticQuestionnaire.getResizing().getAny();
        // -> tests
    }

    @Test
    void resizingListIsNotEmpty() {
        assertFalse(resizingList.isEmpty());
    }

}
