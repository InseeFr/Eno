package fr.insee.eno.core.converter;

import fr.insee.eno.core.model.CodeList;
import fr.insee.lunatic.model.flat.BodyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LunaticTableConverterTest {

    private CodeList codeList;

    private static CodeList.CodeItem createCode(String label) {
        CodeList.CodeItem codeItem = new CodeList.CodeItem();
        codeItem.setLabel(label);
        return codeItem;
    }

    @BeforeEach
    public void createCodeList() {
        codeList = new CodeList();
        CodeList.CodeItem c1 = createCode("1");
        CodeList.CodeItem c2 = createCode("2");
        CodeList.CodeItem c3 = createCode("3");
        codeList.getCodeItems().add(c1);
        codeList.getCodeItems().add(c2);
        codeList.getCodeItems().add(c3);
        CodeList.CodeItem c21 = createCode("21");
        CodeList.CodeItem c22 = createCode("22");
        CodeList.CodeItem c23 = createCode("23");
        c2.getCodeItems().add(c21);
        c2.getCodeItems().add(c22);
        c2.getCodeItems().add(c23);
        CodeList.CodeItem c221 = createCode("221");
        CodeList.CodeItem c222 = createCode("222");
        CodeList.CodeItem c223 = createCode("223");
        c22.getCodeItems().add(c221);
        c22.getCodeItems().add(c222);
        c22.getCodeItems().add(c223);
        CodeList.CodeItem c231 = createCode("231");
        CodeList.CodeItem c232 = createCode("232");
        c23.getCodeItems().add(c231);
        c23.getCodeItems().add(c232);
        CodeList.CodeItem c2231 = createCode("2231");
        CodeList.CodeItem c2232 = createCode("2232");
        c223.getCodeItems().add(c2231);
        c223.getCodeItems().add(c2232);
    }

    @Test
    public void testComputeVerticalSizes() {
        //
        codeList.computeSizes();
        //
        assertEquals(1, codeList.getCodeItems().get(0).getVSize());
        assertEquals(7, codeList.getCodeItems().get(1).getVSize());
        assertEquals(1, codeList.getCodeItems().get(2).getVSize());
        assertEquals(1, codeList.getCodeItems().get(1).getCodeItems().get(0).getVSize());
        assertEquals(4, codeList.getCodeItems().get(1).getCodeItems().get(1).getVSize());
        assertEquals(2, codeList.getCodeItems().get(1).getCodeItems().get(2).getVSize());
        //...
        assertEquals(4, codeList.getCodeItems().get(0).getHSize());
        assertEquals(1, codeList.getCodeItems().get(1).getHSize());
        assertEquals(4, codeList.getCodeItems().get(2).getHSize());
        assertEquals(3, codeList.getCodeItems().get(1).getCodeItems().get(0).getHSize());
        assertEquals(1, codeList.getCodeItems().get(1).getCodeItems().get(1).getHSize());
        assertEquals(2, codeList.getCodeItems().get(1).getCodeItems().get(1).getCodeItems().get(0).getHSize());
        assertEquals(1, codeList.getCodeItems().get(1).getCodeItems().get(1).getCodeItems()
                .get(2).getCodeItems().get(0).getHSize());
        //...
    }

    @Test
    public void testFlattenFunction() {
        //
        List<BodyType> res = LunaticTableConverter.flattenCodeList(codeList);
        //
        assertEquals(1, res.get(0).getBodyLine().size());
        assertEquals("1", res.get(0).getBodyLine().get(0).getLabel().getValue());
        assertEquals(2, res.get(1).getBodyLine().size());
        assertEquals("2", res.get(1).getBodyLine().get(0).getLabel().getValue());
        assertEquals("21", res.get(1).getBodyLine().get(1).getLabel().getValue());
        assertEquals(2, res.get(2).getBodyLine().size());
        assertEquals("22", res.get(2).getBodyLine().get(0).getLabel().getValue());
        assertEquals("221", res.get(2).getBodyLine().get(1).getLabel().getValue());
        assertEquals(1, res.get(3).getBodyLine().size());
        assertEquals("222", res.get(3).getBodyLine().get(0).getLabel().getValue());
        assertEquals(2, res.get(4).getBodyLine().size());
        assertEquals("223", res.get(4).getBodyLine().get(0).getLabel().getValue());
        assertEquals("2231", res.get(4).getBodyLine().get(1).getLabel().getValue());
        //...
        assertEquals(9, res.size());
    }
}
