package fr.insee.eno.core.converter;

import fr.insee.ddi.lifecycle33.datacollection.LoopType;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DDILoopConversion {

    private DDILoopConversion() {}

    static EnoObject instantiateFrom(LoopType loopType) {
        if (loopType.getInitialValue() == null && loopType.getLoopWhile() == null) {
            return new LinkedLoop();
        } else {
            // A standalone loop should have both "initial value" and "loop while" defined
            if (loopType.getInitialValue() == null)
                log.warn("DDI Loop '{}' has a null initial value.", loopType.getIDArray(0).getStringValue());
            if (loopType.getLoopWhile() == null)
                log.warn("DDI Loop '{}' has a null loop while.", loopType.getIDArray(0).getStringValue());
            return new StandaloneLoop();
        }
    }

}
