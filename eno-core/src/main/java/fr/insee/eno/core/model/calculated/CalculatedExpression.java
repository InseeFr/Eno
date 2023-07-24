package fr.insee.eno.core.model.calculated;

import fr.insee.eno.core.Constant;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.LabelType;
import lombok.Getter;
import lombok.Setter;
import reusable33.CommandType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** Class used to for the mapping of calculated expressions that can be found in objects like
 * calculated variables, controls, filters. */
@Getter
@Setter
@Context(format = Format.DDI, type = CommandType.class)
@Context(format = Format.LUNATIC, type = LabelType.class)
public class CalculatedExpression extends EnoObject {

    public static CalculatedExpression defaultExpression() {
        CalculatedExpression res = new CalculatedExpression();
        res.setValue("true");
        res.setType(Constant.LUNATIC_LABEL_VTL);
        return res;
    }

    /** Expression. */
    @DDI("getCommandContent()")
    @Lunatic("setValue(#param)")
    private String value;

    /** For now, Lunatic type in label objects does not come from metadata, but is hardcoded here in Eno.
     * See labels documentation. */
    @Lunatic("setType(#param)")
    String type = Constant.LUNATIC_LABEL_VTL;

    /** In DDI, the expression contains variable references instead of variables names.
     * This list contains the references of these variables. */
    @DDI("getInParameterList()")
    private Set<BindingReference> bindingReferences = new HashSet<>();

}
