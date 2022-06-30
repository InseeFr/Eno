package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import lombok.*;

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
                    "T(fr.insee.eno.core.model.question.UniqueChoiceQuestion.DisplayFormat).DROPDOWN : null")
    DisplayFormat displayFormat;

}
