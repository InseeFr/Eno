package fr.insee.eno.core.processing.out.steps.lunatic.control;

import fr.insee.eno.core.exceptions.business.RequiredPropertyException;
import fr.insee.eno.core.processing.out.steps.lunatic.control.DurationControlMessage.HourMinuteControlMessage;
import fr.insee.eno.core.processing.out.steps.lunatic.control.DurationControlMessage.YearMonthControlMessage;
import fr.insee.lunatic.model.flat.*;
import lombok.NonNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class responsible for controlling the validity of durations, ensuring that they fall within specified minimum and maximum values.
 * This class handles durations in two formats: years and months, or hours and minutes.
 */
public class LunaticDurationControl implements LunaticFormatControl<Duration> {

    private static final Pattern LUNATIC_YEAR_MONTH_PATTERN = Pattern.compile("P(\\d+)Y(\\d+)M");
    private static final Pattern LUNATIC_HOURS_MINUTES_PATTERN = Pattern.compile("PT(\\d+)H(\\d+)M");

    @Override
    public List<ControlType> generateFormatControls(Duration lunaticDuration) {
        return List.of(generateDurationFormatControl(lunaticDuration));
    }

    static ControlType generateDurationFormatControl(@NonNull Duration lunaticDurationComponent) {

        checkNonNullProperties(lunaticDurationComponent);
        String min = lunaticDurationComponent.getMin();
        String max = lunaticDurationComponent.getMax();
        DurationFormat format = lunaticDurationComponent.getFormat();
        String responseName = lunaticDurationComponent.getResponse().getName();

        String id = lunaticDurationComponent.getId() + "-format";

        String controlExpression;
        String controlMessage;

        if (format == DurationFormat.YEARS_MONTHS) {
            YearMonthValue minValue = parseYearMonth(min);
            YearMonthValue maxValue = parseYearMonth(max);

            controlExpression = DurationControlExpression.generateControlExpression(responseName, minValue, maxValue);
            controlMessage = new YearMonthControlMessage().generateControlMessage(minValue, maxValue);
        }
        else if (format == DurationFormat.HOURS_MINUTES) {
            HourMinuteValue minValue = parseHourMinute(min);
            HourMinuteValue maxValue = parseHourMinute(max);

            controlExpression = DurationControlExpression.generateControlExpression(responseName, minValue, maxValue);
            controlMessage = new HourMinuteControlMessage().generateControlMessage(minValue, maxValue);
        }
        else {
            throw new IllegalArgumentException("Unknown duration format: " + format);
        }

        return LunaticFormatControl.createFormatControl(id, controlExpression, controlMessage);
    }

    private static void checkNonNullProperties(@NonNull Duration lunaticDurationComponent) {
        String exceptionMessage = "Duration component '%s' has a null %s.";
        String id = lunaticDurationComponent.getId();
        if (lunaticDurationComponent.getFormat() == null)
            throw new RequiredPropertyException(String.format(exceptionMessage, id, "format"));
        if (lunaticDurationComponent.getMin() == null)
            throw new RequiredPropertyException(String.format(exceptionMessage, id, "min"));
        if (lunaticDurationComponent.getMax() == null)
            throw new RequiredPropertyException(String.format(exceptionMessage, id, "format"));
    }

    interface DurationValue {}
    record YearMonthValue(Integer years, Integer months) implements DurationValue {}
    record HourMinuteValue(Integer hours, Integer minutes) implements DurationValue {}

    static YearMonthValue parseYearMonth(String yearMonthString) {
        Matcher matcher = LUNATIC_YEAR_MONTH_PATTERN.matcher(yearMonthString);

        if (!(matcher.find() && matcher.groupCount() == 2))
            throw new IllegalArgumentException("Invalid year/month duration: " + yearMonthString);

        Integer year = Integer.parseInt(matcher.group(1));
        Integer month = Integer.parseInt(matcher.group(2));
        return new YearMonthValue(year, month);
    }

    static HourMinuteValue parseHourMinute(String hourMinuteString) {
        Matcher matcher = LUNATIC_HOURS_MINUTES_PATTERN.matcher(hourMinuteString);

        if (!(matcher.find() && matcher.groupCount() == 2))
            throw new IllegalArgumentException("Invalid hour/minute duration: " + hourMinuteString);

        Integer hour = Integer.parseInt(matcher.group(1));
        Integer minute = Integer.parseInt(matcher.group(2));
        return new HourMinuteValue(hour, minute);
    }

}
