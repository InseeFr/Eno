package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.declaration.DeclarationInterface;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.processing.InProcessingInterface;
import fr.insee.eno.core.reference.EnoCatalog;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public class DDIResolveVariableReferencesInLabels implements InProcessingInterface {

    /** In DDI, in declarations / instructions / controls, variable names are replaces by their reference,
     * surrounded by this character. */
    public static final String VARIABLE_REFERENCE_MARKER = "¤";

    private EnoCatalog enoCatalog;

    /**
     * In DDI instructions / declarations / controls, variables are replaced by a reference surrounded by a special character.
     * This method replaces references by variables name in each instruction / declaration / control.
     * This method also fills the object's list of variable names used in its label for declarations / instructions.
     *
     * @param enoQuestionnaire Eno questionnaire to be processed.
     */
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        // Get all declarations and instructions
        List<DeclarationInterface> declarations = new ArrayList<>(enoQuestionnaire.getDeclarations());
        List<Control> controls = new ArrayList<>();
        enoCatalog.getComponents()
                .forEach(enoComponent -> {
                    declarations.addAll(enoComponent.getInstructions());
                    controls.addAll(enoComponent.getControls());
                });

        for (DeclarationInterface declaration : declarations) {
            String declarationLabel = declaration.getLabel().getValue();
            List<Variable> variableReferences = getVariableReferences(declarationLabel);
            String resolvedLabel = getResolvedLabel(declarationLabel, variableReferences);
            declaration.getLabel().setValue(resolvedLabel);
            List<String> variableNames = variableReferences.stream().map(Variable::getName).toList();
            declaration.getVariableNames().addAll(variableNames);
        }

        for (Control control : controls) {
            String message = control.getMessage().getValue();
            List<Variable> variableReferences = getVariableReferences(message);
            String resolvedMessage = getResolvedLabel(message, variableReferences);
            control.getMessage().setValue(resolvedMessage);
        }
    }

    /**
     * resolve a label, by changing the variable ids in the label by the variable names
     * @param label label to resolve
     * @param variables variables for this label
     * @return the resolved label
     */
    private String getResolvedLabel(String label, List<Variable> variables) {
        String resolvedLabel = label;

        for(Variable variable : variables) {
            resolvedLabel = resolvedLabel.replace(VARIABLE_REFERENCE_MARKER + variable.getReference() + VARIABLE_REFERENCE_MARKER, variable.getName());
        }

        return resolvedLabel;
    }

    /**
     * Get variable references of a specific label
     * @param label label from which we retrieve variable ids
     * @return variables used in the label
     */
    private List<Variable> getVariableReferences(String label) {
        List<String> variableIds = new ArrayList<>();
        Pattern pattern = Pattern.compile(VARIABLE_REFERENCE_MARKER + "(.+?)"+ VARIABLE_REFERENCE_MARKER);

        for (Matcher matcher = pattern.matcher(label); matcher.find();) {
            String match = matcher.group();
            String variableId = match.substring(1, match.length()-1);
            if(!variableIds.contains(variableId)) {
                variableIds.add(variableId);
            }
        }

        return variableIds.stream()
                        .map(variableId -> enoCatalog.getVariable(variableId))
                        .toList();
    }

}
