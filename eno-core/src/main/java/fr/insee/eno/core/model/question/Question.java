package fr.insee.eno.core.model.question;

import datacollection33.QuestionGridType;
import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.*;
import fr.insee.eno.core.model.declaration.Declaration;
import fr.insee.eno.core.model.declaration.Instruction;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.lunatic.model.flat.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class Question extends EnoIdentifiableObject implements EnoComponent {

    /** Attribute is defined here to factor toString methods,
     * but DDI mapping is done in subclasses since DDI classes are different. */
    String name;

    @DDI(contextType = {QuestionItemType.class, QuestionGridType.class},
            field = "getQuestionTextArray(0)")
    @Lunatic(contextType = {Input.class, Textarea.class, InputNumber.class, CheckboxBoolean.class, Datepicker.class, CheckboxOne.class, Radio.class, Dropdown.class, CheckboxGroup.class, Table.class},
            field = "setLabel(#param)")
    DynamicLabel label;

    @Lunatic(contextType = {Input.class, Textarea.class, InputNumber.class, CheckboxBoolean.class, Datepicker.class, CheckboxOne.class, Radio.class, Dropdown.class, CheckboxGroup.class, Table.class},
            field = "getDeclarations()")
    private final List<Declaration> declarations = new ArrayList<>();

    @DDI(contextType = {QuestionItemType.class, QuestionGridType.class},
            field = "getInterviewerInstructionReferenceList().![#index.get(#this.getIDArray(0).getStringValue())]")
    @Lunatic(contextType = {Input.class, Textarea.class, InputNumber.class, CheckboxBoolean.class, Datepicker.class, CheckboxOne.class, Radio.class, Dropdown.class, CheckboxGroup.class, Table.class},
            field = "getDeclarations()")
    List<Instruction> instructions = new ArrayList<>();

    @Lunatic(contextType = {Input.class, Textarea.class, InputNumber.class, CheckboxBoolean.class, Datepicker.class, CheckboxOne.class, Radio.class, Dropdown.class, CheckboxGroup.class, Table.class},
            field = "getControls()")
    private final List<Control> controls = new ArrayList<>();

    /** Question filter.
     * In DDI, the filters are mapped in the questionnaire object.
     * If there is a declared filter for this question, it is put here through a 'processing' class.
     * Otherwise, there is a default filter (with expression "true").
     * In Lunatic, a ComponentType object has a ConditionFilter object. */
    @Lunatic(contextType = {Input.class, Textarea.class, InputNumber.class, CheckboxBoolean.class, Datepicker.class, CheckboxOne.class, Radio.class, Dropdown.class, CheckboxGroup.class, Table.class},
            field = "setConditionFilter(#param)")
    private Filter filter = new Filter();

    @Override
    public String toString() {
        return this.getClass() + "[id="+this.getId()+", name="+getName()+"]";
    }

}