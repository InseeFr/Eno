package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.PoguesDDIToLunatic;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.navigation.ComponentFilter;
import fr.insee.eno.core.model.question.table.ResponseCell;
import fr.insee.eno.core.model.question.table.TextCell;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TableCellsReadOnlyTest {

    @Test
    void unitTest() {
        ResponseCell enoCell = new TextCell();
        enoCell.setComponentReadOnly(new ComponentFilter());
        enoCell.getComponentReadOnly().setValue("<some expression>");
        BodyCell lunaticCell = new BodyCell();

        new LunaticMapper().mapEnoObject(enoCell, lunaticCell);

        assertEquals("<some expression>", lunaticCell.getConditionReadOnly().getValue());
        assertEquals(LabelTypeEnum.VTL, lunaticCell.getConditionReadOnly().getType());
    }

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
