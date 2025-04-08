package fr.insee.eno.core.utils;

import fr.insee.pogues.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static List<ResponseType> getPoguesTableResponse(QuestionType tableQuestion, boolean noDataCells){
        List<String> coordinateOfNoDataCells = tableQuestion.getResponseStructure()
                .getAttribute().stream()
                .filter(a ->"NoDataByDefinition".equals(a.getAttributeValue()))
                .map(AttributeType::getAttributeTarget)
                .toList();
        List<MappingType> mappings = tableQuestion.getResponseStructure().getMapping();
        Map<String, String> indexResponse = new HashMap<>();
        mappings.forEach(mappingType -> indexResponse.put(mappingType.getMappingSource(), mappingType.getMappingTarget()));
        return tableQuestion.getResponse().stream()
                .filter(responseType -> {
                    String cord = indexResponse.get(responseType.getId());
                    if(noDataCells) return coordinateOfNoDataCells.contains(cord);
                    return !coordinateOfNoDataCells.contains(cord);
                }).toList();
    }

    public static List<ResponseType> getPoguesTableNoDataCells(QuestionType tableQuestion){
       return getPoguesTableResponse(tableQuestion, true);
    }

    public static List<ResponseType> getPoguesTableResponseCells(QuestionType tableQuestion){
        return getPoguesTableResponse(tableQuestion, false);
    }
}
