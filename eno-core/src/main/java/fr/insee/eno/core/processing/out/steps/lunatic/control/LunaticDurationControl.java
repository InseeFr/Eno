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
            throw new RequiredPropertyException("TODO");

        ControlType lunaticControl = new ControlType();

        lunaticControl.setControl(new LabelType());
        lunaticControl.getControl().setValue("TODO");
        lunaticControl.getControl().setType(LabelTypeEnum.VTL);

        lunaticControl.setErrorMessage(new LabelType());
        lunaticControl.getErrorMessage().setValue("TODO");
        lunaticControl.getErrorMessage().setType(LabelTypeEnum.VTL);

        return lunaticControl;
    }

    record YearMonthValue(Integer year, Integer month){}
    record HourMinuteValue(Integer hours, Integer minutes){}

    static YearMonthValue parseYearMonth(String yearMonthString) {
        Matcher matcher = LUNATIC_YEAR_MONTH_PATTERN.matcher(yearMonthString);

        if (! (matcher.find() && matcher.groupCount() == 2))
            throw new IllegalArgumentException("TODO");

        Integer year = Integer.parseInt(matcher.group(1));
        Integer month = Integer.parseInt(matcher.group(2));
        return new YearMonthValue(year, month);
    }

    static HourMinuteValue parseHourMinute(String hourMinuteString) {
        Matcher matcher = LUNATIC_HOURS_MINUTES_PATTERN.matcher(hourMinuteString);

        if (! (matcher.find() && matcher.groupCount() == 2))
            throw new IllegalArgumentException("TODO");

        Integer hour = Integer.parseInt(matcher.group(1));
        Integer minute = Integer.parseInt(matcher.group(2));
        return new HourMinuteValue(hour, minute);
    }

    static String generateControlExpression(YearMonthValue yearMonthValue) {
        return null
    }

    static String generateControlExpression(HourMinuteValue hourMinuteValue) {
        return null;
    }

    static String generateControlMessage(YearMonthValue yearMonthValue) {
        if ( != null &&  != null) {
            return String.format("\"La durée saisie doit être comprise entre %s et %s.\"", , );
        } else if ( != null) {
            return String.format("\"La durée saisie doit être inférieure à %s.\"", );
        } else {
            return String.format("\"La durée saisie doit être supérieure à %s.\"", );
        }
    }

    static String generateControlMessage(HourMinuteValue hourMinuteValue) {
        return null;
    }

}
