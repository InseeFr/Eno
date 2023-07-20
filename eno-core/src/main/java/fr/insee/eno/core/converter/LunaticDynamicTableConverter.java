package fr.insee.eno.core.converter;

import fr.insee.eno.core.Constant;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.model.question.table.TableCell;
import fr.insee.lunatic.model.flat.BodyCell;
import fr.insee.lunatic.model.flat.LabelType;
import fr.insee.lunatic.model.flat.LinesRoster;
import fr.insee.lunatic.model.flat.RosterForLoop;
import lombok.extern.slf4j.Slf4j;

/** Class that holds the conversion logic between model tables and Lunatic tables. */
@Slf4j
public class LunaticDynamicTableConverter {

    private LunaticDynamicTableConverter() {
        throw new IllegalArgumentException("Utility class");
    }

    public static RosterForLoop convertEnoDynamicTable(DynamicTableQuestion enoTable) {
        //
        RosterForLoop lunaticRoster = new RosterForLoop();

        // Header
        lunaticRoster.getHeader().addAll(LunaticTableConverter.convertEnoHeaders(enoTable.getHeader()));

        LinesRoster lines = new LinesRoster();
        LabelType minLabel = new LabelType();
        minLabel.setType(Constant.LUNATIC_LABEL_VTL);
        minLabel.setValue(Integer.toString(enoTable.getMinLines().intValue()));
        lines.setMin(minLabel);

        LabelType maxLabel = new LabelType();
        maxLabel.setType(Constant.LUNATIC_LABEL_VTL);
        maxLabel.setValue(Integer.toString(enoTable.getMaxLines().intValue()));
        lines.setMax(maxLabel);
        lunaticRoster.setLines(lines);

        for (int indexCell=0; indexCell < enoTable.getTableCells().size(); indexCell++) {
            TableCell enoCell = enoTable.getTableCells().get(indexCell);
            String variableName = enoTable.getVariableNames().get(indexCell);
            BodyCell lunaticCell = LunaticTableConverter.convertEnoCell(enoCell, variableName);
            lunaticRoster.getComponents().add(lunaticCell);
        }
        return lunaticRoster;
    }
}
