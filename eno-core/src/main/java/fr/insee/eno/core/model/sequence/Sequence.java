package fr.insee.eno.core.model.sequence;

import fr.insee.ddi.lifecycle33.datacollection.SequenceType;
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
@Context(format = Format.POGUES, type = fr.insee.pogues.model.SequenceType.class)
@Context(format = Format.DDI, type = SequenceType.class)
@Context(format = Format.LUNATIC, type = fr.insee.lunatic.model.flat.Sequence.class)
public class Sequence extends AbstractSequence {

    /** For some reason, Pogues adds a fake end sequence at the end of questionnaires.
     * This sequence has a special identifier. */
    public static final String POGUES_FAKE_END_SEQUENCE_ID = "idendquest";

    /** Field specific to Lunatic.
     * Note: maybe redundant with "type" field in Lunatic serialized documents. */
    @Lunatic("setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    private String componentType = "SEQUENCE";

}
