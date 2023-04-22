package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.Constant;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
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
public class TextQuestion extends SingleResponseQuestion {

    /** Maximal length authorized.
     * BigInteger since it is like this in both DDI and Lunatic.
     * See Lunatic converter about Input vs Textarea. */
    @DDI(contextType = QuestionItemType.class, field = "getResponseDomain()?.getMaxLength()?.intValue()")
    @Lunatic(contextType = {Input.class, Textarea.class}, field = "setMaxLength(#param)")
    BigInteger maxLength;

    // Lunatic component type set by Lunatic converter
    // Note: the mapping of this information is done several ways among question types,
    // but this should be managed by Lunatic-Model itself, so no critical need to implement a neater way for this.

}
