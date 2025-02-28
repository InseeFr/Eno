package fr.insee.eno.core.processing.out.steps.lunatic.control;

import fr.insee.eno.core.exceptions.business.RequiredPropertyException;
import fr.insee.lunatic.model.flat.*;
import lombok.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class responsible for controlling the validity of durations, ensuring that they fall within specified minimum and maximum values.
 * This class handles durations in two formats: years and months, or hours and minutes.
 */
public class LunaticDurationControl {

    public static void createFormatControlsForDuration(Duration duration) {
        ControlType durationControl = LunaticDurationControl.generateDurationFormatControl(duration);
        duration.getControls().addFirst(durationControl);
    }

    private static final Pattern LUNATIC_YEAR_MONTH_PATTERN = Pattern.compile("P(\\d+)Y(\\d+)M");
    private static final Pattern LUNATIC_HOURS_MINUTES_PATTERN = Pattern.compile("PT(\\d+)H(\\d+)M");

    public static ControlType generateDurationFormatControl(@NonNull Duration lunaticDurationComponent) {

        String min = lunaticDurationComponent.getMin();
        String max = lunaticDurationComponent.getMax();
        DurationFormat format = lunaticDurationComponent.getFormat();
        if (min == null || max == null || format == null)
            throw new RequiredPropertyException("Min, Max ou Format manquant dans l'entrée.");

        ControlType lunaticControl = new ControlType();
        lunaticControl.setTypeOfControl(ControlTypeEnum.FORMAT);
        lunaticControl.setCriticality(ControlCriticalityEnum.ERROR);
        String controlExpression;
        String controlMessage;

        if (format == DurationFormat.YEARS_MONTHS) {
            YearMonthValue minValue = parseYearMonth(min);
            YearMonthValue maxValue = parseYearMonth(max);

            controlExpression = generateControlExpression(minValue, maxValue);
            controlMessage = generateControlMessage(minValue, maxValue);
        } else {
            HourMinuteValue minValue = parseHourMinute(min);
            HourMinuteValue maxValue = parseHourMinute(max);

            controlExpression = generateControlExpression(minValue, maxValue);
            controlMessage = generateControlMessage(minValue, maxValue);
        }

        lunaticControl.setControl(new LabelType());
        lunaticControl.getControl().setValue(controlExpression);
        lunaticControl.getControl().setType(LabelTypeEnum.VTL);

        lunaticControl.setErrorMessage(new LabelType());
        lunaticControl.getErrorMessage().setValue(controlMessage);
        lunaticControl.getErrorMessage().setType(LabelTypeEnum.VTL);

        return lunaticControl;
    }

    record YearMonthValue(Integer years, Integer months) {
    }

    record HourMinuteValue(Integer hours, Integer minutes) {
    }

    static YearMonthValue parseYearMonth(String yearMonthString) {
        Matcher matcher = LUNATIC_YEAR_MONTH_PATTERN.matcher(yearMonthString);

        if (!(matcher.find() && matcher.groupCount() == 2))
            throw new IllegalArgumentException("Format invalide pour la durée année/mois.");

        Integer year = Integer.parseInt(matcher.group(1));
        Integer month = Integer.parseInt(matcher.group(2));
        return new YearMonthValue(year, month);
    }

    static HourMinuteValue parseHourMinute(String hourMinuteString) {
        Matcher matcher = LUNATIC_HOURS_MINUTES_PATTERN.matcher(hourMinuteString);

        if (!(matcher.find() && matcher.groupCount() == 2))
            throw new IllegalArgumentException("Format invalide pour la durée heure/minute.");

        Integer hour = Integer.parseInt(matcher.group(1));
        Integer minute = Integer.parseInt(matcher.group(2));
        return new HourMinuteValue(hour, minute);
    }

