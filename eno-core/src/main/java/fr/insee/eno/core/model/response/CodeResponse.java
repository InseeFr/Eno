package fr.insee.eno.core.model.response;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.ResponsesCheckboxGroup;
import lombok.Getter;
import lombok.Setter;
import reusable33.ParameterType;

@Getter
@Setter
@Context(format = Format.DDI, type = ParameterType.class)
@Context(format = Format.LUNATIC, type = ResponsesCheckboxGroup.class)
public class CodeResponse extends EnoIdentifiableObject {

    @DDI("#index.get(#index.get(#index.getParent(#this.getIDArray(0).getStringValue())" +
            ".getGridDimensionArray(0).getCodeDomain().getCodeListReference().getIDArray(0).getStringValue())" +
            ".getCodeArray(#listIndex).getCategoryReference().getIDArray(0).getStringValue())" +
            ".getLabelArray(0)") //TODO: >_< see todo in DDIMapper....
    @Lunatic("setLabel(#param)")
    private Label label;

    @DDI("#this")
    @Lunatic("setResponse(#param)")
    Response response;

}
