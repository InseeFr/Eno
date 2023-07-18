package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.Setter;

/**
 * Duration question.
 * In DDI, it is very similar to date questions.
 * It is not supported in Lunatic yet. */
@Getter
@Setter
@Context(format = Format.DDI, type = QuestionItemType.class)
public class DurationQuestion extends SingleResponseQuestion {

    /**
     * Minimum duration value allowed.
     */
    @DDI(contextType = QuestionItemType.class, field = "getResponseDomain() != null ? " +
            "getResponseDomain().getRangeArray(0)?.getMinimumValue()?.getStringValue() : " +
            "#index.get(#this.getResponseDomainReference().getIDArray(0).getStringValue())" +
            ".getRangeArray(0)?.getMinimumValue()?.getStringValue()")
    private String minValue;

    /**
     * Maximum duration value allowed.
     */
    @DDI(contextType = QuestionItemType.class, field = "getResponseDomain() != null ? " +
            "getResponseDomain().getRangeArray(0)?.getMaximumValue()?.getStringValue() : " +
            "#index.get(#this.getResponseDomainReference().getIDArray(0).getStringValue())" +
            ".getRangeArray(0)?.getMaximumValue()?.getStringValue()")
    private String maxValue;

    /**
     * Duration format.
     */
    @DDI(contextType = QuestionItemType.class, field = "getResponseDomain() != null ? " +
            "getResponseDomain().getDateFieldFormat().getStringValue() : " +
            "#index.get(#this.getResponseDomainReference().getIDArray(0).getStringValue())" +
            ".getDateFieldFormat().getStringValue()")
    private String format;
}
