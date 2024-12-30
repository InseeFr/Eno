package fr.insee.eno.core.mapping.in;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class VariableUnitTest {

    private EnoQuestionnaire enoQuestionnaire;
    private Map<String, Variable> variableMap;

    @BeforeEach
    void initEnoQuestionnaire() {
        enoQuestionnaire = new EnoQuestionnaire();
    }

    @Test
    void ddiMapping() throws DDIParsingException {
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(
                DDIDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-dynamic-unit.xml")),
                enoQuestionnaire);

        // Index variables by name to ease testing
        variableMap = enoQuestionnaire.getVariables().stream()
                .collect(Collectors.toMap(Variable::getName, variable -> variable));

        List.of("NUMBER_DYNAMIC_UNIT", "TABLE12", "TABLE22", "DYNAMIC_TABLE2").forEach(variableName ->
                assertTrue(variableMap.get(variableName).getUnit().contains("¤")));
    }

    @Test
    void poguesMapping() throws PoguesDeserializationException {
        PoguesMapper poguesMapper = new PoguesMapper();
        poguesMapper.mapPoguesQuestionnaire(
                PoguesDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/pogues/pogues-dynamic-unit.json")),
                enoQuestionnaire);

        // Index variables by name to ease testing
        variableMap = enoQuestionnaire.getVariables().stream()
                .collect(Collectors.toMap(Variable::getName, variable -> variable));

        List.of("NUMBER_DYNAMIC_UNIT", "TABLE12", "TABLE22", "DYNAMIC_TABLE2").forEach(variableName ->
                assertTrue(variableMap.get(variableName).getUnit().contains("$")));
    }

    @AfterEach
    void testVariablesUnit() {
        //
        assertEquals("€", variableMap.get("NUMBER_FIXED_UNIT").getUnit());
        assertEquals("%", variableMap.get("TABLE11").getUnit());
        assertEquals("%", variableMap.get("TABLE21").getUnit());
        assertEquals("€", variableMap.get("DYNAMIC_TABLE1").getUnit());
        //
        List.of("NUMBER_FIXED_UNIT", "TABLE11", "TABLE21", "DYNAMIC_TABLE1").forEach(variableName ->
                assertFalse(variableMap.get(variableName).getIsUnitDynamic()));
        List.of("NUMBER_DYNAMIC_UNIT", "TABLE12", "TABLE22", "DYNAMIC_TABLE2").forEach(variableName ->
                assertTrue(variableMap.get(variableName).getIsUnitDynamic()));
    }
}
