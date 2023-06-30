package fr.insee.eno.core;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @Test
    @DisplayName("DDI 'l20g2ba7' to Lunatic (acceptance test)")
    void test01() throws DDIParsingException {
        //
        Questionnaire lunaticQuestionnaire = DDIToLunatic.transform(
                classLoader.getResourceAsStream("end-to-end/ddi/ddi-l20g2ba7.xml"),
                enoParameters);

        //
        assertNotNull(lunaticQuestionnaire);

        // Components
        assertEquals(ComponentTypeEnum.SEQUENCE, lunaticQuestionnaire.getComponents().get(0).getComponentType());
        assertEquals(ComponentTypeEnum.SUBSEQUENCE, lunaticQuestionnaire.getComponents().get(1).getComponentType());
        assertEquals(ComponentTypeEnum.INPUT, lunaticQuestionnaire.getComponents().get(2).getComponentType());
        assertEquals(ComponentTypeEnum.TEXTAREA, lunaticQuestionnaire.getComponents().get(3).getComponentType());
        assertEquals(ComponentTypeEnum.SUBSEQUENCE, lunaticQuestionnaire.getComponents().get(4).getComponentType());
        assertEquals(ComponentTypeEnum.INPUT_NUMBER, lunaticQuestionnaire.getComponents().get(5).getComponentType());
        assertEquals(ComponentTypeEnum.INPUT_NUMBER, lunaticQuestionnaire.getComponents().get(6).getComponentType());
        assertEquals(ComponentTypeEnum.INPUT_NUMBER, lunaticQuestionnaire.getComponents().get(7).getComponentType());
        assertEquals(ComponentTypeEnum.SUBSEQUENCE, lunaticQuestionnaire.getComponents().get(8).getComponentType());
        assertEquals(ComponentTypeEnum.DATEPICKER, lunaticQuestionnaire.getComponents().get(9).getComponentType());
        assertEquals(ComponentTypeEnum.DATEPICKER, lunaticQuestionnaire.getComponents().get(10).getComponentType());
        assertEquals(ComponentTypeEnum.DATEPICKER, lunaticQuestionnaire.getComponents().get(11).getComponentType());
        assertEquals(ComponentTypeEnum.CHECKBOX_BOOLEAN, lunaticQuestionnaire.getComponents().get(12).getComponentType()); // FAILS -> "duration" question
        //
        //assertEquals(53, lunaticQuestionnaire.getComponents().size()); // FAILS -> work in progress
    }

}
