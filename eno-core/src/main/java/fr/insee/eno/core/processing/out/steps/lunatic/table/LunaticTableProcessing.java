package fr.insee.eno.core.processing.out.steps.lunatic.table;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.ComplexMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.model.question.MultipleResponseQuestion;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;

@Slf4j
public class LunaticTableProcessing implements ProcessingStep<Questionnaire> {

    private final EnoQuestionnaire enoQuestionnaire;

    public LunaticTableProcessing(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
    }

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        lunaticQuestionnaire.getComponents().forEach(this::processTableComponent);
    }

    private void processTableComponent(ComponentType componentType) {
        ComponentTypeEnum type = componentType.getComponentType();
        if (Objects.requireNonNull(type) == ComponentTypeEnum.LOOP)
            ((Loop) componentType).getComponents().forEach(this::processTableComponent);
        if (type == ComponentTypeEnum.TABLE)
            processTable((Table) componentType);
        if (type == ComponentTypeEnum.ROSTER_FOR_LOOP)
            processRosterForLoop((RosterForLoop) componentType);
    }

    private void processTable(Table table) {
        MultipleResponseQuestion enoQuestion = findEnoQuestion(table);
        if (enoQuestion instanceof ComplexMultipleChoiceQuestion enoComplexMCQ)
            ComplexMultipleChoiceQuestionProcessing.process(table, enoComplexMCQ);
        if (enoQuestion instanceof TableQuestion enoTableQuestion)
            TableQuestionProcessing.process(table, enoTableQuestion);
    }

    private void processRosterForLoop(RosterForLoop rosterForLoop) {
        DynamicTableQuestion enoDynamicTableQuestion = (DynamicTableQuestion) findEnoQuestion(rosterForLoop);
        DynamicTableQuestionProcessing.process(rosterForLoop, enoDynamicTableQuestion);
    }

    private MultipleResponseQuestion findEnoQuestion(ComponentType lunaticComponent) {
        Optional<MultipleResponseQuestion> searchedEnoQuestion =  enoQuestionnaire.getMultipleResponseQuestions()
                .stream()
                .filter(enoQuestion -> lunaticComponent.getId().equals(enoQuestion.getId()))
                .findAny();
        if (searchedEnoQuestion.isEmpty())
            throw new MappingException("Unable to find Eno question with id " + lunaticComponent.getId());
        return searchedEnoQuestion.get();
    }

}
