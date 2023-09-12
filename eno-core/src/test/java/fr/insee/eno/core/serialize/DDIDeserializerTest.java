package fr.insee.eno.core.serialize;

import datacollection33.ControlConstructType;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import group33.ResourcePackageType;
import instance33.DDIInstanceType;
import logicalproduct33.CodeListType;
import logicalproduct33.CodeType;
import logicalproduct33.VariableType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DDIDeserializerTest {

    @Test
    void parserDDIWithFilter() throws DDIParsingException {
        //
        DDIInstanceType ddiInstance = DDIDeserializer.deserialize(
                        this.getClass().getClassLoader().getResource("integration/ddi/ddi-filters.xml"))
                .getDDIInstance();
        //
        assertNotNull(ddiInstance);
        //
        List<ControlConstructType> controlConstructList = ddiInstance.getResourcePackageArray(0)
                .getControlConstructSchemeArray(0).getControlConstructList();
        assertNotNull(controlConstructList);
    }

    @Test
    void parseSandboxDDI() throws DDIParsingException {
        //
        DDIInstanceType ddiInstance = DDIDeserializer.deserialize(
                        this.getClass().getClassLoader().getResource("end-to-end/ddi/ddi-l8x6fhtd.xml"))
                .getDDIInstance();
        //
        ResourcePackageType resourcePackage = ddiInstance.getResourcePackageArray(0);
        //
        List<VariableType> variables = resourcePackage.getVariableSchemeArray(0).getVariableList();
        assertFalse(variables.isEmpty());
    }

    @Test
    void parseDDIComplexCodeList() throws DDIParsingException {
        //
        DDIInstanceType ddiInstance = DDIDeserializer.deserialize(
                        this.getClass().getClassLoader().getResource("integration/ddi/ddi-nested-code-lists.xml"))
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
