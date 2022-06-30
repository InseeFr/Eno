package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.Instruction;
import fr.insee.eno.core.model.Response;
import fr.insee.lunatic.model.flat.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@ToString(of="name")
@Getter
@Setter
public abstract class SingleResponseQuestion {

    @DDI(contextType = QuestionItemType.class, field = "getIDArray(0).getStringValue()")
    @Lunatic(contextType = {Input.class, Textarea.class, InputNumber.class, CheckboxBoolean.class, Datepicker.class, CheckboxOne.class, Radio.class, Dropdown.class},
            field = "setId(#param)")
    String id;

    @DDI(contextType = QuestionItemType.class,
            field = "getQuestionItemNameArray(0).getStringArray(0).getStringValue()")
    String name;

    @DDI(contextType = QuestionItemType.class,
            field = "getQuestionTextArray(0).getTextContentArray(0).getText().getStringValue()") //TODO: (warning) unsafe subclass method call
    @Lunatic(contextType = {Input.class, Textarea.class, InputNumber.class, CheckboxBoolean.class, Datepicker.class, CheckboxOne.class, Radio.class, Dropdown.class},
            field = "setLabel(#param)")
    String label;

    @DDI(contextType = QuestionItemType.class,
            field = "getInterviewerInstructionReferenceList().![#index.get(#this.getIDArray(0).getStringValue())]")
    @Lunatic(contextType = {Input.class, Textarea.class, InputNumber.class, CheckboxBoolean.class, Datepicker.class, CheckboxOne.class, Radio.class, Dropdown.class},
            field = "getDeclarations()")
    List<Instruction> instructions = new ArrayList<>();

    /** List of variable names on which the question is dependent. */ //TODO: where is this info in DDI?
    List<String> bindingDependencies = new ArrayList<>();

    @DDI(contextType = QuestionItemType.class, field = "getOutParameterArray(0)")
    @Lunatic(contextType = {Input.class, Textarea.class, InputNumber.class, CheckboxBoolean.class, Datepicker.class, CheckboxOne.class, Radio.class, Dropdown.class},
            field = "setResponse(#param)")
    Response response;

    @DDI(contextType = QuestionItemType.class,
            field = "getResponseCardinality()?.getMinimumResponses() != null ? " +
                    "getResponseCardinality().getMinimumResponses().intValue() > 0 : false")
    @Lunatic(contextType = {Input.class, Textarea.class, InputNumber.class, CheckboxBoolean.class, Datepicker.class, CheckboxOne.class, Radio.class, Dropdown.class},
            field = "setMandatory(#param)")
    boolean mandatory;

}
