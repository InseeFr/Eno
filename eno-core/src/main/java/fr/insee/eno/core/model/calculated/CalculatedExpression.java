package fr.insee.eno.core.model.calculated;

import fr.insee.ddi.lifecycle33.reusable.CommandType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.VariableIndex;
import fr.insee.eno.core.utils.vtl.VtlSyntaxUtils;
import fr.insee.lunatic.model.flat.LabelType;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import fr.insee.pogues.model.ExpressionType;
import fr.insee.pogues.model.TypedValueType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** Class used to for the mapping of calculated expressions that can be found in objects like
 * calculated variables, controls, filters. */
@Getter
@Setter
@Slf4j
@Context(format = Format.POGUES, type = {ExpressionType.class, TypedValueType.class, String.class})
@Context(format = Format.DDI, type = CommandType.class)
@Context(format = Format.LUNATIC, type = LabelType.class)
public class CalculatedExpression extends EnoObject {

    private static final Pattern POGUES_VARIABLE_PATTERN = Pattern.compile("\\$(\\w+)\\$");

    public static CalculatedExpression defaultExpression() {
        CalculatedExpression res = new CalculatedExpression();
        res.setValue(VtlSyntaxUtils.VTL_TRUE);
        res.setType(LabelTypeEnum.VTL.value());
        return res;
    }

    /**
     * Expression.
     */
    @Pogues("#this instanceof T(java.lang.String) ? " +
            "T(fr.insee.eno.core.model.calculated.CalculatedExpression).removeSurroundingDollarSigns(#this) : " +
            "T(fr.insee.eno.core.model.calculated.CalculatedExpression).removeSurroundingDollarSigns(getValue())")
    @DDI("getCommandContent()")
    @Lunatic("setValue(#param)")
    private String value;

    /**
     * In Pogues, the type can be things like 'number' or 'VTL'.
     * In Lunatic, all calculated expressions are of VTL type.
     */
    @Pogues("#this instanceof T(fr.insee.pogues.model.TypedValueType) ? getType().value() : 'VTL'")
    @Lunatic("setType(T(fr.insee.lunatic.model.flat.LabelTypeEnum).fromValue(#param))")
    String type = "VTL";

    /**
     * In DDI, the expression contains variable references instead of variables names.
     * This list contains the references of these variables.
     */
    @Pogues("#this instanceof T(java.lang.String) ? " +
            "T(fr.insee.eno.core.model.calculated.CalculatedExpression).extractBindingReferences(#this, #poguesIndex) : " +
            "T(fr.insee.eno.core.model.calculated.CalculatedExpression).extractBindingReferences(getValue(), #poguesIndex)")
    @DDI("getInParameterList()")
    private List<BindingReference> bindingReferences = new ArrayList<>();

    /** The method "extractBindingReferences" allows, from the expression of a "CalculatedVariable",
     * to construct the associated "BindingReferences". Note that the identifier (a concept derived from DDI)
     * is null for each reference. */
    public static Set<BindingReference> extractBindingReferences(String expression, VariableIndex variableIndex) {
        Set<BindingReference> references = new LinkedHashSet<>(); // linked hash set to have consistent order
        Matcher matcher = POGUES_VARIABLE_PATTERN.matcher(expression);

        while (matcher.find()) {
            String variableName = matcher.group(1);
            validatePoguesReference(expression, variableName, variableIndex);
            references.add(new BindingReference(null, variableName));
        }

        return references;
    }

   private static void validatePoguesReference(String expression, String variableName, VariableIndex variableIndex) {
        if (!variableIndex.containsVariable(variableName)) {
            String message = String.format(
                    "Name '%s' used in expression:%n%s%n" +
                            "does not match any variable.",
                    variableName, expression);
            log.warn(message);
            // should be an exception, yet Pogues composition feature creates cases where this is allowed
        }
    }

    /** The removeSurroundingDollarSigns method removes the "$" symbols surrounding the reference to
     * a variable in the expression of the "CalculatedVariable". */
    public static String removeSurroundingDollarSigns(String expression) {
        return expression.replaceAll(POGUES_VARIABLE_PATTERN.pattern(), "$1");
    }
}
