package fr.insee.eno.core.model.code;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.model.EnoIdentifiableObject;
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

    @DDI(contextType = CodeListType.class, field = "getCodeList()")
    List<CodeItem> codeItems = new ArrayList<>();

    /** Max depth of the code list. */
    private int maxDepth;

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
            if (level > maxDepth) maxDepth = level;
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
            codeItem.setHSize(maxDepth +1 - codeItem.level);
        } else {
            assert maxDepth >= 1 : "Horizontal sizes can not be computed if max level has not been computed.";
            // (default value for an int is 0, and if we get in this else that means that we must have max level >= 1)
            codeItem.setHSize(1);
            for (CodeItem codeItem1 : codeItem.getCodeItems()) {
                computeHorizontalSizes(codeItem1);
            }
        }
    }

}
