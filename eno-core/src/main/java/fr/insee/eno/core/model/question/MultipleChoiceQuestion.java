package fr.insee.eno.core.model.question;

import datacollection33.QuestionGridType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.response.CodeResponse;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.CheckboxGroup;
import fr.insee.lunatic.model.flat.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Eno model class to represent multiple choice questions (MCQ).
 * See child classes for details.
 */
@Getter
@Setter
@Context(format = Format.DDI, type = QuestionGridType.class)
@Context(format = Format.LUNATIC, type = {CheckboxGroup.class, Table.class})
public abstract class MultipleChoiceQuestion extends MultipleResponseQuestion {

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
    public static class Simple extends MultipleResponseQuestion {

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

    /**
     * "Complex" multiple choice question.
     * Each modality is itself a list of possibilities
     * (in this way the multiple choice question looks like a combination of unique choice questions).
     * In DDI, it corresponds to a QuestionGrid.
     * In Lunatic, it corresponds to a Table component.
     */
    @Getter
    @Setter
    @Slf4j
    @Context(format = Format.DDI, type = QuestionGridType.class)
    @Context(format = Format.LUNATIC, type = Table.class)
    public static class Complex extends MultipleResponseQuestion {

        public Complex() {
            log.warn("'Complex' multiple choice questions mapping is not implemented!");
        }

        /** Lunatic component type property.
         * This should be inserted by Lunatic-Model serializer later on. */
        @Lunatic(contextType = Table.class,
                field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
        String lunaticComponentType = "TABLE";

    }
}
