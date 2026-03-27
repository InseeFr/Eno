package fr.insee.eno.core.utils.lunatic;

import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.stream.Stream;

/** In Lunatic, components can be used 'directly', or wrapper in the Question component.
 * This class provides methods to ease management of these two cases. */
@Slf4j
public class LunaticQuestionHelper {

    private LunaticQuestionHelper() {}

    /** This method looks at all components in the given questionnaire, and returns a stream of all components that
     * correspond to the given type. */
    public static <T extends ComponentType> Stream<T> findAllInQuestionnaire(Class<T> type, Questionnaire lunaticQuestionnaire) {

        // Questionnaire-level components
        Stream<T> questionnaireComponents = filterComponentsOfType(
                type, lunaticQuestionnaire.getComponents().stream());

        // Components nested in other components (such as loop, roundabout, pairwise)
        Stream<T> nestedComponents = lunaticQuestionnaire.getComponents().stream()
                .filter(ComponentNestingType.class::isInstance)
                .map(ComponentNestingType.class::cast)
                .flatMap(nestingComponent -> LunaticQuestionHelper.filterComponentsOfType(
                        type, nestingComponent.getComponents().stream()));

        return Stream.concat(questionnaireComponents, nestedComponents);
    }

    /** This method filters the given stream, keeping components that have the given type.
     * It manages the 'Question' component case. */
    private static <T extends ComponentType> Stream<T> filterComponentsOfType(Class<T> type, Stream<ComponentType> lunaticComponents) {
        return lunaticComponents
                .map(lunaticComponent -> isQuestionOfType(type, lunaticComponent))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    private static <T extends ComponentType> Optional<T> isQuestionOfType(Class<T> type, ComponentType lunaticComponent) {
        if (type.isInstance(lunaticComponent))
            return Optional.of(type.cast(lunaticComponent));
        if (lunaticComponent instanceof Question questionComponent) {
            oneInnerComponentCheck(questionComponent);
            return isQuestionOfType(type, questionComponent.getComponents().getFirst());
        }
        return Optional.empty();
    }

    // Note: this one could be make public and reused at some places.
    /**
     * The Question component has been designed with a list of components in Lunatic.
     * In practice, we expect Question components to have only one component (at least in the vast majority of cases).
     */
    private static void oneInnerComponentCheck(Question questionComponent) {
        int size = questionComponent.getComponents().size();
        if (size != 1)
            log.warn("Found question component with {} components (id='{}').", size, questionComponent.getId());
    }

}
