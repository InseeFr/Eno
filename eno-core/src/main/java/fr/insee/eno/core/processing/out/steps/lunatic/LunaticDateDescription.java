package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.business.InvalidValueException;
import fr.insee.eno.core.i18n.date.DateFormatter;
import fr.insee.eno.core.model.question.DateQuestion;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.*;

import java.util.List;

public class LunaticDateDescription implements ProcessingStep<Questionnaire> {

    private final EnoParameters.Language language;

    public LunaticDateDescription(EnoParameters.Language language) {
        this.language = language;
    }

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        generateDateDescription(lunaticQuestionnaire.getComponents());
        lunaticQuestionnaire.getComponents().stream()
                .filter(Loop.class::isInstance).map(Loop.class::cast)
                .map(Loop::getComponents)
                .forEach(this::generateDateDescription);
    }

    private void generateDateDescription(List<ComponentType> components) {
        components.stream()
                .filter(Datepicker.class::isInstance).map(Datepicker.class::cast)
                .forEach(this::generateDateDescription);
    }

    private void generateDateDescription(Datepicker datepicker) {
        String min = datepicker.getMin();
        String max = datepicker.getMax();
        String format = datepicker.getDateFormat();

        String generatedDescription;
        switch (format) {
            case DateQuestion.YEAR_MONTH_DAY_FORMAT -> generatedDescription = generateYearMonthDayDescription(min, max);
            case DateQuestion.YEAR_MONTH_FORMAT -> generatedDescription = generateYearMonthDescription(min, max);
            case DateQuestion.YEAR_FORMAT -> generatedDescription = generateYearDescription(min, max);
            default -> throw new InvalidValueException("Date format '" + format + "' is invalid.");
        }

        if (generatedDescription != null) {
            LabelType description = new LabelType();
            description.setValue(generatedDescription);
            description.setType(LabelTypeEnum.TXT);
            datepicker.setDescription(description);
        }
    }

    private String generateYearMonthDescription(String min, String max) {
        return generateDescription(min, max, language, DateQuestion.YEAR_MONTH_FORMAT);
    }

    private String generateYearDescription(String min, String max) {
        return generateDescription(min, max, language, DateQuestion.YEAR_FORMAT);
    }

    private String generateYearMonthDayDescription(String min, String max) {
        return generateDescription(min, max, language, DateQuestion.YEAR_MONTH_DAY_FORMAT);
    }

    private String generateDescription(String min, String max, EnoParameters.Language lang, String dateFormat) {
        if ((min == null || min.isEmpty()) && (max == null || max.isEmpty())) {
            return null;
        }

        DateFormatter formatter = DateFormatter.Factory.forLanguage(lang);
        DateFormatter.Result minResult = null;
        DateFormatter.Result maxResult = null;

        if (min != null && !min.isEmpty()) {
            minResult = switch (dateFormat) {
                case DateQuestion.YEAR_MONTH_DAY_FORMAT -> formatter.convertYearMontDayDate(min);
                case DateQuestion.YEAR_MONTH_FORMAT -> formatter.convertYearMontDate(min);
                case DateQuestion.YEAR_FORMAT -> formatter.convertYearDate(min);
                default -> throw new IllegalArgumentException("Unhandled format type: " + dateFormat);
            };
        }

        if (max != null && !max.isEmpty()) {
            maxResult = switch (dateFormat) {
                case DateQuestion.YEAR_MONTH_DAY_FORMAT -> formatter.convertYearMontDayDate(max);
                case DateQuestion.YEAR_MONTH_FORMAT -> formatter.convertYearMontDate(max);
                case DateQuestion.YEAR_FORMAT -> formatter.convertYearDate(max);
                default -> throw new IllegalArgumentException("Unhandled format type: " + dateFormat);
            };
        }

        String minFormatted = (minResult != null) ? minResult.value() : null;
        String maxFormatted = (maxResult != null) ? maxResult.value() : null;

        return dateDescriptionValue(minFormatted, maxFormatted);
    }

    private String dateDescriptionValue(String minFormatted, String maxFormatted) {
        if (minFormatted == null || minFormatted.isEmpty()) return String.format("Avant %s",maxFormatted);
        if (maxFormatted == null || maxFormatted.isEmpty()) return String.format("Après %s",minFormatted);
        return String.format("Entre %s et %s", minFormatted, maxFormatted);
    }

}
