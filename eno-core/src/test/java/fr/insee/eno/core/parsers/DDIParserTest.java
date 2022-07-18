package fr.insee.eno.core.parsers;

import datacollection33.*;
import datacollection33.impl.LiteralTextTypeImpl;
import datacollection33.impl.TextTypeImpl;
import fr.insee.eno.core.HelloTest;
import instance33.DDIInstanceDocument;
import instance33.DDIInstanceType;
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
        List<ControlConstructType> foo = ddiInstanceDocument.getDDIInstance().getResourcePackageArray(0).getControlConstructSchemeArray(0).getControlConstructList()
                .stream()
                .filter(controlConstructType -> controlConstructType instanceof SequenceType)
                .toList();
        assertNotNull(foo);

        //
        List<QuestionItemType> questionItemList = ddiInstanceDocument.getDDIInstance().getResourcePackageArray(0).getQuestionSchemeArray(0).getQuestionItemList();
        List<QuestionGridType> questionGridList = ddiInstanceDocument.getDDIInstance().getResourcePackageArray(0).getQuestionSchemeArray(0).getQuestionGridList();
        questionItemList.get(0).getOutParameterArray(0).getParameterNameArray(0).getStringArray(0).getStringValue();

        //
        String firstInstructionLabel = ((TextTypeImpl) ((LiteralTextType) ddiInstanceDocument.getDDIInstance()
                .getResourcePackageArray(0)
                .getInterviewerInstructionSchemeArray(0).getInstructionArray(0)
                .getInstructionTextArray(0).getTextContentArray(0))
                .getText()).getStringValue();
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

}
