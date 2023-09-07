package fr.insee.eno.core.processing.out.steps.lunatic.table;

import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.question.ComplexMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.table.TableCell;
import fr.insee.lunatic.model.flat.BodyCell;
import fr.insee.lunatic.model.flat.BodyLine;
import fr.insee.lunatic.model.flat.LabelType;
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

        List<BodyCell> headers = convertEnoHeaders(enoMCQ.getHeaders());

        for (int indexCell=0; indexCell < enoMCQ.getTableCells().size(); indexCell++) {
            BodyLine bodyLine = new BodyLine();

            TableCell enoCell = enoMCQ.getTableCells().get(indexCell);

            BodyCell lunaticCell = TableQuestionProcessing.convertEnoCell(enoCell);
            bodyLine.getBodyCells().add(headers.get(indexCell));
            bodyLine.getBodyCells().add(lunaticCell);
            lunaticTable.getBodyLines().add(bodyLine);
        }
    }

    private static List<BodyCell> convertEnoHeaders(CodeList headers) {
        return headers.getCodeItems().stream()
                .map(headerItem -> {
                    LabelType headerLabel = new LabelType();
                    headerLabel.setValue(headerItem.getLabel().getValue());
                    headerLabel.setType(headerItem.getLabel().getType());
                    BodyCell lunaticHeader = new BodyCell();
                    lunaticHeader.setValue(headerItem.getValue());
                    lunaticHeader.setLabel(headerLabel);
                    return lunaticHeader;
                }).toList();
    }

}
