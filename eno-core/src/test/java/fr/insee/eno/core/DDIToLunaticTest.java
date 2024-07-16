package fr.insee.eno.core;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.exceptions.business.UnauthorizedHeaderException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Loop;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static fr.insee.lunatic.model.flat.ComponentTypeEnum.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Functional tests for the DDI to Lunatic transformation.
 */
class DDIToLunaticTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "l20g2ba7",
            "kx0a2hn8",
            "kzy5kbtl",
            "ldodefpq",
            "lhpz68wp",
            "li49zxju",
            "lmyjrqbb",
            "ljr4jm9a",
            "l5v3spn0",
    })
    @DisplayName("Large questionnaires, DDI to Lunatic, transformation should succeed")
    void transformQuestionnaire_nonNullOutput(String questionnaireId) throws DDIParsingException {
        //
        EnoParameters enoParameters = EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC);
        enoParameters.getLunaticParameters().setDsfr(true);
        Questionnaire lunaticQuestionnaire = DDIToLunatic.transform(
                this.getClass().getClassLoader().getResourceAsStream("functional/ddi/ddi-" +questionnaireId+".xml"),
                enoParameters);
        //
        assertNotNull(lunaticQuestionnaire);
    }

    @Test
    void ddiWithTableWithNestedCodeListHeader_shouldThrowException() throws IOException {
        // Given
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(
                "functional/ddi/ddi-l8x6fhtd.xml")) {
            EnoParameters enoParameters = EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC);
            // When + Then
            assertThrows(UnauthorizedHeaderException.class, () -> DDIToLunatic.transform(inputStream, enoParameters));
        } catch (IOException e) {
            throw new IOException("IOException occurred with test DDI file 'l8x6fhtd'.");
        }
    }

    @Test
    void componentWithBeforeQuestionDeclaration() throws DDIParsingException {
        //
        Questionnaire lunaticQuestionnaire = DDIToLunatic.transform(
                this.getClass().getClassLoader().getResourceAsStream("functional/ddi/ddi-lqnje8yr.xml"),
                EnoParameters.of(Context.DEFAULT, ModeParameter.CAPI, Format.LUNATIC));
        //
        assertNotNull(lunaticQuestionnaire);
        ComponentType componentThatShouldHaveDeclaration = lunaticQuestionnaire.getComponents().stream()
                .filter(component -> "lsa7m4oz".equals(component.getId()))
                .findAny().orElse(null);
        assertNotNull(componentThatShouldHaveDeclaration);
        assertEquals(2, componentThatShouldHaveDeclaration.getDeclarations().size());
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("DDI to Lunatic, functional test with 'l20g2ba7'")
    class FunctionalTest1 {

        private Questionnaire lunaticQuestionnaire;

        @BeforeAll
        void mapLunaticQuestionnaire() throws DDIParsingException {
            lunaticQuestionnaire = DDIToLunatic.transform(
                    DDIToLunaticTest.class.getClassLoader().getResourceAsStream("functional/ddi/ddi-l20g2ba7.xml"),
                    EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC));
        }

        @Test
        @DisplayName("We should have the correct number of components")
        void testComponentsSize() {
            assertEquals(53, lunaticQuestionnaire.getComponents().size());
        }

        @Test // This test is tedious => we should figure out a way to automate this kind of tests
        @DisplayName("Components should be in the right order")
        void testComponentsOrder() {
            List<ComponentTypeEnum> expectedComponentTypeSequence = List.of(
                    SEQUENCE,
                    SUBSEQUENCE, INPUT, TEXTAREA,
                    SUBSEQUENCE, INPUT_NUMBER, INPUT_NUMBER, INPUT_NUMBER,
                    SUBSEQUENCE, DATEPICKER, DATEPICKER, DATEPICKER, DURATION, DURATION, CHECKBOX_BOOLEAN,
                    SEQUENCE,
                    SUBSEQUENCE, RADIO, CHECKBOX_ONE, CHECKBOX_ONE, CHECKBOX_ONE, INPUT, DROPDOWN, INPUT,
                    SUBSEQUENCE, CHECKBOX_GROUP, TABLE, TABLE,
                    SEQUENCE,
                    TABLE, TABLE, TABLE, TABLE, TABLE, ROSTER_FOR_LOOP,
                    SEQUENCE
                    // ...
                    );
            for (int i=0; i< expectedComponentTypeSequence.size(); i++) {
                assertEquals(expectedComponentTypeSequence.get(i),
                        lunaticQuestionnaire.getComponents().get(i).getComponentType());
            }
        }

        @Test
        @DisplayName("Loop components should have their component type property set")
        void testLoopComponentTypes() {
            lunaticQuestionnaire.getComponents().stream()
                    .filter(componentType -> componentType instanceof Loop).forEach(componentType ->
                            assertNotNull(componentType.getComponentType()));
        }

    }

}
