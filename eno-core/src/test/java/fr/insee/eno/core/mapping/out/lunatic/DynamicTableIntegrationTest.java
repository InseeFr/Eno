package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DynamicTableIntegrationTest {

    @Test
    void ddiToLunatic_dynamicTable() throws DDIParsingException {
        // Given
        String rosterResponseName1 = "TABLEAUBASIQUE1";
        String rosterResponseName2 = "TABLEAUBASIQUE2";

        // When
        Questionnaire lunaticQuestionnaire = DDIToLunatic.transform(
                this.getClass().getClassLoader().getResourceAsStream(
                        "functional/ddi/dynamic-table/ddi-dynamic-table.xml"),
                EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC));

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
            Optional<IVariableType> rosterVariable = lunaticQuestionnaire.getVariables().stream()
                    .filter(variable -> rosterResponseName.equals(variable.getName())).findAny();
            assertTrue(rosterVariable.isPresent());
            assertInstanceOf(VariableTypeArray.class, rosterVariable.get());
        });

        // roster component should have 2 controls
        // dropdown cell should have 1 control
        // table cell should have 1 control
    }

}
