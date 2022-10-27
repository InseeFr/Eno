package fr.insee.eno.core.model.sequence;

import fr.insee.eno.core.annotations.Lunatic;
import lombok.Getter;
import lombok.Setter;

/** Eno object for sequence.
 * Note: a 'sequence' is sometimes called a 'module' in Eno.
 * In DDI, a sequence is a SequenceType object that has the value 'module' in its 'type of sequence' field.
 * In Lunatic, a sequence is a SequenceType object. */
@Getter
@Setter
public class Sequence extends AbstractSequence {

    /** Field specific to Lunatic.
     * Note: maybe redundant with "type" field in Lunatic serialized documents. */
    @Lunatic(contextType = fr.insee.lunatic.model.flat.SequenceType.class,
            field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    private String componentType = "SEQUENCE";

    @Override
    public String toString() {
        return "Sequence(id="+getId()+")";
    }

}
