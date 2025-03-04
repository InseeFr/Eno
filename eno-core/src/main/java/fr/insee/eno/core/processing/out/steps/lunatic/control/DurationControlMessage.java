package fr.insee.eno.core.processing.out.steps.lunatic.control;

import fr.insee.eno.core.processing.out.steps.lunatic.control.LunaticDurationControl.DurationValue;
import fr.insee.eno.core.processing.out.steps.lunatic.control.LunaticDurationControl.YearMonthValue;

/**
 * Class that contains the logic for generating error messages for duration format controls.
 * Template method for different durations formats.
 * */
public abstract class DurationControlMessage<T extends DurationValue> {

    String generateControlMessage(T minValue, T maxValue) {
        if (isZeroDuration(maxValue)) {
            throw new IllegalArgumentException("A duration question maximum value cannot be zero.");
        }
        if (isZeroDuration(minValue)) {
            return String.format("\"La durée saisie doit être inférieure à %s.\"", formatDuration(maxValue));
        }
        return String.format("\"La durée saisie doit être comprise entre %s et %s.\"",
                formatDuration(minValue), formatDuration(maxValue));
    }

    abstract boolean isZeroDuration(T durationValue);
    abstract String formatDuration(T durationValue);

    public static class YearMonthControlMessage extends DurationControlMessage<YearMonthValue> {

        @Override
        boolean isZeroDuration(YearMonthValue durationValue) {
            return durationValue.years() == 0 && durationValue.months() == 0;
        }

        @Override
        String formatDuration(YearMonthValue durationValue) {
            int years = durationValue.years();
            int months = durationValue.months();
            if (years > 0 && months > 0)
                return formatYears(years) + " et " + formatMonths(months);
            if (years > 0)
                return formatYears(years);
            if (months > 0)
                return formatMonths(months);
            throw new IllegalArgumentException("Invalid duration: " + durationValue);
        }
        public static String formatYears(int years) {
            return years == 1 ? "1 an" : String.format("%d ans", years);
        }
        public static String formatMonths(int months) {
            return String.format("%d mois", months);
        }
    }

    public static class HourMinuteControlMessage extends DurationControlMessage<LunaticDurationControl.HourMinuteValue> {

        @Override
        boolean isZeroDuration(LunaticDurationControl.HourMinuteValue durationValue) {
            return durationValue.hours() == 0 && durationValue.minutes() == 0;
        }

        @Override
        String formatDuration(LunaticDurationControl.HourMinuteValue durationValue) {
            int hours = durationValue.hours();
            int minutes = durationValue.minutes();
            if (hours > 0 && minutes > 0)
                return formatHours(hours) + "et" + formatMinutes(minutes);
            if (hours > 0)
                return formatHours(hours);
            if (minutes > 0)
                return formatMinutes(minutes);
            throw new IllegalArgumentException("Invalid duration: " + durationValue);
        }
        public static String formatMinutes(int minutes) {
            return minutes == 1 ? "1 minute" : String.format("%d minutes", minutes);
        }
        public static String formatHours(int hours) {
            return hours == 1 ? "1 heure" : String.format("%d heures", hours);
        }
    }

}
