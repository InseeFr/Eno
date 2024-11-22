package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.processing.ProcessingStep;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * In DDI, row-level controls (for dynamic tables lines or roundabout occurrences) are control construct references
 * listed in the control construct references list in loop objects (more precisely in the virtual sequence that is
 * associated with the loop).
 * This processing step identifies these controls and marks them as "row" controls.
 */
public class DDIMarkRowControls implements ProcessingStep<EnoQuestionnaire> {

    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        // index controls by id
        Map<String, Control> controls = mapQuestionnaireControls(enoQuestionnaire);
        // iterate on loop control references, to set the context of corresponding controls as "row"
        enoQuestionnaire.getLoops().forEach(loop -> controlReferencesStream(loop)
                .forEach(itemReference -> controls.get(itemReference.getId()).setContext(Control.Context.ROW))
        );
    }

    static Map<String, Control> mapQuestionnaireControls(EnoQuestionnaire enoQuestionnaire) {
        return enoQuestionnaire.getControls().stream().collect(Collectors.toMap(Control::getId, control -> control));
    }

    static Stream<ItemReference> controlReferencesStream(Loop loop) {
        return loop.getLoopItems().stream()
                .filter(itemReference -> ItemReference.ItemType.CONTROL.equals(itemReference.getType()));
    }

}
