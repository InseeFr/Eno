package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.processing.out.steps.lunatic.control.LunaticDurationControl;
import fr.insee.eno.core.processing.out.steps.lunatic.control.LunaticDurationControl.HourMinuteValue;
import fr.insee.eno.core.processing.out.steps.lunatic.control.LunaticDurationControl.YearMonthValue;
import fr.insee.lunatic.model.flat.*;

import java.util.List;

import static fr.insee.eno.core.processing.out.steps.lunatic.control.DurationControlMessage.HourMinuteControlMessage.formatHours;
import static fr.insee.eno.core.processing.out.steps.lunatic.control.DurationControlMessage.HourMinuteControlMessage.formatMinutes;
import static fr.insee.eno.core.processing.out.steps.lunatic.control.DurationControlMessage.YearMonthControlMessage.formatMonths;
import static fr.insee.eno.core.processing.out.steps.lunatic.control.DurationControlMessage.YearMonthControlMessage.formatYears;

/**
 * Generate a description for duration input components.
 * This must be processed before wrapping components in Question components.
 */
public class LunaticDurationDescription implements ProcessingStep<Questionnaire> {

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        generateDurationDescription(lunaticQuestionnaire.getComponents());
        lunaticQuestionnaire.getComponents().stream()
                .filter(Loop.class::isInstance).map(Loop.class::cast)
                .map(Loop::getComponents)
                .forEach(this::generateDurationDescription);
    }

    private void generateDurationDescription(List<ComponentType> components) {
        components.stream()
                .filter(Duration.class::isInstance).map(Duration.class::cast)
                .forEach(this::generateDurationDescription);
    }

    private void generateDurationDescription(Duration duration) {
        String min = duration.getMin();
        String max = duration.getMax();
        DurationFormat format = duration.getFormat();

        String generatedDescription;
        if (format == DurationFormat.YEARS_MONTHS) {
            generatedDescription = generateYearMonthDescription(min, max);
        } else if (format == DurationFormat.HOURS_MINUTES) {
            generatedDescription = generateHourMinuteDescription(min, max);
        } else {
            throw new IllegalArgumentException("Unknown duration format: " + format);
        }

        LabelType description = new LabelType();
        description.setValue(generatedDescription);
        description.setType(LabelTypeEnum.TXT);
        duration.setDescription(description);
    }

    private String generateYearMonthDescription(String min, String max) {
        YearMonthValue minValue = LunaticDurationControl.parseYearMonth(min);
        YearMonthValue maxValue = LunaticDurationControl.parseYearMonth(max);

        if (minValue.years() == 0 && minValue.months() == 0) {
            return String.format("Jusqu'à %s", formatYearMonth(maxValue));
        }
        return String.format("De %s à %s", formatYearMonth(minValue), formatYearMonth(maxValue));
    }

    private String generateHourMinuteDescription(String min, String max) {
        HourMinuteValue minValue = LunaticDurationControl.parseHourMinute(min);
        HourMinuteValue maxValue = LunaticDurationControl.parseHourMinute(max);

        if (minValue.hours() == 0 && minValue.minutes() == 0) {
            return String.format("Jusqu'à %s", formatHourMinute(maxValue));
        }
        return String.format("De %s à %s", formatHourMinute(minValue), formatHourMinute(maxValue));
    }

    String formatYearMonth(YearMonthValue value) {
        int years = value.years();
        int months = value.months();
        if (years > 0 && months > 0)
            return formatYears(years) + " et " + formatMonths(months);
        if (years > 0)
            return formatYears(years);
        if (months > 0)
            return formatMonths(months);
        throw new IllegalArgumentException("Invalid duration: " + value);
    }

    String formatHourMinute(HourMinuteValue value) {
        int hours = value.hours();
        int minutes = value.minutes();
        if (hours > 0 && minutes > 0)
            return formatHours(hours) + " et " + formatMinutes(minutes);
        if (hours > 0)
            return formatHours(hours);
        if (minutes > 0)
            return formatMinutes(minutes);
        throw new IllegalArgumentException("Invalid duration: " + value);
    }
}



