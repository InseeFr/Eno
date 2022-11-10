package fr.insee.eno.core.model.label;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Format;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.lunatic.model.flat.LabelType;
import lombok.Getter;
import lombok.Setter;
import reusable33.InternationalStringType;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** Label object at the questionnaire level. */
@Getter
@Setter
@Context(format = Format.DDI, type = InternationalStringType.class)
public class QuestionnaireLabel extends EnoObject {

    @DDI(contextType = InternationalStringType.class, field = "getStringArray(0).getStringValue()")
    @Lunatic(contextType = LabelType.class, field = "setValue(#param)")
    String value;

    @Lunatic(contextType = LabelType.class, field = "setType('TODO')") //TODO: mapping or processing for this
    String type;

}