package fr.insee.eno.core.model.question;

import fr.insee.ddi.lifecycle33.datacollection.QuestionItemType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.Duration;
import lombok.Getter;
import lombok.Setter;

/**
 * Duration question.
 * In DDI, it is very similar to date questions.
 */
@Getter
@Setter
@Context(format = Format.DDI, type = QuestionItemType.class)
@Context(format = Format.LUNATIC, type = Duration.class)
public class DurationQuestion extends SingleResponseQuestion {

    /**
     * Minimum duration value allowed.
     * Unused in Lunatic for now.
     */
    @DDI("getResponseDomain() != null ? " +
            "getResponseDomain().getRangeArray(0)?.getMinimumValue()?.getStringValue() : " +
            "#index.get(#this.getResponseDomainReference().getIDArray(0).getStringValue())" +
            ".getRangeArray(0)?.getMinimumValue()?.getStringValue()")
    private String minValue;

    /**
     * Maximum duration value allowed.
     * Unused in Lunatic for now.
     */
    @DDI("getResponseDomain() != null ? " +
            "getResponseDomain().getRangeArray(0)?.getMaximumValue()?.getStringValue() : " +
            "#index.get(#this.getResponseDomainReference().getIDArray(0).getStringValue())" +
            ".getRangeArray(0)?.getMaximumValue()?.getStringValue()")
    private String maxValue;

    /**
     * Duration format.
     */
    @DDI("getResponseDomain() != null ? " +
            "getResponseDomain().getDateFieldFormat().getStringValue() : " +
            "#index.get(#this.getResponseDomainReference().getIDArray(0).getStringValue())" +
            ".getDateFieldFormat().getStringValue()")
    @Lunatic("setFormat(T(fr.insee.lunatic.model.flat.DurationFormat).fromValue(#param))")
    private String format;

}
