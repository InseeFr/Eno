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

    private LunaticDurationControl() {}

    public static ControlType generateDurationFormatControl(@NonNull Duration lunaticDurationComponent) {
        String min = lunaticDurationComponent.getMin();
        String max = lunaticDurationComponent.getMin();
        DurationFormat format = lunaticDurationComponent.getFormat();
        if (min == null || max == null || format == null)
            throw new RequiredPropertyException("TODO");
        
    }

    private record YearMonthValue(Integer year, Integer month){}
    private record HourMinuteValue(Integer hours, Integer minutes){}

    private YearMonthValue parseYearMonth(String yearMonthString, DurationFormat durationFormat) {
        Matcher matcher = LUNATIC_YEAR_MONTH_PATTERN.matcher(yearMonthString);

        if (! (matcher.find() && matcher.groupCount() == 2))
            throw new IllegalArgumentException("TODO");

        Integer year = Integer.parseInt(matcher.group(1));
        Integer month = Integer.parseInt(matcher.group(2));
        return new YearMonthValue(year, month);
    }

}
