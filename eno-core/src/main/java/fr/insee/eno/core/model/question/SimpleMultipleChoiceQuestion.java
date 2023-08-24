package fr.insee.eno.core.model.question;

import datacollection33.QuestionGridType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.response.CodeResponse;
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
public class SimpleMultipleChoiceQuestion extends MultipleResponseQuestion {

    /**
     * List of modalities of the multiple choice question.
     * In DDI, this corresponds to a list ouf out parameters.
     * In Lunatic, the CheckboxGroup component has a particular way to hold this information.
     */
    @DDI(contextType = QuestionGridType.class, field = "getOutParameterList()")
    @Lunatic(contextType = CheckboxGroup.class, field = "getResponses()")
    List<CodeResponse> codeList = new ArrayList<>();

    /** Lunatic component type property.
     * This should be inserted by Lunatic-Model serializer later on. */
    @Lunatic(contextType = CheckboxGroup.class,
            field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "CHECKBOX_GROUP";
}