package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.Constant;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Input;
import fr.insee.lunatic.model.flat.Textarea;
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
@Context(format = Format.DDI, type = QuestionItemType.class)
@Context(format = Format.LUNATIC, type = {Input.class, Textarea.class})
public class TextQuestion extends SingleResponseQuestion {

    /** Enum to discriminate short text question with long text questions. */
    public enum LengthType {SHORT, LONG}

    /** Maximal length authorized.
     * BigInteger since it is like this in both DDI and Lunatic.
     * See Lunatic converter about Input vs Textarea. */
    @DDI("getResponseDomain().getMaxLength().intValue()")
    @Lunatic("setMaxLength(#param)")
    BigInteger maxLength;

    @DDI("T(fr.insee.eno.core.model.question.TextQuestion).qualifyLength(" +
            "#this.getResponseDomain()?.getMaxLength()?.intValue())")
    @Lunatic("setComponentType(" +
            "T(fr.insee.eno.core.model.question.TextQuestion).lengthTypeToLunatic(#param))")
    LengthType lengthType;

    /**
     * Qualifies the length of the question using the dedicated enum.
     * @param maxLength Maximum length of the question.
     * @return A value of the LengthType enum.
     */
    public static LengthType qualifyLength(int maxLength) {
        return maxLength < Constant.LUNATIC_SMALL_TEXT_LIMIT ? LengthType.SHORT : LengthType.LONG;
    }

    public static ComponentTypeEnum lengthTypeToLunatic(LengthType lengthType) {
        return switch (lengthType) {
            case SHORT -> ComponentTypeEnum.INPUT;
            case LONG -> ComponentTypeEnum.TEXTAREA;
        };
    }

}
