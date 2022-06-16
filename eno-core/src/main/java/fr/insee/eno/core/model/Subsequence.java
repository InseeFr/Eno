package fr.insee.eno.core.model;

import datacollection33.SequenceType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import lombok.Getter;
import lombok.Setter;

/**
 * Class to represent subsequences (i.e. a sequence within a sequence, but that cannot contain a sequence...).
 * (while waiting for Lunatic to take over n-number of depth levels in subsequences,
 * this class should disappear when it will be the case).
 */
@Getter
@Setter
public class Subsequence {

    @DDI(contextType = SequenceType.class, field = "getIDArray(0).getStringValue()")
    @Lunatic(contextType = fr.insee.lunatic.model.flat.Subsequence.class, field = "setId(#param)")
    private String id;
}
