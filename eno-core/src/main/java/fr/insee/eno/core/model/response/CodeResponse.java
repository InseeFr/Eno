package fr.insee.eno.core.model.response;

import fr.insee.ddi.lifecycle33.reusable.ParameterType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.ResponseCheckboxGroup;
import fr.insee.pogues.model.ResponseType;
import lombok.Getter;
import lombok.Setter;

/**
 * Object that represents a modality of a "simple" multiple choice question
 * (i.e. a multiple choice question whose modality responses are boolean).
 */
@Getter
@Setter
@Context(format = Format.POGUES, type = ResponseType.class)
@Context(format = Format.DDI, type = ParameterType.class)
@Context(format = Format.LUNATIC, type = ResponseCheckboxGroup.class)
public class CodeResponse extends EnoIdentifiableObject {

    /** Label of this modality.
     * In Pogues and DDI, it is inserted there through a processing.
     * @see fr.insee.eno.core.processing.in.steps.ddi.DDIInsertMultipleChoiceLabels
     */
    @Lunatic("setLabel(#param)")
    private Label label;

    /** Response of the modality. */
    @Pogues("#this")
    @DDI("#this")
    @Lunatic("setResponse(#param)")
    Response response;

    /** Additional "please specify" field of the modality.
     * In DDI, it is inserted here through a processing.
     * In Pogues, it is mapped at the question level and inserted here in a processing step. */
    @Lunatic("setDetail(#param)")
    DetailResponse detailResponse;

    /** Condition for filtering the modality.
     * In Pogues, it is mapped at the question level and inserted here in a processing step. */
    @Lunatic("setConditionFilter(#param)")
    CodeFilter conditionFilter;

}
