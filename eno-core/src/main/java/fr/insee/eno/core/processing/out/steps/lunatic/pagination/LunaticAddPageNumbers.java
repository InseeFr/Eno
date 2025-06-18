package fr.insee.eno.core.processing.out.steps.lunatic.pagination;

import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.parameter.LunaticParameters;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.Pagination;
import fr.insee.lunatic.model.flat.Questionnaire;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class LunaticAddPageNumbers implements ProcessingStep<Questionnaire> {

    private final LunaticParameters.LunaticPaginationMode mode;
    private final List<Loop> enoLoops;

    public LunaticAddPageNumbers(LunaticParameters.LunaticPaginationMode mode, List<Loop> enoLoops) {
        this.mode = mode;
        this.enoLoops = enoLoops;
    }

    /**
     * Add pagination information in the given Lunatic questionnaire.
     * Warning: this method supposes that components are sorted.
     * TODO: (!) weird if the questionnaire ends with an empty subsequence with no declarations.
     *
     * @param lunaticQuestionnaire Lunatic questionnaire to be processed.
     */
    public void apply(Questionnaire lunaticQuestionnaire) {
        lunaticQuestionnaire.setPagination(lunaticNumberingMode(mode));
        switch (mode) {
            case NONE -> log.info("No pagination.");
            case QUESTION -> {
                log.info("Adding page numbers by question.");
                new LunaticPaginationQuestionMode(enoLoops).apply(lunaticQuestionnaire);
            }
            case SEQUENCE -> {
                log.info("Adding page numbers by sequence.");
                new LunaticPaginationSequenceMode().apply(lunaticQuestionnaire);
            }
        }
    }

    private static Pagination lunaticNumberingMode(LunaticParameters.LunaticPaginationMode paginationMode) {
        return switch (paginationMode) {
            case NONE -> Pagination.NONE;
            case SEQUENCE -> Pagination.SEQUENCE;
            case QUESTION -> Pagination.QUESTION;
        };
    }

}
