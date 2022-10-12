package fr.insee.eno.core.model;

import datacollection33.IfThenElseType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Format;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.ConditionFilterType;
import fr.insee.lunatic.model.flat.ControlType;
import fr.insee.lunatic.model.flat.LabelType;
import fr.insee.lunatic.model.flat.VariableType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reusable33.CommandType;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** Class that could be used to refactor calculated expressions' mapping.
 * Unused yet since the Lunatic mapper doesn't allow to dig into a complex object without creating a new instance. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Context(format = Format.DDI, type = CommandType.class)
@Context(format = Format.LUNATIC, type = LabelType.class)
public class CalculatedExpression extends EnoObject {

    public static CalculatedExpression defaultExpression() {
        CalculatedExpression res = new CalculatedExpression();
        res.setValue("true");
        return res;
    }

    /** Expression. */
    @DDI(contextType = IfThenElseType.class, field = "getCommandContent()")
    @Lunatic(contextType = LabelType.class, field = "setValue(#param)")
    private String value;

    /** In DDI, the expression contains variable references instead of variables names.
     * This list contains the references of these variables. */
    @DDI(contextType = CommandType.class, field = "getInParameterList()")
    private List<BindingReference> bindingReferences = new ArrayList<>();

}
