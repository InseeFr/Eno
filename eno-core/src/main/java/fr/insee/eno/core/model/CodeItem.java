package fr.insee.eno.core.model;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.Options;
import logicalproduct33.CodeType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeItem {

    @DDI(contextType = CodeType.class,
            field = "#index.get(#this.getCategoryReference().getIDArray(0).getStringValue())" +
                    ".getLabelArray(0).getContentArray(0).getStringValue()")
    @Lunatic(contextType = Options.class, field = "setLabel(#param)")
    String label;

    @DDI(contextType = CodeType.class, field = "getValue().getStringValue()")
    @Lunatic(contextType = Options.class, field = "setValue(#param)")
    String value;
}
