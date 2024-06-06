package fr.insee.eno.core.model.question;

import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.question.table.CellLabel;
import fr.insee.eno.core.model.question.table.NoDataCell;
import fr.insee.eno.core.model.question.table.ResponseCell;
import fr.insee.eno.core.model.question.table.TableCell;

import java.util.List;

/** Interface to make polymorphism between different kinds of table question objects. */
public interface EnoTable {

    String getId();
    String getName();
    String getHeaderCodeListReference();
    CodeList getHeader();
    void setHeader(CodeList codeList);
    List<String> getVariableNames();
    List<ResponseCell> getResponseCells();
    List<NoDataCell> getNoDataCells();
    List<CellLabel> getCellLabels();

    // No left column since it is not a property of dynamic tables

}
