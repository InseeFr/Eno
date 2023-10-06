package fr.insee.eno.core.parameter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnoParameterTest {

    @Test
    void lunaticParameters_DefaultCAWILunatic_testLunaticValues() {
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        //
        LunaticParameters lunaticParameters = enoParameters.getLunaticParameters();
        assertTrue(lunaticParameters.isControls());
        assertTrue(lunaticParameters.isToolTip());
        assertFalse(lunaticParameters.isFilterDescription());
        assertTrue(lunaticParameters.isFilterResult());
        assertFalse(lunaticParameters.isMissingVariables());
        assertEquals(EnoParameters.LunaticPaginationMode.QUESTION, lunaticParameters.getLunaticPaginationMode());
    }

    @Test
    void lunaticParameters_DefaultPAPILunatic_shouldThrowException() {
        assertThrows(Exception.class, () ->
                EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.PAPI, Format.LUNATIC));
    }

}
