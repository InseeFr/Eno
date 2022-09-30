package fr.insee.eno.core.mappers;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class MapperTest {

    @Test
    void isSimpleType_nominal() {
        assertTrue((Mapper.isSimpleType(String.class)));
        assertTrue((Mapper.isSimpleType(int.class)));
        assertTrue((Mapper.isSimpleType(Integer.class)));
        assertTrue((Mapper.isSimpleType(Double.class)));
        assertTrue((Mapper.isSimpleType(double.class)));
        assertTrue((Mapper.isSimpleType(BigInteger.class)));
        assertTrue((Mapper.isSimpleType(boolean.class)));

        assertFalse(Mapper.isSimpleType(Mapper.class));
    }

    @Test
    void isSimpleType_null(){
        assertFalse(Mapper.isSimpleType(null));
    }


}