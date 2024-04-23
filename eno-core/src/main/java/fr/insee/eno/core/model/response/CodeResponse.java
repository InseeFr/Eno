package fr.insee.eno.core.model.response;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.ResponsesCheckboxGroup;
import lombok.Getter;
import lombok.Setter;
import reusable33.ParameterType;

/**
 * Object that represents a modality of a "simple" multiple choice question
 * (i.e. a multiple choice question whose modality responses are boolean).
 */
@Getter
@Setter
@Context(format = Format.DDI, type = ParameterType.class)
@Context(format = Format.LUNATIC, type = ResponsesCheckboxGroup.class)
public class CodeResponse extends EnoIdentifiableObject {

    /** Label of this modality.
     * In DDI, it is inserted there through a processing. */
    @Lunatic("setLabel(#param)")
    private Label label;

    /** Response of the modality. */
    @DDI("#this")
    @Lunatic("setResponse(#param)")
    Response response;

    /** Additional "please specify" field of the modality.
     * In DDI, it is inserted here through a processing. */
    @Lunatic("setDetail(#param)")
    DetailResponse detailResponse;

}
