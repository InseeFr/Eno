package fr.insee.eno.core.parsers;

import datacollection33.*;
import datacollection33.impl.LiteralTextTypeImpl;
import datacollection33.impl.TextTypeImpl;
import fr.insee.eno.core.HelloTest;
import group33.ResourcePackageType;
import instance33.DDIInstanceDocument;
import instance33.DDIInstanceType;
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
    public void parserDDITest() throws IOException {
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
    public void parserDDIWithFilter() throws IOException {
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
    public void parserDDIWithMcq() throws IOException {
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
    public void parseSandboxDDI() throws IOException {
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

}
