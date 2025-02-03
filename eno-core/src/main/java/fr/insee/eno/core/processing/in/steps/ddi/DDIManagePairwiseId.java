package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.PairwiseQuestion;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoIndex;

import java.util.List;

/**
 * The pairwise question has an inner question (unique choice question).
 * After mapping, both have the same identifier, which would cause issues.
 * This processing step gives a separate identifier for the inner question,
 * and updates the Eno index.
 * */
public class DDIManagePairwiseId implements ProcessingStep<EnoQuestionnaire> {

    private static final String PAIRWISE_DROPDOWN_SUFFIX = "-pairwise-dropdown";

    private final EnoIndex enoIndex;

    public DDIManagePairwiseId(EnoIndex enoIndex) {
        this.enoIndex = enoIndex;
    }

    /** See class documentation. */
    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        List<PairwiseQuestion> pairwiseList = enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(PairwiseQuestion.class::isInstance)
                .map(PairwiseQuestion.class::cast)
                .toList();
        if (pairwiseList.isEmpty())
            return;
        if (pairwiseList.size() > 1)
            throw new MappingException("A questionnaire should not have several pairwise questions.");
        PairwiseQuestion pairwiseQuestion = pairwiseList.getFirst();
        UniqueChoiceQuestion innerQuestion = pairwiseQuestion.getUniqueChoiceQuestions().getFirst();
        String id = pairwiseQuestion.getId();
        assert innerQuestion.getId().equals(id); // at this point identifiers should be the same
        innerQuestion.setId(id + PAIRWISE_DROPDOWN_SUFFIX);
        //
        enoIndex.put(pairwiseQuestion);
        enoIndex.put(innerQuestion);
    }
}
