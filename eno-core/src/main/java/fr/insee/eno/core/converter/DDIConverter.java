package fr.insee.eno.core.converter;

import datacollection33.*;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.question.*;
import lombok.extern.slf4j.Slf4j;
import reusable33.RepresentationType;
import reusable33.StandardKeyValuePairType;
import reusable33.TextDomainType;

@Slf4j
public class DDIConverter {

    public static final String INSEE_VOCABULARY_ID = "INSEE-GOF-CV";
    public static final String RADIO_OUTPUT_FORMAT = "radio-button";
    public static final String CHECKBOX_OUTPUT_FORMAT = "checkbox";
    public static final String DROPDOWN_OUTPUT_FORMAT = "drop-down-list";
    //TODO: Constants class for these? + use these constants in DDI mapping annotation

    public static final String DDI_PAIRWISE_KEY = "UIComponent";
    public static final String DDI_PAIRWISE_VALUE = "HouseholdPairing";

    /**
     * Return an Eno instance corresponding to the given DDI object.
     *
     * @return A Eno model object.
     */
    public static EnoObject instantiateFromDDIObject(Object ddiObject) {
        if (ddiObject instanceof QuestionItemType)
            return instantiateFrom((QuestionItemType) ddiObject);
        else if (ddiObject instanceof QuestionGridType)
            return instantiateFrom((QuestionGridType) ddiObject);
        else if (ddiObject instanceof GridResponseDomainInMixedType)
            return instantiateFrom((GridResponseDomainInMixedType) ddiObject);
        else
            throw new RuntimeException("Eno conversion for DDI type " + ddiObject.getClass() + " not implemented.");
    }

    private static EnoObject instantiateFrom(QuestionItemType questionItemType) {
        RepresentationType representationType = questionItemType.getResponseDomain();
        if (representationType instanceof NominalDomainType) {
            return new BooleanQuestion();
        }
        else if (representationType instanceof TextDomainType) {
            return new TextQuestion();
        }
        else if (representationType instanceof NumericDomainType) {
            return new NumericQuestion();
        }
        else if (representationType instanceof DateTimeDomainType) {
            return new DateQuestion();
        }
        else if (representationType instanceof CodeDomainType) {
            if (! questionItemType.getUserAttributePairList().isEmpty()) {
                StandardKeyValuePairType userAttributePair = questionItemType.getUserAttributePairArray(0);
                if (! userAttributePair.getAttributeKey().getStringValue().equals(DDI_PAIRWISE_KEY))
                    log.warn("TODO"); //TODO: with caps lock
                if (! userAttributePair.getAttributeValue().getStringValue().equals(DDI_PAIRWISE_VALUE))
                    log.warn("TODO");
                return new PairwiseQuestion();
            }
            else {
                return new UniqueChoiceQuestion();
            }

        }
        else {
            throw new RuntimeException(
                    "Unable to identify question type in DDI question item " +
                            questionItemType.getIDArray(0).getStringValue());
        }
    }

    private static EnoObject instantiateFrom(QuestionGridType questionGridType) {
        //
        int dimensionSize = questionGridType.getGridDimensionList().size();
        //
        if (dimensionSize == 1) {
            RepresentationType representationType = questionGridType.getStructuredMixedGridResponseDomain()
                    .getGridResponseDomainInMixedArray(0) // supposing that it is the same for all modalities
                    .getResponseDomain();
            if (representationType instanceof NominalDomainType)
                return new MultipleChoiceQuestion.Simple();
            else if (representationType instanceof CodeDomainType)
                return new MultipleChoiceQuestion.Complex();
            else
                throw new RuntimeException(
                        "Unable to identify question type in DDI question grid " +
                                questionGridType.getIDArray(0).getStringValue());
        }
        else if (dimensionSize == 2) {
            GridDimensionType gridDimensionType = questionGridType.getGridDimensionList().stream()
                    .filter(gridDimensionType1 -> gridDimensionType1.getRank().intValue() == 1)
                    .findAny().orElse(null);
            if (gridDimensionType == null) {
                throw new RuntimeException(String.format(
                        "Question grid '%s' has no grid dimension of rank 1.",
                        questionGridType.getIDArray(0).getStringValue()));
            } else {
                if (gridDimensionType.getCodeDomain() != null) {
                    return new TableQuestion();
                } else if (gridDimensionType.getRoster() != null) {
                    return new DynamicTableQuestion();
                } else {
                    throw new RuntimeException(String.format(
                            "Grid dimension of rank 1 of question grid '%s' is neither a CodeDomain nor a Roster. " +
                                    "This case is unexpected and Eno is unable to convert this question.",
                            questionGridType.getIDArray(0).getStringValue()));
                }
            }

        }
        else {
            throw new RuntimeException(String.format(
                    "Question grid '%s' has %s grid dimension objects. " +
                            "Eno expects question grids to have exactly 1 or 2 of these.",
                    questionGridType.getIDArray(0).getStringValue(), dimensionSize));
        }
    }

    private static EnoObject instantiateFrom(GridResponseDomainInMixedType gridResponseDomainInMixedType) {
        RepresentationType representationType = gridResponseDomainInMixedType.getResponseDomain();
        if (representationType instanceof NominalDomainType) {
            return new TableCell.BooleanCell();
        }
        else if (representationType instanceof TextDomainType) {
            return new TableCell.TextCell();
        }
        else if (representationType instanceof NumericDomainType) {
            return new TableCell.NumericCell();
        }
        else if (representationType instanceof DateTimeDomainType) {
            return new TableCell.DateCell();
        }
        else if (representationType instanceof CodeDomainType) {
            return new TableCell.UniqueChoiceCell();
        }
        else {
            throw new RuntimeException(
                    "Unable to identify cell type in DDI GridResponseDomainInMixed object " +
                            "with response domain of type "+representationType.getClass()+".");
        }
    }

}
