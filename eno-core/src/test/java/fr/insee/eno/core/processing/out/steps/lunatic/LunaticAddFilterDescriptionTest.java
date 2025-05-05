package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.PoguesDDIToLunatic;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.FilterDescription;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.insee.lunatic.model.flat.ComponentTypeEnum.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class LunaticAddFilterDescriptionTest {

    @Test
    void unitTest() {
        // TODO
        /* Note: cases to handle:
        - filter on sequence
        - filter on subsequence
        - filter on question
        - filter in a loop
        - make sure occurrence filters does not generate filter description components
         */
    }

    @Test
    void integrationTest_fromPoguesDDI() throws ParsingException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        // Given + When
        EnoParameters enoParameters = EnoParameters.of(EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        Questionnaire lunaticQuestionnaire = PoguesDDIToLunatic.fromInputStreams(
                classLoader.getResourceAsStream("integration/pogues/pogues-filter-description.json"),
                classLoader.getResourceAsStream("integration/ddi/ddi-filter-description.xml"))
                .transform(enoParameters);
        //
        assertEquals(
                List.of(
                        SEQUENCE, QUESTION, QUESTION,
                        SEQUENCE, FILTER_DESCRIPTION, QUESTION, FILTER_DESCRIPTION, QUESTION, QUESTION,
                        SEQUENCE),
                lunaticQuestionnaire.getComponents().stream().map(ComponentType::getComponentType).toList()
        );
        FilterDescription filterDescription1 = assertInstanceOf(FilterDescription.class,
                lunaticQuestionnaire.getComponents().get(4));
        FilterDescription filterDescription2 = assertInstanceOf(FilterDescription.class,
                lunaticQuestionnaire.getComponents().get(6));
        assertEquals("\"Filter for questions 1 to 3\"", filterDescription1.getLabel().getValue());
        assertEquals("\"Filter for question 2\"", filterDescription2.getLabel().getValue());
        assertEquals(LabelTypeEnum.TXT, filterDescription1.getLabel().getType());
        assertEquals(LabelTypeEnum.TXT, filterDescription2.getLabel().getType());
    }

}
