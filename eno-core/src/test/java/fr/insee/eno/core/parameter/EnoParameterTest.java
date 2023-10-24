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

    @Test
    void parameters_HouseholdCAWILunatic_testEnoValues() {
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        //
        assertEquals(EnoParameters.QuestionNumberingMode.NONE, enoParameters.getQuestionNumberingMode());
        assertFalse(enoParameters.isArrowCharInQuestions());
        assertFalse(enoParameters.isCommentSection());
    }

    @Test
    void parameters_HouseholdCAPILunatic_enoValues() {
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAPI, Format.LUNATIC);
        //
        assertEquals(EnoParameters.QuestionNumberingMode.NONE, enoParameters.getQuestionNumberingMode());
        assertFalse(enoParameters.isArrowCharInQuestions());
    }

    @Test
    void parameters_HouseholdCATILunatic_enoValues() {
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAPI, Format.LUNATIC);
        //
        assertEquals(EnoParameters.QuestionNumberingMode.NONE, enoParameters.getQuestionNumberingMode());
        assertFalse(enoParameters.isArrowCharInQuestions());
    }

    @Test
    void parameters_BusinessCAPILunatic_lunaticPagination() {
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.BUSINESS, EnoParameters.ModeParameter.CAPI, Format.LUNATIC);
        //
        LunaticParameters lunaticParameters = enoParameters.getLunaticParameters();
        assertEquals(EnoParameters.LunaticPaginationMode.SEQUENCE, lunaticParameters.getLunaticPaginationMode());
    }

}
