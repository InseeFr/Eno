package fr.insee.eno.core.processing.out.steps.lunatic.control;

import fr.insee.eno.core.PoguesDDIToLunatic;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.Question;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.RosterForLoop;
import fr.insee.lunatic.model.flat.Table;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LunaticTableControlTest {

    @Test
    void dateInTables_integrationTest() throws ParsingException {
        ClassLoader classLoader = this.getClass().getClassLoader();

        // Given + When
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        Questionnaire lunaticQuestionnaire = PoguesDDIToLunatic
                .fromInputStreams(
                        classLoader.getResourceAsStream("integration/pogues/pogues-tables-date.json"),
                        classLoader.getResourceAsStream("integration/ddi/ddi-tables-date.xml"))
                .transform(enoParameters);

        // Then
        Question tableQuestion = (Question) lunaticQuestionnaire.getComponents().get(1);
        Question dynamicTableQuestion = (Question) lunaticQuestionnaire.getComponents().get(2);
        Table table = (Table) tableQuestion.getComponents().getFirst();
        RosterForLoop dynamicTable = (RosterForLoop) dynamicTableQuestion.getComponents().getFirst();
        //
        assertEquals(2, table.getControls().size());
        table.getControls().forEach(control ->
                assertTrue(control.getId().contains("format-date-borne-inf-sup")));
        assertEquals(1, dynamicTable.getControls().size());
        assertTrue(dynamicTable.getControls().getFirst().getId().contains("format-date-borne-inf-sup"));
    }

}
