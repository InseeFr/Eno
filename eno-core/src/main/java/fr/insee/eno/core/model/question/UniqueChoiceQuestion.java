package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.lunatic.model.flat.CheckboxOne;
import fr.insee.lunatic.model.flat.Dropdown;
import fr.insee.lunatic.model.flat.Radio;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/*@NoArgsConstructor
@AllArgsConstructor
@Builder*/
@Getter
@Setter
public class UniqueChoiceQuestion extends SingleResponseQuestion {

    public enum DisplayFormat {RADIO, CHECKBOX, DROPDOWN}

    @DDI(contextType = QuestionItemType.class,
            field = "getResponseDomain().getGenericOutputFormat().getStringValue().equals('radio-button') ? " +
                    "T(fr.insee.eno.core.model.question.UniqueChoiceQuestion.DisplayFormat).RADIO : " +
                    "getResponseDomain().getGenericOutputFormat().getStringValue().equals('checkbox') ? " +
                    "T(fr.insee.eno.core.model.question.UniqueChoiceQuestion.DisplayFormat).CHECKBOX : " +
                    "getResponseDomain().getGenericOutputFormat().getStringValue().equals('drop-down-list') ? " +
                    "T(fr.insee.eno.core.model.question.UniqueChoiceQuestion.DisplayFormat).DROPDOWN : null") //TODO: static method in this class to simplify this field
    DisplayFormat displayFormat;

    @DDI(contextType = QuestionItemType.class,
            field = "#index.get(#this.getResponseDomain().getCodeListReference().getIDArray(0).getStringValue()).getCodeList()") //TODO: map this only once
    @Lunatic(contextType = {CheckboxOne.class, Radio.class, Dropdown.class}, field = "getOptions()")
    List<CodeList.CodeItem> codeList = new ArrayList<>();

}
