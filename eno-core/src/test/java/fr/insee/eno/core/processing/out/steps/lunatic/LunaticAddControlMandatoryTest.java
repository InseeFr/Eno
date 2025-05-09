package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.PoguesDDIToLunatic;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.*;

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
    @DisplayName("Unique choice component with detail response.")
    void ucqComponentWithDetail() {
        //
        Radio lunaticRadioComponent = new Radio();
        lunaticRadioComponent.setId("input-number-id");
        lunaticRadioComponent.setMandatory(true);
        lunaticRadioComponent.setResponse(new ResponseType());
        lunaticRadioComponent.getResponse().setName("FOO_VAR");
        Option option1 = new Option();
        Option option2 = new Option();
        Option optionOther = new Option();
        option1.setValue("1");
        option2.setValue("2");
        optionOther.setValue("9");
        optionOther.setDetail(new DetailResponse());
        optionOther.getDetail().setResponse(new ResponseType());
        optionOther.getDetail().getResponse().setName("DETAIL_VAR");
        lunaticRadioComponent.getOptions().add(option1);
        lunaticRadioComponent.getOptions().add(option2);
        lunaticRadioComponent.getOptions().add(optionOther);
        lunaticQuestionnaire.getComponents().add(lunaticRadioComponent);
        //
        new LunaticAddControlMandatory().apply(lunaticQuestionnaire);
        //
        assertEquals(2, lunaticRadioComponent.getControls().size());
        ControlType mandatoryControl = lunaticRadioComponent.getControls().getFirst();
        assertEquals("not(isnull(FOO_VAR))", mandatoryControl.getControl().getValue());
        ControlType detailMandatoryControl = lunaticRadioComponent.getControls().get(1);
        assertEquals("not((FOO_VAR = \"9\") and (trim(nvl(DETAIL_VAR, \"\")) = \"\"))", detailMandatoryControl.getControl().getValue());
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

        @Test
        void mandatoryProp() {
            List.of(1,3,4,5,6,7,8,9).forEach(index ->
                    assertTrue(getResponseComponentAtIndex(lunaticQuestionnaire, index).getMandatory()));
            assertFalse(getResponseComponentAtIndex(lunaticQuestionnaire, 2).getMandatory());
        }

        @Test
        void mandatoryControlsId() {
            List.of(1,3,4,5,6,7,8,9).forEach(index -> {
                // Mandatory control is expected to be the first in the list
                ControlType mandatoryControl = getComponentAtIndex(lunaticQuestionnaire, index).getControls().getFirst();
                assertTrue(mandatoryControl.getId().contains("-mandatory-check"));
            });
        }

        @Test
        void mandatoryControlsTypology() {
            List.of(1,3,4,5,6,7,8,9).forEach(index -> {
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
                    getComponentAtIndex(lunaticQuestionnaire, 9).getControls().getFirst().getControl().getValue());
        }

        @Test
        void mandatoryControlsExpressionType() {
            List.of(1,3,4,5,6,7,8,9).forEach(index -> {
                ControlType mandatoryControl = getComponentAtIndex(lunaticQuestionnaire, index).getControls().getFirst();
                assertEquals(LabelTypeEnum.VTL, mandatoryControl.getControl().getType());
            });
        }

        @Test
        void mandatoryControlsMessage_defaultLanguage() {
            List.of(1,3,4,5,6,7,8,9).forEach(index -> {
                ControlType mandatoryControl = getComponentAtIndex(lunaticQuestionnaire, index).getControls().getFirst();
                assertEquals("La réponse à cette question est obligatoire.", mandatoryControl.getErrorMessage().getValue());
                assertEquals(LabelTypeEnum.TXT, mandatoryControl.getErrorMessage().getType());
            });
        }

    }

}
