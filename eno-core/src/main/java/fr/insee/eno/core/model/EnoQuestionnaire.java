package fr.insee.eno.core.model;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.SequenceType;
import fr.insee.lunatic.model.flat.VariableType;
import instance33.DDIInstanceDocument;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Root class for Eno model.
 */
@Getter
@Setter
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

    @DDI(contextType = DDIInstanceDocument.class,
            field = "getDDIInstance().getResourcePackageArray(0).getControlConstructSchemeArray(0).getControlConstructList()" +
                    ".?[#this instanceof T(datacollection33.SequenceType) " +
                    "and #this.getTypeOfSequenceArray(0).getStringValue() == 'module']")
    @Lunatic(contextType = Questionnaire.class, field = "getComponents()",
            instanceType = SequenceType.class)
    private final List<Sequence> sequences = new ArrayList<>();

    @DDI(contextType = DDIInstanceDocument.class,
            field = "getDDIInstance().getResourcePackageArray(0).getControlConstructSchemeArray(0).getControlConstructList()" +
                    ".?[#this instanceof T(datacollection33.SequenceType) " +
                    "and #this.getTypeOfSequenceArray(0).getStringValue() == 'submodule']")
    @Lunatic(contextType = Questionnaire.class, field = "getComponents()",
            instanceType = fr.insee.lunatic.model.flat.Subsequence.class)
    private final List<Subsequence> subsequences = new ArrayList<>();

    @DDI(contextType = DDIInstanceDocument.class,
            field = "getDDIInstance().getResourcePackageArray(0).getQuestionSchemeArray(0).getQuestionItemList()")
    private final List<Question> questions = new ArrayList<>();

}
