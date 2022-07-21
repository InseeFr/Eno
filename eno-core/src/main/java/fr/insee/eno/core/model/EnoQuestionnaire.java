package fr.insee.eno.core.model;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Format;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.question.MultipleResponseQuestion;
import fr.insee.eno.core.model.question.SingleResponseQuestion;
import instance33.DDIInstanceType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.eno.core.annotations.Contexts.Context;

/**
 * Root class for Eno model.
 */
@Getter
@Setter
@Context(format = Format.POGUES, type = fr.insee.pogues.model.Questionnaire.class)
@Context(format = Format.DDI, type = DDIInstanceType.class)
@Context(format = Format.LUNATIC, type = fr.insee.lunatic.model.flat.Questionnaire.class)
public class EnoQuestionnaire extends EnoObject {

    @DDI(contextType = DDIInstanceType.class, field = "getIDArray(0).getStringValue()")
    @Lunatic(contextType = fr.insee.lunatic.model.flat.Questionnaire.class, field ="setId(#param)")
    private String id;

    @DDI(contextType = DDIInstanceType.class, field = "getCitation().getTitle().getStringArray(0).getStringValue()")
    @Lunatic(contextType = fr.insee.lunatic.model.flat.Questionnaire.class, field = "setLabel(#param)")
    private String label;

    @DDI(contextType = DDIInstanceType.class,
            field = "getResourcePackageArray(0).getVariableSchemeArray(0)" +
                    ".getVariableArray(0).getVariableNameArray(0).getStringArray(0).getStringValue()")
    private String firstVariableName;

    @DDI(contextType = DDIInstanceType.class,
            field = "getResourcePackageArray(0).getVariableSchemeArray(0).getVariableArray(0)")
    private Variable firstVariable;

    @DDI(contextType = DDIInstanceType.class,
            field = "getResourcePackageArray(0).getVariableSchemeArray(0).getVariableList()")
    @Lunatic(contextType = fr.insee.lunatic.model.flat.Questionnaire.class, field ="getVariables()")
    private final List<Variable> variables = new ArrayList<>();

    @DDI(contextType = DDIInstanceType.class,
            field = "getResourcePackageArray(0).getVariableSchemeArray(0).getVariableGroupList()")
    private final List<VariableGroup> variableGroups = new ArrayList<>();

    @DDI(contextType = DDIInstanceType.class,
            field = "getResourcePackageArray(0).getControlConstructSchemeArray(0).getControlConstructList()" +
                    ".?[#this instanceof T(datacollection33.SequenceType) " +
                    "and not #this.getTypeOfSequenceList().isEmpty()]" +
                    ".?[#this.getTypeOfSequenceArray(0).getStringValue() == 'module']")
    @Lunatic(contextType = fr.insee.lunatic.model.flat.Questionnaire.class, field = "getComponents()")
    private final List<Sequence> sequences = new ArrayList<>();

    @DDI(contextType = DDIInstanceType.class,
            field = "getResourcePackageArray(0).getControlConstructSchemeArray(0).getControlConstructList()" +
                    ".?[#this instanceof T(datacollection33.SequenceType) " +
                    "and not #this.getTypeOfSequenceList().isEmpty()]" +
                    ".?[#this.getTypeOfSequenceArray(0).getStringValue() == 'submodule']")
    @Lunatic(contextType = fr.insee.lunatic.model.flat.Questionnaire.class, field = "getComponents()")
    private final List<Subsequence> subsequences = new ArrayList<>();

    @DDI(contextType = DDIInstanceType.class,
            field = "getResourcePackageArray(0).getControlConstructSchemeArray(0).getControlConstructList()" +
                    ".?[#this instanceof T(datacollection33.IfThenElseTextType)]")
    private final List<Filter> filters = new ArrayList<>();

    @DDI(contextType = DDIInstanceType.class,
            field = "getResourcePackageArray(0).getQuestionSchemeArray(0).getQuestionItemList()")
    @Lunatic(contextType = fr.insee.lunatic.model.flat.Questionnaire.class, field = "getComponents()")
    private final List<SingleResponseQuestion> singleResponseQuestions = new ArrayList<>();

    @DDI(contextType = DDIInstanceType.class,
            field = "getResourcePackageArray(0).getQuestionSchemeArray(0).getQuestionGridList()")
    @Lunatic(contextType = fr.insee.lunatic.model.flat.Questionnaire.class, field = "getComponents()")
    private final List<MultipleResponseQuestion> multipleResponseQuestions = new ArrayList<>();

}
