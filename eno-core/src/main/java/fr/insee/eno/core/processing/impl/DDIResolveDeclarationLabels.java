package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.DeclarationInterface;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.Variable;
import fr.insee.eno.core.processing.InProcessingInterface;
import fr.insee.eno.core.reference.EnoCatalog;
import fr.insee.eno.core.reference.EnoIndex;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public class DDIResolveDeclarationLabels implements InProcessingInterface {

    /** In DDI, in declarations / instructions, variable names are replaces by their reference,
     * surrounded by this character. */
    public static final String DECLARATION_REFERENCE_MARKER = "¤";

    private EnoCatalog enoCatalog;

    /** In DDI instructions / declarations, variables are replaced by a reference surrounded by a special character.
     * This method replaces references by variables name in each instruction / declaration.
     * This method also fills the object's list of variable names used in its label. */
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        // Get all declarations and instructions
        List<DeclarationInterface> declarations = new ArrayList<>(enoQuestionnaire.getDeclarations());
        enoCatalog.getComponents()
                .forEach(enoComponent -> declarations.addAll(enoComponent.getInstructions()));
        //
        Pattern pattern = Pattern.compile(DECLARATION_REFERENCE_MARKER + "(.+?)"+ DECLARATION_REFERENCE_MARKER);
        for (DeclarationInterface declaration : declarations) {
            String declarationLabel = declaration.getLabel();
            // TODO: we could do what follows a bit neater maybe
            List<String> variableReferences = new ArrayList<>();
            for (Matcher matcher = pattern.matcher(declarationLabel); matcher.find();) {
                String match = matcher.group();
                String variableReference = match.substring(1, match.length()-1);
                variableReferences.add(variableReference);
            }
            for (String variableReference : variableReferences) {
                String variableName = enoCatalog.getVariable(variableReference).getName();
                declarationLabel = declarationLabel.replace(
                        DECLARATION_REFERENCE_MARKER + variableReference + DECLARATION_REFERENCE_MARKER,
                        variableName);
                declaration.getVariableNames().add(variableName);
            }
            declaration.setLabel(declarationLabel);
        }
    }

}
