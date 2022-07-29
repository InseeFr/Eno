package fr.insee.eno.core.model.question;

import datacollection33.QuestionGridType;
import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.Control;
import fr.insee.eno.core.model.Declaration;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.lunatic.model.flat.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    @Lunatic(contextType = {Input.class, Textarea.class, InputNumber.class, CheckboxBoolean.class, Datepicker.class, CheckboxOne.class, Radio.class, Dropdown.class, CheckboxGroup.class, Table.class},
            field = "getDeclarations()")
    private final List<Declaration> declarations = new ArrayList<>();

    @Lunatic(contextType = {Input.class, Textarea.class, InputNumber.class, CheckboxBoolean.class, Datepicker.class, CheckboxOne.class, Radio.class, Dropdown.class, CheckboxGroup.class, Table.class},
            field = "getControls()")
    private final List<Control> controls = new ArrayList<>();

}
