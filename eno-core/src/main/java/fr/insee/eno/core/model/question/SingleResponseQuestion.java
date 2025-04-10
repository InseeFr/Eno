package fr.insee.eno.core.model.question;

import fr.insee.ddi.lifecycle33.datacollection.QuestionItemType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.response.Response;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.ComponentSimpleResponseType;
import fr.insee.pogues.model.QuestionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Context(format = Format.POGUES, type = QuestionType.class)
@Context(format = Format.DDI, type = QuestionItemType.class)
@Context(format = Format.LUNATIC, type = ComponentSimpleResponseType.class)
public abstract class SingleResponseQuestion extends Question {

    @Pogues("getName()")
    @DDI("getQuestionItemNameArray(0).getStringArray(0).getStringValue()")
    String name;

    @Pogues("getResponse().getFirst()")
    @DDI("getOutParameterArray(0)")
    @Lunatic("setResponse(#param)")
    Response response;

    /**
     * Indicates whether the response is mandatory for this component.
     */
    @Pogues("getResponse().getFirst().isMandatory()")
    @DDI("getResponseDomain()?.getResponseCardinality()?.getMinimumResponses() != null ? " +
            "getResponseDomain().getResponseCardinality().getMinimumResponses().intValue() > 0 : false")
    @Lunatic("!(#this instanceof T(fr.insee.lunatic.model.flat.PairwiseLinks)) ? setMandatory(#param) : null")
    boolean mandatory;
}
