package fr.insee.eno.core.processing.out.steps.lunatic.table;

import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.model.question.table.TableCell;
import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Class that holds the conversion logic between model dynamic tables and Lunatic 'roster' tables. */
@Slf4j
public class DynamicTableQuestionProcessing {

    private DynamicTableQuestionProcessing() {
        throw new IllegalArgumentException("Utility class");
    }

    public static void process(RosterForLoop lunaticRoster, DynamicTableQuestion enoTable) {

        // Header
        lunaticRoster.getHeader().addAll(HeaderCellsProcessing.from(enoTable, 0));

        LinesRoster lines = new LinesRoster();
        LabelType minLabel = new LabelType();
        minLabel.setType(LabelTypeEnum.VTL);
        minLabel.setValue(Integer.toString(enoTable.getMinLines().intValue()));
        lines.setMin(minLabel);

        LabelType maxLabel = new LabelType();
        maxLabel.setType(LabelTypeEnum.VTL);
        maxLabel.setValue(Integer.toString(enoTable.getMaxLines().intValue()));
        lines.setMax(maxLabel);
        lunaticRoster.setLines(lines);

        List<TableCell> enoTableCells = new ArrayList<>();
        enoTableCells.addAll(enoTable.getResponseCells());
        enoTableCells.addAll(enoTable.getNoDataCells());
        enoTableCells.sort(Comparator.comparing(TableCell::getColumnNumber));
        for (TableCell enoCell : enoTableCells) {
            BodyCell lunaticCell = TableQuestionProcessing.convertEnoCell(enoCell);
            lunaticRoster.getComponents().add(lunaticCell);
        }
    }
}
