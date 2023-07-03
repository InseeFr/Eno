package fr.insee.eno.core;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Loop;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static fr.insee.lunatic.model.flat.ComponentTypeEnum.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Functional tests for the DDI to Lunatic transformation.
 */
class DDIToLunaticTest {

    private final ClassLoader classLoader = this.getClass().getClassLoader();
    private EnoParameters enoParameters;

    @BeforeEach
    void setupParameters() {
        enoParameters = new EnoParameters();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "lhpz68wp",
            "l20g2ba7",
            //"l5v3spn0",
            "kx0a2hn8",
            //"kzy5kbtl",
            //"l8x6fhtd",
            //"ldodefpq",
    })
    @DisplayName("Many questionnaires, non null output")
    void testAll(String questionnaireId) throws DDIParsingException {
        //
        Questionnaire lunaticQuestionnaire = DDIToLunatic.transform(
                classLoader.getResourceAsStream("end-to-end/ddi/ddi-"+questionnaireId+".xml"),
                enoParameters);
        //
        assertNotNull(lunaticQuestionnaire);
    }

    @Nested
    @DisplayName("DDI 'l20g2ba7' to Lunatic (acceptance test)")
    class AcceptanceTest {

        static Questionnaire lunaticQuestionnaire;

        @BeforeAll
        static void mapLunaticQuestionnaire() throws DDIParsingException {
            lunaticQuestionnaire = DDIToLunatic.transform(
                    AcceptanceTest.class.getClassLoader().getResourceAsStream("end-to-end/ddi/ddi-l20g2ba7.xml"),
                    EnoParameters.defaultParameters());
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
                    SUBSEQUENCE, DATEPICKER, DATEPICKER, DATEPICKER, CHECKBOX_BOOLEAN,
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
