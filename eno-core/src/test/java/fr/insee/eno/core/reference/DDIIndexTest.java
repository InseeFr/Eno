package fr.insee.eno.core.reference;

import datacollection33.ControlConstructSchemeType;
import datacollection33.InstructionType;
import datacollection33.QuestionItemType;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.parsers.DDIParser;
import instance33.DDIInstanceDocument;
import instance33.DDIInstanceType;
import logicalproduct33.VariableGroupType;
import logicalproduct33.VariableType;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DDIIndexTest {

    @Test
    void indexDDITest() throws IOException, DDIParsingException {
        //
        DDIInstanceDocument ddiInstanceDocument = DDIParser.parse(
                DDIIndexTest.class.getClassLoader().getResource("in/ddi/l10xmg2l.xml"));
        //
        DDIIndex ddiIndex = new DDIIndex();
        ddiIndex.indexDDI(ddiInstanceDocument);

        // DDI instance
        assertTrue(ddiIndex.containsId("INSEE-l10xmg2l"));
        assertTrue(DDIInstanceType.class.isAssignableFrom(ddiIndex.get("INSEE-l10xmg2l").getClass()));
        assertEquals("INSEE-l10xmg2l",
                ((DDIInstanceType) ddiIndex.get("INSEE-l10xmg2l")).getIDArray(0).getStringValue());
        // Instruction
        assertTrue(ddiIndex.containsId("jfazqgv2"));
        assertEquals("instruction",
                ((InstructionType) ddiIndex.get("jfazqgv2")).getInstructionNameArray(0).getStringArray(0).getStringValue());
        // Variable
        assertTrue(ddiIndex.containsId("kzwoti00"));
        assertEquals("COCHECASE",
                ((VariableType) ddiIndex.get("kzwoti00")).getVariableNameArray(0).getStringArray(0).getStringValue());
        // Group
        assertTrue(ddiIndex.containsId("INSEE-Instrument-l10xmg2l-vg"));
        assertEquals("DOCSIMPLE",
                ((VariableGroupType) ddiIndex.get("INSEE-Instrument-l10xmg2l-vg")).getVariableGroupNameArray(0).getStringArray(0).getStringValue());
        // SingleResponseQuestion item
        assertTrue(ddiIndex.containsId("jfazk91m"));
        assertEquals("COCHECASE",
                ((QuestionItemType) ddiIndex.get("jfazk91m")).getQuestionItemNameArray(0).getStringArray(0).getStringValue());


        //
        ControlConstructSchemeType foo = (ControlConstructSchemeType) ddiIndex.get("ControlConstructScheme-l10xmg2l");
        assertNotNull(foo);

        //
        Object codeList = ddiIndex.get("jfjevykh");
        assertNotNull(codeList);
        //
        Object code = ddiIndex.get("CA-jfjevykh-1");
        assertNotNull(code);

        //
        Object instruction = ddiIndex.get("kk47fewm");
        assertNotNull(instruction);

        //
        Object ifThenElse = ddiIndex.get("l3laoj9p");
        assertNotNull(ifThenElse);
    }

    @Test
    void indexSandboxDDI() throws IOException, DDIParsingException {
        //
        DDIInstanceDocument ddiInstanceDocument = DDIParser.parse(
                DDIIndexTest.class.getClassLoader().getResource("in/ddi/sandbox.xml"));
        //
        DDIIndex ddiIndex = new DDIIndex();
        ddiIndex.indexDDI(ddiInstanceDocument);

        //
        Object processingInstruction = ddiIndex.get("l6f4bgf0-GI");
        assertNotNull(processingInstruction);
    }
}
