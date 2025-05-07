package fr.insee.eno.core.parameter;

import fr.insee.eno.core.exceptions.business.EnoParametersException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnoParameterTest {

    @Test
    void parseParameters_invalidProperty_shouldThrowException() {
        //
        InputStream inputStream = new ByteArrayInputStream("{\"foo\": \"bar\"}".getBytes());
        //
        assertThrows(EnoParametersException.class, () ->
                EnoParameters.parse(inputStream));
    }

    @Test
    void parseParameters_malformedJson_shouldThrowException() {
        //
        InputStream inputStream = new ByteArrayInputStream("{".getBytes());
        //
        assertThrows(EnoParametersException.class, () ->
                EnoParameters.parse(inputStream));
    }

    @Test
    void lunaticParameters_DefaultCAWILunatic_testLunaticValues() {
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        //
        LunaticParameters lunaticParameters = enoParameters.getLunaticParameters();
        assertTrue(lunaticParameters.isControls());
        assertTrue(lunaticParameters.isToolTip());
        assertTrue(lunaticParameters.isFilterDescription());
        assertTrue(lunaticParameters.isFilterResult());
        assertFalse(lunaticParameters.isMissingVariables());
        assertEquals(LunaticParameters.LunaticPaginationMode.QUESTION, lunaticParameters.getLunaticPaginationMode());
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
        assertTrue(enoParameters.isArrowCharInQuestions());
        assertTrue(enoParameters.isCommentSection());
    }

    @Test
    void parameters_HouseholdCATILunatic_enoValues() {
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAPI, Format.LUNATIC);
        //
        assertEquals(EnoParameters.QuestionNumberingMode.NONE, enoParameters.getQuestionNumberingMode());
        assertTrue(enoParameters.isArrowCharInQuestions());
        assertTrue(enoParameters.isCommentSection());
    }

    @Test
    void parameters_CAPILunatic_lunaticPagination() {
        List.of(EnoParameters.Context.DEFAULT, EnoParameters.Context.HOUSEHOLD, EnoParameters.Context.BUSINESS)
                .forEach(context -> {
                    //
                    EnoParameters enoParameters = EnoParameters.of(
                            context, EnoParameters.ModeParameter.CAPI, Format.LUNATIC);
                    //
                    LunaticParameters lunaticParameters = enoParameters.getLunaticParameters();
                    assertEquals(LunaticParameters.LunaticPaginationMode.QUESTION,
                            lunaticParameters.getLunaticPaginationMode());
        });
    }

}
