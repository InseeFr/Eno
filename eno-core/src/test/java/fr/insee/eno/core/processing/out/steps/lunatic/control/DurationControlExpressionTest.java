package fr.insee.eno.core.processing.out.steps.lunatic.control;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DurationControlExpressionTest {

    @Test
    void testCastExpressionYear() {
        String result = DurationControlExpression.generateYearVTLCast("DURATION_VAR");
        String expected = "cast(substr(DURATION_VAR, 2, instr(DURATION_VAR, \"Y\") - 2), integer)";
        assertEquals(expected, result);
    }

    @Test
    void testCastExpressionMonth() {
        String result = DurationControlExpression.generateMonthVTLCast("DURATION_VAR");
        String expected = "cast(substr(DURATION_VAR, instr(DURATION_VAR, \"Y\") + 1, instr(DURATION_VAR, \"M\") - instr(DURATION_VAR, \"Y\") - 1), integer)";
        assertEquals(expected, result);
    }

    @Test
    void testCastExpressionHour() {
        String result = DurationControlExpression.generateHourVTLCast("DURATION_VAR");
        String expected = "cast(substr(DURATION_VAR, 3, instr(DURATION_VAR, \"H\") - 3), integer)";
        assertEquals(expected, result);
    }

    @Test
    void testCastExpressionMinute() {
        String result = DurationControlExpression.generateMinuteVTLCast("DURATION_VAR");
        String expected = "cast(substr(DURATION_VAR, instr(DURATION_VAR, \"H\") + 1, instr(DURATION_VAR, \"M\") - instr(DURATION_VAR, \"H\") - 1), integer)";
        assertEquals(expected, result);
    }

    @Test
    void fullExpression_yearMonth() {
        //
        LunaticDurationControl.YearMonthValue minValue = new LunaticDurationControl.YearMonthValue(0, 3);
        LunaticDurationControl.YearMonthValue maxValue = new LunaticDurationControl.YearMonthValue(1, 6);
        //
        String result = DurationControlExpression.generateControlExpression("DURATION_VAR", minValue, maxValue);
        //
        String expected = """
                not(
                  not(isnull(DURATION_VAR)) and (
                    cast(substr(DURATION_VAR, 2, instr(DURATION_VAR, "Y") - 2), integer) < 0 or (
                      cast(substr(DURATION_VAR, 2, instr(DURATION_VAR, "Y") - 2), integer) = 0 and
                      cast(substr(DURATION_VAR, instr(DURATION_VAR, "Y") + 1, instr(DURATION_VAR, "M") - instr(DURATION_VAR, "Y") - 1), integer) < 3
                    ) or
                    cast(substr(DURATION_VAR, 2, instr(DURATION_VAR, "Y") - 2), integer) > 1 or (
                      cast(substr(DURATION_VAR, 2, instr(DURATION_VAR, "Y") - 2), integer) = 1 and
                      cast(substr(DURATION_VAR, instr(DURATION_VAR, "Y") + 1, instr(DURATION_VAR, "M") - instr(DURATION_VAR, "Y") - 1), integer) > 6
                    )
                  )
                )""".replace("\n", "").replace(" ", "");
        assertEquals(expected, result.replace(" ", ""));
    }

    @Test
    void fullExpression_hourMinute() {
        //
        LunaticDurationControl.HourMinuteValue minValue = new LunaticDurationControl.HourMinuteValue(0, 15);
        LunaticDurationControl.HourMinuteValue maxValue = new LunaticDurationControl.HourMinuteValue(1, 30);
        //
        String result = DurationControlExpression.generateControlExpression("DURATION_VAR", minValue, maxValue);
        //
        String expected = """
                not(
                  not(isnull(DURATION_VAR)) and (
                    cast(substr(DURATION_VAR, 3, instr(DURATION_VAR, "H") - 3), integer) < 0 or (
                      cast(substr(DURATION_VAR, 3, instr(DURATION_VAR, "H") - 3), integer) = 0 and
                      cast(substr(DURATION_VAR, instr(DURATION_VAR, "H") + 1, instr(DURATION_VAR, "M") - instr(DURATION_VAR, "H") - 1), integer) < 15
                    ) or
                    cast(substr(DURATION_VAR, 3, instr(DURATION_VAR, "H") - 3), integer) > 1 or (
                      cast(substr(DURATION_VAR, 3, instr(DURATION_VAR, "H") - 3), integer) = 1 and
                      cast(substr(DURATION_VAR, instr(DURATION_VAR, "H") + 1, instr(DURATION_VAR, "M") - instr(DURATION_VAR, "H") - 1), integer) > 30
                    )
                  )
                )""".replace("\n", "").replace(" ", "");
        assertEquals(expected, result.replace(" ", ""));
    }

}
