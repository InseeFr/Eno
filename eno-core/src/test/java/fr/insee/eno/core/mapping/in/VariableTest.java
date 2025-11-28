package fr.insee.eno.core.mapping.in;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceType;
import fr.insee.eno.core.PoguesDDIToLunatic;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.InMapper;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.model.variable.CollectedVariable;
import fr.insee.eno.core.model.variable.ExternalVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.lunatic.model.flat.variable.ExternalVariableType;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class VariableTest {

    private static final ClassLoader classLoader = VariableTest.class.getClassLoader();

    private static Stream<Arguments> integrationTest() throws ParsingException {
        Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(classLoader
                .getResourceAsStream("integration/pogues/pogues-variables.json"));
        DDIInstanceType ddiInstance = DDIDeserializer.deserialize(classLoader
                .getResourceAsStream("integration/ddi/ddi-variables.xml")).getDDIInstance();
        return Stream.of(
                Arguments.of(Format.DDI, new DDIMapper(), ddiInstance)
                ,Arguments.of(Format.POGUES, new PoguesMapper(), poguesQuestionnaire)
        );
    }
    @ParameterizedTest
    @MethodSource
    void integrationTest(Format format, InMapper inMapper, Object inputObject) {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        inMapper.mapInputObject(inputObject, enoQuestionnaire);

        //
        Map<String, CollectedVariable> collectedVariables = enoQuestionnaire.getVariables().stream()
                .filter(CollectedVariable.class::isInstance).map(CollectedVariable.class::cast)
                .collect(Collectors.toMap(Variable::getName, variable -> variable));
        Map<String, CalculatedVariable> calculatedVariables = enoQuestionnaire.getVariables().stream()
                .filter(CalculatedVariable.class::isInstance).map(CalculatedVariable.class::cast)
                .collect(Collectors.toMap(Variable::getName, variable -> variable));
        Map<String, ExternalVariable> externalVariables = enoQuestionnaire.getVariables().stream()
                .filter(ExternalVariable.class::isInstance).map(ExternalVariable.class::cast)
                .collect(Collectors.toMap(Variable::getName, variable -> variable));

        //
        assertEquals(11, collectedVariables.size());
        assertEquals(9, calculatedVariables.size());
        assertEquals(2, externalVariables.size());
        if (format == Format.DDI)
            assertEquals("cast(lk6qier3-IP-1, number) * 10",
                    calculatedVariables.get("CALCULATED1").getExpression().getValue());
        if (format == Format.POGUES)
            assertEquals("cast(NUMBER1, number) * 10",
                    calculatedVariables.get("CALCULATED1").getExpression().getValue());
    }

    @Test
    void resetableVariableIntegrationTest() throws ParsingException {
        // Given + When
        fr.insee.lunatic.model.flat.Questionnaire lunaticQuestionnaire = PoguesDDIToLunatic
                .fromInputStreams(
                        classLoader.getResourceAsStream("integration/pogues/pogues-resetable-external.json"),
                        classLoader.getResourceAsStream("integration/ddi/ddi-resetable-external.xml"))
                .transform(EnoParameters.of(EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC));
        // Then
        List<ExternalVariableType> lunaticExternalVariables = lunaticQuestionnaire.getVariables().stream()
                .filter(ExternalVariableType.class::isInstance)
                .map(ExternalVariableType.class::cast)
                .toList();
        assertEquals(2, lunaticExternalVariables.size());
        assertEquals("EXTERNAL_VAR1", lunaticExternalVariables.get(0).getName());
        assertTrue(lunaticExternalVariables.get(0).getIsDeletedOnReset() == null ||
                lunaticExternalVariables.get(0).getIsDeletedOnReset() == false);
        assertEquals("EXTERNAL_VAR2", lunaticExternalVariables.get(1).getName());
        assertTrue(lunaticExternalVariables.get(1).getIsDeletedOnReset());
    }

}
