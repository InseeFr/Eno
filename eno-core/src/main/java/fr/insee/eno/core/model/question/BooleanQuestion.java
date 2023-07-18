package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.CheckboxBoolean;
import lombok.Getter;
import lombok.Setter;

/**
 * Eno model class to represent boolean questions.
 * In DDI, it corresponds to a QuestionItem. object.
 * In Lunatic, it corresponds to a CheckboxBoolean component.
 * Note: an issue is opened to replace Lunatic CheckboxBoolean by a new Switch component.
 */
@Getter
@Setter
@Context(format = Format.DDI, type = QuestionItemType.class)
@Context(format = Format.LUNATIC, type = CheckboxBoolean.class)
public class BooleanQuestion extends SingleResponseQuestion {

    /** Lunatic component type property.
     * This should be inserted by Lunatic-Model serializer later on. */
    @Lunatic(contextType = CheckboxBoolean.class,
            field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "CHECKBOX_BOOLEAN";

}
