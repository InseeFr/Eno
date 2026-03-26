package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.eno.core.model.question.table.UniqueChoiceCell;
import fr.insee.lunatic.model.flat.BodyCell;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UniqueChoiceCellTest {

    @Test
    void dynamic_ucq_cell_sets_option_source() {
        UniqueChoiceCell enoCell = new UniqueChoiceCell();
        enoCell.setDisplayFormat(UniqueChoiceQuestion.DisplayFormat.RADIO);
        enoCell.setOptionSource("FIRST_NAME");

        BodyCell lunaticCell = new BodyCell();

        new LunaticMapper().mapEnoObject(enoCell, lunaticCell);

        assertEquals("FIRST_NAME", lunaticCell.getOptionSource());
    }

    @Test
    void dynamic_ucq_cell_has_no_static_options() {
        UniqueChoiceCell enoCell = new UniqueChoiceCell();
        enoCell.setDisplayFormat(UniqueChoiceQuestion.DisplayFormat.RADIO);
        enoCell.setOptionSource("FIRST_NAME");

        BodyCell lunaticCell = new BodyCell();

        new LunaticMapper().mapEnoObject(enoCell, lunaticCell);

        assertTrue(lunaticCell.getOptions().isEmpty());
    }

    @Test
    void ucqCellVariableOptions() {
        UniqueChoiceCell enoCell = new UniqueChoiceCell();
        enoCell.setOptionSource("SOME_ITERATION_VARIABLE");

        BodyCell lunaticCell = new BodyCell();

        new LunaticMapper().mapEnoObject(enoCell, lunaticCell);

        assertEquals("SOME_ITERATION_VARIABLE", lunaticCell.getOptionSource());
    }

}
