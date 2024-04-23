package fr.insee.eno.core.model.question;

import datacollection33.QuestionGridType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.navigation.Binding;
import fr.insee.eno.core.model.response.CodeResponse;
import fr.insee.eno.core.model.response.ModalityAttachment;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.CheckboxGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * "Simple" multiple choice question.
 * Each modality has a label, and is checked or not during data collection ("boolean" modalities).
 * In DDI, it corresponds to a QuestionGrid.
 * In Lunatic, it corresponds to the CheckboxGroup component.
 */
@Getter
@Setter
@Context(format = Format.DDI, type = QuestionGridType.class)
@Context(format = Format.LUNATIC, type = CheckboxGroup.class)
public class SimpleMultipleChoiceQuestion extends MultipleResponseQuestion {

    /** Reference to the code list upon which modalities are based. */
    @DDI("getGridDimensionArray(0).getCodeDomain().getCodeListReference().getIDArray(0).getStringValue()")
    String codeListReference;

    /**
     * List of modalities of the multiple choice question.
     * In DDI, this corresponds to a list ouf out parameters.
     * In Lunatic, the CheckboxGroup component has a particular way to hold this information.
     * Note: in DDI, modalities that have an additional "please, specify" field correspond to separate out parameters.
     * In Lunatic, the additional field is inside the response object of the modality.
     * A DDI processing does the insertion of detail responses at the right place.
     */
    @DDI("getOutParameterList()")
    @Lunatic("getResponses()")
    List<CodeResponse> codeResponses = new ArrayList<>();

    /** DDI bindings used to keep the link between detail responses and the code response modality they belong to. */
    @DDI("getBindingList()")
    List<Binding> ddiBindings = new ArrayList<>();

    /** DDI information to link modalities and detail responses. */
    @DDI("getStructuredMixedGridResponseDomain().getGridResponseDomainInMixedList()")
    List<ModalityAttachment> modalityAttachments = new ArrayList<>();

    /** Lunatic component type property.
     * This should be inserted by Lunatic-Model serializer later on. */
    @Lunatic("setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "CHECKBOX_GROUP";

}
