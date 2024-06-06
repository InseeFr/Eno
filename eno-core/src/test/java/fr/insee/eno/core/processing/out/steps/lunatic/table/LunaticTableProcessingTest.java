package fr.insee.eno.core.processing.out.steps.lunatic.table;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class LunaticTableProcessingTest {

    @Test
    void tablesWithNonCollectedCells() throws DDIParsingException {
        // Given
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-no-data-cell.xml"),
                EnoParameters.of(EnoParameters.Context.BUSINESS, EnoParameters.ModeParameter.CAWI, Format.LUNATIC));
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        new LunaticMapper().mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);

        // When
        new LunaticTableProcessing(enoQuestionnaire).apply(lunaticQuestionnaire);

        // Then
        Map<String, ComponentType> components = new HashMap<>();
        lunaticQuestionnaire.getComponents().forEach(component -> components.put(component.getId(), component));

        //
        Table table = assertInstanceOf(Table.class, components.get("lx1jpb68"));
        assertEquals(2, table.getBodyLines().size());
        table.getBodyLines().forEach(bodyLine ->
                assertEquals(4, bodyLine.getBodyCells().size()));
        // Non-collected cells
        BodyCell cell12 = table.getBodyLines().get(1).getBodyCells().get(2);
        BodyCell cell03 = table.getBodyLines().get(0).getBodyCells().get(3);
        assertEquals(ComponentTypeEnum.TEXT, cell12.getComponentType());
        assertEquals(ComponentTypeEnum.TEXT, cell03.getComponentType());
        assertEquals("\"20\"", cell12.getLabel().getValue());
        assertEquals("\"Fixed value for A: \" || Q1", cell03.getLabel().getValue().stripTrailing());
        assertEquals(LabelTypeEnum.VTL_MD, cell12.getLabel().getType());
        assertEquals(LabelTypeEnum.VTL_MD, cell03.getLabel().getType());

        //
        RosterForLoop fixedSizeRoster = assertInstanceOf(RosterForLoop.class, components.get("lwys4n6q"));
        assertEquals(4, fixedSizeRoster.getComponents().size());
        // Non collected columns
        assertEquals(ComponentTypeEnum.TEXT, fixedSizeRoster.getComponents().get(2).getComponentType());
        assertEquals(ComponentTypeEnum.TEXT, fixedSizeRoster.getComponents().get(3).getComponentType());
        assertEquals("\"Foo\"", fixedSizeRoster.getComponents().get(2).getLabel().getValue());
        assertEquals("Q1", fixedSizeRoster.getComponents().get(3).getLabel().getValue().stripTrailing());
        assertEquals(LabelTypeEnum.VTL_MD, fixedSizeRoster.getComponents().get(2).getLabel().getType());
        assertEquals(LabelTypeEnum.VTL_MD, fixedSizeRoster.getComponents().get(3).getLabel().getType());

        //
        RosterForLoop dynamicRoster = assertInstanceOf(RosterForLoop.class, components.get("lx1jrc4l"));
        assertEquals(4, dynamicRoster.getComponents().size());
        // Non collected columns
        assertEquals(ComponentTypeEnum.TEXT, dynamicRoster.getComponents().get(2).getComponentType());
        assertEquals(ComponentTypeEnum.TEXT, dynamicRoster.getComponents().get(3).getComponentType());
        assertEquals("\"Bar\"", dynamicRoster.getComponents().get(2).getLabel().getValue());
        assertEquals("Q1", dynamicRoster.getComponents().get(3).getLabel().getValue().stripTrailing());
        assertEquals(LabelTypeEnum.VTL_MD, dynamicRoster.getComponents().get(2).getLabel().getType());
        assertEquals(LabelTypeEnum.VTL_MD, dynamicRoster.getComponents().get(3).getLabel().getType());
    }

}
