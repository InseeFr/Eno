package fr.insee.eno.core.processing.impl.lunatic.pagination;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.lunatic.model.flat.Questionnaire;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class LunaticAddPageNumbers implements OutProcessingInterface<Questionnaire> {

    private EnoParameters.LunaticPaginationMode mode;

    /**
     * Add pagination information in the given Lunatic questionnaire.
     * Warning: this method supposes that components are sorted.
     * TODO: (!) weird if the questionnaire ends with an empty subsequence with no declarations.
     * @param lunaticQuestionnaire Lunatic questionnaire to be processed.
     */
    public void apply(Questionnaire lunaticQuestionnaire) {
        lunaticQuestionnaire.setPagination(EnoParameters.lunaticNumberingMode(mode));
        switch (mode) {
            case NONE -> log.info("No pagination.");
            case QUESTION -> {
                log.info("Adding page numbers by question.");
                new LunaticAddPageNumbersQuestionMode().apply(lunaticQuestionnaire);
            }
            case SEQUENCE -> {
                log.info("Adding page numbers by sequence.");
                new LunaticAddPageNumbersSequenceMode().apply(lunaticQuestionnaire);
            }
        }
    }
}
