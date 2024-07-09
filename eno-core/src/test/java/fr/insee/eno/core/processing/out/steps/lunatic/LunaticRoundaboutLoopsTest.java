package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.Roundabout;
import fr.insee.lunatic.model.flat.variable.VariableType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Optional;

import static fr.insee.lunatic.model.flat.ComponentTypeEnum.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LunaticRoundaboutLoopsTest {

    // Integration test from a DDI questionnaire with a roundabout

    private Questionnaire lunaticQuestionnaire;
    private Roundabout roundabout;

    @BeforeAll
    void ddiToLunatic() throws DDIParsingException {
        //
        EnoParameters parameters = EnoParameters.of(
                EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        parameters.setIdentificationQuestion(false);
        parameters.setResponseTimeQuestion(false);
        parameters.setCommentSection(false);
        //
        lunaticQuestionnaire = DDIToLunatic.transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-roundabout.xml"),
                parameters);
        // the questionnaire should have 1 roundabout component
        List<Roundabout> roundabouts = lunaticQuestionnaire.getComponents().stream()
                .filter(Roundabout.class::isInstance).map(Roundabout.class::cast)
                .toList();
        assertEquals(1, roundabouts.size());
        roundabout = roundabouts.getFirst();
    }

    @Test
    void questionnaireStructure() {
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        assertEquals(6, components.size());
        assertEquals(SEQUENCE, components.get(0).getComponentType());
        assertEquals(INPUT, components.get(1).getComponentType());
        assertEquals(LOOP, components.get(2).getComponentType());
        assertEquals(ROUNDABOUT, components.get(3).getComponentType());
        assertEquals(SEQUENCE, components.get(4).getComponentType());
        assertEquals(CHECKBOX_BOOLEAN, components.get(5).getComponentType());
    }

    @Test
    void roundaboutProperties() {
        // component properties
        assertEquals("4", roundabout.getPage());
        assertEquals("\"Roundabout on S2\"", roundabout.getLabel().getValue());
        assertEquals(LabelTypeEnum.VTL_MD, roundabout.getLabel().getType());
        assertEquals("true", roundabout.getConditionFilter().getValue());
        // roundabout specific ones
        assertEquals("count(FIRST_NAME)", roundabout.getIterations().getValue());
        assertEquals(LabelTypeEnum.VTL, roundabout.getIterations().getType());
        assertTrue(roundabout.getLocked());
        assertEquals("MAIN_LOOP_PROGRESS", roundabout.getProgressVariable());
    }

    @Test
    void roundaboutItem() {
        Roundabout.Item roundaboutItem = roundabout.getItem();
        assertEquals("FIRST_NAME", roundaboutItem.getLabel().getValue().stripTrailing());
        assertEquals(LabelTypeEnum.VTL_MD, roundaboutItem.getLabel().getType());
        assertEquals("\"Occurrence description of \" || FIRST_NAME",
                roundaboutItem.getDescription().getValue().stripTrailing());
        assertEquals(LabelTypeEnum.VTL_MD, roundaboutItem.getDescription().getType());
        assertEquals("not(FIRST_NAME <> FIRST_NAME_REF)",
                roundaboutItem.getDisabled().getValue().stripTrailing());
        assertEquals(LabelTypeEnum.VTL, roundaboutItem.getDisabled().getType());
    }

    @Test
    void roundaboutComponents() {
        assertEquals(2, roundabout.getComponents().size());
        assertEquals(SEQUENCE, roundabout.getComponents().get(0).getComponentType());
        assertEquals(INPUT, roundabout.getComponents().get(1).getComponentType());
        roundabout.getComponents().forEach(component ->
                assertEquals("true", component.getConditionFilter().getValue()));
    }

    @Test
    void roundaboutVariable() {
        Optional<VariableType> progressVariable = lunaticQuestionnaire.getVariables().stream()
                .filter(variable -> "MAIN_LOOP_PROGRESS".equals(variable.getName()))
                .findAny();
        assertTrue(progressVariable.isPresent());
    }

}
