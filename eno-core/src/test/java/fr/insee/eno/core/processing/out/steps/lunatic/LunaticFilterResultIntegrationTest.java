package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.PoguesDDIToLunatic;
import fr.insee.eno.core.PoguesToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.variable.CalculatedVariableType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class LunaticFilterResultIntegrationTest {

    final ClassLoader classLoader = this.getClass().getClassLoader();
    final EnoParameters enoParameters = EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC);

    private Map<String, CalculatedVariableType> filterResultVariables;

    @BeforeAll
    void mapQuestionnaire() throws Exception {
        Questionnaire lunaticQuestionnaire = mapInputToLunatic();
        filterResultVariables = new HashMap<>();
        lunaticQuestionnaire.getVariables().stream()
                .filter(CalculatedVariableType.class::isInstance)
                .map(CalculatedVariableType.class::cast)
                .filter(variableType -> !variableType.getName().startsWith("FILTER_RESULT_"))
                .forEach(variableType -> filterResultVariables.put(variableType.getName(), variableType));
    }

    abstract Questionnaire mapInputToLunatic() throws ParsingException;

    static class DDITest extends LunaticFilterResultIntegrationTest {
        @Override
        Questionnaire mapInputToLunatic() throws DDIParsingException {
            return DDIToLunatic
                    .fromInputStream(classLoader.getResourceAsStream("integration/ddi/ddi-variables.xml"))
                    .transform(enoParameters);
        }
    }

    static class PoguesTest extends LunaticFilterResultIntegrationTest {
        @Override
        Questionnaire mapInputToLunatic() throws PoguesDeserializationException {
            return PoguesToLunatic
                    .fromInputStream(classLoader.getResourceAsStream("integration/pogues/pogues-variables.json"))
                    .transform(enoParameters);
        }
    }

    static class PoguesDDITest extends LunaticFilterResultIntegrationTest {
        @Override
        Questionnaire mapInputToLunatic() throws ParsingException {
            return PoguesDDIToLunatic
                    .fromInputStreams(
                            classLoader.getResourceAsStream("integration/pogues/pogues-variables.json"),
                            classLoader.getResourceAsStream("integration/ddi/ddi-variables.xml"))
                    .transform(enoParameters);
        }
    }

    @Test
    void variablesCount() {
        assertEquals(9, filterResultVariables.size());
    }

    @Test
    void variableNames() {
        assertEquals(
                Set.of("CALCULATED1", "CALCULATED2", "CALCULATED3", "CALCULATED4",
                        "CALCULATED5", "CALCULATED6", "CALCULATED7", "CALCULATED8", "CALCULATED9"),
                filterResultVariables.keySet());
    }

    @Test
    void calculatedExpressions() {
        assertEquals("cast(NUMBER1, number) * 10",
                filterResultVariables.get("CALCULATED1").getExpression().getValue());
        // ...
    }

    @Test
    void calculatedExpressionType() {
        filterResultVariables.values().forEach(variableType ->
                assertEquals(LabelTypeEnum.VTL, variableType.getExpression().getType()));
    }

    @Test
    void bindingDependencies_collectedOnly() {
        assertThat(filterResultVariables.get("CALCULATED1").getBindingDependencies())
                .containsExactlyInAnyOrderElementsOf(List.of("NUMBER1"));
        assertThat(filterResultVariables.get("CALCULATED2").getBindingDependencies())
                .containsExactlyInAnyOrderElementsOf(List.of("NUMBER1", "NUMBER2"));
    }

    @Test
    void bindingDependencies_withCalculated() {
        assertThat(filterResultVariables.get("CALCULATED3").getBindingDependencies())
                .containsExactlyInAnyOrderElementsOf(List.of("CALCULATED1", "NUMBER1"));
        assertThat(filterResultVariables.get("CALCULATED4").getBindingDependencies())
                .containsExactlyInAnyOrderElementsOf(List.of("CALCULATED2", "NUMBER1", "NUMBER2"));
    }

    @Test
    void bindingDependencies_intermediateCalculatedReference() {
        assertThat(filterResultVariables.get("CALCULATED5").getBindingDependencies())
                .containsExactlyInAnyOrderElementsOf(List.of("CALCULATED4", "NUMBER1", "NUMBER2"));
    }

    @Test
    void bindingDependencies_external() {
        assertThat(filterResultVariables.get("CALCULATED6").getBindingDependencies())
                .containsExactlyInAnyOrderElementsOf(List.of("EXTERNAL_TEXT"));
    }

    @Test
    void bindingDependencies_externalAndCollected() {
        assertThat(filterResultVariables.get("CALCULATED7").getBindingDependencies())
                .containsExactlyInAnyOrderElementsOf(List.of("EXTERNAL_NUMBER", "NUMBER1"));
    }

    @Test
    void bindingDependencies_externalAndCollectedAndCalculated() {
        assertThat(filterResultVariables.get("CALCULATED8").getBindingDependencies())
                .containsExactlyInAnyOrderElementsOf(List.of("EXTERNAL_NUMBER", "NUMBER1", "CALCULATED7"));
    }

    @Test
    void bindingDependencies_finalBoss() {
        assertThat(filterResultVariables.get("CALCULATED9").getBindingDependencies())
                .containsExactlyInAnyOrderElementsOf(List.of("EXTERNAL_NUMBER", "NUMBER1", "NUMBER2",
                        "CALCULATED4", "CALCULATED7"));
    }

}