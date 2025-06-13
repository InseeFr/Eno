package fr.insee.eno.core.mapping.in.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.model.variable.VariableGroup;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VariableGroupTest {

    @Test
    void mapVariableGroups() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(
                DDIDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-dimensions.xml")),
                enoQuestionnaire);
        //
        assertEquals(4, enoQuestionnaire.getVariableGroups().size());
        //
        VariableGroup questionnaireGroup = findGroupByType(enoQuestionnaire, VariableGroup.Type.QUESTIONNAIRE);
        assertNotNull(questionnaireGroup);
        assertEquals(0, questionnaireGroup.getLoopReferences().size());
        assertThat(questionnaireGroup.getVariables().stream().map(Variable::getName))
                .containsExactlyInAnyOrderElementsOf(List.of("Q1", "CALC1", "EXT1", "Q_LAST"));
        //
        VariableGroup loopGroup = findGroupByName(enoQuestionnaire, "LOOP");
        assertNotNull(loopGroup);
        assertEquals(2, loopGroup.getLoopReferences().size());
        assertThat(loopGroup.getVariables().stream().map(Variable::getName))
                .containsExactlyInAnyOrderElementsOf(List.of("Q21", "Q22", "CALC2", "EXT2", "Q4"));
        //
        VariableGroup dynamicTableGroup = findGroupByName(enoQuestionnaire, "Q31");
        assertNotNull(dynamicTableGroup);
        assertEquals(2, dynamicTableGroup.getLoopReferences().size());
        assertThat(dynamicTableGroup.getVariables().stream().map(Variable::getName))
                .containsExactlyInAnyOrderElementsOf(List.of("Q311", "Q312", "Q32", "CALC3", "EXT3", "Q4"));
        //
        VariableGroup pairwiseLinksGroup = findGroupByType(enoQuestionnaire, VariableGroup.Type.PAIRWISE_LINKS);
        assertNotNull(pairwiseLinksGroup);
        assertEquals(2, pairwiseLinksGroup.getLoopReferences().size());
        assertThat(pairwiseLinksGroup.getVariables().stream().map(Variable::getName))
                .containsExactlyInAnyOrderElementsOf(List.of("Q4"));
    }

    private static VariableGroup findGroupByName(EnoQuestionnaire enoQuestionnaire, String name) {
        return enoQuestionnaire.getVariableGroups().stream()
                .filter(variableGroup -> name.equals(variableGroup.getName()))
                .findAny().orElse(null);
    }

    private static VariableGroup findGroupByType(EnoQuestionnaire enoQuestionnaire, VariableGroup.Type type) {
        return enoQuestionnaire.getVariableGroups().stream()
                .filter(variableGroup -> type.equals(variableGroup.getType()))
                .findAny().orElse(null);
    }

}
