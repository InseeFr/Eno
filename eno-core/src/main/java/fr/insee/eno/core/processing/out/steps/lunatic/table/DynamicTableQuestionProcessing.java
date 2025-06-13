package fr.insee.eno.core.processing.out.steps.lunatic.table;

import fr.insee.eno.core.exceptions.technical.MappingException;
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

        setRosterSize(lunaticRoster, enoTable);

        List<TableCell> enoTableCells = new ArrayList<>();
        enoTableCells.addAll(enoTable.getResponseCells());
        enoTableCells.addAll(enoTable.getNoDataCells());
        if (enoTableCells.contains(null))
            throw new MappingException(String.format("Dynamic question '%s' has a null column.", enoTable.getName()));
        enoTableCells.sort(Comparator.comparing(TableCell::getColumnNumber));
        for (TableCell enoCell : enoTableCells) {
            BodyCell lunaticCell = TableQuestionProcessing.convertEnoCell(enoCell);
            lunaticRoster.getComponents().add(lunaticCell);
        }
    }

    private static void setRosterSize(RosterForLoop lunaticRoster, DynamicTableQuestion enoTable) {
        LinesRoster lines = new LinesRoster();
        if (enoTable.getMinLines() != null && enoTable.getMaxLines() != null)
            setMinMaxProperties(enoTable, lines);
        else if (enoTable.getMaxSizeExpression() != null)
            setSizeExpression(enoTable, lines);
        else
            throw new IllegalStateException(
                    "Table question '" + enoTable.getId() + "' has neither a min/max nor an expression size.");
        lunaticRoster.setLines(lines);
    }

    private static void setMinMaxProperties(DynamicTableQuestion enoTable, LinesRoster lines) {
        LabelType minLabel = new LabelType();
        minLabel.setType(LabelTypeEnum.VTL);
        minLabel.setValue(enoTable.getMinLines().toString());
        lines.setMin(minLabel);

        LabelType maxLabel = new LabelType();
        maxLabel.setType(LabelTypeEnum.VTL);
        maxLabel.setValue(enoTable.getMaxLines().toString());
        lines.setMax(maxLabel);
    }

    /** Business rule: if the size of the roster for loop (dynamic table) is defined by an expression, the expression
     * cannot be different between min and max.
     * Note: in this case, we could have chosen to create an 'iterations' property in the Lunatic model to have the
     * same property as in loop objects, but the min=max solution does the job. */
    private static void setSizeExpression(DynamicTableQuestion enoTable, LinesRoster lines) {
        LabelType maxSizeLabel = new LabelType();
        maxSizeLabel.setType(LabelTypeEnum.VTL);
        maxSizeLabel.setValue(enoTable.getMaxSizeExpression().getValue());
        LabelType minSizeLabel = new LabelType();
        minSizeLabel.setType(LabelTypeEnum.VTL);
        if (enoTable.getMinSizeExpression() != null)
            minSizeLabel.setValue(enoTable.getMinSizeExpression().getValue());
        else // if missing, its value is the same as the max
            minSizeLabel.setValue(enoTable.getMaxSizeExpression().getValue());
        lines.setMin(minSizeLabel);
        lines.setMax(maxSizeLabel);
    }

}
