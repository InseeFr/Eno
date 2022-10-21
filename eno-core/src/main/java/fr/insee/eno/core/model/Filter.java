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
    @DDI(contextType = IfThenElseType.class, field = "getIfCondition().getCommandArray(0)")
    private List<BindingReference> bindingReferences = new ArrayList<>();

    /** Command that determines if the filter is applied or not. */
    @DDI(contextType = IfThenElseType.class, field = "getIfCondition().getCommandArray(0)")
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
     * In DDI, we expect to have 'QuestionConstruct' and 'Sequence' references.
     * In the sequence case, the reference id directly designates the corresponding sequence object.
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
                    "    null" +
                    "]")
    private List<String> componentReferences = new ArrayList<>();

}
