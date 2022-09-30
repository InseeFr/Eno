package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.DeclarationInterface;
import fr.insee.eno.core.model.EnoComponent;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.Mode;
import fr.insee.eno.core.processing.EnoProcessingInterface;
import fr.insee.eno.core.reference.EnoCatalog;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class EnoModeSelection implements EnoProcessingInterface {

    private final List<Mode> selectedModes;

    private EnoCatalog enoCatalog;

    /** Remove elements that does not correspond to the "selected modes" parameter.
     * For now, only declarations and instructions are concerned by mode selection. */
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        for (EnoComponent enoComponent : enoCatalog.getComponents()) {
            enoComponent.getDeclarations().removeIf(this::hasNoSelectedMode);
            enoComponent.getInstructions().removeIf(this::hasNoSelectedMode);
        }
    }

    /** Return true if the given instruction matches the selected modes from parameters. */
    private boolean hasNoSelectedMode(DeclarationInterface declaration) {
        return declaration.getModes().stream().noneMatch(selectedModes::contains);
    }

}
