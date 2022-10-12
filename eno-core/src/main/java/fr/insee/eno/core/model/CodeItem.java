package fr.insee.eno.core.model;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.label.Label;
import fr.insee.lunatic.model.flat.Options;
import logicalproduct33.CodeType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeItem extends EnoObject {

    @DDI(contextType = CodeType.class,
            field = "#index.get(#this.getCategoryReference().getIDArray(0).getStringValue())" +
                    ".getLabelArray(0)")
    @Lunatic(contextType = Options.class, field = "setLabel(#param)")
    Label label;

    @DDI(contextType = CodeType.class, field = "getValue().getStringValue()")
    @Lunatic(contextType = Options.class, field = "setValue(#param)")
    String value;
}
