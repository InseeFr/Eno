package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.PoguesDDIToLunatic;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LunaticAddControlMandatoryTest {

    private Questionnaire lunaticQuestionnaire;

    @BeforeEach
    void createLunaticQuestionnaire() {
        lunaticQuestionnaire = new Questionnaire();
    }

    @Test
    @DisplayName("Non mandatory component.")
    void nonMandatory() {
        //
        Input lunaticInput = new Input();
        lunaticInput.setMandatory(false);
        lunaticQuestionnaire.getComponents().add(lunaticInput);
        //
        new LunaticAddControlMandatory().apply(lunaticQuestionnaire);
        //
        assertTrue(lunaticInput.getControls().isEmpty());
    }

    @Test
    @DisplayName("Short text response component.")
    void shortTextComponent() {
        //
        Input lunaticInput = new Input();
        lunaticInput.setId("input-id");
        lunaticInput.setMandatory(true);
        lunaticInput.setResponse(new ResponseType());
        lunaticInput.getResponse().setName("FOO_VAR");
        lunaticQuestionnaire.getComponents().add(lunaticInput);
        //
        new LunaticAddControlMandatory().apply(lunaticQuestionnaire);
        //
        assertEquals(1, lunaticInput.getControls().size());
        ControlType mandatoryControl = lunaticInput.getControls().getFirst();
        assertEquals("input-id-mandatory-check", mandatoryControl.getId());
        assertEquals(ControlContextType.SIMPLE, mandatoryControl.getType());
        assertEquals(ControlTypeEnum.MANDATORY, mandatoryControl.getTypeOfControl());
        assertEquals(ControlCriticalityEnum.ERROR, mandatoryControl.getCriticality());
        assertEquals("not(trim(nvl(FOO_VAR, \"\")) = \"\")", mandatoryControl.getControl().getValue());
        assertEquals(LabelTypeEnum.VTL, mandatoryControl.getControl().getType());
        assertEquals("La réponse à cette question est obligatoire.", mandatoryControl.getErrorMessage().getValue());
        assertEquals(LabelTypeEnum.TXT, mandatoryControl.getErrorMessage().getType());
    }

    @Test
    @DisplayName("Number response component.")
    void numberComponent() {
        //
        InputNumber lunaticInputNumber = new InputNumber();
        lunaticInputNumber.setId("input-number-id");
        lunaticInputNumber.setMandatory(true);
        lunaticInputNumber.setResponse(new ResponseType());
        lunaticInputNumber.getResponse().setName("FOO_VAR");
        lunaticQuestionnaire.getComponents().add(lunaticInputNumber);
        //
        new LunaticAddControlMandatory().apply(lunaticQuestionnaire);
        //
        ControlType mandatoryControl = lunaticInputNumber.getControls().getFirst();
        assertEquals("not(isnull(FOO_VAR))", mandatoryControl.getControl().getValue());
    }

    @Test
    @DisplayName("Internationalized control error message.")
    void i18nErrorMessage() {
        //
        Input lunaticInput = new Input();
        lunaticInput.setId("input-id");
        lunaticInput.setMandatory(true);
        lunaticInput.setResponse(new ResponseType());
        lunaticInput.getResponse().setName("FOO_VAR");
        lunaticQuestionnaire.getComponents().add(lunaticInput);
        //
        new LunaticAddControlMandatory(EnoParameters.Language.EN).apply(lunaticQuestionnaire);
        //
        ControlType mandatoryControl = lunaticInput.getControls().getFirst();
        assertEquals("This question is required.", mandatoryControl.getErrorMessage().getValue());
    }

    @Test
    @DisplayName("Checkbox group mandatory control.")
    void checkboxGroupComponent() {
        CheckboxGroup checkboxGroup = createCheckboxGroup();

        lunaticQuestionnaire.getComponents().add(checkboxGroup);
        new LunaticAddControlMandatory().apply(lunaticQuestionnaire);

        assertEquals(1, checkboxGroup.getControls().size());
        ControlType control = checkboxGroup.getControls().getFirst();

        assertEquals("checkboxGroup-id-mandatory-check", control.getId());
        assertEquals(ControlTypeEnum.MANDATORY, control.getTypeOfControl());
        assertEquals(ControlCriticalityEnum.ERROR, control.getCriticality());
        assertEquals(LabelTypeEnum.VTL, control.getControl().getType());
        assertEquals(LabelTypeEnum.TXT, control.getErrorMessage().getType());
        assertEquals("La réponse à cette question est obligatoire.", control.getErrorMessage().getValue());
        assertEquals(
                "not(nvl(CHOICE_1, false) = false and nvl(CHOICE_2, false) = false and nvl(CHOICE_3, false) = false)",
                control.getControl().getValue()
        );
    }

    private static CheckboxGroup createCheckboxGroup() {
        CheckboxGroup checkboxGroup = new CheckboxGroup();
        checkboxGroup.setId("checkboxGroup-id");
        checkboxGroup.setMandatory(true);

        ResponseCheckboxGroup responseCheckboxGroup1 = new ResponseCheckboxGroup();
        ResponseType responseType1 = new ResponseType();
        responseType1.setName("CHOICE_1");
        responseCheckboxGroup1.setResponse(responseType1);

        ResponseCheckboxGroup responseCheckboxGroup2 = new ResponseCheckboxGroup();
        ResponseType responseType2 = new ResponseType();
        responseType2.setName("CHOICE_2");
        responseCheckboxGroup2.setResponse(responseType2);

        ResponseCheckboxGroup responseCheckboxGroup3 = new ResponseCheckboxGroup();
        ResponseType responseType3 = new ResponseType();
        responseType3.setName("CHOICE_3");
        responseCheckboxGroup3.setResponse(responseType3);

        checkboxGroup.setResponses(List.of(responseCheckboxGroup1, responseCheckboxGroup2, responseCheckboxGroup3));
        return checkboxGroup;
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class IntegrationTest {

        private Questionnaire lunaticQuestionnaire;

        @BeforeAll
        void poguesPlusDDIMapping() throws ParsingException {
            ClassLoader classLoader = LunaticAddControlMandatoryTest.class.getClassLoader();
            lunaticQuestionnaire = PoguesDDIToLunatic.fromInputStreams(
                            classLoader.getResourceAsStream("integration/pogues/pogues-mandatory-questions.json"),
                            classLoader.getResourceAsStream("integration/ddi/ddi-mandatory-questions.xml"))
                    .transform(EnoParameters.of(
                            EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC));
        }

        /** Helper method for the following tests to get a simple response component by index. */
        private static ComponentSimpleResponseType getResponseComponentAtIndex(Questionnaire lunaticQuestionnaire, int index) {
            Question lunaticQuestion = (Question) lunaticQuestionnaire.getComponents().get(index);
            return (ComponentSimpleResponseType) lunaticQuestion.getComponents().getFirst();
        }
        /** Second method with return typed as a Lunatic component object. */
        private static ComponentType getComponentAtIndex(Questionnaire lunaticQuestionnaire, int index) {
            Question lunaticQuestion = (Question) lunaticQuestionnaire.getComponents().get(index);
            return lunaticQuestion.getComponents().getFirst();
        }

        // Note:
        // Bug on UCQ with a "please specify" / detail response.
        // There is a bug in DDI mapping: the "please specify" makes the response domain be a
        // StructuredMixedResponseDomain object, so mapping gets incorrect in that case.

        // Single response component tests

        @Test
        void mandatoryProp() {
            List.of(1,3,4,5,6,7,8,10).forEach(index ->
                    assertTrue(getResponseComponentAtIndex(lunaticQuestionnaire, index).getMandatory()));
            assertFalse(getResponseComponentAtIndex(lunaticQuestionnaire, 2).getMandatory());
        }

        @Test
        void mandatoryControlsId() {
            List.of(1,3,4,5,6,7,8,10).forEach(index -> {
                // Mandatory control is expected to be the first in the list
                ControlType mandatoryControl = getComponentAtIndex(lunaticQuestionnaire, index).getControls().getFirst();
                assertTrue(mandatoryControl.getId().contains("-mandatory-check"));
            });
        }

        @Test
        void mandatoryControlsTypology() {
            List.of(1,3,4,5,6,7,8,10).forEach(index -> {
                // Mandatory control is expected to be the first in the list
                ControlType mandatoryControl = getComponentAtIndex(lunaticQuestionnaire, index).getControls().getFirst();
                assertEquals(ControlContextType.SIMPLE, mandatoryControl.getType());
                assertEquals(ControlTypeEnum.MANDATORY, mandatoryControl.getTypeOfControl());
                assertEquals(ControlCriticalityEnum.ERROR, mandatoryControl.getCriticality());
            });
        }

        @Test
        void mandatoryControlsExpression() {
            assertEquals("not(nvl(CHECK_MANDATORY, false) = false)",
                    getComponentAtIndex(lunaticQuestionnaire, 1).getControls().getFirst().getControl().getValue());
            assertEquals("not(trim(nvl(Q_TEXT1_MANDATORY, \"\")) = \"\")",
                    getComponentAtIndex(lunaticQuestionnaire, 3).getControls().getFirst().getControl().getValue());
            assertEquals("not(trim(nvl(Q_TEXT2_MANDATORY, \"\")) = \"\")",
                    getComponentAtIndex(lunaticQuestionnaire, 4).getControls().getFirst().getControl().getValue());
            assertEquals("not(isnull(Q_NUMBER_MANDATORY))",
                    getComponentAtIndex(lunaticQuestionnaire, 5).getControls().getFirst().getControl().getValue());
            assertEquals("not(isnull(Q_DATE_MANDATORY))",
                    getComponentAtIndex(lunaticQuestionnaire, 6).getControls().getFirst().getControl().getValue());
            assertEquals("not(isnull(Q_DURATION_MANDATORY))",
                    getComponentAtIndex(lunaticQuestionnaire, 7).getControls().getFirst().getControl().getValue());
            assertEquals("not(isnull(Q_RADIO_MANDATORY))",
                    getComponentAtIndex(lunaticQuestionnaire, 8).getControls().getFirst().getControl().getValue());
            assertEquals("not(isnull(Q_DROPDOWN_MANDATORY))",
                    getComponentAtIndex(lunaticQuestionnaire, 10).getControls().getFirst().getControl().getValue());
        }

        @Test
        void mandatoryControlsExpressionType() {
            List.of(1,3,4,5,6,7,8,10).forEach(index -> {
                ControlType mandatoryControl = getComponentAtIndex(lunaticQuestionnaire, index).getControls().getFirst();
                assertEquals(LabelTypeEnum.VTL, mandatoryControl.getControl().getType());
            });
        }

        @Test
        void mandatoryControlsMessage_defaultLanguage() {
            List.of(1,3,4,5,6,7,8,10).forEach(index -> {
                ControlType mandatoryControl = getComponentAtIndex(lunaticQuestionnaire, index).getControls().getFirst();
                assertEquals("La réponse à cette question est obligatoire.", mandatoryControl.getErrorMessage().getValue());
                assertEquals(LabelTypeEnum.TXT, mandatoryControl.getErrorMessage().getType());
            });
        }

        // Multiple choice question tests

        @ParameterizedTest
        @ValueSource(ints = {11, 12})
        void mandatoryMultipleChoice(int index) {
            CheckboxGroup checkboxGroup = (CheckboxGroup) getComponentAtIndex(lunaticQuestionnaire, index);
            assertTrue(checkboxGroup.getMandatory());

            assertEquals(1, checkboxGroup.getControls().size());
            ControlType mandatoryControl = checkboxGroup.getControls().getFirst();
            assertTrue(mandatoryControl.getId().contains("-mandatory-check"));
            assertEquals(ControlContextType.SIMPLE, mandatoryControl.getType());
            assertEquals(ControlTypeEnum.MANDATORY, mandatoryControl.getTypeOfControl());
            assertEquals(ControlCriticalityEnum.ERROR, mandatoryControl.getCriticality());
            assertEquals(LabelTypeEnum.VTL, mandatoryControl.getControl().getType());

            if (index == 11) {
                String expectedExpression = "not(nvl(MCQ_MANDATORY1, false) = false " +
                        "and nvl(MCQ_MANDATORY2, false) = false and nvl(MCQ_MANDATORY3, false) = false)";
                assertEquals(expectedExpression, mandatoryControl.getControl().getValue());
            }
            if (index == 12) {
                String expectedExpression = "not(nvl(MCQ_MANDATORY_DETAIL1, false) = false " +
                        "and nvl(MCQ_MANDATORY_DETAIL2, false) = false " +
                        "and nvl(MCQ_MANDATORY_DETAIL3, false) = false)";
                assertEquals(expectedExpression, mandatoryControl.getControl().getValue());
            }
        }

    }

}
