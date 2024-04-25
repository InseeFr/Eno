package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.Loop;
import fr.insee.lunatic.model.flat.Question;
import fr.insee.lunatic.model.flat.Questionnaire;

import java.util.List;

/**
 * Lunatic V3 introduced a question component.
 * This processing wraps each response components (such as Input, InputNumber, Table, etc.) in a Question component.
 */
public class LunaticQuestionComponent implements ProcessingStep<Questionnaire> {

    /**
     * Wraps each response component (e.g. Input, InputNumber, Table, etc.) is a question component.
     * @param lunaticQuestionnaire Lunatic questionnaire object.
     */
    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        // Questionnaire-level components
        wrapComponentsInQuestions(lunaticQuestionnaire.getComponents());
        // Loop components
        lunaticQuestionnaire.getComponents().stream()
                .filter(Loop.class::isInstance).map(Loop.class::cast)
                .forEach(loop -> wrapComponentsInQuestions(loop.getComponents()));
    }

    private static void wrapComponentsInQuestions(List<ComponentType> components) {
        components.replaceAll(componentType -> {
            //
            if (! Question.isQuestionComponent(componentType))
                return componentType;
            //
            Question question = new Question();
            question.setId("question-"+componentType.getId());
            question.setPage(componentType.getPage());
            question.setConditionFilter(question.getConditionFilter());
            question.addComponent(componentType);
            return question;
        });
    }

}
