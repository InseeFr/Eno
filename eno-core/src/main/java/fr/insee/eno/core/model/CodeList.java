package fr.insee.eno.core.model;

import fr.insee.eno.core.annotations.DDI;
import logicalproduct33.CodeListType;
import logicalproduct33.CodeType;
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

    @Getter
    @Setter
    public static class CodeItem extends EnoIdentifiableObject {

        @DDI(contextType = CodeType.class,
                field = "getValue().getStringValue()")
        String value;

        @DDI(contextType = CodeType.class,
                field = "#index.get(#this.getCategoryReference().getIDArray(0).getStringValue())" +
                        ".getLabelArray(0).getContentArray(0).getStringValue()")
        String label;

    }

    @DDI(contextType = CodeListType.class, field = "getLabelArray(0).getContentArray(0).getStringValue()")
    String name;

    @DDI(contextType = CodeListType.class,
            field = "getCodeList()")
    List<CodeItem> codeItems = new ArrayList<>();

    /** Return the number of codes in the code list. */
    public int size() {
        return codeItems.size();
    }

}
