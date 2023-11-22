package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.LunaticParameters;
import fr.insee.eno.core.processing.common.steps.EnoAddPrefixInQuestionLabels;
import fr.insee.eno.core.processing.common.steps.EnoAddResponseTimeSection;
import fr.insee.eno.core.processing.out.steps.lunatic.pagination.LunaticAddPageNumbers;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LunaticResponseTimeQuestionPaginationTest {

    @Test
    void questionPaginationMode_hoursAndMinutesQuestionShouldHaveTheSameNumber() {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        enoQuestionnaire.setIndex(new EnoIndex());
        new EnoAddResponseTimeSection(new EnoAddPrefixInQuestionLabels(
                true, EnoParameters.QuestionNumberingMode.NONE, EnoParameters.ModeParameter.CAWI))
                .apply(enoQuestionnaire);
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        //
        new LunaticAddPageNumbers(EnoParameters.LunaticPaginationMode.QUESTION).apply(lunaticQuestionnaire);

        // When
        new LunaticResponseTimeQuestionPagination().apply(lunaticQuestionnaire);

        // Then
        Optional<ComponentType> hoursQuestionComponent = lunaticQuestionnaire.getComponents().stream()
                .filter(component -> EnoAddResponseTimeSection.HOURS_QUESTION_ID.equals(component.getId()))
                .findAny();
        Optional<ComponentType> minutesQuestionComponent = lunaticQuestionnaire.getComponents().stream()
                .filter(component -> EnoAddResponseTimeSection.MINUTES_QUESTION_ID.equals(component.getId()))
                .findAny();
        assertTrue(hoursQuestionComponent.isPresent());
        assertTrue(minutesQuestionComponent.isPresent());
        assertThat(hoursQuestionComponent.get().getPage()).isEqualTo(minutesQuestionComponent.get().getPage());
    }

}
