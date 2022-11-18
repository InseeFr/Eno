package fr.insee.eno.core.model.code;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.label.Label;
import fr.insee.lunatic.model.flat.BodyLine;
import fr.insee.lunatic.model.flat.HeaderType;
import fr.insee.lunatic.model.flat.Options;
import logicalproduct33.CodeListType;
import logicalproduct33.CodeType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
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
    public static class CodeItem extends EnoObject {

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

        /** Nested code lists. */
        @DDI(contextType = CodeType.class, field = "getCodeList()")
        List<CodeItem> codeItems = new ArrayList<>();

        /** Depth level of the code. Starts at 0 for codes in CodeList. */
        int level;

        /** "Horizontal" size onf the code item.
         * In Lunatic "colspan" is set only if it is > 1. */
        @Lunatic(contextType = BodyLine.class,
                field = "#this instanceof T(fr.insee.lunatic.model.flat.BodyType) ? " +
                        "setColspan(#param > 1 ? T(java.math.BigInteger).valueOf(#param) : null) :" +
                        "null")
        int hSize;

        /** "Vertical" size onf the code item.
         * In Lunatic "rowspan" is set only if it is > 1. */
        @Lunatic(contextType = BodyLine.class,
                field = "#this instanceof T(fr.insee.lunatic.model.flat.BodyType) ? " +
                        "setRowspan(#param > 1 ? T(java.math.BigInteger).valueOf(#param) : null) :" +
                        "null")
        int vSize;

        /** Return the number of sub-codes in the code item. */
        public int size() {
            return codeItems.size();
        }

        void foo(int i) {
            BodyLine b= new BodyLine();
            b.setColspan(i > 1 ? BigInteger.valueOf(5) : null);
        }
    }

    @DDI(contextType = CodeListType.class, field = "getLabelArray(0).getContentArray(0).getStringValue()")
    String name;

    @DDI(contextType = CodeListType.class, field = "getCodeList()")
    List<CodeItem> codeItems = new ArrayList<>();

    /** Max depth of the code list. */
    int maxLevel;

    /** Return the number of codes in the code list. */
    public int size() {
        return codeItems.size();
    }

    public void computeSizes() {
        // Compute methods have to be used in a certain order
        // Some methods could be merged for slight performance improvement, but it would be to the detriment of code readability
        computeVerticalSizes();
        computeDepths();
        computeHorizontalSizes();
    }
    private void computeVerticalSizes() {
        codeItems.forEach(this::computeVerticalSizes);
    }
    private int computeVerticalSizes(CodeItem codeItem) {
        if (codeItem.size() == 0) {
            codeItem.setVSize(1);
            return 1;
        } else {
            int vSize = 0;
            for (CodeItem codeItem1 : codeItem.getCodeItems()) {
                vSize += computeVerticalSizes(codeItem1);
            }
            codeItem.setVSize(vSize);
            return vSize;
        }
    }
    private void computeDepths() {
        codeItems.forEach(codeItem -> computeDepths(codeItem, 0));
    }
    private void computeDepths(CodeItem codeItem, int level) {
        codeItem.setLevel(level);
        if (codeItem.size() == 0) {
            if (level > maxLevel) maxLevel = level;
        } else {
            for (CodeItem codeItem1 : codeItem.getCodeItems()) {
                computeDepths(codeItem1, level+1);
            }
        }
    }
    private void computeHorizontalSizes() {
        codeItems.forEach(this::computeHorizontalSizes);
    }
    private void computeHorizontalSizes(CodeItem codeItem) {
        if (codeItem.size() == 0) {
            codeItem.setHSize(maxLevel+1 - codeItem.level);
        } else {
            assert maxLevel >= 1 : "Horizontal sizes can not be computed if max level has not been computed.";
            // (default value for an int is 0, and if we get in this else that means that we must have max level >= 1)
            codeItem.setHSize(1);
            for (CodeItem codeItem1 : codeItem.getCodeItems()) {
                computeHorizontalSizes(codeItem1);
            }
        }
    }

}
