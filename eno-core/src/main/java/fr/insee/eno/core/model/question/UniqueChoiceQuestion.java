package fr.insee.eno.core.model.question;

import fr.insee.ddi.lifecycle33.datacollection.CodeDomainType;
import fr.insee.ddi.lifecycle33.datacollection.QuestionItemType;
import fr.insee.ddi.lifecycle33.datacollection.ResponseDomainInMixedType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.eno.core.model.navigation.Binding;
import fr.insee.eno.core.model.response.DetailResponse;
import fr.insee.eno.core.model.response.Response;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.CheckboxOne;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Dropdown;
import fr.insee.lunatic.model.flat.Radio;
import fr.insee.pogues.model.QuestionType;
import fr.insee.pogues.model.ResponseType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Eno model class to represent unique choice questions (UCQ).
 * In DDI, it corresponds to a QuestionItem.
 * In Lunatic, it corresponds to the InputNumber component.
 */
@Getter
@Setter
@Slf4j
@Context(format = Format.POGUES, type = QuestionType.class)
@Context(format = Format.DDI, type = QuestionItemType.class)
@Context(format = Format.LUNATIC, type = {CheckboxOne.class, Radio.class, Dropdown.class})
public class UniqueChoiceQuestion extends SingleResponseQuestion {

    /**
     * DDI metadata to mark the content of a GenericOutputFormat as a unique choice question display format.
     * (Unused yet but will be useful in DDI out mapping).
     */
    public static final String DDI_UCQ_VOCABULARY_ID = "INSEE-GOF-CV";

    /** DDI value for radio display format. */
    public static final String DDI_UCQ_RADIO_OUTPUT_FORMAT = "radio-button";
    /** DDI value for checkbox display format. */
    public static final String DDI_UCQ_CHECKBOX_OUTPUT_FORMAT = "checkbox";
    /** DDI value for dropdown display format. */
    public static final String DDI_UCQ_DROPDOWN_OUTPUT_FORMAT = "drop-down-list";

    /**
     * Enum for unique choice question display format.
     * A unique choice question can be displayed as radio buttons, checkboxes (discouraged since checkboxes should be
     * only used for multiple choice questions), or dropdown.
     */
    public enum DisplayFormat {RADIO, CHECKBOX, DROPDOWN}

    /**
     * Property used to convert to unique choice question to the right Lunatic component.
     * In DDI, there are conventional values in the "generic output format" property.
     * In Lunatic, it is used by the converter to create the right object, and to set the component type property. */
    @Pogues("T(fr.insee.eno.core.model.question.UniqueChoiceQuestion).convertPoguesVisualizationHint(" +
            "#this.getResponse().getFirst())")
    @DDI("T(fr.insee.eno.core.model.question.UniqueChoiceQuestion).convertDDIOutputFormat(#this)")
    @Lunatic("setComponentType(" +
            "T(fr.insee.eno.core.model.question.UniqueChoiceQuestion).convertDisplayFormatToLunatic(#param))")
    DisplayFormat displayFormat;

    /** Reference to the code list that contain the modalities of the question. */
    @Pogues("getResponse().getFirst().getCodeListReference()")
    @DDI("T(fr.insee.eno.core.model.question.UniqueChoiceQuestion).mapDDICodeListReference(#this)")
    String codeListReference;

    /**
     * List of modalities of the unique choice question.
     * In Pogues and DDI, these are inserted here through a processing.
     */
    @Lunatic("getOptions()")
    List<CodeItem> codeItems = new ArrayList<>();

    /** List of DDI bindings that contain the links between detail response name and label. */
    @DDI("getBindingList()")
    List<Binding> ddiBindings = new ArrayList<>();

    /** List of responses defined in the DDI question item.
     * Used in the processing that inserts detail response names. */
    @DDI("getOutParameterList()")
    List<Response> ddiResponses = new ArrayList<>();

    /** Detail responses for modalities that have a "please specify" field.
     * In DDI, these are mapped at question level.
     * In Lunatic, they are inserted in option through a processing. */
    @Pogues("getClarificationQuestion()")
    @DDI("T(fr.insee.eno.core.model.question.UniqueChoiceQuestion).mapDetailResponses(#this)")
    List<DetailResponse> detailResponses = new ArrayList<>();

    public static DisplayFormat convertPoguesVisualizationHint(ResponseType poguesResponse) {
        return switch (poguesResponse.getDatatype().getVisualizationHint()) {
            case CHECKBOX -> DisplayFormat.CHECKBOX;
            case DROPDOWN -> DisplayFormat.DROPDOWN;
            case RADIO -> DisplayFormat.RADIO;
            case SUGGESTER -> throw new ConversionException(
                    "Question should have been converted to a suggester question and not a unique choice question.");
        };
    }

