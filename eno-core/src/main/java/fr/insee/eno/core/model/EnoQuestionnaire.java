package fr.insee.eno.core.model;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.declaration.Declaration;
import fr.insee.eno.core.model.label.QuestionnaireLabel;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.question.MultipleResponseQuestion;
import fr.insee.eno.core.model.question.SingleResponseQuestion;
import fr.insee.eno.core.model.sequence.RoundaboutSequence;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.model.variable.VariableGroup;
import fr.insee.eno.core.parameter.Format;
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
public class EnoQuestionnaire extends EnoIdentifiableObject {

    /** Name of the questionnaire model. */
    @DDI("getResourcePackageArray(0).getCodeListSchemeArray(0)" +
            ".getCodeListSchemeNameArray(0).getStringArray(0).getStringValue()") //TODO: see if it's that one
    @Lunatic("setModele(#param)")
    private String questionnaireModel;

    /** Short description of the questionnaire. */
    @DDI("getCitation()?.getTitle()")
    @Lunatic("setLabel(#param)")
    private QuestionnaireLabel label;

    /** Questionnaire variables. Note: variables can have different "scope" (questionnaire-level, loop or dynamic
     * table level), yet all variables are defined in this list. */
    @DDI("getResourcePackageArray(0).getVariableSchemeArray(0).getVariableList()")
    @Lunatic("getVariables()")
    private final List<Variable> variables = new ArrayList<>();

    /** Variable groups of the questionnaire. A variable group contains variables that share a common scope. */
    @DDI("getResourcePackageArray(0).getVariableSchemeArray(0).getVariableGroupList()")
    private final List<VariableGroup> variableGroups = new ArrayList<>();

    /** List of questionnaire's sequences. */
    @DDI("getResourcePackageArray(0).getControlConstructSchemeArray(0).getControlConstructList()" +
            ".?[#this instanceof T(fr.insee.ddi.lifecycle33.datacollection.SequenceType) " +
            "and not #this.getTypeOfSequenceList().isEmpty()]" +
            ".?[#this.getTypeOfSequenceArray(0).getStringValue() == 'module']")
    @Lunatic("getComponents()")
    private final List<Sequence> sequences = new ArrayList<>();

    /** List of questionnaire's subsequences.
     * Note: the order and hierarchy of the sequences and subsequences is stored in the sequence objects. */
    @DDI("getResourcePackageArray(0).getControlConstructSchemeArray(0).getControlConstructList()" +
            ".?[#this instanceof T(fr.insee.ddi.lifecycle33.datacollection.SequenceType) " +
            "and not #this.getTypeOfSequenceList().isEmpty()]" +
            ".?[#this.getTypeOfSequenceArray(0).getStringValue() == 'submodule']")
    @Lunatic("getComponents()")
    private final List<Subsequence> subsequences = new ArrayList<>();

    /** Roundabouts are described as a special type of sequence in DDI.
     * These are resolved in Lunatic through a dedicated processing step. */
    @DDI("getResourcePackageArray(0).getControlConstructSchemeArray(0).getControlConstructList()" +
            ".?[#this instanceof T(fr.insee.ddi.lifecycle33.datacollection.SequenceType) " +
            "and not #this.getTypeOfSequenceList().isEmpty()]" +
            ".?[#this.getTypeOfSequenceArray(0).getStringValue() == 'roundabout']")
    private final List<RoundaboutSequence> roundaboutSequences = new ArrayList<>();

    /** Loops defined in the questionnaire.
     * In DDI, a loop is defined at the questionnaire level.
     * In Lunatic, a loop is a component containing components within its scope.
     */
    @DDI("getResourcePackageArray(0).getControlConstructSchemeArray(0).getControlConstructList()" +
            ".?[#this instanceof T(fr.insee.ddi.lifecycle33.datacollection.LoopType)]")
    @Lunatic("getComponents()")
    private final List<Loop> loops = new ArrayList<>();

    /** In DDI, all filters are mapped at the questionnaire level.
     * They are inserted in the objects they belong to through a DDI processing.
     * Note: there is a difference between filter objects mapped in this list (in the questionnaire object) and filter
     * objects that are inserted in components (sequences, questions etc.)
     */
    @DDI("getResourcePackageArray(0).getControlConstructSchemeArray(0).getControlConstructList()" +
            ".?[#this instanceof T(fr.insee.ddi.lifecycle33.datacollection.IfThenElseType)]")
    private final List<Filter> filters = new ArrayList<>();

    /** In DDI, all controls are mapped at the questionnaire level.
     * They are inserted in the objects they belong to through a DDI processing.
     */
    @DDI("getResourcePackageArray(0).getControlConstructSchemeArray(0).getControlConstructList()" +
            ".?[#this instanceof T(fr.insee.ddi.lifecycle33.datacollection.ComputationItemType)]")
    private final List<Control> controls = new ArrayList<>();

    /** In DDI, all declarations are mapped at the questionnaire level.
     * They are inserted in the objects they belong to through a DDI processing.
     */
    @DDI("getResourcePackageArray(0).getControlConstructSchemeArray(0).getControlConstructList()" +
            ".?[#this instanceof T(fr.insee.ddi.lifecycle33.datacollection.StatementItemType)]")
    private final List<Declaration> declarations = new ArrayList<>();

    /** Single response questions.
     * This corresponds to DDI "QuestionItem" objects.
     * Question objects are components in the Lunatic questionnaire.
     */
    @DDI("getResourcePackageArray(0).getQuestionSchemeArray(0).getQuestionItemList()")
    @Lunatic("getComponents()")
    private final List<SingleResponseQuestion> singleResponseQuestions = new ArrayList<>();

    /** Multiple response questions.
     * This corresponds to DDI "QuestionGrid" objects.
     * Question objects are components in the Lunatic questionnaire.
     */
    @DDI("getResourcePackageArray(0).getQuestionSchemeArray(0).getQuestionGridList()")
    @Lunatic("getComponents()")
    private final List<MultipleResponseQuestion> multipleResponseQuestions = new ArrayList<>();

    /** In DDI, code lists are mapped at the questionnaire level.
     * They are inserted in objects that rely on a code list through a DDI processing.
     */
    @DDI("getResourcePackageArray(0).getCodeListSchemeArray(0).getCodeListList()")
    List<CodeList> codeLists = new ArrayList<>();

}
