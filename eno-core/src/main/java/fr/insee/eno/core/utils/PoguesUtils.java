package fr.insee.eno.core.utils;

import fr.insee.pogues.model.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class that provide some methods for Pogues-Model objects.
 */
public class PoguesUtils {

    public static final String POGUES_NO_DATA_ATTRIBUTE = "NoDataByDefinition";

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

    /**
     * Returns the list of Pogues response objects that correspond to a collected cell in the given Pogues table
     * question.
     * @param tableQuestion A Pogues table question object.
     * @return List of Pogues response objects that correspond to a collected cell.
     */
    public static List<ResponseType> getPoguesTableResponseCells(QuestionType tableQuestion){
        return getPoguesTableResponse(tableQuestion, false);
    }

    /**
     * Returns the list of Pogues response objects that correspond to a no data cell in the given Pogues table
     * question.
     * @param tableQuestion A Pogues table question object.
     * @return List of Pogues response objects that correspond to a no data cell.
     */
    public static List<ResponseType> getPoguesTableNoDataCells(QuestionType tableQuestion){
       return getPoguesTableResponse(tableQuestion, true);
    }

    /** Returns either collected cells response or no data cells response object of the given Pogues table question.
     * @throws IllegalArgumentException if Pogues question object in not a table. */
    private static List<ResponseType> getPoguesTableResponse(QuestionType tableQuestion, boolean noDataCells) {

        if (! QuestionTypeEnum.TABLE.equals(tableQuestion.getQuestionType()))
            throw new IllegalArgumentException("Pogues question " + tableQuestion.getId() + " is not a table.");

        List<String> coordinateOfNoDataCells = tableQuestion.getResponseStructure()
                .getAttribute().stream()
                .filter(a -> POGUES_NO_DATA_ATTRIBUTE.equals(a.getAttributeValue()))
                .map(AttributeType::getAttributeTarget)
                .toList();
        Map<String, String> coordinateMap = tableQuestion.getResponseStructure().getMapping().stream()
                .collect(Collectors.toMap(MappingType::getMappingSource, MappingType::getMappingTarget));
        return tableQuestion.getResponse().stream()
                .filter(responseType -> {
                    String responseCoordinate = coordinateMap.get(responseType.getId());
                    if (noDataCells)
                        return coordinateOfNoDataCells.contains(responseCoordinate);
                    return !coordinateOfNoDataCells.contains(responseCoordinate);
                }).toList();
    }

}
