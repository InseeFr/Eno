package fr.insee.eno.core.processing.out.steps.lunatic.control;

/**
 * Class that contains the logic of VTL duration format control generation.
 * See Eno documentation for details about the design of VTL expression.
 * */
public class DurationControlExpression {

    private DurationControlExpression() {}

    static String generateYearVTLCast(String responseName) {
        return String.format("cast(substr(%s, 2, instr(%s, \"Y\") - 2), integer)", responseName, responseName);
    }

    static String generateMonthVTLCast(String responseName) {
        return String.format(
                "cast(substr(%s, instr(%s, \"Y\") + 1, instr(%s, \"M\") - instr(%s, \"Y\") - 1), integer)",
                responseName, responseName, responseName, responseName);
    }

    static String generateHourVTLCast(String responseName) {
        return String.format("cast(substr(%s, 3, instr(%s, \"H\") - 3), integer)", responseName, responseName);
    }

    static String generateMinuteVTLCast(String responseName) {
        return String.format(
                "cast(substr(%s, instr(%s, \"H\") + 1, instr(%s, \"M\") - instr(%s, \"H\") - 1), integer)",
                responseName, responseName, responseName, responseName);
    }

    static String generateControlExpression(String responseName,
            LunaticDurationControl.YearMonthValue minValue, LunaticDurationControl.YearMonthValue maxValue) {
        String yearVTLCast = generateYearVTLCast(responseName);
        String monthVTLCast = generateMonthVTLCast(responseName);
        String minCondition = String.format("%s < %d or (%s = %d and %s < %d)",
                yearVTLCast, minValue.years(), yearVTLCast, minValue.years(), monthVTLCast, minValue.months());
        String maxCondition = String.format("%s > %d or (%s = %d and %s > %d)",
                yearVTLCast, maxValue.years(), yearVTLCast, maxValue.years(), monthVTLCast, maxValue.months());
        return String.format("not(not(isnull(%s)) and (%s or %s))", responseName, minCondition, maxCondition);
    }

    static String generateControlExpression(String responseName,
            LunaticDurationControl.HourMinuteValue minValue, LunaticDurationControl.HourMinuteValue maxValue) {
        String yearVTLCast = generateHourVTLCast(responseName);
        String monthVTLCast = generateMinuteVTLCast(responseName);
        String minCondition = String.format("%s < %d or (%s = %d and %s < %d)",
                yearVTLCast, minValue.hours(), yearVTLCast, minValue.hours(), monthVTLCast, minValue.minutes());
        String maxCondition = String.format("%s > %d or (%s = %d and %s > %d)",
                yearVTLCast, maxValue.hours(), yearVTLCast, maxValue.hours(), monthVTLCast, maxValue.minutes());
        return String.format("not(not(isnull(%s)) and (%s or %s))", responseName, minCondition, maxCondition);
    }

}
