package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoComponent;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.navigation.ComponentFilter;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LunaticFilterVariablesDependenciesTest {

    private static final String PRENOM = "PRENOM";
    private static final String SUM_AGE = "SUM_AGE";
    private static final String NO_TYPE = "NO_TYPE";
    private static final String X_AXIS = "xAxis";
    private static final String Y_AXIS = "yAxis";
    private static final String PRENOM_FILTER = "PRENOM = \"a\"";
    private static final String LOOP_ID = "loop-id";

    EnoIndex enoIndex;
    Questionnaire lunaticQuestionnaire;
    LunaticFilterVariablesDependencies processing;

    @BeforeEach
    void init() {
        enoIndex = new EnoIndex();
        lunaticQuestionnaire = new Questionnaire();
        processing = new LunaticFilterVariablesDependencies(enoIndex);
        lunaticQuestionnaire.getVariables().addAll(List.of(
                variable(PRENOM, VariableTypeEnum.COLLECTED),
                variable("AGE1", VariableTypeEnum.COLLECTED),
                variable("TITI", VariableTypeEnum.EXTERNAL),
                variable(SUM_AGE, VariableTypeEnum.CALCULATED)));
    }

    // ----- Helpers -----

    private static VariableType variable(String name, VariableTypeEnum type) {
        VariableType variable = switch (type) {
            case COLLECTED -> new CollectedVariableType();
            case CALCULATED -> new CalculatedVariableType();
            case EXTERNAL -> new ExternalVariableType();
        };
        variable.setName(name);
        return variable;
    }

    private static ConditionFilterType lunaticFilter(String vtl) {
        ConditionFilterType filter = new ConditionFilterType();
        filter.setType(LabelTypeEnum.VTL);
        filter.setValue(vtl);
        return filter;
    }

    /** Registers an Eno question with the given direct references, returns the matching Lunatic input. */
    private Input inputWithEnoFilter(String id, String vtl, String... enoReferences) {
        TextQuestion enoQuestion = new TextQuestion();
        enoQuestion.setId(id);
        ComponentFilter enoFilter = new ComponentFilter();
        for (String name : enoReferences)
            enoFilter.getBindingReferences().add(new BindingReference("ref-" + name, name));
        enoQuestion.setComponentFilter(enoFilter);
        enoIndex.put(enoQuestion);
        Input input = new Input();
        input.setId(id);
        input.setConditionFilter(lunaticFilter(vtl));
        return input;
    }

    // ----- Components built from an Eno filter -----

    @Test
    void threeVariableTypes() {
        Input input = inputWithEnoFilter("q1", "SUM_AGE + PRENOM + TITI", SUM_AGE, PRENOM, "TITI");
        lunaticQuestionnaire.getComponents().add(input);

        processing.apply(lunaticQuestionnaire);

        VariablesDependencies dependencies = input.getConditionFilter().getVariablesDependencies();
        assertEquals(List.of(PRENOM), dependencies.getCollected());
        assertEquals(List.of("TITI"), dependencies.getExternal());
        assertEquals(List.of(SUM_AGE), dependencies.getCalculated());
    }

    @Test
    void emptyTypesAreEmptyListsNotAbsent() {
        Input input = inputWithEnoFilter("q1", "TITI = 1", "TITI");
        lunaticQuestionnaire.getComponents().add(input);

        processing.apply(lunaticQuestionnaire);

        VariablesDependencies dependencies = input.getConditionFilter().getVariablesDependencies();
        assertNotNull(dependencies);
        assertEquals(List.of(), dependencies.getCollected());
        assertEquals(List.of("TITI"), dependencies.getExternal());
        assertEquals(List.of(), dependencies.getCalculated());
    }

    @Test
    void transitiveDependenciesAreIgnored() {
        Input input = inputWithEnoFilter("q1", "SUM_AGE > 18", SUM_AGE);
        // What EnoResolveBindingReferences would add: the sources of the calculated variable
        ((EnoComponent) enoIndex.get("q1")).getComponentFilter().getResolvedBindingReferences()
                .add(new BindingReference("ref-AGE1", "AGE1"));
        lunaticQuestionnaire.getComponents().add(input);

        processing.apply(lunaticQuestionnaire);

        VariablesDependencies dependencies = input.getConditionFilter().getVariablesDependencies();
        assertEquals(List.of(SUM_AGE), dependencies.getCalculated());
        assertEquals(List.of(), dependencies.getCollected()); // AGE1 must not appear
    }

    @Test
    void duplicatedReferencesAppearOnce() {
        Input input = inputWithEnoFilter("q1", "PRENOM = \"a\" or PRENOM = \"b\"", PRENOM, PRENOM);
        lunaticQuestionnaire.getComponents().add(input);

        processing.apply(lunaticQuestionnaire);

        assertEquals(List.of(PRENOM),
                input.getConditionFilter().getVariablesDependencies().getCollected());
    }

    @Test
    void filterWithoutAnyVariableHasThreeEmptyLists() {
        Input input = inputWithEnoFilter("q1", "true");
        lunaticQuestionnaire.getComponents().add(input);

        processing.apply(lunaticQuestionnaire);

        VariablesDependencies dependencies = input.getConditionFilter().getVariablesDependencies();
        assertEquals(List.of(), dependencies.getCollected());
        assertEquals(List.of(), dependencies.getExternal());
        assertEquals(List.of(), dependencies.getCalculated());
    }

    @Test
    void componentWithoutFilterIsUntouched() {
        Input input = new Input();
        input.setId("q1");
        lunaticQuestionnaire.getComponents().add(input);

        processing.apply(lunaticQuestionnaire);

        assertNull(input.getConditionFilter());
    }

    @Test
    void componentNotFoundInEnoIndexGetsEmptyLists() {
        Input input = new Input();
        input.setId("unknown-id");
        input.setConditionFilter(lunaticFilter(PRENOM_FILTER));
        lunaticQuestionnaire.getComponents().add(input);

        processing.apply(lunaticQuestionnaire);

        VariablesDependencies dependencies = input.getConditionFilter().getVariablesDependencies();
        assertNotNull(dependencies);
        assertEquals(List.of(), dependencies.getCollected());
        assertEquals(List.of(), dependencies.getExternal());
        assertEquals(List.of(), dependencies.getCalculated());
    }

    // ----- Errors -----

    @Test
    void unknownVariableThrows() {
        lunaticQuestionnaire.getComponents().add(inputWithEnoFilter("q1", "UNKNOWN = 1", "UNKNOWN"));

        assertThrows(MappingException.class, () -> processing.apply(lunaticQuestionnaire));
    }

    @Test
    void variableWithUndeterminableTypeThrows() {
        // Anonymous subclass: no variable type set and not one of the three known classes
        VariableType strange = new VariableType() { };
        strange.setName("STRANGE");
        lunaticQuestionnaire.getVariables().add(strange);
        lunaticQuestionnaire.getComponents().add(inputWithEnoFilter("q1", "STRANGE = 1", "STRANGE"));

        assertThrows(MappingException.class, () -> processing.apply(lunaticQuestionnaire));
    }

    // ----- Type fallback -----

    @Test
    void typeIsDeducedFromClassWhenVariableTypeIsNull() {
        CalculatedVariableType noType = new CalculatedVariableType();
        noType.setName(NO_TYPE);
        noType.setVariableType(null);
        lunaticQuestionnaire.getVariables().add(noType);
        Input input = inputWithEnoFilter("q1", "NO_TYPE = 1", NO_TYPE);
        lunaticQuestionnaire.getComponents().add(input);

        processing.apply(lunaticQuestionnaire);

        assertEquals(List.of(NO_TYPE),
                input.getConditionFilter().getVariablesDependencies().getCalculated());
    }

    // ----- Synthetic filters -----

    @Test
    void loopUsesItsBindingDependencies() {
        Loop loop = new Loop();
        loop.setId(LOOP_ID); // deliberately absent from the Eno index
        ConditionFilterType loopFilter = lunaticFilter(PRENOM_FILTER);
        loopFilter.setBindingDependencies(List.of(PRENOM));
        loop.setConditionFilter(loopFilter);
        lunaticQuestionnaire.getComponents().add(loop);

        processing.apply(lunaticQuestionnaire);

        assertEquals(List.of(PRENOM), loopFilter.getVariablesDependencies().getCollected());
    }

    @Test
    void loopWithDefaultFilterHasThreeEmptyLists() {
        Loop loop = new Loop();
        loop.setId(LOOP_ID);
        ConditionFilterType loopFilter = lunaticFilter("true");
        loop.setConditionFilter(loopFilter);
        lunaticQuestionnaire.getComponents().add(loop);

        processing.apply(lunaticQuestionnaire);

        VariablesDependencies dependencies = loopFilter.getVariablesDependencies();
        assertEquals(List.of(), dependencies.getCollected());
        assertEquals(List.of(), dependencies.getExternal());
        assertEquals(List.of(), dependencies.getCalculated());
    }

    @Test
    void pairwiseInnerComponentUsesAxisVariables() {
        for (String axis : List.of(X_AXIS, Y_AXIS)) {
            CalculatedVariableType axisVariable = new CalculatedVariableType();
            axisVariable.setName(axis);
            lunaticQuestionnaire.getVariables().add(axisVariable);
        }
        Input inner = new Input();
        inner.setId("pairwise-inner-id");
        ConditionFilterType innerFilter = lunaticFilter("...");
        innerFilter.setBindingDependencies(List.of(X_AXIS, Y_AXIS));
        inner.setConditionFilter(innerFilter);
        PairwiseLinks pairwise = new PairwiseLinks();
        pairwise.setId("pairwise-id");
        pairwise.getComponents().add(inner);
        lunaticQuestionnaire.getComponents().add(pairwise);

        processing.apply(lunaticQuestionnaire);

        assertEquals(List.of(X_AXIS, Y_AXIS), innerFilter.getVariablesDependencies().getCalculated());
    }

    // ----- Recursion -----

    @Test
    void componentsInsideLoopAreProcessed() {
        Input input = inputWithEnoFilter("q-in-loop", PRENOM_FILTER, PRENOM);
        Loop loop = new Loop();
        loop.setId(LOOP_ID);
        loop.getComponents().add(input);
        lunaticQuestionnaire.getComponents().add(loop);

        processing.apply(lunaticQuestionnaire);

        assertEquals(List.of(PRENOM),
                input.getConditionFilter().getVariablesDependencies().getCollected());
        assertNull(loop.getConditionFilter()); // loop without filter stays untouched
    }

    // ----- Sequences -----

    @Test
    void sequenceFilterIsProcessed() {
        fr.insee.eno.core.model.sequence.Sequence enoSequence =
                new fr.insee.eno.core.model.sequence.Sequence();
        enoSequence.setId("seq-id");
        enoSequence.getComponentFilter().getBindingReferences()
                .add(new BindingReference("ref-PRENOM", PRENOM));
        enoIndex.put(enoSequence);

        fr.insee.lunatic.model.flat.Sequence lunaticSequence = new fr.insee.lunatic.model.flat.Sequence();
        lunaticSequence.setId("seq-id");
        lunaticSequence.setConditionFilter(lunaticFilter(PRENOM_FILTER));
        lunaticQuestionnaire.getComponents().add(lunaticSequence);

        processing.apply(lunaticQuestionnaire);

        assertEquals(List.of(PRENOM),
                lunaticSequence.getConditionFilter().getVariablesDependencies().getCollected());
    }

    @Test
    void filterDescriptionSharesTheFilterOfItsComponent() {
        Input input = inputWithEnoFilter("q1", PRENOM_FILTER, PRENOM);
        FilterDescription description = new FilterDescription();
        description.setId("filter-id-description"); // not in the Eno index
        description.setConditionFilter(input.getConditionFilter()); // same object, as in LunaticAddFilterDescription
        lunaticQuestionnaire.getComponents().addAll(List.of(description, input));

        processing.apply(lunaticQuestionnaire);

        assertEquals(List.of(PRENOM), description.getConditionFilter().getVariablesDependencies().getCollected());
        assertSame(input.getConditionFilter(), description.getConditionFilter());
    }
}