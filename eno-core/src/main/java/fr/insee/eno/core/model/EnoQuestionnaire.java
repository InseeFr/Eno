package fr.insee.eno.core.model;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.VariableType;
import instance33.DDIInstanceDocument;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Root class for Eno model.
 */
@ToString(of={"id", "firstVariableName"})
public class EnoQuestionnaire {

    @DDI(contextType = DDIInstanceDocument.class,
            field = "getDDIInstance().getIDArray(0).getStringValue()")
    @Lunatic(contextType = Questionnaire.class, field ="setId(#param)")
    private String id;

    @DDI(contextType = DDIInstanceDocument.class,
            field = "getDDIInstance().getResourcePackageArray(0).getVariableSchemeArray(0)" +
                    ".getVariableArray(0).getVariableNameArray(0).getStringArray(0).getStringValue()")
    private String firstVariableName;

    @DDI(contextType = DDIInstanceDocument.class,
            field = "getDDIInstance().getResourcePackageArray(0).getVariableSchemeArray(0).getVariableArray(0)")
    private Variable firstVariable;

    @DDI(contextType = DDIInstanceDocument.class,
            field = "getDDIInstance().getResourcePackageArray(0).getVariableSchemeArray(0).getVariableList()")
    @Lunatic(contextType = Questionnaire.class, field ="getVariables()", instanceType = VariableType.class)
    private final List<Variable> variables = new ArrayList<>();

    @DDI(contextType = DDIInstanceDocument.class,
            field = "getDDIInstance().getResourcePackageArray(0).getVariableSchemeArray(0).getVariableGroupList()")
    private final List<VariableGroup> variableGroups = new ArrayList<>();


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

    public List<VariableGroup> getVariableGroups() {
        return variableGroups;
    }
}
