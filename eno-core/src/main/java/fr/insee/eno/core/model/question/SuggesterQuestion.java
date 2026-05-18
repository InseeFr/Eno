package fr.insee.eno.core.model.question;

import fr.insee.ddi.lifecycle33.datacollection.QuestionItemType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.response.ArbitraryResponse;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.Suggester;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Context(format = Format.DDI, type = QuestionItemType.class)
@Context(format = Format.LUNATIC, type = Suggester.class)
public class SuggesterQuestion extends SingleResponseQuestion {

    @DDI("getResponseDomain().getCodeListReference().getIDArray(0).getStringValue()")
    @Lunatic("setStoreName(#param)")
    private String codeListReference;

    @Pogues("getArbitraryResponse()")
    @Lunatic("setArbitrary(#param)")
    private ArbitraryResponse arbitraryResponse;

    /** Indicates whether the response is mandatory for this component. */
    @DDI("getResponseDomain()?.getResponseCardinality()?.getMinimumResponses() != null ? " +
            "getResponseDomain().getResponseCardinality().getMinimumResponses().intValue() > 0 : false")
    @Lunatic("setMandatory(#param)")
    boolean mandatory;

}
