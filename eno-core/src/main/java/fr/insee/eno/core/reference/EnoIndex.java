package fr.insee.eno.core.reference;

import fr.insee.eno.core.model.*;
import fr.insee.eno.core.model.question.Question;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Class designed to store all Eno identifiable objects within an Eno object in a flat map.
 */
@Slf4j
public class EnoIndex {

    Map<String, EnoIdentifiableObject> index = new HashMap<>();

    /** Recursively path through all eno identifiable objects and put them in the index. */
    private void recursiveIndexing(EnoIdentifiableObject enoIdentifiableObject) {
        // Finally done in mapper, TODO: remove this method
    }

    public void put(String enoObjectId, EnoIdentifiableObject enoIdentifiableObject) {
        index.put(enoObjectId, enoIdentifiableObject);
    }

    public EnoIdentifiableObject get(String enoObjectId) {
        if (! index.containsKey(enoObjectId)) {
            log.debug("No Eno object with id '"+enoObjectId+"' in the index.");
            log.debug("If it should be, make sure that the corresponding object inherits "
                    +EnoIdentifiableObject.class.getSimpleName()+".");
        }
        return index.get(enoObjectId);
    }

}
