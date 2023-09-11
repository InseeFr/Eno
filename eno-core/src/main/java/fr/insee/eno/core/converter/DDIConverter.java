package fr.insee.eno.core.converter;

import datacollection33.*;
import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.model.question.*;
import fr.insee.eno.core.model.question.table.*;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.model.variable.CollectedVariable;
import fr.insee.eno.core.model.variable.ExternalVariable;
import fr.insee.eno.core.reference.DDIIndex;
import logicalproduct33.VariableType;
import lombok.extern.slf4j.Slf4j;
import reusable33.*;

import java.util.Set;

@Slf4j
public class DDIConverter {

    public static final String DDI_PAIRWISE_KEY = "UIComponent";
    public static final String DDI_PAIRWISE_VALUE = "HouseholdPairing";
    public static final Set<String> DDI_DATE_TYPE_CODE = Set.of("date", "gYearMonth", "gYear");
    public static final Set<String> DDI_DURATION_TYPE_CODE = Set.of("duration");

    private DDIConverter() {}

    /**
     * Return an Eno instance corresponding to the given DDI object.
     *
     * @return A Eno model object.
     */
    public static EnoObject instantiateFromDDIObject(Object ddiObject, DDIIndex ddiIndex) {
        if (ddiObject instanceof LoopType loopType)
            return instantiateFrom(loopType);
        else if (ddiObject instanceof QuestionItemType questionItemType)
            return instantiateFrom(questionItemType, ddiIndex);
        else if (ddiObject instanceof QuestionGridType questionGridType)
            return instantiateFrom(questionGridType);
        else if (ddiObject instanceof GridResponseDomainInMixedType gridResponseDomainInMixedType)
            return instantiateFrom(gridResponseDomainInMixedType);
        else if (ddiObject instanceof VariableType variableType)
            return instantiateFrom(variableType);
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

    public static EnoObject instantiateFrom(QuestionItemType questionItemType, DDIIndex ddiIndex) {
        RepresentationType representationType = questionItemType.getResponseDomain();
        DomainReferenceType referenceType = questionItemType.getResponseDomainReference();

        if (representationType instanceof NominalDomainType) {
            return new BooleanQuestion();
        }

        if (representationType instanceof TextDomainType) {
            return new TextQuestion();
        }

        if (representationType instanceof NumericDomainType) {
            return new NumericQuestion();
        }

        if (representationType instanceof DateTimeDomainType dateTimeDomainType) {
            return convertDateTimeQuestion(dateTimeDomainType);
        }

        if(referenceType != null && referenceType.getTypeOfObject().equals(TypeOfObjectType.MANAGED_DATE_TIME_REPRESENTATION)) {
            return convertDateTimeQuestion(referenceType, ddiIndex);
        }

        if (representationType instanceof CodeDomainType) {
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

        throw new ConversionException(
                "Unable to identify question type in DDI question item " +
                        questionItemType.getIDArray(0).getStringValue());

    }

    private static EnoObject convertDateTimeQuestion(DateTimeDomainType dateTimeDomainType) {
        String dateTypeCode = dateTimeDomainType.getDateTypeCode().getStringValue();
        if (DDI_DATE_TYPE_CODE.contains(dateTypeCode))
            return new DateQuestion();
        if (DDI_DURATION_TYPE_CODE.contains(dateTypeCode)) {
            return new DurationQuestion();
        }
        // If none match, thrown an exception
        throw new ConversionException("Unknown date type code: "+dateTypeCode);
    }

    private static EnoObject convertDateTimeQuestion(DomainReferenceType referenceType, DDIIndex ddiIndex) {
        AbstractIdentifiableType ddiObject = ddiIndex.get(referenceType.getIDArray(0).getStringValue());
        ManagedDateTimeRepresentationType dateTimeRepresentation = (ManagedDateTimeRepresentationType) ddiObject;
        String dateTypeCode = dateTimeRepresentation.getDateTypeCode().getStringValue();

        if (DDI_DATE_TYPE_CODE.contains(dateTypeCode))
            return new DateQuestion();
        if (DDI_DURATION_TYPE_CODE.contains(dateTypeCode)) {
            return new DurationQuestion();
        }
        // If none match, thrown an exception
        throw new ConversionException("Unknown date type code: "+dateTypeCode);
    }

    public static EnoObject instantiateFrom(QuestionGridType questionGridType) {
        int dimensionSize = questionGridType.getGridDimensionList().size();
        if (dimensionSize == 1) {
            RepresentationType representationType = questionGridType.getStructuredMixedGridResponseDomain()
                    .getGridResponseDomainInMixedArray(0) // supposing that it is the same for all modalities
                    .getResponseDomain();
            if (representationType instanceof NominalDomainType)
                return new SimpleMultipleChoiceQuestion();
            else if (representationType instanceof CodeDomainType)
                return new ComplexMultipleChoiceQuestion();
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
            return new BooleanCell();
        }
        else if (representationType instanceof TextDomainType) {
            return new TextCell();
        }
        else if (representationType instanceof NumericDomainType) {
            return new NumericCell();
        }
        else if (representationType instanceof DateTimeDomainType) {
            return new DateCell();
        }
        else if (representationType instanceof CodeDomainType) {
            return new UniqueChoiceCell();
        }
        else {
            throw new ConversionException(
                    "Unable to identify cell type in DDI GridResponseDomainInMixed object " +
                            "with response domain of type "+representationType.getClass()+".");
        }
    }

    /** <p>In "Insee" DDI:</p>
     * <ul>
     *   <li>collected variables are characterized by having a "question reference"</li>
     *   <li>calculated variables are characterized by having a "processing instruction reference"
     *   in their "variable representation"</li>
     *   <li>external variables have no specific characteristics so these are the remaining cases. </li>
     * </ul>
     * */
    public static EnoObject instantiateFrom(VariableType variableType) {
        if (! variableType.getQuestionReferenceList().isEmpty())
            return new CollectedVariable();
        if (variableType.getVariableRepresentation() != null &&
                variableType.getVariableRepresentation().getProcessingInstructionReference() != null)
            return new CalculatedVariable();
        return new ExternalVariable();
    }

}
