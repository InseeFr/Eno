package fr.insee.eno.core.model.navigation;

import datacollection33.LoopType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * A loop is defined by its scope, which is a list of sequence or subsequence references.
 * */
@Getter
@Setter
@Slf4j
@Context(format = Format.DDI, type = LoopType.class)
@Context(format = Format.LUNATIC, type = fr.insee.lunatic.model.flat.Loop.class)
public abstract class Loop extends EnoIdentifiableObject {

    @Lunatic("setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    private String componentType = "LOOP";

    /** Loop business name.
     * Unused in Lunatic. */
    @DDI("getConstructNameArray(0).getStringArray(0).getStringValue()")
    private String name;

    /** Same principle as sequence items list in sequence objects. */
    @DDI("#index.get(#this.getControlConstructReference().getIDArray(0).getStringValue())" +
            ".getControlConstructReferenceList()")
    private final List<ItemReference> loopItems = new ArrayList<>();

    /** References of sequences or subsequences that are in the scope of the loop.
     * Note: in Pogues a loop can only be defined on sequence or subsequences.
     * (In other formats, nothing makes it formally impossible to have loops defined directly on questions.)
     * In DDI, this property is filled by a processing using the "loopItems" property. */
    private final List<StructureItemReference> loopScope = new ArrayList<>();

    /** A loop can be in the scope of a filter.
     * In DDI, filters are mapped at questionnaire level and inserted through a processing step. */
    @Lunatic("setConditionFilter(#param)")
    private ComponentFilter filter;

}
