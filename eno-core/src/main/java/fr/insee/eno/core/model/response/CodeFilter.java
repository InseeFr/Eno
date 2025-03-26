package fr.insee.eno.core.model.response;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Context(format = Format.POGUES, type = fr.insee.pogues.model.CodeFilter.class)
public class CodeFilter extends EnoObject {

    @Pogues("getCodeListId()")
    String codeListId;

    @Pogues("getCodeValue()")
    String codeValue;

    @Pogues("getConditionFilter()")
    String conditionFilter;

}
