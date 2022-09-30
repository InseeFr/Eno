package fr.insee.eno.core.sandbox;

import datacollection33.*;
import datacollection33.impl.TextTypeImpl;
import fr.insee.eno.core.parsers.DDIParser;
import group33.ResourcePackageType;
import instance33.DDIInstanceDocument;
import logicalproduct33.VariableGroupType;
import org.junit.jupiter.api.Test;
import reusable33.IDType;
import reusable33.ReferenceType;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DDITests {

    @Test
    public void xmlBeansAndDDI() {
        IDType idType = IDType.Factory.newInstance();
        String stringId = "foo";
        idType.setStringValue(stringId);
        assertEquals(stringId, idType.getStringValue());
    }

    @Test
    public void ddiObjects() throws IOException {
        // (Parse)
        DDIInstanceDocument ddiInstanceDocument = DDIParser.parse(
                this.getClass().getClassLoader().getResource("l10xmg2l.xml"));

        // Resource package
        ResourcePackageType resourcePackage = ddiInstanceDocument.getDDIInstance().getResourcePackageArray(0);

        // Citation
        String citation = ddiInstanceDocument.getDDIInstance().getCitation().getTitle().getStringArray(0).getStringValue();

        // Variable group
        VariableGroupType firstVariableGroupType = resourcePackage.getVariableSchemeArray(0).getVariableGroupArray(0);

        // References in a variable group
        List<ReferenceType> referenceList = firstVariableGroupType.getVariableGroupReferenceList();
        String firstReference = referenceList.get(0).getIDArray(0).getStringValue();

        // Statement item
        StatementItemType statementItem = (StatementItemType) resourcePackage.getControlConstructSchemeArray(0)
                .getControlConstructList().get(20);

        // Label of a statement item
        String statementItemLabel = ((TextTypeImpl) ((LiteralTextType) statementItem.getDisplayTextArray(0)
                .getTextContentArray(0)).getText()).getStringValue();
        // Note: why does TextType don't have the getStringValue() method, but the implementation has it??

        // Instruction name
        String firstInstructionName = resourcePackage.getInterviewerInstructionSchemeArray(0)
                .getInstructionArray(0).getInstructionNameArray(0).getStringArray(0).getStringValue();

        // Question scheme
        QuestionSchemeType questionScheme = ddiInstanceDocument.getDDIInstance().getResourcePackageArray(0)
                .getQuestionSchemeArray(0);
    }

}
