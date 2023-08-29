package fr.insee.eno.core.model;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.ComponentType;
import lombok.Getter;
import lombok.Setter;
import reusable33.AbstractIdentifiableType;

/** Abstract class to factorize the mapping of the identifier in many objects.
 * Note: an object that doesn't inherit this class cannot be indexed in an EnoIndex object.
 * @see EnoIndex */
@Getter
@Setter
@Context(format = Format.DDI, type = AbstractIdentifiableType.class)
@Context(format = Format.LUNATIC, type = ComponentType.class)
public abstract class EnoIdentifiableObject extends EnoObject {

    /** Object identifier.
     * In DDI, it is the content of the first 'ID' element.
     * In Lunatic, it is the 'id' attribute. */
    @DDI("getIDArray(0).getStringValue()")
    @Lunatic("setId(#param)")
    String id;

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"[id="+getId()+"]";
    }

}
