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

@Getter
@Setter
public class TextQuestion extends SingleResponseQuestion {

    /** BigInteger since it is like this in both DDI and Lunatic. */
    @DDI(contextType = QuestionItemType.class, field = "getResponseDomain()?.getMaxLength()?.intValue()")
    @Lunatic(contextType = {Input.class, Textarea.class}, field = "setMaxLength(#param)")
    BigInteger maxLength;

    // Lunatic component type set by Lunatic converter
    // Note: the mapping of this information is done several ways among question types,
    // but this should be managed by Lunatic-Model itself, so no critical need to implement a neater way for this.

}
