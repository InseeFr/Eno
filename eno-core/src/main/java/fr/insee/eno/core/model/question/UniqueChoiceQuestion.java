package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.lunatic.model.flat.CheckboxOne;
import fr.insee.lunatic.model.flat.Dropdown;
import fr.insee.lunatic.model.flat.Radio;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/*@NoArgsConstructor
@AllArgsConstructor
@Builder*/
@Getter
@Setter
@Slf4j
public class UniqueChoiceQuestion extends SingleResponseQuestion {

    public static final String DDI_UCQ_VOCABULARY_ID = "INSEE-GOF-CV";
    // (Unused yet but will be useful in DDI out mapping)

    public static final String DDI_UCQ_RADIO_OUTPUT_FORMAT = "radio-button";
    public static final String DDI_UCQ_CHECKBOX_OUTPUT_FORMAT = "checkbox";
    public static final String DDI_UCQ_DROPDOWN_OUTPUT_FORMAT = "drop-down-list";

    public enum DisplayFormat {RADIO, CHECKBOX, DROPDOWN}

    @DDI(contextType = QuestionItemType.class,
            field = "T(fr.insee.eno.core.model.question.UniqueChoiceQuestion).convertDDIOutputFormat(#this)")
    DisplayFormat displayFormat;

    @DDI(contextType = QuestionItemType.class,
            field = "#index.get(#this.getResponseDomain().getCodeListReference().getIDArray(0).getStringValue()).getCodeList()") //TODO: map this only once
    @Lunatic(contextType = {CheckboxOne.class, Radio.class, Dropdown.class}, field = "getOptions()")
    List<CodeItem> codeList = new ArrayList<>();

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

}
