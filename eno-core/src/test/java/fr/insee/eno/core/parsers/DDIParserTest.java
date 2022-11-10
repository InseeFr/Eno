package fr.insee.eno.core.parsers;

import datacollection33.*;
import datacollection33.impl.LiteralTextTypeImpl;
import datacollection33.impl.TextTypeImpl;
import fr.insee.eno.core.HelloTest;
import fr.insee.eno.core.exceptions.DDIParsingException;
import group33.ResourcePackageType;
import instance33.DDIInstanceDocument;
import instance33.DDIInstanceType;
import logicalproduct33.CodeListType;
import logicalproduct33.CodeType;
import logicalproduct33.VariableType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.impl.schema.SchemaTypeImpl;
import org.junit.jupiter.api.Test;
import reusable33.TextDomainType;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DDIParserTest {

    @Test
    public void parserDDITest() throws IOException, DDIParsingException {
        //
        DDIInstanceDocument ddiInstanceDocument = DDIParser.parse( // FIXME: https://stackoverflow.com/questions/45718145/intellij-errorjava-java-lang-exceptionininitializererror
                this.getClass().getClassLoader().getResource("in/ddi/l10xmg2l.xml"));


        //
        assertNotNull(ddiInstanceDocument);
        //
        assertFalse(ddiInstanceDocument.getDDIInstance().getIDList().isEmpty());
        assertEquals("INSEE-l10xmg2l", ddiInstanceDocument.getDDIInstance().getIDList().get(0).getStringValue());

        //
        ResourcePackageType resourcePackage = ddiInstanceDocument.getDDIInstance().getResourcePackageArray(0);

        //
        List<ControlConstructType> foo = resourcePackage.getControlConstructSchemeArray(0).getControlConstructList()
                .stream()
                .filter(controlConstructType -> controlConstructType instanceof SequenceType)
                .toList();
        assertNotNull(foo);

        //
        List<QuestionItemType> questionItemList = resourcePackage.getQuestionSchemeArray(0).getQuestionItemList();
        List<QuestionGridType> questionGridList = resourcePackage.getQuestionSchemeArray(0).getQuestionGridList();
        questionItemList.get(0).getOutParameterArray(0).getParameterNameArray(0).getStringArray(0).getStringValue();
        //questionGridList.get(0).getStructuredMixedGridResponseDomain().getGridResponseDomainInMixedList();

        //
        String firstInstructionLabel = ((TextTypeImpl) ((LiteralTextType) resourcePackage
                .getInterviewerInstructionSchemeArray(0).getInstructionArray(0)
                .getInstructionTextArray(0).getTextContentArray(0))
                .getText()).getStringValue();

        //
        VariableType unitVariable = resourcePackage.getVariableSchemeArray(0).getVariableList()
                .stream().filter(variableType -> variableType.getIDArray(0).getStringValue().equals("kyis3r6p"))
                .findAny().orElse(null);
        assertNotNull(unitVariable);

        //
        ControlConstructSchemeType controlConstructScheme = resourcePackage.getControlConstructSchemeArray(0);
        assertNotNull(controlConstructScheme);
    }

    @Test
    public void parserDDIWithFilter() throws IOException, DDIParsingException {
        //
        DDIInstanceType ddiInstance = DDIParser.parse(
                        this.getClass().getClassLoader().getResource("in/ddi/questionnaire-avec-filtre-eno-java.xml"))
                .getDDIInstance();
        //
        assertNotNull(ddiInstance);
        //
        List<ControlConstructType> controlConstructList = ddiInstance.getResourcePackageArray(0)
                .getControlConstructSchemeArray(0).getControlConstructList();
        assertNotNull(controlConstructList);
    }

    @Test
    public void parserDDIWithMcq() throws IOException, DDIParsingException {
        //
        DDIInstanceType ddiInstance = DDIParser.parse(
                        this.getClass().getClassLoader().getResource("in/ddi/l10xmg2l_avec_qcm_et_obligatoires.xml"))
                .getDDIInstance();
        //
        assertNotNull(ddiInstance);

        //
        List<QuestionGridType> questionGridList = ddiInstance.getResourcePackageArray(0).getQuestionSchemeArray(0).getQuestionGridList();

        //
        String codeListId = ((CodeDomainType) ddiInstance.getResourcePackageArray(0).getQuestionSchemeArray(0)
                .getQuestionItemList().get(13).getResponseDomain())
                .getCodeListReference().getIDArray(0).getStringValue();
        //
        assertEquals("jfjevykh", codeListId);
    }

    @Test
    public void parseSandboxDDI() throws IOException, DDIParsingException {
        //
        DDIInstanceType ddiInstance = DDIParser.parse(
                        this.getClass().getClassLoader().getResource("in/ddi/sandbox.xml"))
                .getDDIInstance();
        //
        ResourcePackageType resourcePackage = ddiInstance.getResourcePackageArray(0);
        //
        List<VariableType> variables = resourcePackage.getVariableSchemeArray(0).getVariableList();
        assertFalse(variables.isEmpty());
    }

    @Test
    public void parseDDIComplexCodeList() throws IOException, DDIParsingException {
        //
        DDIInstanceType ddiInstance = DDIParser.parse(
                        this.getClass().getClassLoader().getResource("in/ddi/liste-de-codes-imbrications.xml"))
                .getDDIInstance();
        //
        ResourcePackageType resourcePackage = ddiInstance.getResourcePackageArray(0);
        CodeListType codeList = resourcePackage.getCodeListSchemeArray(0).getCodeListList().get(0);

        //
        assertTrue(codeList.getCodeList().get(0).getIsDiscrete());
        assertFalse(codeList.getCodeList().get(1).getIsDiscrete());

        //
        CodeType m2 = codeList.getCodeList().get(1);
        CodeType m21 = m2.getCodeList().get(0);
        CodeType m22 = m2.getCodeList().get(1);
        CodeType m221 = m22.getCodeList().get(0);
        CodeType m222 = m22.getCodeList().get(1);
        CodeType m23 = m2.getCodeList().get(2);
        //
        assertFalse(m2.getIsDiscrete());
        assertTrue(m21.getIsDiscrete());
        assertFalse(m22.getIsDiscrete());
        assertTrue(m221.getIsDiscrete());
        assertTrue(m222.getIsDiscrete());
        assertTrue(m23.getIsDiscrete());
        // (to check that we have empty lists and not null in "discrete" code lists)
        assertFalse(m2.getCodeList().isEmpty());
        assertTrue(m21.getCodeList().isEmpty());
        assertFalse(m22.getCodeList().isEmpty());
    }

}
