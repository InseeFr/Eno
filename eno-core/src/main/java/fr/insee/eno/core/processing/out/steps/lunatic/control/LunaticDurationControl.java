package fr.insee.eno.core.processing.out.steps.lunatic.control;

import fr.insee.eno.core.exceptions.business.RequiredPropertyException;
import fr.insee.lunatic.model.flat.ControlType;
import fr.insee.lunatic.model.flat.Duration;
import fr.insee.lunatic.model.flat.DurationFormat;
import lombok.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LunaticDurationControl {

    private static final Pattern LUNATIC_YEAR_MONTH_PATTERN = Pattern.compile("P(\\d+)Y(\\d+)M");
    private static final Pattern LUNATIC_HOURS_MINUTES_PATTERN = Pattern.compile("PT(\\d+)H(\\d+)M");

    private LunaticDurationControl(@NonNull Duration lunaticDurationComponent) {
        DurationFormat format = lunaticDurationComponent.getFormat();
        switch(format){
            case YEARS_MONTHS -> parseYearMonth()
            case HOURS_MINUTES -> parseHourMinute()
        }
    }

    public static ControlType generateDurationFormatControl(@NonNull Duration lunaticDurationComponent) {
        String min = lunaticDurationComponent.getMin();
        String max = lunaticDurationComponent.getMax();
        DurationFormat format = lunaticDurationComponent.getFormat();
        if (min == null || max == null || format == null)
            throw new RequiredPropertyException("TODO");
        
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

}
