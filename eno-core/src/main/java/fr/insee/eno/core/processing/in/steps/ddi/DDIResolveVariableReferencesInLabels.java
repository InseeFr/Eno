package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.label.EnoLabel;
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
        enoCatalog.getLabels().forEach(this::resolveLabel);
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

    /**
     * For now, Pogues allows users to input invalid VTL expressions for static labels
     * (i.e. to input a value without quotes), and Pogues does not add the missing quotes.
     * @param staticLabel A static label value.
     * @return The label value surrounded with quotes
     */
    private String toValidStringExpression(String staticLabel) {
        StringBuilder stringBuilder = new StringBuilder();
        if (! staticLabel.startsWith("\""))
            stringBuilder.append("\"");
        stringBuilder.append(staticLabel);
        if(! staticLabel.endsWith("\""))
            stringBuilder.append("\"");
        return stringBuilder.toString();
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

}
