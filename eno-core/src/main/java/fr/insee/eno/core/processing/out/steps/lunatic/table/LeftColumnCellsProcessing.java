package fr.insee.eno.core.processing.out.steps.lunatic.table;

import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.lunatic.model.flat.BodyCell;
import fr.insee.lunatic.model.flat.BodyLine;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LeftColumnCellsProcessing {

    private int leftColumnColspan;
    private List<BodyLine> lunaticBody;

    private LeftColumnCellsProcessing() {}

    /**
     * Uses the Eno code list given to compute "rowspan" and "colspan" of Lunatic cells of the left column,
     * and return  a list of Lunatic body lines containing them.
     * @param enoTable Description of the table in the Eno model.
     * @return The "body" part of the Lunatic table, with only cells that correspond to the left column.
     */
    public static LeftColumnCellsProcessing from(TableQuestion enoTable) {
        return LeftColumnCellsProcessing.from(enoTable.getLeftColumn());
    }

    /**
     * Uses the Eno code list given to compute "rowspan" and "colspan" of Lunatic cells of the left column,
     * and return  a list of Lunatic body lines containing them.
     * @param leftColumnCodeList Description of the left column of a Lunatic table in the Eno model.
     * @return The "body" part of the Lunatic table, with only cells that correspond to the left column.
     */
    public static LeftColumnCellsProcessing from(CodeList leftColumnCodeList) {
        LeftColumnCellsProcessing result = new LeftColumnCellsProcessing();
        ComputeCodeListSizes.of(leftColumnCodeList);
        result.leftColumnColspan = leftColumnCodeList.getMaxDepth() + 1;
        result.lunaticBody = createLeftColumnCells(leftColumnCodeList);
        return result;
    }

    /**
     * Returns a list of body line containing body cells of the left column.
     * @param leftColumnCodeList which has the size properties computed.
     * @return A list of body line containing body cells of the left column.
     */
    private static List<BodyLine> createLeftColumnCells(CodeList leftColumnCodeList) {
        List<BodyLine> lunaticLines = new ArrayList<>();
        for (CodeItem codeItem : leftColumnCodeList.getCodeItems()) {
            lunaticLines.add(new BodyLine());
            flattenCodeItem(codeItem, lunaticLines);
            lunaticLines.remove(lunaticLines.size()-1); // (We could do something neater here maybe)
        }
        return lunaticLines;
    }

    /**
     * Iterates recursively on the code item and flatten it, maps each code item to a Lunatic body cell,
     * and inserts them in Lunatic body lines.
     * @param codeItem A code item object (that may hold other code items etc.)
     * @param lunaticLines Lunatic body lines.
     */
    private static void flattenCodeItem(CodeItem codeItem, List<BodyLine> lunaticLines) {
        // Map code item on lunatic cell
        BodyCell lunaticCell = new BodyCell();
        new LunaticMapper().mapEnoObject(codeItem, lunaticCell);
        // Add lunatic cell in flat list
        lunaticLines.get(lunaticLines.size()-1).getBodyCells().add(lunaticCell);
        // If the current code has no sub-codes, it's a new line
        if (codeItem.size() == 0) {
            lunaticLines.add(new BodyLine());
            return;
        }
        // Otherwise, recursive call on sub-codes
        for (CodeItem codeItem1 : codeItem.getCodeItems()) {
            flattenCodeItem(codeItem1, lunaticLines);
        }
    }

}
