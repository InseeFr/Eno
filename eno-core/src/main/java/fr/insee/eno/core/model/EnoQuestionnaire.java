package fr.insee.eno.core.model;

import fr.insee.eno.core.annotations.DDI;
import instance33.DDIInstanceDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Root class for Eno model.
 */
public class EnoQuestionnaire {

    @DDI(contextType = DDIInstanceDocument.class,
            field = "getDDIInstance().getIDArray(0).getStringValue()")
    private String id;

    @DDI(contextType = DDIInstanceDocument.class,
            field = "getDDIInstance().getResourcePackageArray(0).getVariableSchemeArray(0)" +
                    ".getVariableArray(0).getVariableNameArray(0).getStringArray(0).getStringValue()")
    private String firstVariableName;

    @DDI(contextType = DDIInstanceDocument.class,
            field = "getDDIInstance().getResourcePackageArray(0).getVariableSchemeArray(0).getVariableArray(0)")
    private Variable firstVariable;

    @DDI(contextType = DDIInstanceDocument.class,
            field = "getDDIInstance().getResourcePackageArray(0).getVariableSchemeArray(0).getVariableArray()")
    private final List<Variable> variables = new ArrayList<>();


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getFirstVariableName() {
        return firstVariableName;
    }
    public void setFirstVariableName(String firstVariableName) {
        this.firstVariableName = firstVariableName;
    }

    public Variable getFirstVariable() {
        return firstVariable;
    }
    public void setFirstVariable(Variable firstVariable) {
        this.firstVariable = firstVariable;
    }

    public List<Variable> getVariables() {
        return variables;
    }
}
