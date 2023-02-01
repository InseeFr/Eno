package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.response.Response;
import fr.insee.lunatic.model.flat.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class SingleResponseQuestion extends Question {

    @DDI(contextType = QuestionItemType.class,
            field = "getQuestionItemNameArray(0).getStringArray(0).getStringValue()")
    String name;

    @DDI(contextType = QuestionItemType.class, field = "getOutParameterArray(0)")
    @Lunatic(contextType = {Input.class, Textarea.class, InputNumber.class, CheckboxBoolean.class, Datepicker.class, CheckboxOne.class, Radio.class, Dropdown.class},
            field = "setResponse(#param)")
    Response response;

    @DDI(contextType = QuestionItemType.class,
            field = "getResponseDomain()?.getResponseCardinality()?.getMinimumResponses() != null ? " +
                    "getResponseDomain().getResponseCardinality().getMinimumResponses().intValue() > 0 : false")
    @Lunatic(contextType = {Input.class, Textarea.class, InputNumber.class, CheckboxBoolean.class, Datepicker.class, CheckboxOne.class, Radio.class, Dropdown.class},
            field = "setMandatory(#param)")
    boolean mandatory;

}
