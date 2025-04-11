package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.PoguesDDIToLunatic;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.Question;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.RosterForLoop;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TableCellsFilterTest {

    private final ClassLoader classLoader = this.getClass().getClassLoader();

    /** Same Eno parameters for all tests. */
    private final EnoParameters enoParameters = EnoParameters.of(
            EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);

    @Test
    @DisplayName("Component filter for InputNumber in RosterForLoop.")
    void functionalTest_fromPoguesPlusDDI() throws ParsingException {
        Questionnaire lunaticQuestionnaire = PoguesDDIToLunatic
                .fromInputStreams(
                        classLoader.getResourceAsStream("functional/pogues/cells-filtered/pogues-m92r209h.json"),
                        classLoader.getResourceAsStream("functional/ddi/cells-filtered/ddi-m92r209h.xml"))
                .transform(enoParameters);
        RosterForLoop rosterForLoop = (RosterForLoop) ((Question) lunaticQuestionnaire.getComponents().get(1)).getComponents().getFirst();
        assertEquals("AGE >= 18", rosterForLoop.getComponents().get(2).getConditionFilter().getValue());
    }

    @Test
    @DisplayName("Dynamic table with filtered column.")
    void integrationTest_fromPoguesPlusDDI() throws ParsingException {
        Questionnaire lunaticQuestionnaire = PoguesDDIToLunatic
                .fromInputStreams(
                        classLoader.getResourceAsStream("integration/pogues/pogues-table-cell-filter.json"),
                        classLoader.getResourceAsStream("integration/ddi/ddi-table-cell-filter.xml"))
                .transform(enoParameters);
        RosterForLoop rosterForLoop = (RosterForLoop) ((Question) lunaticQuestionnaire.getComponents().get(1)).getComponents().getFirst();
        assertEquals("DYNAMIC_TABLE1", rosterForLoop.getComponents().get(1).getConditionFilter().getValue());
    }

}
