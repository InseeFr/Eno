package fr.insee.eno.core.model.code;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.label.Label;
import fr.insee.lunatic.model.flat.BodyLine;
import fr.insee.lunatic.model.flat.HeaderType;
import fr.insee.lunatic.model.flat.Options;
import logicalproduct33.CodeType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CodeItem extends EnoObject {

    @DDI(contextType = CodeType.class, field = "getIDArray(0).getStringValue()")
    String id;

    @DDI(contextType = CodeType.class,
            field = "getValue().getStringValue()")
    @Lunatic(contextType = {Options.class, HeaderType.class, BodyLine.class}, field = "setValue(#param)")
    String value;

    @DDI(contextType = CodeType.class,
            field = "#index.get(#this.getCategoryReference().getIDArray(0).getStringValue())" +
                    ".getLabelArray(0)")
    @Lunatic(contextType = {Options.class, HeaderType.class, BodyLine.class}, field = "setLabel(#param)")
    Label label;

    /**
     * Nested code lists.
     */
    @DDI(contextType = CodeType.class, field = "getCodeList()")
    List<CodeItem> codeItems = new ArrayList<>();

    /**
     * Depth level of the code. Starts at 0 for codes in CodeList.
     */
    int level;

    /**
     * "Horizontal" size onf the code item.
     * In Lunatic "colspan" is set only if it is > 1.
     */
    @Lunatic(contextType = BodyLine.class,
            field = "#this instanceof T(fr.insee.lunatic.model.flat.BodyType) ? " +
                    "setColspan(#param > 1 ? T(java.math.BigInteger).valueOf(#param) : null) :" +
                    "null")
    int hSize;

    /**
     * "Vertical" size onf the code item.
     * In Lunatic "rowspan" is set only if it is > 1.
     */
    @Lunatic(contextType = BodyLine.class,
            field = "#this instanceof T(fr.insee.lunatic.model.flat.BodyType) ? " +
                    "setRowspan(#param > 1 ? T(java.math.BigInteger).valueOf(#param) : null) :" +
                    "null")
    int vSize;

    /**
     * Return the number of sub-codes in the code item.
     */
    public int size() {
        return codeItems.size();
    }

}
