package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.declaration.Declaration;
import fr.insee.eno.core.model.declaration.Instruction;
import fr.insee.eno.core.model.label.EnoLabel;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.model.sequence.AbstractSequence;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoCatalog;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public class DDIResolveVariableReferencesInLabels implements ProcessingStep<EnoQuestionnaire> {

    /** In DDI, in labels, variable names are replaces by their reference, surrounded by this character. */
    public static final String VARIABLE_REFERENCE_MARKER = "¤";

    private EnoCatalog enoCatalog;

    /**
     * In DDI labels, variables are replaced by a reference surrounded by a special character.
     * This method replaces references by variables name in each concerned object.
     * Note: This processing doesn't keep the link between labels and the variables used in them.
     * (We could do this for Lunatic binding dependencies if it turns useful.)
     * @param enoQuestionnaire Eno questionnaire to be processed.
     */
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        // Sequences and subsequences
        enoQuestionnaire.getSequences().stream().map(AbstractSequence::getLabel).forEach(this::resolveLabel);
        enoQuestionnaire.getSubsequences().stream().map(AbstractSequence::getLabel).forEach(this::resolveLabel);
        // Questions
        enoCatalog.getQuestions().stream().map(Question::getLabel).forEach(this::resolveLabel);
        // Declarations, instructions and controls within components
        enoCatalog.getComponents().forEach(enoComponent -> {
                enoComponent.getDeclarations().stream().map(Declaration::getLabel).forEach(this::resolveLabel);
                enoComponent.getInstructions().stream().map(Instruction::getLabel).forEach(this::resolveLabel);
        });
        enoCatalog.getQuestions().forEach(enoQuestion ->
                enoQuestion.getControls().stream().map(Control::getMessage).forEach(this::resolveLabel));
        // Code lists
        enoQuestionnaire.getCodeLists().stream().map(CodeList::getCodeItems).forEach(this::resolveCodeItemsLabel);
        // Code lists in multiple response questions (might be refactored afterward)
        enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(SimpleMultipleChoiceQuestion.class::isInstance)
                .map(SimpleMultipleChoiceQuestion.class::cast)
                .forEach(this::resolveCodeResponsesLabel);
    }

    /**
     * Resolve the value of a label, by replacing the variable references by the variable names.
     * @param enoLabel Label to resolve.
     */
    private void resolveLabel(EnoLabel enoLabel) {
        String resolvedValue = enoLabel.getValue();
        List<Variable> referencedVariables = getReferencedVariables(enoLabel);
        if (referencedVariables.isEmpty())
            resolvedValue = toValidStringExpression(resolvedValue);
        for (Variable variable : referencedVariables) {
            resolvedValue = resolvedValue.replace(
                    VARIABLE_REFERENCE_MARKER + variable.getReference() + VARIABLE_REFERENCE_MARKER,
                    variable.getName());
        }
        enoLabel.setValue(resolvedValue);
    }

    /** For now, Pogues allows users to input invalid VTL expressions for static labels
     * (i.e. to input a value without quotes), and Pogues does not add the missing quotes. */
    private String toValidStringExpression(String staticLabel) {
        // Remove eventual quote characters that might be in the middle of the label
        staticLabel = staticLabel.replace("\"", "");
        // Return the label value surrounded with quotes
        return "\"" + staticLabel + "\"";
    }

    /**
     * Get variable objects from variable references that are in the label.
     * @param enoLabel Label from which we retrieve variables.
     * @return Variables that are used in the label.
     */
    private List<Variable> getReferencedVariables(EnoLabel enoLabel) {
        String labelValue = enoLabel.getValue();
        Set<String> variableReferences = new HashSet<>();
        Pattern pattern = Pattern.compile(VARIABLE_REFERENCE_MARKER + "(.+?)"+ VARIABLE_REFERENCE_MARKER);

        for (Matcher matcher = pattern.matcher(labelValue); matcher.find();) {
            String match = matcher.group();
            String variableReference = match.substring(1, match.length()-1);
            variableReferences.add(variableReference);
        }

        return variableReferences.stream()
                        .map(variableReference -> enoCatalog.getVariable(variableReference))
                        .toList();
    }

    private void resolveCodeItemsLabel(List<CodeItem> codeItems) {
        for (CodeItem codeItem : codeItems) {
            resolveLabel(codeItem.getLabel());
            // Recursive call in case of nested code items
            resolveCodeItemsLabel(codeItem.getCodeItems());
        }
    }

    private void resolveCodeResponsesLabel(SimpleMultipleChoiceQuestion multipleChoiceQuestion) {
        multipleChoiceQuestion.getCodeResponses().forEach(codeResponse -> resolveLabel(codeResponse.getLabel()));
    }

}
