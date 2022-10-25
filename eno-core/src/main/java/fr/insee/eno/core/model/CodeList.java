package fr.insee.eno.core.model;

import fr.insee.eno.core.annotations.DDI;
import logicalproduct33.CodeListType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to hold code list information.
 * Contains two ordered lists with values and corresponding labels.
 * Only used in table questions for now.
 * */
@Getter
@Setter
public class CodeList extends EnoIdentifiableObject {

    @DDI(contextType = CodeListType.class, field = "getLabelArray(0).getContentArray(0).getStringValue()")
    String name;

    @DDI(contextType = CodeListType.class,
            field = "getCodeList().![#this.getValue().getStringValue()]")
    List<String> values = new ArrayList<>();

    @DDI(contextType = CodeListType.class,
            field = "getCodeList().![#index.get(#this.getCategoryReference().getIDArray(0).getStringValue())" +
                    ".getLabelArray(0).getContentArray(0).getStringValue()]")
    List<String> labels = new ArrayList<>();

    /** Return the number of codes in the code list.
     * An exception is raised if values and labels sizes mismatch. */
    public int size() {
        int res = values.size();
        if (res == labels.size())
            return res;
        else
            throw new RuntimeException(String.format(
                    "Code list with id '%s' has %s values and %s labels.", id, res, labels.size()));
    }

}
