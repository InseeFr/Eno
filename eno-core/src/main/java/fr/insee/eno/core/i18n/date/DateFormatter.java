package fr.insee.eno.core.i18n.date;

import fr.insee.eno.core.parameter.EnoParameters;
import lombok.NonNull;

/**
 * Interface to validate and format dates.
 */
public interface DateFormatter {

    class Factory {

        private Factory() {}

        public static DateFormatter forLanguage(EnoParameters.Language language) {
            if (EnoParameters.Language.FR.equals(language))
                return new Iso8601ToFrench();
            // By default, return the implementation that returns date values in ISO-8601 format
            return new Iso8601Formatter();
        }
    }

    record Result(
            boolean isValid,
            String value,
            String errorMessage) {

        public static Result success(String value) {
            return new Result(true, value, null);
        }

        public static Result failure(String errorMessage) {
            return new Result(false, null, errorMessage);
        }
    }

    default Result convertYearDate(@NonNull String date) {
        if (date.length() != 4 || !date.matches("\\d{4}"))
            return Result.failure(errorMessage(date, "YYYY"));
        return Result.success(date);
    }

    Result convertYearMontDate(@NonNull String date);

    Result convertYearMontDayDate(@NonNull String date);

    default String errorMessage(String date, String format) {
        return "Date '" + date + "' is invalid or doesn't match format '" + format + "'.";
    }
}