    /**
     * From DDI question item (that correspond to a unique choice question),
     * return the eno model display format for the unique choice question.
     * @param questionItemType A DDI question item that contains a response domain > generic output format.
     * @return A value of DisplayFormat.
     */
    public static DisplayFormat convertDDIOutputFormat(QuestionItemType questionItemType) {
        String ddiOutputFormat = getDDICodeDomain(questionItemType).getGenericOutputFormat().getStringValue();
        Optional<DisplayFormat> convertedDisplayFormat = ddiValueToDisplayFormat(ddiOutputFormat);
        if (convertedDisplayFormat.isEmpty())
            throw new MappingException(String.format(
                    "Invalid output format '%s' found in DDI question item '%s'.",
                    ddiOutputFormat, questionItemType.getIDArray(0).getStringValue()));
        return convertedDisplayFormat.get();
    }

    /**
     * Converts the DDI output format to an Eno-model display format.
     * @param ddiOutputFormat Output format value in DDI.
     * @return Eno display format corresponding to given DDI output format given.
     */
    public static Optional<DisplayFormat> ddiValueToDisplayFormat(String ddiOutputFormat) {
        return switch (ddiOutputFormat) {
            case DDI_UCQ_RADIO_OUTPUT_FORMAT -> Optional.of(DisplayFormat.RADIO);
            case DDI_UCQ_CHECKBOX_OUTPUT_FORMAT -> Optional.of(DisplayFormat.CHECKBOX);
            case DDI_UCQ_DROPDOWN_OUTPUT_FORMAT -> Optional.of(DisplayFormat.DROPDOWN);
            default -> Optional.empty();
        };
    }

    /**
     * Uses display format given to return corresponding Lunatic component type.
     * @param displayFormat A DisplayFormat value.
     * @return Lunatic component type value.
     */
    public static ComponentTypeEnum convertDisplayFormatToLunatic(DisplayFormat displayFormat) {
        return switch (displayFormat) {
            case RADIO -> ComponentTypeEnum.RADIO;
            case DROPDOWN -> ComponentTypeEnum.DROPDOWN;
            case CHECKBOX -> ComponentTypeEnum.CHECKBOX_ONE;
        };
    }

    public static String mapDDICodeListReference(QuestionItemType questionItemType) {
        return getDDICodeDomain(questionItemType).getCodeListReference().getIDArray(0).getStringValue();
    }

    public static List<ResponseDomainInMixedType> mapDetailResponses(QuestionItemType questionItemType) {
        if (questionItemType.getStructuredMixedResponseDomain() == null)
            return new ArrayList<>();
        return questionItemType.getStructuredMixedResponseDomain().getResponseDomainInMixedList().stream()
                .filter(responseDomainInMixedType ->
                        !(responseDomainInMixedType.getResponseDomain() instanceof CodeDomainType))
                .toList();
    }

    /**
     * Gets the code response domain of the DDI question item. In Insee modeling, a DDI unique choice question
     * is expected to have exactly 1 code response domain.
     * @param ddiUniqueChoiceQuestion A DDI question item that corresponds to a unique choice question.
     * @return DDI code response domain object of the question.
     */
    private static CodeDomainType getDDICodeDomain(QuestionItemType ddiUniqueChoiceQuestion) {
        // Default case:
        if (ddiUniqueChoiceQuestion.getResponseDomain() != null) {
            return (CodeDomainType) ddiUniqueChoiceQuestion.getResponseDomain();
        }
        // Case when some modalities have a detail ("please, specify") response:
        if (ddiUniqueChoiceQuestion.getStructuredMixedResponseDomain() != null) {
            List<CodeDomainType> searchedCodeDomain = ddiUniqueChoiceQuestion.getStructuredMixedResponseDomain()
                    .getResponseDomainInMixedList().stream()
                    .map(ResponseDomainInMixedType::getResponseDomain)
                    .filter(CodeDomainType.class::isInstance)
                    .map(CodeDomainType.class::cast)
                    .toList();
            if (searchedCodeDomain.size() != 1)
                throw new MappingException(String.format(
                        "DDI unique choice question '%s' has no or more than 1 code response domain.",
                        ddiUniqueChoiceQuestion.getIDArray(0).getStringValue()));
            return searchedCodeDomain.getFirst();
        }
        //
        throw new MappingException(String.format(
                "Cannot find code response domain in DDI unique choice question '%s'.",
                ddiUniqueChoiceQuestion.getIDArray(0).getStringValue()));
    }

}
