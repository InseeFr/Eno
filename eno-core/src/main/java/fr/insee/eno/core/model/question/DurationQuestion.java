package fr.insee.eno.core.model.question;

import datacollection33.DateTimeDomainType;
import datacollection33.DomainReferenceType;
import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.reference.DDIIndex;
import lombok.Getter;
import lombok.Setter;
import reusable33.AbstractIdentifiableType;
import reusable33.ManagedDateTimeRepresentationType;
import reusable33.RangeType;

import java.util.Optional;

/**
 * Duration question.
 * In DDI, it is very similar to date questions.
 * It is not supported in Lunatic yet. */
@Getter
@Setter
public class DurationQuestion extends SingleResponseQuestion {

    private enum DurationRangeField { MIN, MAX }

    /**
     * Minimum duration value allowed.
     */
    @DDI(contextType = QuestionItemType.class, field = "T(fr.insee.eno.core.model.question.DateQuestion).convertMinValue(#this, #index)")
    private String minValue;

    /**
     * Maximum duration value allowed.
     */
    @DDI(contextType = QuestionItemType.class, field = "T(fr.insee.eno.core.model.question.DateQuestion).convertMaxValue(#this, #index)")
    private String maxValue;

    /**
     * Duration format.
     */
    @DDI(contextType = QuestionItemType.class, field = "T(fr.insee.eno.core.model.question.DateQuestion).convertFormatValue(#this, #index)")
    private String format;

    /**
     * retrieve min property from DDI for this date question
     * @param questionItemType question item
     * @param ddiIndex DDI index
     * @return value for the min property
     */
    public static String convertMinValue(QuestionItemType questionItemType, DDIIndex ddiIndex) {
        return convertValue(questionItemType, ddiIndex, DurationRangeField.MIN);
    }

    /**
     * retrieve max property from DDI for this date question
     * @param questionItemType question item
     * @param ddiIndex DDI index
     * @return value for the max property
     */
    public static String convertMaxValue(QuestionItemType questionItemType, DDIIndex ddiIndex) {
        return convertValue(questionItemType, ddiIndex, DurationRangeField.MAX);
    }

    /**
     * retrieve min/max property from DDI for this date question
     * @param questionItemType question item
     * @param ddiIndex DDI index
     * @param field date range field
     * @return property value
     */
    public static String convertValue(QuestionItemType questionItemType, DDIIndex ddiIndex, DurationRangeField field) {
        // First we try to get the property directly from the response domain
        DateTimeDomainType domainType = (DateTimeDomainType) questionItemType.getResponseDomain();
        if(domainType != null && domainType.getRangeArray() != null && domainType.getRangeArray().length > 0) {
            RangeType rangeDate = domainType.getRangeArray(0);
            return getValue(rangeDate, field);
        }

        // if no response domain, there should be a representation type with default values
        Optional<ManagedDateTimeRepresentationType> dateTimeRepresentation = getManagedDateTimeRepresentation(ddiIndex, questionItemType);
        if(dateTimeRepresentation.isEmpty() || dateTimeRepresentation.get().getRangeList() == null || dateTimeRepresentation.get().getRangeList().isEmpty()) {
            return null;
        }

        RangeType rangeDate = dateTimeRepresentation.get().getRangeArray(0);
        return getValue(rangeDate, field);
    }

    /**
     * retrieve format property from DDI for this date question
     * @param questionItemType question item
     * @param ddiIndex DDI index
     * @return value for the format property
     */
    public static String convertFormatValue(QuestionItemType questionItemType, DDIIndex ddiIndex) {
        // First we try to get the property directly from the response domain
        DateTimeDomainType domainType = (DateTimeDomainType) questionItemType.getResponseDomain();
        if(domainType != null && domainType.getDateFieldFormat() != null) {
            return domainType.getDateFieldFormat().getStringValue();
        }

        // if no response domain, there should be a representation type with default values
        Optional<ManagedDateTimeRepresentationType> dateTimeRepresentation = getManagedDateTimeRepresentation(ddiIndex, questionItemType);
        if(dateTimeRepresentation.isEmpty() || dateTimeRepresentation.get().getDateFieldFormat() == null) {
            return null;
        }

        return dateTimeRepresentation.get().getDateFieldFormat().getStringValue();
    }

    /**
     * return a date time representation for the question item used as parameter
     * @param ddiIndex DDI index
     * @param questionItemType question item
     * @return a date time representation if exists
     */
    private static Optional<ManagedDateTimeRepresentationType> getManagedDateTimeRepresentation(DDIIndex ddiIndex, QuestionItemType questionItemType) {
        DomainReferenceType referenceType = questionItemType.getResponseDomainReference();
        if(referenceType == null || referenceType.getIDList() == null || referenceType.getIDList().isEmpty()) {
            return Optional.empty();
        }

        AbstractIdentifiableType ddiObject = ddiIndex.get(referenceType.getIDArray(0).getStringValue());
        ManagedDateTimeRepresentationType dateTimeRepresentation = (ManagedDateTimeRepresentationType) ddiObject;
        return Optional.of(dateTimeRepresentation);
    }

    /**
     *
     * @param rangeDate min/max range for duration
     * @param field min/max field we need to retrieve the value
     * @return value for the field defined as parameter
     */
    private static String getValue(RangeType rangeDate, DurationRangeField field) {
        if(field.equals(DurationRangeField.MIN) && rangeDate.getMinimumValue() != null) {
            return rangeDate.getMinimumValue().getStringValue();
        }

        if(field.equals(DurationRangeField.MAX) && rangeDate.getMaximumValue() != null) {
            return rangeDate.getMaximumValue().getStringValue();
        }
        return null;
    }
}
