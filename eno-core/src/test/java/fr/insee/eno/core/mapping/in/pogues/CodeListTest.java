package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.pogues.model.CodeList;
import fr.insee.pogues.model.CodeLists;
import fr.insee.pogues.model.CodeType;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CodeListTest {

    @Test
    void simpleCodeList() {
        //
        Questionnaire poguesQuestionnaire = new Questionnaire();
        CodeLists codeLists = new CodeLists();
        CodeList poguesCodeList = new CodeList();
        poguesCodeList.setId("code-list-id");
        poguesCodeList.setLabel("CODE_LIST_NAME");
        CodeType code1 = new CodeType();
        code1.setLabel("\"Code 1\"");
        code1.setValue("1");
        poguesCodeList.getCode().add(code1);
        CodeType code2 = new CodeType();
        code2.setLabel("\"Code 2\"");
        code2.setValue("2");
        poguesCodeList.getCode().add(code2);
        codeLists.getCodeList().add(poguesCodeList);
        poguesQuestionnaire.setCodeLists(codeLists);
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);
        //
        assertEquals(1, enoQuestionnaire.getCodeLists().size());
        fr.insee.eno.core.model.code.CodeList enoCodeList = enoQuestionnaire.getCodeLists().getFirst();
        assertEquals("code-list-id", enoCodeList.getId());
        assertEquals("CODE_LIST_NAME", enoCodeList.getName());
        assertEquals(2, enoCodeList.size());
        assertEquals("\"Code 1\"", enoCodeList.getCodeItems().get(0).getLabel().getValue());
        assertEquals("\"Code 2\"", enoCodeList.getCodeItems().get(1).getLabel().getValue());
        assertEquals("1", enoCodeList.getCodeItems().get(0).getValue());
        assertEquals("2", enoCodeList.getCodeItems().get(1).getValue());
    }

    @Test
    void hierarchicalCodeList() {
        //
        Questionnaire poguesQuestionnaire = new Questionnaire();
        CodeLists codeLists = new CodeLists();
        CodeList poguesCodeList = new CodeList();
        CodeType code1 = new CodeType();
        code1.setValue("1");
        poguesCodeList.getCode().add(code1);
        CodeType code2 = new CodeType();
        code2.setValue("2");
        poguesCodeList.getCode().add(code2);
        CodeType code21 = new CodeType();
        code21.setValue("21");
        code21.setParent("2");
        poguesCodeList.getCode().add(code21);
        codeLists.getCodeList().add(poguesCodeList);
        poguesQuestionnaire.setCodeLists(codeLists);
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);
        //
        fr.insee.eno.core.model.code.CodeList enoCodeList = enoQuestionnaire.getCodeLists().getFirst();
        assertEquals(3, enoCodeList.size());
        assertEquals("1", enoCodeList.getCodeItems().get(0).getValue());
        assertEquals("2", enoCodeList.getCodeItems().get(1).getValue());
        assertEquals("21", enoCodeList.getCodeItems().get(2).getValue());
        assertNull(enoCodeList.getCodeItems().get(0).getParentValue());
        assertNull(enoCodeList.getCodeItems().get(1).getParentValue());
        assertEquals(enoCodeList.getCodeItems().get(1).getValue(), enoCodeList.getCodeItems().get(2).getParentValue());
    }

}
