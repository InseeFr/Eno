package fr.insee.eno.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.insee.eno.core.reference.EnoIndex;
import lombok.Getter;
import lombok.Setter;

public abstract class EnoObject {

    @Getter
    @Setter
    @JsonIgnore
    @Deprecated // TODO: see comments in processing class, to be removed
    private EnoObject parent;

    @Getter
    @Setter
    private EnoIndex index;

    /** Shortcut method to get an object stored in the index. */
    public EnoIdentifiableObject get(String enoObjectId) {
        return index != null ? index.get(enoObjectId) : null;
    }

}
