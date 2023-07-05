package fr.insee.eno.core.model.navigation;

import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoComponent;
import fr.insee.eno.core.model.EnoObject;
import lombok.Getter;
import lombok.Setter;
import datacollection33.LoopType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reusable33.ReferenceType;

/**
 * A loop is defined by its scope, which is a sequence or subsequence reference.
 * TODO: current DDI modeling only works with scope = single sequence/subsequence. */
@Getter
@Setter
@Slf4j
public abstract class Loop extends EnoIdentifiableObject {

    @Lunatic(contextType = fr.insee.lunatic.model.flat.Subsequence.class,
            field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    private String componentType = "LOOP";

    /** Loop business name.
     * Unused in Lunatic. */
    @DDI(contextType = LoopType.class, field = "getConstructNameArray(0).getStringArray(0).getStringValue()")
    private String name;

    /** Sequence or sub-sequence to loop on.
     * In Lunatic, components within the referenced sequence will be moved in the loop components' list.
     * This is done in a Lunatic processing. */
    @DDI(contextType = LoopType.class,
            field = "T(fr.insee.eno.core.model.navigation.Loop).mapSequenceReference(#this)")
    private String sequenceReference;

    public static String mapSequenceReference(LoopType ddiLoop) {
        ReferenceType controlConstruct = ddiLoop.getControlConstructReference();
        String typeOfObject = controlConstruct.getTypeOfObject().toString();
        if (! "Sequence".equals(typeOfObject)) {
            log.warn(String.format("DDI loop '%s' references an object of type '%s' (should be 'Sequence')",
                    ddiLoop.getIDArray(0).getStringValue(), typeOfObject));
        }
        return controlConstruct.getIDArray(0).getStringValue();
    }

    /** A loop can be in the scope of a filter.
     * In DDI, filters are mapped at questionnaire level and inserted through a processing step. */
    @Lunatic(contextType = fr.insee.lunatic.model.flat.Loop.class, field = "setConditionFilter(#param)")
    private Filter filter;

}
