package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.lunatic.model.flat.CheckboxGroup;
import fr.insee.lunatic.model.flat.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class MultipleResponseQuestion extends EnoObject {

    @DDI(contextType = QuestionItemType.class, field = "getIDArray(0).getStringValue()")
    @Lunatic(contextType = {CheckboxGroup.class, Table.class},
            field = "setId(#param)")
    String id;

    @DDI(contextType = QuestionItemType.class,
            field = "getQuestionGridNameArray(0).getStringArray(0).getStringValue()")
    String name;

    @DDI(contextType = QuestionItemType.class,
            field = "getQuestionTextArray(0).getTextContentArray(0).getText().getStringValue()") //TODO: unsafe superclass method call
    @Lunatic(contextType = {CheckboxGroup.class, Table.class},
            field = "setLabel(#param)")
    String label;

    /*
    @DDI(contextType = QuestionItemType.class, field = "getResponseCardinality()?.getMinimumResponses()?.intValue()")
    @Lunatic(contextType = {CheckboxGroup.class, Table.class},
            field = "setMandatory(#param > 0)")
    int minResponse;

    @DDI(contextType = QuestionItemType.class, field = "getResponseCardinality()?.getMaximumResponses()?.intValue()")
    int maxResponse;
    */

}
