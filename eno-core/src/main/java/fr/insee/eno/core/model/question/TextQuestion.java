package fr.insee.eno.core.model.question;

import fr.insee.ddi.lifecycle33.datacollection.QuestionItemType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.Input;
import fr.insee.lunatic.model.flat.Textarea;
import fr.insee.pogues.model.QuestionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

/**
 * Eno model class to represent text questions.
 * In DDI, it corresponds to a QuestionItem.
 * In Lunatic, it corresponds to Input or Textarea component, in function of maximal length authorized.
 */
@Getter
@Setter
@Context(format = Format.POGUES, type = QuestionType.class)
@Context(format = Format.DDI, type = QuestionItemType.class)
@Context(format = Format.LUNATIC, type = {Input.class, Textarea.class})
public class TextQuestion extends SingleResponseQuestion {

    private static final int LUNATIC_SMALL_TEXT_LIMIT = 250;

    /** Enum to discriminate short text question with long text questions. */
    public enum LengthType {SHORT, LONG}

    /** Maximal length authorized.
     * BigInteger since it is like this in both DDI and Lunatic.
     * See Lunatic converter about Input vs Textarea. */
    @Pogues("getResponse().getFirst().getDatatype().getMaxLength()")
    @DDI("getResponseDomain().getMaxLength()")
    @Lunatic("setMaxLength(#param)")
    BigInteger maxLength;

    @Pogues("T(fr.insee.eno.core.model.question.TextQuestion).qualifyLength(" +
            "#this.getResponse().getFirst().getDatatype().getMaxLength().intValue())")
    @DDI("T(fr.insee.eno.core.model.question.TextQuestion).qualifyLength(" +
            "#this.getResponseDomain()?.getMaxLength()?.intValue())")
    LengthType lengthType;

    /** Indicates whether the response is mandatory for this component. */
    @DDI("getResponseDomain()?.getResponseCardinality()?.getMinimumResponses() != null ? " +
            "getResponseDomain().getResponseCardinality().getMinimumResponses().intValue() > 0 : false")
    @Lunatic("setMandatory(#param)")
    boolean mandatory; // TODO: should probably be removed here

    /**
     * Qualifies the length of the question using the dedicated enum.
     * @param maxLength Maximum length of the question.
     * @return A value of the LengthType enum.
     */
    public static LengthType qualifyLength(int maxLength) {
        return maxLength < LUNATIC_SMALL_TEXT_LIMIT ? LengthType.SHORT : LengthType.LONG;
    }

}
