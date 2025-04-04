package fr.insee.eno.core.model.response;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.Setter;

/**
 * The modalities of QCU/QCM can be filtered based on a display condition linked to
 * previous responses or external data.
 * This feature is not implemented for DDI (only for Pogues).
 */
@Getter
@Setter
@Context(format = Format.POGUES, type = fr.insee.pogues.model.CodeFilter.class)
public class CodeFilter extends EnoObject {

    /** Code value of the modality concerned by the filtering. */
    @Pogues("getCodeValue()")
    String codeValue;

    /** Filtering condition of the modality. */
    @Pogues("getConditionFilter()")
    String conditionFilter;

}
