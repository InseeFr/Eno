package fr.insee.eno.core.model.question;

import fr.insee.ddi.lifecycle33.datacollection.QuestionGridType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.navigation.Binding;
import fr.insee.eno.core.model.response.CodeFilter;
import fr.insee.eno.core.model.response.CodeResponse;
import fr.insee.eno.core.model.response.DetailResponse;
import fr.insee.eno.core.model.response.ModalityAttachment;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.CheckboxGroup;
import fr.insee.pogues.model.QuestionType;
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
@Context(format = Format.POGUES, type = QuestionType.class)
@Context(format = Format.DDI, type = QuestionGridType.class)
@Context(format = Format.LUNATIC, type = CheckboxGroup.class)
public class SimpleMultipleChoiceQuestion extends MultipleResponseQuestion {

    /** Reference to the code list upon which modalities are based. */
    @Pogues("getResponseStructure().getDimension().getFirst().getCodeListReference()")
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
    @Pogues("getResponse()")
    @DDI("getOutParameterList()")
    @Lunatic("getResponses()")
    List<CodeResponse> codeResponses = new ArrayList<>();

    /**
     * Detail responses for modalities that have a "please specify" field.
     * In DDI, this information is inserted directly at the 'right place' i.e. in CodeResponse objects through a
     * processing step.
     * @see fr.insee.eno.core.processing.in.steps.ddi.DDIInsertDetailResponses
     * In Pogues, this information is at the question level. It is mapped here, then inserted in CodeResponse objects
     * through a processing step.
     * @see fr.insee.eno.core.processing.in.steps.pogues.PoguesCodeResponseDetails
     */
    @Pogues("getClarificationQuestion()")
    List<DetailResponse> detailResponses = new ArrayList<>();

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

    /**
     * List of conditions for the modalities to be filtered by previous responses or external data.
     *  In Lunatic, they are inserted in code response objects through a processing. */
    @Pogues("getCodeFilters()")
    List<CodeFilter> codeFilters = new ArrayList<>();

    /** Indicates whether the response is mandatory for this component. */
    @Pogues("isMandatory() ?: false")
    @Lunatic("setMandatory(#param)")
    boolean mandatory;

}
