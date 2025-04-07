package fr.insee.eno.core.utils;

import fr.insee.pogues.model.QuestionType;
import fr.insee.pogues.model.SequenceType;

import java.util.stream.Stream;

/**
 * Utility class that provide some methods for Pogues-Model objects.
 */
public class PoguesUtils {

    private PoguesUtils() {}

    /**
     * Returns a stream of sequence objects within the given sequence.
     * @param poguesSequence Pogues sequence object (questionnaire, sequence or subsequence).
     * @return Stream of the sequences of the given object.
     */
    public static Stream<SequenceType> poguesSequenceStream(SequenceType poguesSequence) {
        return poguesSequence.getChild().stream()
                .filter(SequenceType.class::isInstance).map(SequenceType.class::cast);
    }

    /**
     * Returns a stream of question objects within the given sequence.
     * @param poguesSequence Pogues sequence object (questionnaire, sequence or subsequence).
     * @return Stream of the sequences of the given object.
     */
    public static Stream<QuestionType> poguesQuestionStream(SequenceType poguesSequence) {
        return poguesSequence.getChild().stream()
                .filter(QuestionType.class::isInstance).map(QuestionType.class::cast);
    }

}
