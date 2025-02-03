package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.processing.in.steps.ddi.DDIDeserializeSuggesterConfiguration;
import fr.insee.eno.core.processing.in.steps.ddi.DDIInsertCodeLists;
import fr.insee.eno.core.processing.in.steps.ddi.DDIInsertResponseInTableCells;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.lunatic.model.flat.BodyCell;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.RosterForLoop;
import fr.insee.lunatic.model.flat.variable.CollectedVariableType;
import fr.insee.lunatic.model.flat.variable.CollectedVariableValues;
import fr.insee.lunatic.model.flat.variable.VariableType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DynamicTableIntegrationTest {

    @Test
    void ddiToLunatic_dynamicTable() throws DDIParsingException {
        // Given
        String rosterResponseName1 = "TABLEAUBASIQUE1";
        String rosterResponseName2 = "TABLEAUBASIQUE2";

        // When
        Questionnaire lunaticQuestionnaire = DDIToLunatic.fromInputStream(
                this.getClass().getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-dynamic-table-2.xml"))
                .transform(EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC));

        // Then
        // There is one roster for loop component
        List<RosterForLoop> rosterForLoopList = lunaticQuestionnaire.getComponents().stream()
                .filter(RosterForLoop.class::isInstance)
                .map(RosterForLoop.class::cast)
                .toList();
        assertEquals(1, rosterForLoopList.size());
        // Roster component has two "column" components
        RosterForLoop rosterForLoop = rosterForLoopList.getFirst();
        assertEquals(2, rosterForLoop.getComponents().size());
        assertEquals(ComponentTypeEnum.DROPDOWN, rosterForLoop.getComponents().get(0).getComponentType());
        assertEquals(ComponentTypeEnum.RADIO, rosterForLoop.getComponents().get(1).getComponentType());
        // First column component
        BodyCell dropdownCell = rosterForLoop.getComponents().get(0);
        assertEquals(rosterResponseName1, dropdownCell.getResponse().getName());
        assertEquals(5, dropdownCell.getOptions().size());
        // Second column component
        BodyCell radioCell = rosterForLoop.getComponents().get(1);
        assertEquals(rosterResponseName2, radioCell.getResponse().getName());
        assertEquals(2, radioCell.getOptions().size());

        // Two entries in header
        assertEquals(2, rosterForLoop.getHeader().size());

        // Roster variables are present and are array variables
        List.of(rosterResponseName1, rosterResponseName2).forEach(rosterResponseName -> {
            Optional<VariableType> searched = lunaticQuestionnaire.getVariables().stream()
                    .filter(variable -> rosterResponseName.equals(variable.getName())).findAny();
            assertTrue(searched.isPresent());
            CollectedVariableType rosterVariable = assertInstanceOf(CollectedVariableType.class, searched.get());
            assertInstanceOf(CollectedVariableValues.class, rosterVariable.getValues());
        });

        // roster component should have 2 controls
        // dropdown cell should have 1 control
        // table cell should have 1 control
    }

    @Test
    void dynamicTableWithSuggester() throws DDIParsingException {
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
        Map<String, RosterForLoop> rosterComponents = new HashMap<>();
        lunaticQuestionnaire.getComponents().stream()
                .filter(RosterForLoop.class::isInstance)
                .map(RosterForLoop.class::cast)
                .forEach(rosterForLoop -> {
                    assertEquals(ComponentTypeEnum.ROSTER_FOR_LOOP, rosterForLoop.getComponentType());
                    rosterComponents.put(rosterForLoop.getId(), rosterForLoop);
                });
        //
        assertEquals(2, rosterComponents.size());
        //
        RosterForLoop roster1 = rosterComponents.get("lruekois");
        BodyCell suggesterCell1 = roster1.getComponents().getFirst();
        assertEquals(ComponentTypeEnum.SUGGESTER, suggesterCell1.getComponentType());
        assertEquals("L_NATIONALITE-1-2-0", suggesterCell1.getStoreName());
        assertEquals("NATIONALIT1", suggesterCell1.getResponse().getName());
        //
        RosterForLoop roster2 = rosterComponents.get("lruen7yc");
        BodyCell suggesterCell21 = roster2.getComponents().get(0);
        BodyCell suggesterCell22 = roster2.getComponents().get(1);
        assertEquals(ComponentTypeEnum.SUGGESTER, suggesterCell21.getComponentType());
        assertEquals("L_PCS_HOMMES-1-5-0", suggesterCell21.getStoreName());
        assertEquals("PCSDYNAMIQ1", suggesterCell21.getResponse().getName());
        assertEquals(ComponentTypeEnum.SUGGESTER, suggesterCell22.getComponentType());
        assertEquals("L_PCS_FEMMES-1-5-0", suggesterCell22.getStoreName());
        assertEquals("PCSDYNAMIQ2", suggesterCell22.getResponse().getName());
    }

}
