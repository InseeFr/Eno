package fr.insee.eno.core.converter;

import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.pogues.model.DynamicIterationType;
import fr.insee.pogues.model.IterationType;

public class PoguesLoopConversion {

    private PoguesLoopConversion() {}

    static EnoObject instantiateFrom(IterationType iterationType) {
        // There is only one type of loop in the Pogues model currently
        if (! (iterationType instanceof DynamicIterationType poguesLoop))
            throw new IllegalArgumentException("Unknown type of loop object: " + iterationType.getClass());
        //
        if (poguesLoop.getIterableReference() != null && !poguesLoop.getIterableReference().isEmpty())
            return new LinkedLoop();
        return new StandaloneLoop();
    }

}
