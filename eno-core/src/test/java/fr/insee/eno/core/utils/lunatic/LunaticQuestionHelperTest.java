package fr.insee.eno.core.utils.lunatic;

import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Note: unit tests mostly AI-generated
class LunaticQuestionHelperTest {

    @Test
    void shouldFindDirectComponentsInQuestionnaire() {
        // Given
        Input input1 = new Input();
        Input input2 = new Input();
        InputNumber other = new InputNumber();

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.getComponents().addAll(List.of(input1, other, input2));

        // When
        List<Input> result = LunaticQuestionHelper
                .findAllInQuestionnaire(Input.class, questionnaire)
                .toList();

        // Then
        assertThat(result).containsExactlyInAnyOrder(input1, input2);
    }

    @Test
    void shouldFindComponentsInsideQuestionWrapper() {
        // Given
        Input innerComponent = new Input();

        Question question = new Question();
        question.getComponents().add(innerComponent);

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.getComponents().add(question);

        // When
        List<Input> result = LunaticQuestionHelper
                .findAllInQuestionnaire(Input.class, questionnaire)
                .toList();

        // Then
        assertThat(result).containsExactly(innerComponent);
    }

    @Test
    void shouldFindComponentsInLoop() {
        // Given
        Input nestedComponent = new Input();

        ComponentNestingType nesting = new Loop();
        nesting.getComponents().add(nestedComponent);

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.getComponents().add((ComponentType) nesting);

        // When
        List<Input> result = LunaticQuestionHelper
                .findAllInQuestionnaire(Input.class, questionnaire)
                .toList();

        // Then
        assertThat(result).containsExactly(nestedComponent);
    }

    @Test
    void shouldFindComponentsWithinQuestionsInLoop() {
        // Given
        Input innerComponent = new Input();

        Question question = new Question();
        question.getComponents().add(innerComponent);

        ComponentNestingType nesting = new Loop();
        nesting.getComponents().add(question);

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.getComponents().add((ComponentType) nesting);

        // When
        List<Input> result = LunaticQuestionHelper
                .findAllInQuestionnaire(Input.class, questionnaire)
                .toList();

        // Then
        assertThat(result).containsExactly(innerComponent);
    }

    @Test
    void shouldReturnEmptyWhenNoMatchingType() {
        // Given
        InputNumber otherComponent = new InputNumber();

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.getComponents().add(otherComponent);

        // When
        List<Input> result = LunaticQuestionHelper
                .findAllInQuestionnaire(Input.class, questionnaire)
                .toList();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleEmptyQuestionnaire() {
        // Given
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.getComponents().addAll(List.of());

        // When
        List<Input> result = LunaticQuestionHelper
                .findAllInQuestionnaire(Input.class, questionnaire)
                .toList();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleQuestionWithMultipleInnerComponents() {
        // Given
        Input component1 = new Input();
        Input component2 = new Input();

        Question question = new Question();
        question.getComponents().addAll(List.of(component1, component2));
        question.setId("test-question-id");

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.getComponents().add(question);

        // When
        List<Input> result = LunaticQuestionHelper
                .findAllInQuestionnaire(Input.class, questionnaire)
                .toList();

        // Then
        // Only the first component is used
        assertThat(result).containsExactly(component1);
    }

}
