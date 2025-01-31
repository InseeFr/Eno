package fr.insee.eno.core.processing.out.steps.lunatic.resizing;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.VariableType;
import fr.insee.lunatic.model.flat.variable.VariableTypeEnum;

import java.util.*;
import java.util.stream.Collectors;

import static fr.insee.eno.core.processing.out.steps.lunatic.resizing.LunaticLoopResizingLogic.insertIterationEntry;

public class LunaticRosterResizingLogic {

    private final Questionnaire lunaticQuestionnaire;
    private final EnoQuestionnaire enoQuestionnaire;

    public LunaticRosterResizingLogic(Questionnaire lunaticQuestionnaire, EnoQuestionnaire enoQuestionnaire) {
        this.lunaticQuestionnaire = lunaticQuestionnaire;
        this.enoQuestionnaire = enoQuestionnaire;
    }

    /**
     * Insert resizing entries for the given roster component.
     * @param lunaticRoster Lunatic RosterForLoop (dynamic table) object.
     * @param lunaticResizing Lunatic resizing block object.
     */
    public void buildResizingEntries(RosterForLoop lunaticRoster, ResizingType lunaticResizing) {

        // Corresponding Eno loop object
        DynamicTableQuestion enoDynamicTable = getDynamicTable(enoQuestionnaire, lunaticRoster.getId());

        // If the dynamic table size is not defined by a VTL expression, nothing to do here
        if (enoDynamicTable.getSizeExpression() == null)
            return;

        // Variable names that are the keys of the resizing (using a set to make sure there is no duplicates)
        Set<String> resizingVariableNames = findResizingVariablesForRoster(enoDynamicTable);

        // Expression that resizes the concerned variables
        String sizeExpression = enoDynamicTable.getSizeExpression().getValue();

        // Concerned variables to be resized: responses of the roster component
        List<String> resizedVariableNames = lunaticRoster.getComponents().stream()
                .map(BodyCell::getResponse)
                .filter(Objects::nonNull)
                .map(ResponseType::getName)
                .toList();

        // Insert resizing entries (the logic is the same as for loops)
        resizingVariableNames.forEach(variableName -> insertIterationEntry(
                lunaticResizing, variableName, sizeExpression, resizedVariableNames));
    }

    private DynamicTableQuestion getDynamicTable(EnoQuestionnaire enoQuestionnaire, String id) {
        Optional<DynamicTableQuestion> searched = enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(DynamicTableQuestion.class::isInstance).map(DynamicTableQuestion.class::cast)
                .filter(dynamicTableQuestion -> id.equals(dynamicTableQuestion.getId()))
                .findAny();
        if (searched.isEmpty())
            throw new MappingException("Cannot find dynamic table question of id " + id);
        return searched.get();
    }

    private Set<String> findResizingVariablesForRoster(DynamicTableQuestion enoDynamicTable) {
        List<String> sizeDependencies = enoDynamicTable.getSizeExpression().getBindingReferences().stream()
                .map(BindingReference::getVariableName)
                .toList();
        // Note: we could simply return this dependencies list,
        // but we use the Lunatic questionnaire to filter non-collected variables
        return lunaticQuestionnaire.getVariables().stream()
                .filter(variable -> VariableTypeEnum.COLLECTED.equals(variable.getVariableType()))
                .map(VariableType::getName)
                .filter(sizeDependencies::contains)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        // NB: using a linked hash set to preserve the same order in different generations
    }
}
