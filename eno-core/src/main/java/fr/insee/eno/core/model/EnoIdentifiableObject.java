package fr.insee.eno.core.model;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.ComponentType;
import lombok.Getter;
import lombok.Setter;
import reusable33.AbstractIdentifiableType;

/** Abstract class to factorize the mapping of the identifier in many objects. */
@Getter
@Setter
public abstract class EnoIdentifiableObject extends EnoObject {

    /** Object identifier.
     * In DDI, it is the content of the first 'ID' element.
     * In Lunatic, it is the 'id' attribute. */
    @DDI(contextType = AbstractIdentifiableType.class, field = "getIDArray(0).getStringValue()")
    @Lunatic(contextType = ComponentType.class, field ="setId(#param)")
    String id;

    public EnoIdentifiableObject() {}

    /** Create and instance with given id, and put it in the given index.
     * TODO: to be removed, useless since java doesn't support constructor inheritance... */
    public EnoIdentifiableObject(String id, EnoIndex enoIndex) {
        this.id = id;
        enoIndex.put(id, this);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"[id="+id+"]";
    }

}
