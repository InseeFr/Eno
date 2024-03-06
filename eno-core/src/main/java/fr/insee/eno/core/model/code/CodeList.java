package fr.insee.eno.core.model.code;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.exceptions.business.IllegalDDIElementException;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.suggester.SuggesterConfigurationDTO;
import fr.insee.eno.core.parameter.Format;
import logicalproduct33.CodeListType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to hold code list information.
 * Contains two ordered lists with values and corresponding labels.
 * */
@Getter
@Setter
@Context(format = Format.DDI, type = CodeListType.class)
public class CodeList extends EnoIdentifiableObject {

    private static final String SUGGESTER_CODE_LIST_KEY = "SuggesterConfiguration";

    @DDI("!getCodeListNameList().isEmpty() ? getCodeListNameArray(0).getStringArray(0).getStringValue() : null")
    String name;

    /** Code list label inputted in Pogues.
     * Not used in Lunatic, so it is directly mapped as a string for now. */
    @DDI("!getLabelList().isEmpty ? getLabelArray(0).getContentArray(0).getStringValue() : null")
    String label;

    @DDI("getCodeList()")
    List<CodeItem> codeItems = new ArrayList<>();

    /** In DDI, suggester configuration options are defined in code list object.
     * Suggester configuration options are written in a CDATA in the "user attribute" property.
     * This property contains the raw content of the CDATA, it is deserialized in the 'suggesterConfiguration'
     * property through a processing. */
    @DDI("T(fr.insee.eno.core.model.code.CodeList).mapDDISuggesterConfiguration(#this)")
    String xmlSuggesterConfiguration;

    /** Suggester configuration options. */
    SuggesterConfigurationDTO suggesterConfiguration;

    public static String mapDDISuggesterConfiguration(CodeListType codeListType) {
        if (codeListType.getUserAttributePairList().isEmpty())
            return null;
        String userAttributeKey = codeListType.getUserAttributePairArray(0).getAttributeKey().getStringValue();
        if (! SUGGESTER_CODE_LIST_KEY.equals(userAttributeKey))
            throw new IllegalDDIElementException(String.format(
                    "Unexpected user attribute key '%s' in user attribute key in code list '%s'.",
                    userAttributeKey, codeListType.getIDArray(0).getStringValue()));
        return codeListType.getUserAttributePairArray(0).getAttributeValue().getStringValue();
    }

    /** Max depth of the code list. */
    private int maxDepth;

    /** Return the number of codes in the code list. */
    public int size() {
        return codeItems.size();
    }

    // TODO: the logic should be refactored outside the object imo
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
