package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.*;

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

    /**
     * Wraps each response component (e.g. Input, InputNumber, Table, etc.) is a question component.
     * Some properties are moved in the question component, others remain in the response component.
     * @param components A list of Lunatic components.
     */
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
            question.setLabel(componentType.getLabel());
            question.setConditionFilter(componentType.getConditionFilter());
            componentType.getDeclarations().forEach(declarationType ->
                    insertQuestionDeclaration(declarationType, question));
            question.addComponent(componentType);
            //
            componentType.setLabel(null);
            componentType.setConditionFilter(null); // not an obligation but for slight performance improvement in Lunatic
            componentType.getDeclarations().clear();
            //
            return question;
        });
    }

    /**
     * Insert the given declaration in the question component.
     * If the declaration type is "STATEMENT", it is changed to "HELP".
     * @param declarationType Lunatic declaration.
     * @param question Lunatic question.
     */
    private static void insertQuestionDeclaration(DeclarationType declarationType, Question question) {
        if (DeclarationTypeEnum.STATEMENT.equals(declarationType.getDeclarationType()))
            declarationType.setDeclarationType(DeclarationTypeEnum.HELP);
        question.getDeclarations().add(declarationType);
    }

}
