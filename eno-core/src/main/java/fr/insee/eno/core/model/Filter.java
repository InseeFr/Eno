package fr.insee.eno.core.model;

import datacollection33.IfThenElseType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.ConditionFilterType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Filter extends EnoIdentifiableObject implements EnoObjectWithExpression {

    /** In DDI, filter have a "scope" that can contain other filters (nested filters).
     * Parent filter relationships are set during a DDI processing.
     * In Lunatic, each component has a unique filter. Its expression is a logical concatenation of the filter
     * with its parent expressions. */
    private Filter parentFilter; //TODO: nested filters logical concatenation for Lunatic

    /** Default expression is "true". */
    public Filter() {
        stringExpression = "true";
    }

    /** Lunatic filters doesn't have an identifier. */
    @DDI(contextType = IfThenElseType.class, field = "getIDArray(0).getStringValue()")
    private String id;

    //TODO: Asked Lunatic-Model to use LabelType in ConditionFilterType to refactor this.

    /** In DDI, the expression contains variable references instead of variables names.
     * This list contains the references of these variables. */
    @DDI(contextType = IfThenElseType.class, field = "getIfCondition().getCommandArray(0).getInParameterList()")
    private List<BindingReference> bindingReferences = new ArrayList<>();

    /** Command that determines if the filter is applied or not. */
    @DDI(contextType = IfThenElseType.class, field = "getIfCondition().getCommandArray(0).getCommandContent()")
    @Lunatic(contextType = ConditionFilterType.class, field = "setValue(#param)")
    private String stringExpression;

    private CalculatedExpression expression;

    public CalculatedExpression getExpression() {
        if (expression == null) {
            expression = new CalculatedExpression();
            expression.setValue(stringExpression);
            expression.setBindingReferences(bindingReferences);
        }
        return expression;
    }

    //

    /** List of the identifier of components that are in the scope of the filter.
     * In DDI, we expect to have 'Sequence', 'QuestionConstruct' and 'IfThenElse' references.
     * In the sequence case, the reference id directly designates the corresponding sequence object.
     * Same in the filter (IfThenElse) case.
     * In the question construct case, the reference id designates a QuestionConstruct object,
     * that contains a reference to the concrete question object. */
    @DDI(contextType = IfThenElseType.class,
            field = "#index.get(#this.getThenConstructReference().getIDArray(0).getStringValue())" +
                    ".getControlConstructReferenceList().![" +
                    "    #this.getTypeOfObject().toString() == 'QuestionConstruct' ? " +
                    "        #index.get(#this.getIDArray(0).getStringValue())" +
                    "        .getQuestionReference().getIDArray(0).getStringValue() : " +
                    "    #this.getTypeOfObject().toString() == 'Sequence' ? " +
                    "        #this.getIDArray(0).getStringValue() : " +
                    "    #this.getTypeOfObject().toString() == 'IfThenElse' ? " +
                    "        #this.getIDArray(0).getStringValue() : " +
                    "    T(fr.insee.eno.core.model.Filter).unexpectedDDIComponent(#root, #this.getTypeOfObject().toString())" +
                    "]")
    private List<String> componentReferences = new ArrayList<>();

    public static String unexpectedDDIComponent(IfThenElseType ifThenElseType, String typeOfObject) {
        throw new RuntimeException(String.format(
                "Unexpected type of object '%s' found in ThenConstructReference sequence of IfThenElse '%s'",
                typeOfObject, ifThenElseType.getIDArray(0).getStringValue()));
    }

}
