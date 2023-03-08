package fr.insee.eno.core.converter;

import datacollection33.*;
import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.model.question.*;
import lombok.extern.slf4j.Slf4j;
import reusable33.RepresentationType;
import reusable33.StandardKeyValuePairType;
import reusable33.TextDomainType;

@Slf4j
public class DDIConverter {

    public static final String DDI_PAIRWISE_KEY = "UIComponent";
    public static final String DDI_PAIRWISE_VALUE = "HouseholdPairing";

    private DDIConverter() {}

    /**
     * Return an Eno instance corresponding to the given DDI object.
     *
     * @return A Eno model object.
     */
    public static EnoObject instantiateFromDDIObject(Object ddiObject) {
        if (ddiObject instanceof LoopType loopType)
            return instantiateFrom(loopType);
        else if (ddiObject instanceof QuestionItemType questionItemType)
            return instantiateFrom(questionItemType);
        else if (ddiObject instanceof QuestionGridType questionGridType)
            return instantiateFrom(questionGridType);
        else if (ddiObject instanceof GridResponseDomainInMixedType gridResponseDomainInMixedType)
            return instantiateFrom(gridResponseDomainInMixedType);
        else
            throw new ConversionException("Eno conversion for DDI type " + ddiObject.getClass() + " not implemented.");
    }

    public static EnoObject instantiateFrom(LoopType loopType) {
        if (loopType.getInitialValue() == null && loopType.getLoopWhile() == null) {
            return new LinkedLoop();
        } else {
            // A standalone loop should have both "initial value" and "loop while" defined
            if (loopType.getInitialValue() == null)
                log.warn("DDI Loop '{}' has a null initial value.", loopType.getIDArray(0).getStringValue());
            if (loopType.getLoopWhile() == null)
                log.warn("DDI Loop '{}' has a null loop while.", loopType.getIDArray(0).getStringValue());
            return new StandaloneLoop();
        }
    }

    public static EnoObject instantiateFrom(QuestionItemType questionItemType) {
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
                String attributeKey = userAttributePair.getAttributeKey().getStringValue();
                String attributeValue = userAttributePair.getAttributeValue().getStringValue();
                if (! DDI_PAIRWISE_KEY.equals(attributeKey))
                    log.warn(String.format(
                            "Attribute pair list found in question item '%s', but key is equal to '%s' (should be '%s')",
                            questionItemType.getIDArray(0).getStringValue(), attributeKey, DDI_PAIRWISE_KEY));
                if (! DDI_PAIRWISE_VALUE.equals(attributeValue))
                    log.warn(String.format(
                            "Attribute pair list found in question item '%s', but value is equal to '%s' (should be '%s')",
                            questionItemType.getIDArray(0).getStringValue(), attributeValue, DDI_PAIRWISE_VALUE));
                return new PairwiseQuestion();
            }
            else {
                return new UniqueChoiceQuestion();
            }

        }
        else {
            throw new ConversionException(
                    "Unable to identify question type in DDI question item " +
                            questionItemType.getIDArray(0).getStringValue());
        }
    }

    public static EnoObject instantiateFrom(QuestionGridType questionGridType) {
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
                throw new ConversionException(
                        "Unable to identify question type in DDI question grid " +
                                questionGridType.getIDArray(0).getStringValue());
        }
        else if (dimensionSize == 2) {
            GridDimensionType gridDimensionType = questionGridType.getGridDimensionList().stream()
                    .filter(gridDimensionType1 -> gridDimensionType1.getRank().intValue() == 1)
                    .findAny().orElse(null);
            if (gridDimensionType == null) {
                throw new ConversionException(String.format(
                        "Question grid '%s' has no grid dimension of rank 1.",
                        questionGridType.getIDArray(0).getStringValue()));
            } else {
                if (gridDimensionType.getCodeDomain() != null) {
                    return new TableQuestion();
                } else if (gridDimensionType.getRoster() != null) {
                    return new DynamicTableQuestion();
                } else {
                    throw new ConversionException(String.format(
                            "Grid dimension of rank 1 of question grid '%s' is neither a CodeDomain nor a Roster. " +
                                    "This case is unexpected and Eno is unable to convert this question.",
                            questionGridType.getIDArray(0).getStringValue()));
                }
            }

        }
        else {
            throw new ConversionException(String.format(
                    "Question grid '%s' has %s grid dimension objects. " +
                            "Eno expects question grids to have exactly 1 or 2 of these.",
                    questionGridType.getIDArray(0).getStringValue(), dimensionSize));
        }
    }

    public static EnoObject instantiateFrom(GridResponseDomainInMixedType gridResponseDomainInMixedType) {
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
            throw new ConversionException(
                    "Unable to identify cell type in DDI GridResponseDomainInMixed object " +
                            "with response domain of type "+representationType.getClass()+".");
        }
    }

}