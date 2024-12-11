package fr.insee.eno.core.mapping.in.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VariableTest {

    @Test
    void variableUnitIntegrationTest() throws DDIParsingException {
        // Given + When
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(
                DDIDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-dynamic-unit.xml")),
                enoQuestionnaire);

        // Then
        // Index variables by name to ease testing
        Map<String, Variable> variableMap = enoQuestionnaire.getVariables().stream()
                .collect(Collectors.toMap(Variable::getName, variable -> variable));
        //
        List.of("NUMBER_FIXED_UNIT", "TABLE11", "TABLE21", "DYNAMIC_TABLE1").forEach(variableName ->
                assertFalse(variableMap.get(variableName).getIsUnitDynamic()));
        List.of("NUMBER_DYNAMIC_UNIT", "TABLE12", "TABLE22", "DYNAMIC_TABLE2").forEach(variableName ->
                assertTrue(variableMap.get(variableName).getIsUnitDynamic()));
    }
}
