package fr.insee.eno.core.processing.out.steps.lunatic.loop;

import fr.insee.eno.core.exceptions.business.LunaticLoopException;
import fr.insee.eno.core.utils.vtl.VtlSyntaxUtils;
import fr.insee.lunatic.model.flat.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class LunaticLoopFilter {

    public static void computeAndSetConditionFilter(Loop lunaticLoop) {
        Class<? extends ComponentType> loopStructureType = determineLoopStructure(lunaticLoop);
        List<ConditionFilterType> loopStructureFilters = lunaticLoop.getComponents().stream()
                .filter(loopStructureType::isInstance)
                .map(loopStructureType::cast)
                .map(ComponentType::getConditionFilter)
                .toList();
        Optional<ConditionFilterType> computedFilter = computeLoopFilterExpression(loopStructureFilters);
        if (computedFilter.isEmpty())
            return;
        lunaticLoop.setConditionFilter(computedFilter.get());
    }

    public static void removeOccurrenceFilterExpression(Loop lunaticLoop, String occurrenceFilterExpression) {
        if (occurrenceFilterExpression.isEmpty())
            return;
        String expression = lunaticLoop.getConditionFilter().getValue();
        lunaticLoop.getConditionFilter().setValue(
                VtlSyntaxUtils.replaceByTrue(expression, occurrenceFilterExpression)
        );
    }

    private static Class<? extends ComponentType> determineLoopStructure(Loop lunaticLoop) {
        if (isLoopOfSequence(lunaticLoop))
            return Sequence.class;
        if (isLoopOfSubsequence(lunaticLoop)) {
            safetyCheck(lunaticLoop);
            return Subsequence.class;
        }
        throw new LunaticLoopException(
                "First element of loop " + lunaticLoop + " is neither a sequence or a subsequence.");
    }

    private static boolean isLoopOfSequence(Loop lunaticLoop) {
        return lunaticLoop.getComponents().getFirst() instanceof Sequence;
    }

    private static boolean isLoopOfSubsequence(Loop lunaticLoop) {
        return lunaticLoop.getComponents().getFirst() instanceof Subsequence;
    }

    private static void safetyCheck(Loop lunaticLoop) {
        if (lunaticLoop.getComponents().stream().anyMatch(Sequence.class::isInstance))
            throw new LunaticLoopException(
                    "Loop " + lunaticLoop + " starts on a subsequence, shouldn't contain a sequence.");
    }

    private static Optional<ConditionFilterType> computeLoopFilterExpression(List<ConditionFilterType> loopStructureFilters) {
        // If any structure component is not filtered, the whole loop will never be filtered.
        if (loopStructureFilters.stream().anyMatch(Objects::isNull))
            return Optional.empty();

        ConditionFilterType loopFilter = new ConditionFilterType();
        String expression = loopStructureFilters.stream()// concatenate VTL expressions
                .map(LabelType::getValue)
                .distinct() // don't put the same expression twice
                .collect(Collectors.joining(" " + VtlSyntaxUtils.AND_KEYWORD + " "));
        loopFilter.setValue(expression);
        loopFilter.setType(LabelTypeEnum.VTL);
        loopFilter.setShapeFrom(loopStructureFilters.getFirst().getShapeFrom());
        List<String> bindingDependencies = loopStructureFilters.stream() // concatenate bonding dependencies
                .flatMap(filter -> filter.getBindingDependencies().stream())
                .distinct()
                .toList();
        loopFilter.setBindingDependencies(bindingDependencies);
        return Optional.of(loopFilter);
    }

}
