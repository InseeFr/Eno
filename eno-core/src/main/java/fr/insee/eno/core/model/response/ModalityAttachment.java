package fr.insee.eno.core.model.response;

import fr.insee.ddi.lifecycle33.datacollection.GridResponseDomainInMixedType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

/**
 * Class designed for the DDI mapping of the multiple choice question modalities, to make the link between
 * a detail ("please specify") response and the corresponding code response modality.
 */
@Getter
@Setter
@Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
public abstract class ModalityAttachment extends EnoObject {

    /** Identifier that corresponds to the source reference in the question grid bindings. */
    @DDI("getResponseDomain().getOutParameter().getIDArray(0).getStringValue()")
    String responseDomainId;

    /**
     * DDI attachment of a modality response.
     */
    @Getter
    @Setter
    @Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
    public static class CodeAttachment extends ModalityAttachment {

        /** Attachment value of a modality response. */
        @DDI("getAttachmentBase()")
        BigInteger attachmentBase;
    }

    /**
     * DDI attachment of a detail response.
     */
    @Getter
    @Setter
    @Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
    public static class DetailAttachment extends ModalityAttachment {

        /** Attachment value of detail response. */
        @DDI("getResponseAttachmentLocation().getDomainSpecificValueArray(0).getAttachmentDomain()")
        BigInteger attachmentDomain;

        /** Label displayed, e.g. "Please, specify" for a detail response. */
        @DDI("!getResponseDomain().getLabelList().isEmpty() ? getResponseDomain().getLabelArray(0) : null")
        Label label;

        /** Maximum allowed length of the detail response field. */
        @DDI("getResponseDomain().getMaxLength()")
        BigInteger maxLength;
    }

}
