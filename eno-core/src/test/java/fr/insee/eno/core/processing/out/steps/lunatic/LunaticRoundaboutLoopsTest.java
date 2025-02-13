package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.VariableType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Optional;

import static fr.insee.lunatic.model.flat.ComponentTypeEnum.*;
import static org.junit.jupiter.api.Assertions.*;

class LunaticRoundaboutLoopsTest {

    // Integration test from a DDI questionnaire with a roundabout on a sequence
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class RoundaboutOnSequence {
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
            lunaticQuestionnaire = DDIToLunatic.fromInputStream(
                    this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-roundabout.xml"))
                    .transform(parameters);
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
            assertEquals("\"Roundabout declaration\"", roundabout.getDescription().getValue());
            assertEquals(LabelTypeEnum.VTL_MD, roundabout.getDescription().getType());
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
            assertEquals("not(not(FIRST_NAME <> FIRST_NAME_REF))",
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


    // Integration test from a DDI questionnaire with a roundabout
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class RoundaboutOnSubsequence {
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
            lunaticQuestionnaire = DDIToLunatic.fromInputStream(
                    this.getClass().getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-roundabout-subsequence.xml"))
                    .transform(parameters);
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
            assertEquals(INPUT_NUMBER, components.get(1).getComponentType());
            assertEquals(LOOP, components.get(2).getComponentType());
            assertEquals(ROUNDABOUT, components.get(3).getComponentType());
            assertEquals(SEQUENCE, components.get(4).getComponentType());
            assertEquals(INPUT, components.get(5).getComponentType());
        }

        @Test
        void roundaboutProperties() {
            // component properties
            assertEquals("4", roundabout.getPage());
            assertEquals("\"Roundabout on SS2\"", roundabout.getLabel().getValue());
            assertEquals(LabelTypeEnum.VTL_MD, roundabout.getLabel().getType());
            assertEquals("true", roundabout.getConditionFilter().getValue());
            // roundabout specific ones
            assertEquals("count(Q1)", roundabout.getIterations().getValue());
            assertEquals(LabelTypeEnum.VTL, roundabout.getIterations().getType());
            assertFalse(roundabout.getLocked());
            assertEquals("LOOP_SS1_PROGRESS", roundabout.getProgressVariable());
        }

        @Test
        void roundaboutItem() {
            Roundabout.Item roundaboutItem = roundabout.getItem();
            assertEquals("\"Hello\"", roundaboutItem.getLabel().getValue().stripTrailing());
            assertEquals(LabelTypeEnum.VTL_MD, roundaboutItem.getLabel().getType());
            assertNull(roundaboutItem.getDescription());
            assertNull(roundaboutItem.getDisabled());
        }

        @Test
        void roundaboutComponents() {
            assertEquals(2, roundabout.getComponents().size());
            assertEquals(SUBSEQUENCE, roundabout.getComponents().get(0).getComponentType());
            assertEquals(INPUT, roundabout.getComponents().get(1).getComponentType());
            roundabout.getComponents().forEach(component ->
                    assertEquals("true", component.getConditionFilter().getValue()));
        }

        @Test
        void roundaboutVariable() {
            Optional<VariableType> progressVariable = lunaticQuestionnaire.getVariables().stream()
                    .filter(variable -> "LOOP_SS1_PROGRESS".equals(variable.getName()))
                    .findAny();
            assertTrue(progressVariable.isPresent());
        }
    }

    // Integration test from a DDI questionnaire with a roundabout that contains controls
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class RoundaboutWithControls {
        private Roundabout roundabout;

        @BeforeAll
        void ddiToLunatic() throws DDIParsingException {
            //
            EnoParameters parameters = EnoParameters.of(
                    EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
            //
            Questionnaire lunaticQuestionnaire = DDIToLunatic.fromInputStream(
                    this.getClass().getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-roundabout-controls.xml"))
                    .transform(parameters);
            // the questionnaire should have 1 roundabout component
            List<Roundabout> roundabouts = lunaticQuestionnaire.getComponents().stream()
                    .filter(Roundabout.class::isInstance).map(Roundabout.class::cast)
                    .toList();
            assertEquals(1, roundabouts.size());
            roundabout = roundabouts.getFirst();
        }

        @Test
        void lockedPropertyTest() {
            assertFalse(roundabout.getLocked());
        }

        @Test
        void controlsCount() {
            assertEquals(2, roundabout.getControls().size());
        }

        @Test
        void roundaboutControlTest() {
            List<ControlType> roundaboutControls = roundabout.getControls().stream()
                    .filter(controlType -> ControlContextType.SIMPLE.equals(controlType.getType()))
                    .toList();
            assertEquals(1, roundaboutControls.size());
            ControlType roundaboutControl = roundaboutControls.getFirst();
            assertEquals("not(count(Q2) < 3)", roundaboutControl.getControl().getValue());
            assertEquals(LabelTypeEnum.VTL, roundaboutControl.getControl().getType());
            assertEquals("\"There is less than 3 answers in the roundabout.\"",
                    roundaboutControl.getErrorMessage().getValue());
            assertEquals(LabelTypeEnum.VTL_MD, roundaboutControl.getErrorMessage().getType());
            assertEquals(ControlTypeEnum.CONSISTENCY, roundaboutControl.getTypeOfControl());
            assertEquals(ControlCriticalityEnum.INFO, roundaboutControl.getCriticality());
        }

        @Test
        void occurrenceControlTest() {
            List<ControlType> occurrenceControls = roundabout.getControls().stream()
                    .filter(controlType -> ControlContextType.ROW.equals(controlType.getType()))
                    .toList();
            assertEquals(1, occurrenceControls.size());
            ControlType occurrenceControl = occurrenceControls.getFirst();
            assertEquals("not(Q2 = \"bar\")", occurrenceControl.getControl().getValue());
            assertEquals(LabelTypeEnum.VTL, occurrenceControl.getControl().getType());
            assertEquals("\"Occurrence with question 1 = '\" || Q1 || \"' answered 'bar' at question 2.\"",
                    occurrenceControl.getErrorMessage().getValue().stripTrailing());
            assertEquals(LabelTypeEnum.VTL_MD, occurrenceControl.getErrorMessage().getType());
            assertEquals(ControlTypeEnum.CONSISTENCY, occurrenceControl.getTypeOfControl());
            assertEquals(ControlCriticalityEnum.INFO, occurrenceControl.getCriticality());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class RoundaboutWithExcept {
        private Roundabout roundabout;

        @BeforeAll
        void ddiToLunatic() throws DDIParsingException {
            //
            EnoParameters parameters = EnoParameters.of(
                    EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
            //
            Questionnaire lunaticQuestionnaire = DDIToLunatic.fromInputStream(
                    this.getClass().getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-roundabout-except.xml"))
                    .transform(parameters);
            // the questionnaire should have 1 roundabout component
            List<Roundabout> roundabouts = lunaticQuestionnaire.getComponents().stream()
                    .filter(Roundabout.class::isInstance).map(Roundabout.class::cast)
                    .toList();
            assertEquals(1, roundabouts.size());
            roundabout = roundabouts.getFirst();
        }

        /** The "except" field in Pogues corresponds to the "disabled" expression in Lunatic. */
        @Test
        void disabledCondition() {
            assertEquals("not(not(Q1 = \"foo\"))", roundabout.getItem().getDisabled().getValue());
            assertEquals(LabelTypeEnum.VTL, roundabout.getItem().getDisabled().getType());
        }

        /** The DDI modeling describes both disabled condition and occurrence-level controls as control objects,
         * hence this test. */
        @Test
        void occurrenceControl_shouldBePresent() {
            Optional<ControlType> rowControl = roundabout.getControls().stream()
                    .filter(control -> ControlContextType.ROW.equals(control.getType())).findAny();
            assertTrue(rowControl.isPresent());
            assertEquals(ControlCriticalityEnum.INFO, rowControl.get().getCriticality());
            assertEquals("not(true)", rowControl.get().getControl().getValue());
            assertEquals(LabelTypeEnum.VTL, rowControl.get().getControl().getType());
            assertEquals("\"This control should always be displayed.\"",
                    rowControl.get().getErrorMessage().getValue());
            assertEquals(LabelTypeEnum.VTL_MD, rowControl.get().getErrorMessage().getType());
        }
    }

}
