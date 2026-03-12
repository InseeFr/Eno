package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.PoguesDDIToLunatic;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/** Integration test for the "unique choice with variable options" feature. */
class UniqueChoiceQuestionVariableIT {

    @Test
    @DisplayName("Pogues+DDI to Lunatic, UCQs with variable options.")
    void integrationTest() throws ParsingException {
        ClassLoader classLoader = this.getClass().getClassLoader();

        // Given
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        String poguesResource = "integration/pogues/pogues-ucq-variable-options.json";
        String ddiResource = "integration/ddi/ddi-ucq-variable-options.xml";

        // When
        Questionnaire lunaticQuestionnaire = PoguesDDIToLunatic.fromInputStreams(
                        classLoader.getResourceAsStream(poguesResource),
                        classLoader.getResourceAsStream(ddiResource))
                .transform(enoParameters);

        // Then
        Loop mainLoop = (Loop) lunaticQuestionnaire.getComponents().get(3);
        Radio radio1 = assertInstanceOf(Radio.class, getComponentInQuestion(mainLoop, 1));
        Table staticTable1 = assertInstanceOf(Table.class, getComponentInQuestion(mainLoop, 2));
        BodyCell cell1 = staticTable1.getBodyLines().getFirst().getBodyCells().get(1);
        assertEquals(ComponentTypeEnum.RADIO, cell1.getComponentType());

        Loop linkedLoop = (Loop) lunaticQuestionnaire.getComponents().get(4);
        Radio radio2 = assertInstanceOf(Radio.class, getComponentInQuestion(linkedLoop, 1));
        Table staticTable2 = assertInstanceOf(Table.class, getComponentInQuestion(linkedLoop, 2));
        BodyCell cell2 = staticTable2.getBodyLines().getFirst().getBodyCells().get(1);
        assertEquals(ComponentTypeEnum.RADIO, cell2.getComponentType());

        Radio radio3 = assertInstanceOf(Radio.class, getComponentInQuestion(lunaticQuestionnaire, 6));
        Dropdown dropdown = assertInstanceOf(Dropdown.class, getComponentInQuestion(lunaticQuestionnaire, 7));
        Table staticTable3 = assertInstanceOf(Table.class, getComponentInQuestion(lunaticQuestionnaire, 8));
        BodyCell cell31 = staticTable3.getBodyLines().getFirst().getBodyCells().get(1);
        BodyCell cell32 = staticTable3.getBodyLines().getFirst().getBodyCells().get(2);
        assertEquals(ComponentTypeEnum.RADIO, cell31.getComponentType());
        assertEquals(ComponentTypeEnum.DROPDOWN, cell32.getComponentType());
        RosterForLoop dynamicTable = assertInstanceOf(RosterForLoop.class, getComponentInQuestion(lunaticQuestionnaire, 9));
        BodyCell cellDynamic1 = dynamicTable.getComponents().get(0);
        BodyCell cellDynamic2 = dynamicTable.getComponents().get(1);
        assertEquals(ComponentTypeEnum.RADIO, cellDynamic1.getComponentType());
        assertEquals(ComponentTypeEnum.DROPDOWN, cellDynamic2.getComponentType());

        String optionVariableName = "NAME"; // in this test questionnaire, all UCQ options are defined from that variable
        assertEquals(optionVariableName, radio1.getOptionSource());
        assertEquals(optionVariableName, cell1.getOptionSource());
        assertEquals(optionVariableName, radio2.getOptionSource());
        assertEquals(optionVariableName, cell2.getOptionSource());
        assertEquals(optionVariableName, radio3.getOptionSource());
        assertEquals(optionVariableName, dropdown.getOptionSource());
        assertEquals(optionVariableName, cell31.getOptionSource());
        assertEquals(optionVariableName, cell32.getOptionSource());
        assertEquals(optionVariableName, cellDynamic1.getOptionSource());
        assertEquals(optionVariableName, cellDynamic2.getOptionSource());
    }

    /** Method to ease access of component wrapped in the Question component. */
    private static ComponentType getComponentInQuestion(List<ComponentType> lunaticComponents, int index) {
        if (! (lunaticComponents.get(index) instanceof Question question))
            throw new IllegalArgumentException("Not a question.");
        return question.getComponents().getFirst();
    }
    private static ComponentType getComponentInQuestion(Questionnaire questionnaire, int index) {
        return getComponentInQuestion(questionnaire.getComponents(), index);
    }
    private static ComponentType getComponentInQuestion(Loop loop, int index) {
        return getComponentInQuestion(loop.getComponents(), index);
    }

}
