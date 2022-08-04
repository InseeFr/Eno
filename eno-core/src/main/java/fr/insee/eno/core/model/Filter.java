package fr.insee.eno.core.model;

import datacollection33.IfThenElseType;
import datacollection33.QuestionConstructType;
import datacollection33.SequenceType;
import fr.insee.eno.core.annotations.DDI;
import lombok.Getter;
import lombok.Setter;
import reusable33.InParameterType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Filter extends EnoObject {

    @Getter
    @Setter
    public static class BindingReference extends EnoObject {

        @DDI(contextType = InParameterType.class, field = "getIDArray(0).getStringValue()")
        private String id;

        /** Name of the referenced variable. */
        @DDI(contextType = InParameterType.class, field = "getParameterNameArray(0).getStringArray(0).getStringValue()")
        private String variableName;
        
    }

    /** Default expression is "true". */
    public Filter() {
        expression = "true";
    }

    @DDI(contextType = IfThenElseType.class, field = "getIDArray(0).getStringValue()")
    private String id;

    /** In DDI, the expression contains variable references instead of variables names.
     * This list contains the references of these variables. */
    @DDI(contextType = IfThenElseType.class, field = "getIfCondition().getCommandArray(0).getInParameterList()")
    private List<BindingReference> bindingReferences = new ArrayList<>();

    /** Command that determines if the filter is applied or not. */
    @DDI(contextType = IfThenElseType.class, field = "getIfCondition().getCommandArray(0).getCommandContent()")
    private String expression;

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
