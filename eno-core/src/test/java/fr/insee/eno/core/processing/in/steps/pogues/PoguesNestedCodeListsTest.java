package fr.insee.eno.core.processing.in.steps.pogues;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PoguesNestedCodeListsTest {

    @Test
    void unitTest() {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        CodeList codeList = new CodeList();
        codeList.getCodeItems().add(createCodeItem("1", null));
        codeList.getCodeItems().add(createCodeItem("2", "1"));
        codeList.getCodeItems().add(createCodeItem("3", "1"));
        codeList.getCodeItems().add(createCodeItem("4", "2"));
        codeList.getCodeItems().add(createCodeItem("5", "4"));
        codeList.getCodeItems().add(createCodeItem("6", null));
        enoQuestionnaire.getCodeLists().add(codeList);

        //
        new PoguesNestedCodeLists().apply(enoQuestionnaire);

        //
        CodeItem code1 = codeList.getCodeItems().getFirst();
        assertEquals("1", code1.getValue());
        assertEquals(2, code1.getCodeItems().size());

        CodeItem code2 = code1.getCodeItems().getFirst();
        assertEquals("2", code2.getValue());
        assertEquals(1, code2.getCodeItems().size());

        CodeItem code3 = code1.getCodeItems().get(1);
        assertEquals("3", code3.getValue());
        assertTrue(code3.getCodeItems().isEmpty());

        CodeItem code4 = code2.getCodeItems().getFirst();
        assertEquals("4", code4.getValue());
        assertEquals(1, code4.getCodeItems().size());

        CodeItem code5 = code4.getCodeItems().getFirst();
        assertEquals("5", code5.getValue());
        assertTrue(code5.getCodeItems().isEmpty());

        CodeItem code6 = codeList.getCodeItems().get(1);
        assertEquals("6", code6.getValue());
        assertTrue(code6.getCodeItems().isEmpty());
    }

    /** Utility method used to test this class' logic, such a constructor is not useful in main code. */
    private static CodeItem createCodeItem(String value, String parentValue) {
        CodeItem codeItem = new CodeItem();
        codeItem.setValue(value);
        codeItem.setParentValue(parentValue);
        return codeItem;
    }

    @Test
    void integrationTest() throws PoguesDeserializationException {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new PoguesMapper().mapPoguesQuestionnaire(
                PoguesDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/pogues/pogues-mcq.json")),
                enoQuestionnaire);

        //
        new PoguesNestedCodeLists().apply(enoQuestionnaire);

        //
        assertEquals(3, enoQuestionnaire.getCodeLists().size());

        assertEquals(4, enoQuestionnaire.getCodeLists().get(0).size());

        CodeList nestedCodeList = enoQuestionnaire.getCodeLists().get(1);
        CodeItem code1 = nestedCodeList.getCodeItems().get(0);
        CodeItem code2 = nestedCodeList.getCodeItems().get(1);
        CodeItem code3 = nestedCodeList.getCodeItems().get(2);
        CodeItem code21 = code2.getCodeItems().get(0);
        CodeItem code22 = code2.getCodeItems().get(1);
        CodeItem code31 = code3.getCodeItems().get(0);
        CodeItem code32 = code3.getCodeItems().get(1);

        assertEquals("c1", code1.getValue());
        assertEquals("c2", code2.getValue());
        assertEquals("c21", code21.getValue());
        assertEquals("c22", code22.getValue());
        assertEquals("c3", code3.getValue());
        assertEquals("c31", code31.getValue());
        assertEquals("c32", code32.getValue());

        assertTrue(code1.getCodeItems().isEmpty());
        assertEquals(2, code2.getCodeItems().size());
        assertTrue(code21.getCodeItems().isEmpty());
        assertTrue(code22.getCodeItems().isEmpty());
        assertEquals(2, code3.getCodeItems().size());
        assertEquals(3, code31.getCodeItems().size());
        code31.getCodeItems().forEach(codeItem -> assertTrue(codeItem.getCodeItems().isEmpty()));
        assertTrue(code32.getCodeItems().isEmpty());

        assertEquals(5, enoQuestionnaire.getCodeLists().get(2).size());
    }
}
