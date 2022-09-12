package fr.insee.eno.core.sandbox;

import datacollection33.*;
import datacollection33.impl.TextContentTypeImpl;
import datacollection33.impl.TextTypeImpl;
import fr.insee.eno.core.parsers.DDIParser;
import group33.ResourcePackageType;
import instance33.DDIInstanceDocument;
import logicalproduct33.VariableGroupType;
import org.junit.jupiter.api.Test;
import reusable33.ContentType;
import reusable33.IDType;
import reusable33.LabelType;
import reusable33.ReferenceType;
import reusable33.impl.ContentTypeImpl;

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

    @Test
    public void labelInInstruction() {
        //
        String fooLabel = "Foo label";

        // Instruction
        InstructionType instructionType = InstructionType.Factory.newInstance();
        // Content type of instructionType.getInstructionTextList()
        DynamicTextType instructionText = DynamicTextType.Factory.newInstance();
        // Content types of instructionText.getTextContentList()
        TextContentType textContent = TextContentType.Factory.newInstance();
        LiteralTextType literalText = LiteralTextType.Factory.newInstance();
        // Content type of literalText.getText()
        TextType text = TextType.Factory.newInstance();
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
    public void labelInDeclaration() {
        //
        String fooLabel = "Foo label";

        // Statement item
        StatementItemType statementItemType = StatementItemType.Factory.newInstance();
        // Content type of statementItemType.getDisplayTextList()
        DynamicTextType statementItemText = DynamicTextType.Factory.newInstance();
        // Content types of instructionText.getTextContentList()
        TextContentType textContent = TextContentType.Factory.newInstance();
        LiteralTextType literalText = LiteralTextType.Factory.newInstance();
        // Content type of literalText.getText()
        TextType text = TextType.Factory.newInstance();
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
    public void labelInQuestion() {
        //
        String fooLabel = "Foo label";

        // Question item (note: same for question grid)
        QuestionItemType questionItemType = QuestionItemType.Factory.newInstance();
        // Content type of questionItemType.getQuestionTextList()
        DynamicTextType questionText = DynamicTextType.Factory.newInstance();
        // Content types of instructionText.getTextContentList()
        TextContentType textContent = TextContentType.Factory.newInstance();
        LiteralTextType literalText = LiteralTextType.Factory.newInstance();
        // Content type of literalText.getText()
        TextType text = TextType.Factory.newInstance();
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
    public void labelInSequence() {
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
