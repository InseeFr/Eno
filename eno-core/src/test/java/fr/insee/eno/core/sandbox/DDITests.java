package fr.insee.eno.core.sandbox;

import datacollection33.*;
import datacollection33.impl.TextTypeImpl;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.reference.DDIIndex;
import group33.ResourcePackageType;
import instance33.DDIInstanceDocument;
import instance33.DDIInstanceType;
import logicalproduct33.VariableGroupType;
import logicalproduct33.VariableSchemeType;
import org.junit.jupiter.api.Test;
import reusable33.*;
import reusable33.impl.ContentTypeImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DDITests {

    @Test
    void xmlBeansAndDDI() {
        IDType idType = IDType.Factory.newInstance();
        String stringId = "foo";
        idType.setStringValue(stringId);
        assertEquals(stringId, idType.getStringValue());
    }

    @Test
    void tableQuestion() throws DDIParsingException {
        //
        DDIIndex ddiIndex = new DDIIndex();
        ddiIndex.indexDDI(DDIDeserializer.deserialize(
                this.getClass().getClassLoader().getResource("functional/ddi/ddi-l20g2ba7.xml")));
        //
        QuestionGridType tableQuestionGrid = (QuestionGridType) ddiIndex.get("l8u8d67h");
        assertNotNull(tableQuestionGrid);
    }

    @Test
    void ddiObjects() {
        // DDI instance
        DDIInstanceDocument ddiInstanceDocument = DDIInstanceDocument.Factory.newInstance();
        DDIInstanceType ddiInstanceType = DDIInstanceType.Factory.newInstance();
        ddiInstanceDocument.setDDIInstance(ddiInstanceType);

        // Resource package
        ResourcePackageType resourcePackage = ResourcePackageType.Factory.newInstance();
        ddiInstanceDocument.getDDIInstance().getResourcePackageList().add(resourcePackage);

        // Citation
        String ddiCitation = "foo-citation";
        ddiInstanceDocument.getDDIInstance().setCitation(CitationType.Factory.newInstance());
        ddiInstanceDocument.getDDIInstance().getCitation().setTitle(InternationalStringType.Factory.newInstance());
        ddiInstanceDocument.getDDIInstance().getCitation().getTitle().getStringList().add(StringType.Factory.newInstance());
        ddiInstanceDocument.getDDIInstance().getCitation().getTitle().getStringArray(0).setStringValue(ddiCitation);
        String citation = ddiInstanceDocument.getDDIInstance().getCitation().getTitle().getStringArray(0).getStringValue();
        assertEquals(ddiCitation, citation);

        // Variable group
        VariableSchemeType variableSchemeType = VariableSchemeType.Factory.newInstance();
        variableSchemeType.getVariableGroupList().add(VariableGroupType.Factory.newInstance());
        resourcePackage.getVariableSchemeList().add(variableSchemeType);
        VariableGroupType firstVariableGroupType = resourcePackage.getVariableSchemeArray(0).getVariableGroupArray(0);

        // References in a variable group
        String referenceId = "foo-reference-id";
        List<ReferenceType> referenceList = firstVariableGroupType.getVariableGroupReferenceList();
        referenceList.add(ReferenceType.Factory.newInstance());
        referenceList.get(0).getIDList().add(IDType.Factory.newInstance());
        referenceList.get(0).getIDList().get(0).setStringValue(referenceId);
        String firstReference = referenceList.get(0).getIDArray(0).getStringValue();
        assertEquals(referenceId, firstReference);

        // Statement item
        resourcePackage.getControlConstructSchemeList().add(ControlConstructSchemeType.Factory.newInstance());
        resourcePackage.getControlConstructSchemeArray(0).getControlConstructList().add(
                StatementItemType.Factory.newInstance());
        StatementItemType statementItem = (StatementItemType) resourcePackage.getControlConstructSchemeArray(0)
                .getControlConstructList().get(0);

        // Label of a statement item
        String statementString = "foo-statement";
        statementItem.getDisplayTextList().add(DynamicTextType.Factory.newInstance());
        statementItem.getDisplayTextArray(0).getTextContentList().add(LiteralTextType.Factory.newInstance());
        ((LiteralTextType) statementItem.getDisplayTextArray(0).getTextContentArray(0))
                .setText(TextType.Factory.newInstance());
        ((TextTypeImpl) ((LiteralTextType) statementItem.getDisplayTextArray(0).getTextContentArray(0)).getText())
                .setStringValue(statementString);
        String statementItemLabel = ((TextTypeImpl) ((LiteralTextType) statementItem.getDisplayTextArray(0)
                .getTextContentArray(0)).getText()).getStringValue();
        // Note: why does TextType don't have the getStringValue() method, but the implementation has it??
        assertEquals(statementString, statementItemLabel);

        // Instruction name
        String instructionName = "foo-instruction";
        resourcePackage.getInterviewerInstructionSchemeList().add(InterviewerInstructionSchemeType.Factory.newInstance());
        resourcePackage.getInterviewerInstructionSchemeArray(0).getInstructionList()
                .add(InstructionType.Factory.newInstance());
        resourcePackage.getInterviewerInstructionSchemeArray(0).getInstructionArray(0)
                .getInstructionNameList().add(NameType.Factory.newInstance());
        resourcePackage.getInterviewerInstructionSchemeArray(0).getInstructionArray(0)
                .getInstructionNameArray(0).getStringList().add(StringType.Factory.newInstance());
        resourcePackage.getInterviewerInstructionSchemeArray(0).getInstructionArray(0)
                .getInstructionNameArray(0).getStringArray(0).setStringValue(instructionName);
        String firstInstructionName = resourcePackage.getInterviewerInstructionSchemeArray(0)
                .getInstructionArray(0).getInstructionNameArray(0).getStringArray(0).getStringValue();
        assertEquals(instructionName, firstInstructionName);
    }

    @Test
    void labelInInstruction() {
        //
        String fooLabel = "Foo label";

        // Instruction
        InstructionType instructionType = InstructionType.Factory.newInstance();
        // Content type of instructionType.getInstructionTextList()
        DynamicTextType instructionText = DynamicTextType.Factory.newInstance();
        // Content types of instructionText.getTextContentList()
        //TextContentType textContent = TextContentType.Factory.newInstance();
        LiteralTextType literalText = LiteralTextType.Factory.newInstance();
        // Content type of literalText.getText()
        //TextType text = TextType.Factory.newInstance();
        TextTypeImpl textImpl = (TextTypeImpl) TextType.Factory.newInstance();
        // Note: for weird reason, TextType objects doesn't have a string value
        textImpl.setStringValue(fooLabel);
        literalText.setText(textImpl);
        instructionText.getTextContentList().add(literalText);
        instructionType.getInstructionTextList().add(instructionText);

        // getInstructionTextArray(0).getTextContentArray(0).getText().getStringValue()
        String instructionLabel = ((TextTypeImpl) ((LiteralTextType) instructionType.getInstructionTextArray(0)
                .getTextContentArray(0)).getText()).getStringValue();
        //
        assertEquals(fooLabel, instructionLabel);
    }

    @Test
    void labelInDeclaration() {
        //
        String fooLabel = "Foo label";

        // Statement item
        StatementItemType statementItemType = StatementItemType.Factory.newInstance();
        // Content type of statementItemType.getDisplayTextList()
        DynamicTextType statementItemText = DynamicTextType.Factory.newInstance();
        // Content types of instructionText.getTextContentList()
        //TextContentType textContent = TextContentType.Factory.newInstance();
        LiteralTextType literalText = LiteralTextType.Factory.newInstance();
        // Content type of literalText.getText()
        //TextType text = TextType.Factory.newInstance();
        TextTypeImpl textImpl = (TextTypeImpl) TextType.Factory.newInstance();
        // Note: for weird reason, TextType objects doesn't have a string value
        textImpl.setStringValue(fooLabel);
        literalText.setText(textImpl);
        statementItemText.getTextContentList().add(literalText);
        statementItemType.getDisplayTextList().add(statementItemText);

        // getDisplayTextArray(0).getTextContentArray(0).getText().getStringValue()
        String declarationLabel = ((TextTypeImpl) ((LiteralTextType) statementItemType.getDisplayTextArray(0)
                .getTextContentArray(0)).getText()).getStringValue();
        //
        assertEquals(fooLabel, declarationLabel);
    }

    @Test
    void labelInQuestion() {
        //
        String fooLabel = "Foo label";

        // Question item (note: same for question grid)
        QuestionItemType questionItemType = QuestionItemType.Factory.newInstance();
        // Content type of questionItemType.getQuestionTextList()
        DynamicTextType questionText = DynamicTextType.Factory.newInstance();
        // Content types of instructionText.getTextContentList()
        //TextContentType textContent = TextContentType.Factory.newInstance();
        LiteralTextType literalText = LiteralTextType.Factory.newInstance();
        // Content type of literalText.getText()
        //TextType text = TextType.Factory.newInstance();
        TextTypeImpl textImpl = (TextTypeImpl) TextType.Factory.newInstance();
        // Note: for weird reason, TextType objects doesn't have a string value
        textImpl.setStringValue(fooLabel);
        literalText.setText(textImpl);
        questionText.getTextContentList().add(literalText);
        questionItemType.getQuestionTextList().add(questionText);

        // getQuestionTextArray(0).getTextContentArray(0).getText().getStringValue()
        String questionLabel = ((TextTypeImpl) ((LiteralTextType) questionItemType.getQuestionTextArray(0)
                .getTextContentArray(0)).getText()).getStringValue();
        //
        assertEquals(fooLabel, questionLabel);
    }

    @Test
    void labelInSequence() {
        //
        String fooLabel = "Foo label";

        // Sequence
        SequenceType sequenceType = SequenceType.Factory.newInstance();
        // Content type of sequenceType.getLabelList()
        LabelType sequenceLabel = LabelType.Factory.newInstance();
        // Content type of sequenceLabel.getContentList()
        ContentTypeImpl content = (ContentTypeImpl) ContentType.Factory.newInstance();
        // Here again ContentType doesn't have a string value
        content.setStringValue(fooLabel);
        sequenceLabel.getContentList().add(content);
        sequenceType.getLabelList().add(sequenceLabel);

        // getLabelArray(0).getContentArray(0).getStringValue()
        String sequenceStringLabel = ((ContentTypeImpl) sequenceType.getLabelArray(0).getContentArray(0))
                .getStringValue();
        //
        assertEquals(fooLabel, sequenceStringLabel);
    }

}