    static String generateControlExpression(YearMonthValue minValue, YearMonthValue maxValue) {
        return String.format("""
    not(
        not(isnull(VAR_DUREE)) and (
            cast(substr(VAR_DUREE, 2, instr(VAR_DUREE, "Y") - 2), integer) < %d
            or (cast(substr(VAR_DUREE, 2, instr(VAR_DUREE, "Y") - 2), integer) = %d
                and cast(substr(VAR_DUREE, instr(VAR_DUREE, "Y") + 1, instr(VAR_DUREE, "M") - instr(VAR_DUREE, "Y") - 1), integer) < %d))
            or cast(substr(VAR_DUREE, 2, instr(VAR_DUREE, "Y") - 2), integer) > %d
            or (cast(substr(VAR_DUREE, 2, instr(VAR_DUREE, "Y") - 2), integer) = %d
                and cast(substr(VAR_DUREE, instr(VAR_DUREE, "Y") + 1, instr(VAR_DUREE, "M") - instr(VAR_DUREE, "Y") - 1), integer) > %d)
        )
    )""",
                minValue.years(), minValue.years(), minValue.months(),
                maxValue.years(), maxValue.years(), maxValue.months()
        );
    }

    static String generateControlExpression(HourMinuteValue minValue, HourMinuteValue maxValue) {
        return String.format("""
            not(
                not(isnull(VAR_DUREE)) and (
                    cast(substr(VAR_DUREE, 3, instr(VAR_DUREE, "H") - 3), integer) < %d
                    or (cast(substr(VAR_DUREE, 3, instr(VAR_DUREE, "H") - 3), integer) = %d
                        and cast(substr(VAR_DUREE, instr(VAR_DUREE, "H") + 1, instr(VAR_DUREE, "M") - instr(VAR_DUREE, "H") - 1), integer) < %d))
                    or cast(substr(VAR_DUREE, 3, instr(VAR_DUREE, "H") - 3), integer) > %d
                    or (cast(substr(VAR_DUREE, 3, instr(VAR_DUREE, "H") - 3), integer) = %d
                        and cast(substr(VAR_DUREE, instr(VAR_DUREE, "H") + 1, instr(VAR_DUREE, "M") - instr(VAR_DUREE, "H") - 1), integer) > %d)
                )
            )""",
                minValue.hours(), minValue.hours(), minValue.minutes(),
                maxValue.hours(), maxValue.hours(), maxValue.minutes()
        );
    }

    static String generateControlMessage(YearMonthValue minValue, YearMonthValue maxValue) {
        if (isZeroDuration(minValue)) {
            return String.format("\"La durée saisie doit être inférieure à %s.\"", formatDuration(maxValue));
        }
        if (isZeroDuration(maxValue)) {
            throw new IllegalArgumentException("La durée maximale ne peut pas être zéro.");
        }
        return String.format("\"La durée saisie doit être comprise entre %s et %s.\"",
                formatDuration(minValue), formatDuration(maxValue));
    }

    static String generateControlMessage(HourMinuteValue minValue, HourMinuteValue maxValue) {
        if (isZeroDuration(minValue)) {
            return String.format("\"La durée saisie doit être inférieure à %s.\"", formatDuration(maxValue));
        }
        if (isZeroDuration(maxValue)) {
            throw new IllegalArgumentException("La durée maximale ne peut pas être zéro.");
        }
        return String.format("\"La durée saisie doit être comprise entre %s et %s.\"", formatDuration(minValue), formatDuration(maxValue));
    }

    static String formatDuration(YearMonthValue value) {
        int years = value.years();
        int months = value.months();

        if (years > 0 && months > 0) {
            return String.format("%d an%s et %d mois", years, years == 1 ? "" : "s", months);
        } else if (years > 0) {
            return years == 1 ? "1 an" : String.format("%d ans", years);
        } else {
            return String.format("%d mois", months);
        }
    }

    static String formatDuration(HourMinuteValue value) {
        int hours = value.hours();
        int minutes = value.minutes();

        if (hours > 0 && minutes > 0) {
            return String.format("%d heure%s et %d minute%s", hours, hours == 1 ? "" : "s", minutes, minutes == 1 ? "" : "s");
        } else if (hours > 0) {
            return hours == 1 ? "1 heure" : String.format("%d heures", hours);
        } else {
            return minutes == 1 ? "1 minute" : String.format("%d minutes", minutes);
        }
    }

    /**
     * The value of a duration is zero if both year and month are 0.
     */
    private static boolean isZeroDuration(YearMonthValue minValue) {
        return minValue.years() == 0 && minValue.months() == 0;
    }

    private static boolean isZeroDuration(HourMinuteValue value) {
        return value.hours() == 0 && value.minutes() == 0;
    }

}

