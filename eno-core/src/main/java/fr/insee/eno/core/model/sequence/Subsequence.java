package fr.insee.eno.core.model.sequence;

import datacollection33.SequenceType;
import fr.insee.eno.core.annotations.Contexts;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.Setter;

/** Eno object for subsequence.
 * Note: a 'subsequence' is sometimes called a 'submodule' in Eno.
 * A subsequence can contain the same kind of elements as sequence, except subsequences.
 * In DDI, a sequence is a SequenceType that has the value 'submodule' in its 'type of sequence' field.
 * In Lunatic, a subsequence is a Subsequence object. */
@Getter
@Setter
@Contexts.Context(format = Format.DDI, type = SequenceType.class)
@Contexts.Context(format = Format.LUNATIC, type = fr.insee.lunatic.model.flat.Subsequence.class)
public class Subsequence extends AbstractSequence {

    /** Field specific to Lunatic.
     * Note: maybe redundant with "type" field in Lunatic serialized documents. */
    @Lunatic("setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    private String componentType = "SUBSEQUENCE";

}
