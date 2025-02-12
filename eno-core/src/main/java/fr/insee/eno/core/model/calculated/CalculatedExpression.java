package fr.insee.eno.core.model.calculated;

import fr.insee.ddi.lifecycle33.reusable.CommandType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.LabelType;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import fr.insee.pogues.model.ExpressionType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** Class used to for the mapping of calculated expressions that can be found in objects like
 * calculated variables, controls, filters. */
@Getter
@Setter
@Context(format = Format.POGUES, type = ExpressionType.class)
@Context(format = Format.DDI, type = CommandType.class)
@Context(format = Format.LUNATIC, type = LabelType.class)
public class CalculatedExpression extends EnoObject {

    public static CalculatedExpression defaultExpression() {
        CalculatedExpression res = new CalculatedExpression();
        res.setValue("true");
        res.setType(LabelTypeEnum.VTL.value());
        return res;
    }

    /** The method "extractBindingReferences" allows, from the expression of a "CalculatedVariable",
     * to construct the associated "BindingReferences". Note that the identifier (a concept derived from DDI)
     * is null for each reference. */
    public static Set<BindingReference> extractBindingReferences(String expression) {
        Set<BindingReference> references = new HashSet<>();
        Pattern pattern = Pattern.compile("\\$(\\w+)\\$");
        Matcher matcher = pattern.matcher(expression);

        while (matcher.find()) {
            String variableName = matcher.group(1);
            references.add(new BindingReference(null, variableName));
        }

        return references;
    }

    /** The removeSurroundingDollarSigns method removes the "$" symbols surrounding the reference to
     * a variable in the expression of the "CalculatedVariable". */
    public static String removeSurroundingDollarSigns(String expression) {
        return expression.replaceAll("\\$(\\w+)\\$", "$1");
    }

    /**
     * Expression.
     */
    @Pogues("T(fr.insee.eno.core.model.calculated.CalculatedExpression).removeSurroundingDollarSigns(" +
            "getValue())")
    @DDI("getCommandContent()")
    @Lunatic("setValue(#param)")
    private String value;

    /**
     * For now, Lunatic type in label objects does not come from metadata, but is hardcoded here in Eno.
     * See labels documentation.
     */
    @Lunatic("setType(T(fr.insee.lunatic.model.flat.LabelTypeEnum).fromValue(#param))")
    String type = LabelTypeEnum.VTL.value();

    /**
     * In DDI, the expression contains variable references instead of variables names.
     * This list contains the references of these variables.
     */
    @Pogues("T(fr.insee.eno.core.model.calculated.CalculatedExpression).extractBindingReferences(" +
            "getValue())")
    @DDI("getInParameterList()")
    private Set<BindingReference> bindingReferences = new HashSet<>();
}
