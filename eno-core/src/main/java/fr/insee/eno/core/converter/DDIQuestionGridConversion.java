package fr.insee.eno.core.converter;

import datacollection33.CodeDomainType;
import datacollection33.GridDimensionType;
import datacollection33.NominalDomainType;
import datacollection33.QuestionGridType;
import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.question.ComplexMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.TableQuestion;
import reusable33.RepresentationType;

public class DDIQuestionGridConversion {

    private DDIQuestionGridConversion() {}

    static EnoObject instantiateFrom(QuestionGridType questionGridType) {
        int dimensionSize = questionGridType.getGridDimensionList().size();
        if (isMultipleChoiceQuestion(questionGridType)) {
            RepresentationType representationType = questionGridType.getStructuredMixedGridResponseDomain()
                    .getGridResponseDomainInMixedArray(0) // supposing that it is the same for all modalities
                    .getResponseDomain();
            if (representationType instanceof NominalDomainType)
                return new SimpleMultipleChoiceQuestion();
            if (representationType instanceof CodeDomainType)
                return new ComplexMultipleChoiceQuestion();
            throw new ConversionException(
                    "Unable to identify question type in DDI question grid " +
                            questionGridType.getIDArray(0).getStringValue());
        }
        if (isTableQuestion(questionGridType)) {
            GridDimensionType gridDimensionType = getRank1GridDimension(questionGridType);
            if (isStaticTableDimension(gridDimensionType))
                return new TableQuestion();
            if (isDynamicTableDimension(gridDimensionType))
                return new DynamicTableQuestion();
            throw new ConversionException(String.format(
                    "Grid dimension of rank 1 of question grid '%s' is neither a CodeDomain nor a Roster. " +
                            "This case is unexpected and Eno is unable to convert this question.",
                    questionGridType.getIDArray(0).getStringValue()));
        }
        //
        throw new ConversionException(String.format(
                "Question grid '%s' has %s grid dimension objects. " +
                        "Eno expects question grids to have exactly 1 or 2 of these.",
                questionGridType.getIDArray(0).getStringValue(), dimensionSize));
    }

    private static boolean isMultipleChoiceQuestion(QuestionGridType ddiQuestionGrid) {
        return ddiQuestionGrid.getGridDimensionList().size() == 1;
    }

    private static boolean isTableQuestion(QuestionGridType ddiQuestionGrid) {
        return ddiQuestionGrid.getGridDimensionList().size() == 2;
    }

    private static GridDimensionType getRank1GridDimension(QuestionGridType questionGridType) {
        GridDimensionType gridDimensionType = questionGridType.getGridDimensionList().stream()
                .filter(gridDimensionType1 -> gridDimensionType1.getRank().intValue() == 1)
                .findAny().orElse(null);
        if (gridDimensionType == null)
            throw new ConversionException(String.format(
                    "Question grid '%s' has no grid dimension of rank 1.",
                    questionGridType.getIDArray(0).getStringValue()));
        return gridDimensionType;
    }

    private static boolean isStaticTableDimension(GridDimensionType gridDimensionType) {
        return gridDimensionType.getCodeDomain() != null;
    }

    private static boolean isDynamicTableDimension(GridDimensionType gridDimensionType) {
        return gridDimensionType.getRoster() != null;
    }

    public static boolean isDynamicTableQuestion(QuestionGridType questionGridType) {
        if (! isTableQuestion(questionGridType))
            return false;
        GridDimensionType gridDimensionType = getRank1GridDimension(questionGridType);
        return isDynamicTableDimension(gridDimensionType);
    }

}
