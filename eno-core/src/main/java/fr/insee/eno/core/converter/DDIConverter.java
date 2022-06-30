package fr.insee.eno.core.converter;

import datacollection33.*;
import fr.insee.eno.core.model.question.*;
import lombok.extern.slf4j.Slf4j;
import reusable33.RepresentationType;
import reusable33.TextDomainType;

@Slf4j
public class DDIConverter {

    public static final String INSEE_VOCABULARY_ID = "INSEE-GOF-CV";
    public static final String RADIO_OUTPUT_FORMAT = "radio-button";
    public static final String CHECKBOX_OUTPUT_FORMAT = "checkbox";
    public static final String DROPDOWN_OUTPUT_FORMAT = "drop-down-list";
    //TODO: Constants class for these?

    /**
     * Return an Eno instance corresponding to the given DDI object.
     * @return A Eno model object.
     */
    public static Object instantiateFromDDIObject(Object ddiObject) {
        if (ddiObject instanceof QuestionItemType)
            return instantiateFrom((QuestionItemType) ddiObject);
        else if (ddiObject instanceof QuestionGridType)
            return instantiateFrom((QuestionGridType) ddiObject);
        else
            throw new RuntimeException("Eno conversion for DDI type " + ddiObject.getClass() + " not implemented.");
    }

    private static Object instantiateFrom(QuestionItemType questionItemType) {
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
            // Version using annotation in UniqueChoiceQuestion class
            return new UniqueChoiceQuestion();
            // Version outside annotation:
            /*
            String controlledVocabularyID = representationType.getGenericOutputFormat().getControlledVocabularyID();
            if (! controlledVocabularyID.equals(INSEE_VOCABULARY_ID)) {
                log.warn(String.format(
                        "Controlled vocabulary ID not equal to '%s' in DDI question item of ID '%s' (name: '%s')",
                        INSEE_VOCABULARY_ID,
                        questionItemType.getIDArray(0).getStringValue(),
                        questionItemType.getQuestionItemNameArray(0).getStringArray(0).getStringValue()));
            }
            String outputFormat = representationType.getGenericOutputFormat().getStringValue();
            return switch (outputFormat) {
                case RADIO_OUTPUT_FORMAT -> UniqueChoiceQuestion.builder().displayFormat(UniqueChoiceQuestion.DisplayFormat.RADIO).build();
                case CHECKBOX_OUTPUT_FORMAT -> UniqueChoiceQuestion.builder().displayFormat(UniqueChoiceQuestion.DisplayFormat.CHECKBOX).build();
                case DROPDOWN_OUTPUT_FORMAT -> UniqueChoiceQuestion.builder().displayFormat(UniqueChoiceQuestion.DisplayFormat.DROPDOWN).build();
                default -> throw new RuntimeException(
                        "Unable to identify output format in DDI code domain of question item " +
                                questionItemType.getIDArray(0).getStringValue());
            };
            */
        }
        else {
            throw new RuntimeException(
                    "Unable to identify question type in DDI question item " +
                            questionItemType.getIDArray(0).getStringValue());
        }
    }

    private static Object instantiateFrom(QuestionGridType questionGridType) {
        RepresentationType representationType = questionGridType.getStructuredMixedGridResponseDomain()
                .getGridResponseDomainInMixedArray(0) // supposing that it is the same for all modalities
                .getResponseDomain();
        if (representationType instanceof NominalDomainType)
            return new MultipleChoiceQuestion();
        else if (representationType instanceof CodeDomainType)
            return new TableQuestion();
        else
            throw new RuntimeException(
                    "Unable to identify question type in DDI question grid " +
                            questionGridType.getIDArray(0).getStringValue());
    }

}
