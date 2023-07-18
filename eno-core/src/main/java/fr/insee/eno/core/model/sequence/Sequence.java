package fr.insee.eno.core.model.sequence;

import datacollection33.SequenceType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.Setter;

/** Eno object for sequence.
 * Note: a 'sequence' is sometimes called a 'module' in Eno.
 * In DDI, a sequence is a SequenceType object that has the value 'module' in its 'type of sequence' field.
 * In Lunatic, a sequence is a Sequence object. */
@Getter
@Setter
@Context(format = Format.DDI, type = SequenceType.class)
@Context(format = Format.LUNATIC, type = fr.insee.lunatic.model.flat.Sequence.class)
public class Sequence extends AbstractSequence {

    /** Field specific to Lunatic.
     * Note: maybe redundant with "type" field in Lunatic serialized documents. */
    @Lunatic(contextType = fr.insee.lunatic.model.flat.Sequence.class,
            field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    private String componentType = "SEQUENCE";

}
