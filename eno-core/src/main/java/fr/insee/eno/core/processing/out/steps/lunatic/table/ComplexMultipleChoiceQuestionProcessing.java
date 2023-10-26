package fr.insee.eno.core.processing.out.steps.lunatic.table;

import fr.insee.eno.core.model.question.ComplexMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.table.TableCell;
import fr.insee.lunatic.model.flat.BodyCell;
import fr.insee.lunatic.model.flat.BodyLine;
import fr.insee.lunatic.model.flat.Table;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/** Class that holds the conversion logic between model tables and Lunatic tables. */
@Slf4j
public class ComplexMultipleChoiceQuestionProcessing {

    private ComplexMultipleChoiceQuestionProcessing() {
        throw new IllegalArgumentException("Utility class");
    }

    public static void process(Table lunaticTable, ComplexMultipleChoiceQuestion enoMCQ) {

        // Create body lines with the left column cells from the corresponding code list
        List<BodyLine> lunaticBody = LeftColumnCellsProcessing.from(enoMCQ.getLeftColumn()).getLunaticBody();

        // For each line, just add the response cell (that is either a radio, dropdown, or checkboxOne)
        for (int i = 0; i < enoMCQ.getTableCells().size(); i++) {
            BodyLine lunaticLine = lunaticBody.get(i);
            TableCell enoCell = enoMCQ.getTableCells().get(i);
            BodyCell lunaticCell = TableQuestionProcessing.convertEnoCell(enoCell);
            lunaticLine.getBodyCells().add(lunaticCell);
        }

        lunaticTable.setBodyLines(lunaticBody);
    }

}
