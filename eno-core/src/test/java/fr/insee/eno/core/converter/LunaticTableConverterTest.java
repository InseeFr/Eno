package fr.insee.eno.core.converter;

import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.label.Label;
import fr.insee.lunatic.model.flat.BodyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LunaticTableConverterTest {

    private CodeList codeList;

    private static CodeItem createCode(String label) {
        CodeItem codeItem = new CodeItem();
        codeItem.setLabel(new Label());
        codeItem.getLabel().setValue(label);
        return codeItem;
    }

    @BeforeEach
    public void createCodeList() {
        codeList = new CodeList();
        CodeItem c1 = createCode("1");
        CodeItem c2 = createCode("2");
        CodeItem c3 = createCode("3");
        codeList.getCodeItems().add(c1);
        codeList.getCodeItems().add(c2);
        codeList.getCodeItems().add(c3);
        CodeItem c21 = createCode("21");
        CodeItem c22 = createCode("22");
        CodeItem c23 = createCode("23");
        c2.getCodeItems().add(c21);
        c2.getCodeItems().add(c22);
        c2.getCodeItems().add(c23);
        CodeItem c221 = createCode("221");
        CodeItem c222 = createCode("222");
        CodeItem c223 = createCode("223");
        c22.getCodeItems().add(c221);
        c22.getCodeItems().add(c222);
        c22.getCodeItems().add(c223);
        CodeItem c231 = createCode("231");
        CodeItem c232 = createCode("232");
        c23.getCodeItems().add(c231);
        c23.getCodeItems().add(c232);
        CodeItem c2231 = createCode("2231");
        CodeItem c2232 = createCode("2232");
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
