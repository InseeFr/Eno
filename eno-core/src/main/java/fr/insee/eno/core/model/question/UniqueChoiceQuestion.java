package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.CheckboxOne;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Dropdown;
import fr.insee.lunatic.model.flat.Radio;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Eno model class to represent unique choice questions (UCQ).
 * In DDI, it corresponds to a QuestionItem.
 * In Lunatic, it corresponds to the InputNumber component.
 */
@Getter
@Setter
@Slf4j
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
    @DDI("T(fr.insee.eno.core.model.question.UniqueChoiceQuestion).convertDDIOutputFormat(#this)")
    @Lunatic("setComponentType(" +
            "T(fr.insee.eno.core.model.question.UniqueChoiceQuestion).convertDisplayFormatToLunatic(#param))")
    DisplayFormat displayFormat;

    /**
     * List of modalities of the unique choice question.
     */
    @DDI("#index.get(#this.getResponseDomain().getCodeListReference().getIDArray(0).getStringValue()).getCodeList()")
    @Lunatic("getOptions()")
    List<CodeItem> codeList = new ArrayList<>();
    //TODO: map this only once maybe (currently a list of object is created for each unique choice question)

    /**
     * From DDI question item (that correspond to a unique choice question),
     * return the eno model display format for the unique choice question.
     * @param questionItemType A DDI question item that contains a response domain > generic output format.
     * @return A value of DisplayFormat.
     */
    public static DisplayFormat convertDDIOutputFormat(QuestionItemType questionItemType) {
        String ddiOutputFormat = questionItemType.getResponseDomain().getGenericOutputFormat().getStringValue();
        return switch (ddiOutputFormat) {
            case DDI_UCQ_RADIO_OUTPUT_FORMAT -> DisplayFormat.RADIO;
            case DDI_UCQ_CHECKBOX_OUTPUT_FORMAT -> DisplayFormat.CHECKBOX;
            case DDI_UCQ_DROPDOWN_OUTPUT_FORMAT -> DisplayFormat.DROPDOWN;
            default -> {
                String questionId = questionItemType.getIDArray(0).getStringValue();
                log.warn("Unknown output format '"+ddiOutputFormat+"' found in DDI question item '"+questionId+"'.");
                yield null;
            }
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

}
