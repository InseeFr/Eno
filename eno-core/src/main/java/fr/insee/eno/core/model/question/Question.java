package fr.insee.eno.core.model.question;

import datacollection33.QuestionGridType;
import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.lunatic.model.flat.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Question extends EnoObject {

    @DDI(contextType = {QuestionItemType.class, QuestionGridType.class}, field = "getIDArray(0).getStringValue()")
    @Lunatic(contextType = {Input.class, Textarea.class, InputNumber.class, CheckboxBoolean.class, Datepicker.class, CheckboxOne.class, Radio.class, Dropdown.class, CheckboxGroup.class, Table.class},
            field = "setId(#param)")
    String id;

    @DDI(contextType = {QuestionItemType.class, QuestionGridType.class},
            field = "getQuestionTextArray(0).getTextContentArray(0).getText().getStringValue()")
    @Lunatic(contextType = {Input.class, Textarea.class, InputNumber.class, CheckboxBoolean.class, Datepicker.class, CheckboxOne.class, Radio.class, Dropdown.class, CheckboxGroup.class, Table.class},
            field = "setLabel(#param)")
    String label;

}
