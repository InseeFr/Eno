package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.PoguesDDIToLunatic;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.Question;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.RosterForLoop;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TableCellsReadOnlyTest {

    private final ClassLoader classLoader = this.getClass().getClassLoader();

    /** Same Eno parameters for all tests. */
    private final EnoParameters enoParameters = EnoParameters.of(
            EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);

    @Test
    @DisplayName("Dynamic table with ReadOnly")
    void integrationTest_fromPoguesPlusDDI() throws ParsingException {
        Questionnaire lunaticQuestionnaire = PoguesDDIToLunatic
                .fromInputStreams(
                        classLoader.getResourceAsStream("integration/pogues/pogues-dynamic-table-read-only.json"),
                        classLoader.getResourceAsStream("integration/ddi/ddi-dynamic-table-read-only.xml"))
                .transform(enoParameters);
        RosterForLoop rosterForLoop = (RosterForLoop) ((Question) lunaticQuestionnaire.getComponents().get(1)).getComponents().getFirst();
        assertEquals("TABLEAUDYN1 = \"bob\"", rosterForLoop.getComponents().get(2).getConditionReadOnly().getValue());
    }

}
