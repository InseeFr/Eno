package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.parameter.Format;
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

    /** Maximal length authorized.
     * BigInteger since it is like this in both DDI and Lunatic.
     * See Lunatic converter about Input vs Textarea. */
    @DDI("getResponseDomain()?.getMaxLength()?.intValue()")
    @Lunatic("setMaxLength(#param)")
    BigInteger maxLength;

    // Lunatic component type set by Lunatic converter (see comment in LunaticConverter class)
    // Note: the mapping of this information is done several ways among question types,
    // but this should be managed by Lunatic-Model itself, so no critical need to implement a neater way for this.

}
