package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.model.EnoComponent;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.declaration.DeclarationInterface;
import fr.insee.eno.core.model.mode.Mode;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoCatalog;

import java.util.List;

public class EnoModeSelection implements ProcessingStep<EnoQuestionnaire> {

    private final List<Mode> selectedModes;

    private final EnoCatalog enoCatalog;

    public EnoModeSelection(List<Mode> selectedModes, EnoCatalog enoCatalog) {
        this.selectedModes = selectedModes;
        this.enoCatalog = enoCatalog;
    }

    /**
     * Remove elements that does not correspond to the "selected modes" parameter.
     * For now, only declarations and instructions are concerned by mode selection.
     */
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        for (EnoComponent enoComponent : enoCatalog.getComponents()) {
            enoComponent.getDeclarations().removeIf(this::hasNoSelectedMode);
            enoComponent.getInstructions().removeIf(this::hasNoSelectedMode);
        }
    }

    /**
     * Return true if the given instruction matches the selected modes from parameters.
     */
    private boolean hasNoSelectedMode(DeclarationInterface declaration) {
        // If no mode is declared, the declaration is considered valid for all modes
        if (declaration.getModes().isEmpty())
            return false;
        return declaration.getModes().stream().noneMatch(selectedModes::contains);
    }

}
