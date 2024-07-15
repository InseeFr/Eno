package fr.insee.eno.core.model.response;

import fr.insee.ddi.lifecycle33.datacollection.ResponseDomainInMixedType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.Setter;

/**
 * Unique/multiple choice question modalities can have an additional field for a detailed response.
 * ("Other, please specify").
 * DDI mapping annotations concern the unique choice case.
 */
@Getter
@Setter
@Context(format = Format.DDI, type = ResponseDomainInMixedType.class)
@Context(format = Format.LUNATIC, type = fr.insee.lunatic.model.flat.DetailResponse.class)
public class DetailResponse extends EnoObject {

    /** Code list value associated to the detail response.
     * Used to make the link between the detail and the option in Lunatic unique choice question components. */
    @DDI("getAttachmentLocation().getDomainSpecificValueArray(0).getValueArray(0).getStringValue()")
    String value;

    /** Label displayed, e.g. "Please, specify". */
    @DDI("getResponseDomain().getLabelArray(0)")
    @Lunatic("setLabel(#param)")
    Label label;

    /** Collected response for the detail field.
     * In DDI, it is not mapped directly (so to not write a too complicated spel expression) and resolved through
     * a processing. */
    @Lunatic("setResponse(#param)")
    Response response;

    /** DDI reference of the response name. Can be used to retrieve the response name through DDI bindings. */
    @DDI("getResponseDomain().getOutParameter().getIDArray(0).getStringValue()")
    String responseReference;

}
