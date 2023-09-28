package fr.insee.eno.core.parameter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnoParameterTest {

    @Test
    void lunaticParameters_DefaultCAWILunatic_testLunaticValues() {
        //
        EnoParameters parameters = EnoParameters.of(
                EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        //
        assertTrue(parameters.isControls());
        assertTrue(parameters.isToolTip());
        assertFalse(parameters.isFilterDescription());
        assertTrue(parameters.isFilterResult());
        assertFalse(parameters.isMissingVariables());
        assertEquals(EnoParameters.LunaticPaginationMode.QUESTION, parameters.getLunaticPaginationMode());
    }

    @Test
    void lunaticParameters_DefaultPAPILunatic_shouldThrowException() {
        assertThrows(Exception.class, () ->
                EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.PAPI, Format.LUNATIC));
    }

}
