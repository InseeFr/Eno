package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.eno.core.processing.in.steps.ddi.DDIDeserializeSuggesterConfiguration;
import fr.insee.eno.core.processing.in.steps.ddi.DDIInsertCodeLists;
import fr.insee.eno.core.processing.in.steps.ddi.DDIInsertResponseInTableCells;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.lunatic.model.flat.BodyCell;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TableQuestionTest {

    private TableQuestion enoTableQuestion;
    private Table lunaticTable;

    @BeforeEach
    void tableQuestionObjects() {
        enoTableQuestion = new TableQuestion();
        lunaticTable = new Table();
    }

    @Test
    void lunaticComponentType() {
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoTableQuestion, lunaticTable);
        //
        assertEquals(ComponentTypeEnum.TABLE, lunaticTable.getComponentType());
    }

    @Test
    void tableWithSuggester_integrationTest() throws DDIParsingException {
        //
        DDIInstanceDocument ddiInstanceDocument = DDIDeserializer.deserialize(
                this.getClass().getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-suggester.xml"));
        //
        DDIMapper ddiMapper = new DDIMapper();
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        ddiMapper.mapDDI(ddiInstanceDocument, enoQuestionnaire);
        new DDIInsertResponseInTableCells().apply(enoQuestionnaire);
        new DDIDeserializeSuggesterConfiguration().apply(enoQuestionnaire);
        new DDIInsertCodeLists().apply(enoQuestionnaire);
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        new LunaticTableProcessing(enoQuestionnaire).apply(lunaticQuestionnaire);

        //
        Map<String, Table> tableComponents = new HashMap<>();
        lunaticQuestionnaire.getComponents().stream()
                .filter(Table.class::isInstance)
                .map(Table.class::cast)
                .forEach(table -> {
                    assertEquals(ComponentTypeEnum.TABLE, table.getComponentType());
                    tableComponents.put(table.getId(), table);
                });
        //
        assertEquals(2, tableComponents.size());
        //
        Table table1 = tableComponents.get("lrueer0m");
        BodyCell suggesterCell1 = table1.getBodyLines().getFirst().getBodyCells().get(1);
        assertEquals(ComponentTypeEnum.SUGGESTER, suggesterCell1.getComponentType());
        assertEquals("L_PAYS-1-2-0", suggesterCell1.getStoreName());
        assertEquals("PAYS11", suggesterCell1.getResponse().getName());
        //
        Table table2 = tableComponents.get("lruefmjh");
        BodyCell suggesterCell21 = table2.getBodyLines().getFirst().getBodyCells().get(1);
        assertEquals(ComponentTypeEnum.SUGGESTER, suggesterCell21.getComponentType());
        assertEquals("L_PCS_HOMMES-1-5-0", suggesterCell21.getStoreName());
        assertEquals("PCS11", suggesterCell21.getResponse().getName());
        BodyCell suggesterCell22 = table2.getBodyLines().getFirst().getBodyCells().get(2);
        assertEquals(ComponentTypeEnum.SUGGESTER, suggesterCell22.getComponentType());
        assertEquals("L_PCS_FEMMES-1-5-0", suggesterCell22.getStoreName());
        assertEquals("PCS12", suggesterCell22.getResponse().getName());
    }

}
