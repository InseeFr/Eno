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
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class LunaticPaginationFunctionalTests {

    @Test
    void applyPagination() throws DDIParsingException {
        // Given
        EnoParameters enoParameters = EnoParameters.of(EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                LunaticPaginationFunctionalTests.class.getClassLoader().getResourceAsStream(
                        "functional/ddi/pagination/ddi-llxh9g6g.xml"),
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

        // Then
        assertNotNull(lunaticQuestionnaire.getMaxPage());
    }

}
