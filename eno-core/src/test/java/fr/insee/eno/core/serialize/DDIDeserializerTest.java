package fr.insee.eno.core.serialize;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import instance33.DDIInstanceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DDIDeserializerTest {

    @Test
    void deserialize_simpleDDI() throws DDIParsingException {
        //
        DDIInstanceType ddiInstance = DDIDeserializer.deserialize(
                        this.getClass().getClassLoader().getResource("integration/ddi/ddi-simple.xml"))
                .getDDIInstance();
        //
        assertNotNull(ddiInstance);
        assertFalse(ddiInstance.getResourcePackageList().isEmpty());
        assertNotNull(ddiInstance.getResourcePackageArray(0));
    }

}
