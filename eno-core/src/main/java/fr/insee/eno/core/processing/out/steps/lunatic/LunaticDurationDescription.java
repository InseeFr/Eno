package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.processing.out.steps.lunatic.control.DurationControlMessage;
import fr.insee.eno.core.processing.out.steps.lunatic.control.LunaticDurationControl;
import fr.insee.eno.core.processing.out.steps.lunatic.control.LunaticDurationControl.DurationValue;
import fr.insee.eno.core.processing.out.steps.lunatic.control.LunaticDurationControl.HourMinuteValue;
import fr.insee.eno.core.processing.out.steps.lunatic.control.LunaticDurationControl.YearMonthValue;
import fr.insee.lunatic.model.flat.*;

import java.util.List;

import static fr.insee.eno.core.processing.out.steps.lunatic.control.DurationControlMessage.HourMinuteControlMessage;
import static fr.insee.eno.core.processing.out.steps.lunatic.control.DurationControlMessage.YearMonthControlMessage;

/**
 * Generate a description for duration input components.
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
        switch (format) {
            case DurationFormat.YEARS_MONTHS -> generatedDescription = generateYearMonthDescription(min, max);
            case DurationFormat.HOURS_MINUTES -> generatedDescription = generateHourMinuteDescription(min, max);
            default -> throw new IllegalArgumentException("Unknown duration format: " + format);
        }

        LabelType description = new LabelType();
        description.setValue(generatedDescription);
        description.setType(LabelTypeEnum.TXT);
        duration.setDescription(description);
    }

    private String generateYearMonthDescription(String min, String max) {
        YearMonthValue minValue = LunaticDurationControl.parseYearMonth(min);
        YearMonthValue maxValue = LunaticDurationControl.parseYearMonth(max);
        return durationDescriptionValue(minValue, maxValue, new YearMonthControlMessage());
    }

    private String generateHourMinuteDescription(String min, String max) {
        HourMinuteValue minValue = LunaticDurationControl.parseHourMinute(min);
        HourMinuteValue maxValue = LunaticDurationControl.parseHourMinute(max);
        return durationDescriptionValue(minValue, maxValue, new HourMinuteControlMessage());
    }

    private static <T extends DurationValue> String durationDescriptionValue(T minValue, T maxValue, DurationControlMessage<T> durationFormatter) {
        if (durationFormatter.isZeroDuration(minValue)) {
            return String.format("Jusqu'à %s", durationFormatter.formatDuration(maxValue));
        }
        return String.format("De %s à %s", durationFormatter.formatDuration(minValue), durationFormatter.formatDuration(maxValue));
    }

}
