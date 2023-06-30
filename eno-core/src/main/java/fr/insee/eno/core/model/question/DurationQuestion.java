package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import lombok.Getter;
import lombok.Setter;

/**
 * Duration question.
 * In DDI, it is very similar to date questions.
 * It is not supported in Lunatic yet. */
@Getter
@Setter
public class DurationQuestion extends SingleResponseQuestion {

    /**
     * Duration format.
     */
    @DDI(contextType = QuestionItemType.class, field = "getResponseDomain().getDateFieldFormat().getStringValue()")
    private String format;

    /**
     * Minimum duration value allowed.
     */
    @DDI(contextType = QuestionItemType.class, field = "getResponseDomain().getRangeArray(0)?.getMinimumValue()?.getStringValue()")
    private String minValue;

    /**
     * Maximum duration value allowed.
     */
    @DDI(contextType = QuestionItemType.class, field = "getResponseDomain().getRangeArray(0)?.getMaximumValue()?.getStringValue()")
    private String maxValue;

}
