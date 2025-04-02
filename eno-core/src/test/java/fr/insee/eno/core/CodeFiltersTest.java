package fr.insee.eno.core;

import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class CodeFiltersTest { // TODO: move this class somewhere

    private Questionnaire lunaticQuestionnaire;

    abstract Questionnaire mapQuestionnaire(EnoParameters enoParameters) throws ParsingException;

    static class PoguesDDITest extends CodeFiltersTest {
        @Override
        Questionnaire mapQuestionnaire(EnoParameters enoParameters) throws ParsingException {
            ClassLoader classLoader = this.getClass().getClassLoader();
            return PoguesDDIToLunatic.fromInputStreams(
                    classLoader.getResourceAsStream("integration/pogues/pogues-code-filter.json"),
                    classLoader.getResourceAsStream("integration/ddi/ddi-code-filter.xml"))
                    .transform(enoParameters);
        }
    }

    /*static class PoguesOnlyTest extends CodeFiltersTest {
        @Override
        Questionnaire mapQuestionnaire(EnoParameters enoParameters) throws PoguesDeserializationException {
            ClassLoader classLoader = this.getClass().getClassLoader();
            return PoguesToLunatic.fromInputStream(
                    classLoader.getResourceAsStream("integration/pogues/pogues-code-filter.json"))
                    .transform(enoParameters);
        }
    }*/

    @BeforeAll
    void integrationTest_fromPoguesOnly() throws ParsingException {
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        lunaticQuestionnaire = mapQuestionnaire(enoParameters);
    }

    @Test
    @DisplayName("Code filtering using a simple question.")
    void test01() {
        Question lunaticUCQ1Radio = (Question) lunaticQuestionnaire.getComponents().get(2);
        Question lunaticUCQ1Dropdown = (Question) lunaticQuestionnaire.getComponents().get(3);
        Question lunaticMCQ1 = (Question) lunaticQuestionnaire.getComponents().get(4);

        Radio radioComponent1 = (Radio) lunaticUCQ1Radio.getComponents().getFirst();
        Dropdown dropdownComponent1 = (Dropdown) lunaticUCQ1Dropdown.getComponents().getFirst();
        CheckboxGroup checkboxGroup1 = (CheckboxGroup) lunaticMCQ1.getComponents().getFirst();

        assertNull(radioComponent1.getOptions().get(0).getConditionFilter());
        assertNull(radioComponent1.getOptions().get(1).getConditionFilter());
        assertEquals("nvl(AGE, 0) > 18", radioComponent1.getOptions().get(2).getConditionFilter().getValue());
        assertEquals(LabelTypeEnum.VTL, radioComponent1.getOptions().get(2).getConditionFilter().getType());

        assertNull(dropdownComponent1.getOptions().get(0).getConditionFilter());
        assertNull(dropdownComponent1.getOptions().get(1).getConditionFilter());
        assertEquals("nvl(AGE, 0) > 18", dropdownComponent1.getOptions().get(2).getConditionFilter().getValue());
        assertEquals(LabelTypeEnum.VTL, dropdownComponent1.getOptions().get(2).getConditionFilter().getType());

        assertNull(checkboxGroup1.getResponses().get(0).getConditionFilter());
        assertNull(checkboxGroup1.getResponses().get(1).getConditionFilter());
        assertEquals("nvl(AGE, 0) > 18", checkboxGroup1.getResponses().get(2).getConditionFilter().getValue());
        assertEquals(LabelTypeEnum.VTL, checkboxGroup1.getResponses().get(2).getConditionFilter().getType());
    }

    @Test
    @DisplayName("Code filtering using another choice question.")
    void test02() {
        Question lunaticMCQ2 = (Question) lunaticQuestionnaire.getComponents().get(6);
        Question lunaticUCQ2 = (Question) lunaticQuestionnaire.getComponents().get(7);

        CheckboxGroup checkboxGroup2 = (CheckboxGroup) lunaticMCQ2.getComponents().getFirst();
        Radio radioComponent2 = (Radio) lunaticUCQ2.getComponents().getFirst();

        assertNull(checkboxGroup2.getResponses().get(0).getConditionFilter());
        assertNull(checkboxGroup2.getResponses().get(1).getConditionFilter());
        assertNull(checkboxGroup2.getResponses().get(2).getConditionFilter());

        assertEquals("MCQ21", radioComponent2.getOptions().get(0).getConditionFilter().getValue());
        assertEquals("MCQ22", radioComponent2.getOptions().get(1).getConditionFilter().getValue());
        assertEquals("MCQ23", radioComponent2.getOptions().get(2).getConditionFilter().getValue());
        assertEquals(LabelTypeEnum.VTL, radioComponent2.getOptions().get(0).getConditionFilter().getType());
        assertEquals(LabelTypeEnum.VTL, radioComponent2.getOptions().get(1).getConditionFilter().getType());
        assertEquals(LabelTypeEnum.VTL, radioComponent2.getOptions().get(2).getConditionFilter().getType());
    }

    @Test
    @DisplayName("Code filtering in combination with detail responses.")
    void test03() {
        Question lunaticUCQ3 = (Question) lunaticQuestionnaire.getComponents().get(10);
        Question lunaticMCQ3 = (Question) lunaticQuestionnaire.getComponents().get(11);

        Radio radioComponent3 = (Radio) lunaticUCQ3.getComponents().getFirst();
        CheckboxGroup checkboxGroup3 = (CheckboxGroup) lunaticMCQ3.getComponents().getFirst();

        assertNull(radioComponent3.getOptions().get(0).getConditionFilter());
        assertNull(radioComponent3.getOptions().get(1).getConditionFilter());
        assertEquals("YES_NO = \"2\"", radioComponent3.getOptions().get(2).getConditionFilter().getValue());
        assertEquals(LabelTypeEnum.VTL, radioComponent3.getOptions().get(2).getConditionFilter().getType());

        assertNull(checkboxGroup3.getResponses().get(0).getConditionFilter());
        assertNull(checkboxGroup3.getResponses().get(1).getConditionFilter());
        assertEquals("YES_NO = \"2\"", checkboxGroup3.getResponses().get(2).getConditionFilter().getValue());
        assertEquals(LabelTypeEnum.VTL, checkboxGroup3.getResponses().get(2).getConditionFilter().getType());
    }

}
