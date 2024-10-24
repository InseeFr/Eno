package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticReverseConsistencyControlLabel;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/** Class to test the mapping of row-level control in a dynamic table. */
class ControlRowTest {

    private Questionnaire lunaticQuestionnaire;

    @BeforeEach
    void mapDDIToLunatic() throws DDIParsingException {
        //
        lunaticQuestionnaire = new Questionnaire();
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-controls-line.xml"),
                EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));

        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
    }

    @Test
    void mappingFromDDITest() {
        // This questionnaire has a single sequence with a dynamic table question
        RosterForLoop rosterForLoop = lunaticQuestionnaire.getComponents().stream()
                .filter(RosterForLoop.class::isInstance).map(RosterForLoop.class::cast).findAny().orElse(null);
        assertNotNull(rosterForLoop);
        //
        assertEquals(2, rosterForLoop.getControls().size());
        // This question has one control of type "row"
        List<ControlType> rowControls = rosterForLoop.getControls().stream()
                .filter(controlType -> ControlContextType.ROW.equals(controlType.getType()))
                .toList();
        assertEquals(1, rowControls.size());
        //
        ControlType rowControl = rowControls.getFirst();
        assertEquals(ControlCriticalityEnum.WARN, rowControl.getCriticality());
        assertEquals("cast(DYNAMIC_TABLE1, integer) + cast(DYNAMIC_TABLE2, integer) > 100",
                rowControl.getControl().getValue());
        assertEquals(LabelTypeEnum.VTL, rowControl.getControl().getType());
        assertEquals("\"Sum of percentages cannot be > 100%.\"",
                rowControl.getErrorMessage().getValue());
        assertEquals(LabelTypeEnum.VTL_MD, rowControl.getErrorMessage().getType());
    }

    @Test
    void reverseControlExpressions() {
        //
        new LunaticReverseConsistencyControlLabel().apply(lunaticQuestionnaire);

        //
        RosterForLoop rosterForLoop = lunaticQuestionnaire.getComponents().stream()
                .filter(RosterForLoop.class::isInstance).map(RosterForLoop.class::cast).findAny().orElse(null);
        assertNotNull(rosterForLoop);
        //
        List<ControlType> rowControls = rosterForLoop.getControls().stream()
                .filter(controlType -> ControlContextType.ROW.equals(controlType.getType()))
                .toList();
        assertEquals(1, rowControls.size());
        //
        ControlType rowControl = rowControls.getFirst();
        assertEquals("not(cast(DYNAMIC_TABLE1, integer) + cast(DYNAMIC_TABLE2, integer) > 100)",
                rowControl.getControl().getValue());
    }

}
