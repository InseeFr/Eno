package fr.insee.eno.core.processing.out.steps.lunatic.control;

import fr.insee.eno.core.exceptions.business.InvalidValueException;
import fr.insee.eno.core.exceptions.business.RequiredPropertyException;
import fr.insee.eno.core.i18n.date.DateFormatter;
import fr.insee.eno.core.model.question.DateQuestion;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.lunatic.model.flat.ControlType;
import fr.insee.lunatic.model.flat.Datepicker;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class LunaticDatepickerControl implements LunaticFormatControl<Datepicker>  {

    private final DateFormatter dateFormatter;

    public LunaticDatepickerControl(EnoParameters.Language language) {
        dateFormatter = DateFormatter.Factory.forLanguage(language);
    }

    /**
     * Create controls for a date picker component
     * @param datepicker date picker to process
     */
    @Override
    public List<ControlType> generateFormatControls(Datepicker datepicker) {

        String id = datepicker.getId();
        String minValue = datepicker.getMin();
        String maxValue = datepicker.getMax();
        String format = datepicker.getDateFormat();
        String responseName = datepicker.getResponse().getName();

        List<ControlType> controls = new ArrayList<>();

        Optional<ControlType> controlYearFormat = generateDatepickerYearControl(id, format, responseName);
        Optional<ControlType> controlBounds = getFormatControlFromDatepickerAttributes(id, minValue, maxValue, format, responseName);
        controlBounds.ifPresent(controls::addFirst);
        controlYearFormat.ifPresent(controls::addFirst);
        // Note: it's important that the year format is added in first position, since in some cases only the message
        // of the first control is displayed.
        return controls;
    }

    private Optional<DateFormatter.Result> validateAndConvertDate(String date, @NonNull String format) {
        if (date == null)
            return Optional.empty(); // The min/max date properties can eventually be null (not 'required' in Pogues)
        DateFormatter.Result result = switch (format) {
            case DateQuestion.YEAR_MONTH_DAY_FORMAT -> dateFormatter.convertYearMontDayDate(date);
            case DateQuestion.YEAR_MONTH_FORMAT -> dateFormatter.convertYearMontDate(date);
            case DateQuestion.YEAR_FORMAT -> dateFormatter.convertYearDate(date);
            default -> throw new InvalidValueException("Date format '" + format + "' is invalid.");
        };
        return Optional.of(result);
    }

    /**
     * Create controls from date picker attributes
     * @param id date picker id
     * @param minValue min value
     * @param maxValue max value
     * @param format format string
     * @param responseName date picker response attribute
     */
    private Optional<ControlType> getFormatControlFromDatepickerAttributes(String id, String minValue, String maxValue, String format, String responseName) {
        if (format == null)
            throw new RequiredPropertyException("Format is missing in date question '" + id + "'");

        String controlIdPrefix = id + "-format-date";

        Optional<DateFormatter.Result> formattedMinValue = validateAndConvertDate(minValue, format);
        if (formattedMinValue.isPresent() && !formattedMinValue.get().isValid()) {
            String message = "Invalid value for min date of question '" + id + "': " + formattedMinValue.get().errorMessage();
            log.error(message);
            throw new InvalidValueException(message);
        }
        Optional<DateFormatter.Result> formattedMaxValue = validateAndConvertDate(maxValue, format);
        if (formattedMaxValue.isPresent() && !formattedMaxValue.get().isValid()) {
            // Due to a bug in the Pogues -> DDI transformation, an invalid max date doesn't throw an exception for now
            log.warn("Invalid value for max date of question '" + id + "': " + maxValue);
        }

        boolean generateMin = formattedMinValue.isPresent(); // always valid when reached
        boolean generateMax = formattedMaxValue.isPresent() && formattedMaxValue.get().isValid(); // can be invalid

        if (generateMin && generateMax) {
            String controlExpression = String.format(
                    "not(not(isnull(%s)) and " +
                            "(cast(%s, date, \"%s\")<cast(\"%s\", date, \"%s\") or " +
                            "cast(%s, date, \"%s\")>cast(\"%s\", date, \"%s\")))",
                    responseName, responseName, format, minValue, format, responseName, format, maxValue, format
            );
            String controlErrorMessage = String.format(
                    "\"La date saisie doit être comprise entre %s et %s.\"",
                    formattedMinValue.get().value(), formattedMaxValue.get().value()
            );
            return Optional.of(LunaticFormatControl.createFormatControl(
                    controlIdPrefix + "-borne-inf-sup", controlExpression, controlErrorMessage));
        }

        if (generateMax) {
            String controlExpression = String.format(
                    "not(not(isnull(%s)) and (cast(%s, date, \"%s\")>cast(\"%s\", date, \"%s\")))",
                    responseName, responseName, format, maxValue, format
            );
            String controlErrorMessage = String.format(
                    "\"La date saisie doit être antérieure à %s.\"",
                    formattedMaxValue.get().value()
            );
            return Optional.of(LunaticFormatControl.createFormatControl(
                    controlIdPrefix + "-borne-sup", controlExpression, controlErrorMessage));
        }

        if (generateMin) {
            String controlExpression = String.format(
                    "not(not(isnull(%s)) and (cast(%s, date, \"%s\")<cast(\"%s\", date, \"%s\")))",
                    responseName, responseName, format, minValue, format
            );
            String controlErrorMessage = String.format(
                    "\"La date saisie doit être postérieure à %s.\"",
                    formattedMinValue.get().value()
            );
            return Optional.of(LunaticFormatControl.createFormatControl(
                    controlIdPrefix + "-borne-inf", controlExpression, controlErrorMessage));
        }

        return Optional.empty();
    }
    private Optional<ControlType> generateDatepickerYearControl(String id, String format, String responseName) {
        if (format == null || !format.contains("YYYY")) {
            log.warn("Datepicker '{}' (id={}) format is {} which doesn't have the year (YYYY).",
                    responseName, id, format);
            return Optional.empty();
        }
        String controlId = id + "-format-year";
        String expression = String.format("not(not(isnull(%s)) and (" +
                        "cast(cast(cast(%s, date, \"%s\"), string, \"YYYY\"), integer) <= 999 or " +
                        "cast(cast(cast(%s, date, \"%s\"), string, \"YYYY\"), integer) > 9999))",
                responseName, responseName, format, responseName, format);
        String message = "\"L'année doit être saisie avec 4 chiffres.\"";
        return Optional.of(LunaticFormatControl.createFormatControl(controlId, expression, message));
    }

}
