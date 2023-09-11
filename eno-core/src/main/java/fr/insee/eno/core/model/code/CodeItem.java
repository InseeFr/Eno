package fr.insee.eno.core.model.code;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.BodyCell;
import fr.insee.lunatic.model.flat.HeaderType;
import fr.insee.lunatic.model.flat.Options;
import logicalproduct33.CodeType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Context(format = Format.DDI, type = CodeType.class)
@Context(format = Format.LUNATIC, type = {Options.class, HeaderType.class, BodyCell.class})
public class CodeItem extends EnoObject {

    @DDI("getIDArray(0).getStringValue()")
    String id;

    @DDI("getValue().getStringValue()")
    @Lunatic("setValue(#param)")
    String value;

    @DDI("#index.get(#this.getCategoryReference().getIDArray(0).getStringValue()).getLabelArray(0)")
    @Lunatic("setLabel(#param)")
    Label label;

    /**
     * Nested code lists.
     */
    @DDI("getCodeList()")
    List<CodeItem> codeItems = new ArrayList<>();

    /**
     * Depth level of the code. Starts at 0 for codes in CodeList.
     */
    int level;

    /**
     * "Horizontal" size onf the code item.
     * In Lunatic "colspan" is set only if it is > 1.
     */
    @Lunatic("#this instanceof T(fr.insee.lunatic.model.flat.BodyCell) ? " +
            "setColspan(#param > 1 ? T(java.math.BigInteger).valueOf(#param) : null) :" +
            "null")
    int hSize;

    /**
     * "Vertical" size onf the code item.
     * In Lunatic "rowspan" is set only if it is > 1.
     */
    @Lunatic("#this instanceof T(fr.insee.lunatic.model.flat.BodyCell) ? " +
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
