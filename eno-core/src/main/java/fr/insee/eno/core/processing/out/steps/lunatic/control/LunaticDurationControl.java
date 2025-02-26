package fr.insee.eno.core.processing.out.steps.lunatic.control;

import fr.insee.eno.core.exceptions.business.RequiredPropertyException;
import fr.insee.lunatic.model.flat.*;
import lombok.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LunaticDurationControl {

    private static final Pattern LUNATIC_YEAR_MONTH_PATTERN = Pattern.compile("P(\\d+)Y(\\d+)M");
    private static final Pattern LUNATIC_HOURS_MINUTES_PATTERN = Pattern.compile("PT(\\d+)H(\\d+)M");

    public static ControlType generateDurationFormatControl(@NonNull Duration lunaticDurationComponent) {

        String min = lunaticDurationComponent.getMin();
        String max = lunaticDurationComponent.getMax();
        DurationFormat format = lunaticDurationComponent.getFormat();
        if (min == null || max == null || format == null)
            throw new RequiredPropertyException("Min, Max ou Format manquant dans l'entrée.");

        YearMonthValue minValue = parseYearMonth(min);
        YearMonthValue maxValue = parseYearMonth(max);

        String controlExpression = generateControlExpression(minValue, maxValue);
        String controlMessage = generateControlMessage(minValue, maxValue);

        ControlType lunaticControl = new ControlType();

        lunaticControl.setControl(new LabelType());
        lunaticControl.getControl().setValue(controlExpression);
        lunaticControl.getControl().setType(LabelTypeEnum.VTL);

        lunaticControl.setErrorMessage(new LabelType());
        lunaticControl.getErrorMessage().setValue(controlMessage);
        lunaticControl.getErrorMessage().setType(LabelTypeEnum.VTL);

        return lunaticControl;
    }

    record YearMonthValue(Integer year, Integer month){}
    record HourMinuteValue(Integer hours, Integer minutes){}

    static YearMonthValue parseYearMonth(String yearMonthString) {
        Matcher matcher = LUNATIC_YEAR_MONTH_PATTERN.matcher(yearMonthString);

        if (! (matcher.find() && matcher.groupCount() == 2))
            throw new IllegalArgumentException("Format invalide pour la durée année/mois.");

        Integer year = Integer.parseInt(matcher.group(1));
        Integer month = Integer.parseInt(matcher.group(2));
        return new YearMonthValue(year, month);
    }

    static HourMinuteValue parseHourMinute(String hourMinuteString) {
        Matcher matcher = LUNATIC_HOURS_MINUTES_PATTERN.matcher(hourMinuteString);

        if (! (matcher.find() && matcher.groupCount() == 2))
            throw new IllegalArgumentException("Format invalide pour la durée heure/minute.");

        Integer hour = Integer.parseInt(matcher.group(1));
        Integer minute = Integer.parseInt(matcher.group(2));
        return new HourMinuteValue(hour, minute);
    }

    static String generateControlExpression(YearMonthValue minValue, YearMonthValue maxValue) {
        return null
    }

    static String generateControlExpression(HourMinuteValue hourMinuteValue) {
        return null;
    }

    static String generateControlMessage(YearMonthValue minValue, YearMonthValue maxValue) {
        if (minValue.year() == 0 && minValue.month() == 0) {
            return String.format("\"La durée saisie doit être inférieure à %s.\"", formatDuration(maxValue));
        }
        return String.format("\"La durée saisie doit être comprise entre %s et %s.\"",
                formatDuration(minValue), formatDuration(maxValue));
    }

    static String generateControlMessage(HourMinuteValue hourMinuteValue) {
        return null;
    }
}
