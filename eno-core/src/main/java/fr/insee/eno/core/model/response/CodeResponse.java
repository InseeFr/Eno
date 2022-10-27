package fr.insee.eno.core.model.response;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.label.Label;
import fr.insee.lunatic.model.flat.ResponsesCheckboxGroup;
import lombok.Getter;
import lombok.Setter;
import reusable33.ParameterType;

@Getter
@Setter
public class CodeResponse extends EnoIdentifiableObject {

    @DDI(contextType = ParameterType.class,
            field = "#index.get(#index.get(#index.getParent(#this.getIDArray(0).getStringValue())" +
                    ".getGridDimensionArray(0).getCodeDomain().getCodeListReference().getIDArray(0).getStringValue())" +
                    ".getCodeArray(#listIndex).getCategoryReference().getIDArray(0).getStringValue())" +
                    ".getLabelArray(0)") //TODO: >_< see todo in DDIMapper....
    @Lunatic(contextType = ResponsesCheckboxGroup.class, field = "setLabel(#param)")
    private Label label;

    @DDI(contextType = ParameterType.class, field = "#this")
    @Lunatic(contextType = ResponsesCheckboxGroup.class, field = "setResponse(#param)")
    Response response;

}
