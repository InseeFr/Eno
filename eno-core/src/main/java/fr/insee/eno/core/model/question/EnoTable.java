package fr.insee.eno.core.model.question;

import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.question.table.TableCell;

import java.util.List;

/** Interface to make polymorphism between table and dynamic table objects. */
public interface EnoTable {

    String getId();
    String getName();
    CodeList getHeader();
    List<String> getVariableNames();
    List<TableCell> getTableCells();

    // No left column since it is not a property of dynamic tables

}
