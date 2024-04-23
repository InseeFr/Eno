package fr.insee.eno.core.converter;

import datacollection33.*;
import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.question.*;
import fr.insee.eno.core.reference.DDIIndex;
import lombok.extern.slf4j.Slf4j;
import reusable33.*;

import java.util.Set;

@Slf4j
public class DDIQuestionItemConversion {

    private static final String DDI_PAIRWISE_KEY = "UIComponent";
    private static final String DDI_PAIRWISE_VALUE = "HouseholdPairing";
    private static final Set<String> DDI_DATE_TYPE_CODE = Set.of("date", "gYearMonth", "gYear");
    private static final Set<String> DDI_DURATION_TYPE_CODE = Set.of("duration");

    static final String DDI_SUGGESTER_OUTPUT_FORMAT = "suggester";

    private DDIQuestionItemConversion() {}

    /**
     * Instantiate an Eno object that corresponds to the given DDI question item.
     * @param questionItemType DDI question item.
     * @param ddiIndex DDI index used for the date/duration question cases.
     * @return Eno object corresponding to the given DDI question item.
     */
    static EnoObject instantiateFrom(QuestionItemType questionItemType, DDIIndex ddiIndex) {
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
            if (isPairwiseQuestion(questionItemType))
                return new PairwiseQuestion();
            if (isSuggesterQuestion(questionItemType))
                return new SuggesterQuestion();
            return new UniqueChoiceQuestion();
        }

        if (hasMixedResponseDomain(questionItemType))
            return new UniqueChoiceQuestion();

        throw new ConversionException(
                "Unable to identify question type in DDI question item " +
                        questionItemType.getIDArray(0).getStringValue());

    }

    private static EnoObject convertDateTimeQuestion(DateTimeDomainType dateTimeDomainType) {
        String dateTypeCode = dateTimeDomainType.getDateTypeCode().getStringValue();
        if (DDI_DATE_TYPE_CODE.contains(dateTypeCode))
            return new DateQuestion();
        if (DDI_DURATION_TYPE_CODE.contains(dateTypeCode))
            return new DurationQuestion();
        // If none match, thrown an exception
        throw new ConversionException("Unknown date type code: "+dateTypeCode);
    }

    private static EnoObject convertDateTimeQuestion(DomainReferenceType referenceType, DDIIndex ddiIndex) {
        if (ddiIndex == null)
            throw new IllegalArgumentException(
                    "Cannot convert date/time question with a reference domain without a DDI index.");

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

    private static boolean isPairwiseQuestion(QuestionItemType ddiQuestionItem) {
        if (ddiQuestionItem.getUserAttributePairList().isEmpty())
            return false;
        StandardKeyValuePairType userAttributePair = ddiQuestionItem.getUserAttributePairArray(0);
        String attributeKey = userAttributePair.getAttributeKey().getStringValue();
        String attributeValue = userAttributePair.getAttributeValue().getStringValue();
        if (!DDI_PAIRWISE_KEY.equals(attributeKey)) {
            log.warn(String.format(
                    "Attribute pair list found in question item '%s', but key is equal to '%s' (should be '%s')",
                    ddiQuestionItem.getIDArray(0).getStringValue(), attributeKey, DDI_PAIRWISE_KEY));
            return false;
        }
        if (!DDI_PAIRWISE_VALUE.equals(attributeValue)) {
            log.warn(String.format(
                    "Attribute pair list found in question item '%s', but value is equal to '%s' (should be '%s')",
                    ddiQuestionItem.getIDArray(0).getStringValue(), attributeValue, DDI_PAIRWISE_VALUE));
            return false;
        }
        return true;
    }

    private static boolean isSuggesterQuestion(QuestionItemType ddiQuestionItem) {
        String ddiOutputFormat = ddiQuestionItem.getResponseDomain().getGenericOutputFormat().getStringValue();
        return DDI_SUGGESTER_OUTPUT_FORMAT.equals(ddiOutputFormat);
    }

    /**
     * In DDI, a unique choice question normally has a "CodeDomain" response domain.
     * However, if the unique choice question has modalities with a "please, specify", the response domain becomes
     * a mixed response domain containing the code domain, and additional text domains for modalities that have this.
     * This method checks the existence of such a mixed response domain in the question item object.
     * @param ddiQuestionItem A DDI question item.
     * @return True if the response domain is a mixed response domain.
     */
    private static boolean hasMixedResponseDomain(QuestionItemType ddiQuestionItem) {
        return ddiQuestionItem.getStructuredMixedResponseDomain() != null;
    }

}
