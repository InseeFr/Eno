package fr.insee.eno.core.model.question;

import datacollection33.DateTimeDomainType;
import datacollection33.DomainReferenceType;
import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.reference.DDIIndex;
import fr.insee.lunatic.model.flat.Datepicker;
import lombok.Getter;
import lombok.Setter;
import reusable33.AbstractIdentifiableType;
import reusable33.ManagedDateTimeRepresentationType;
import reusable33.RangeType;

import java.util.Optional;

/**
 * Eno model class to represent date questions.
 * In DDI, it corresponds to a QuestionItem object.
 * In Lunatic, it corresponds to the Datepicker component.
 */
@Getter
@Setter
public class DateQuestion extends SingleResponseQuestion {

    private enum DateRangeField { MIN, MAX }

    /**
     * Minimum date value allowed.
     */
    @DDI(contextType = QuestionItemType.class, field = "T(fr.insee.eno.core.model.question.DateQuestion).convertMinValue(#this, #index)")
    @Lunatic(contextType = Datepicker.class, field = "setMin(#param)")
    private String minValue;

    /**
     * Maximum date value allowed.
     */
    @DDI(contextType = QuestionItemType.class, field = "T(fr.insee.eno.core.model.question.DateQuestion).convertMaxValue(#this, #index)")
    @Lunatic(contextType = Datepicker.class, field = "setMax(#param)")
    private String maxValue;

    /**
     * Date format.
     * This property is a String in both DDI and Lunatic.
     */
    @DDI(contextType = QuestionItemType.class, field = "T(fr.insee.eno.core.model.question.DateQuestion).convertFormatValue(#this, #index)")
    @Lunatic(contextType = Datepicker.class, field = "setDateFormat(#param)")
    private String format;

    /** Lunatic component type property.
     * This should be inserted by Lunatic-Model serializer later on. */
    @Lunatic(contextType = Datepicker.class,
            field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "DATEPICKER";

    /**
     * retrieve min property from DDI for this date question
     * @param questionItemType question item
     * @param ddiIndex DDI index
     * @return value for the min property
     */
    public static String convertMinValue(QuestionItemType questionItemType, DDIIndex ddiIndex) {
        return convertValue(questionItemType, ddiIndex, DateRangeField.MIN);
    }

    /**
     * retrieve max property from DDI for this date question
     * @param questionItemType question item
     * @param ddiIndex DDI index
     * @return value for the max property
     */
    public static String convertMaxValue(QuestionItemType questionItemType, DDIIndex ddiIndex) {
        return convertValue(questionItemType, ddiIndex, DateRangeField.MAX);
    }

    /**
     * retrieve min/max property from DDI for this date question
     * @param questionItemType question item
     * @param ddiIndex DDI index
     * @param field date range field
     * @return property value
     */
    public static String convertValue(QuestionItemType questionItemType, DDIIndex ddiIndex, DateRangeField field) {
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
     * @param rangeDate min/max range for date
     * @param field min/max field we need to retrieve the value
     * @return value for the field defined as parameter
     */
    private static String getValue(RangeType rangeDate, DateRangeField field) {
        if(field.equals(DateRangeField.MIN) && rangeDate.getMinimumValue() != null) {
            return rangeDate.getMinimumValue().getStringValue();
        }

        if(field.equals(DateRangeField.MAX) && rangeDate.getMaximumValue() != null) {
            return rangeDate.getMaximumValue().getStringValue();
        }
        return null;
    }
}
