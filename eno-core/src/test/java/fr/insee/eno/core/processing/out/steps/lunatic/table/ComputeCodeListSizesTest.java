package fr.insee.eno.core.processing.out.steps.lunatic.table;

import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.label.Label;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComputeCodeListSizesTest {

    private static CodeItem createCode(String label) {
        CodeItem codeItem = new CodeItem();
        codeItem.setLabel(new Label());
        codeItem.getLabel().setValue(label);
        return codeItem;
    }

    public static CodeList createComplexNestedCodeList() {
        CodeList codeList = new CodeList();
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
        return codeList;
    }

    @Test
    void testSizesComputationLogic() {
        // Given
        CodeList codeList = createComplexNestedCodeList();
        // When
        ComputeCodeListSizes.of(codeList);
        // Then
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

}
