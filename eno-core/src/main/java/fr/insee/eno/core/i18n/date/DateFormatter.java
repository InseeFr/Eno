package fr.insee.eno.core.i18n.date;

import lombok.Getter;
import lombok.NonNull;

/**
 * Interface to validate and format dates.
 */
public interface DateFormatter {

    @Getter
    class Result {
        private final boolean valid;
        private final String value;
        private final String errorMessage;

        private Result(boolean valid, String value, String errorMessage) {
            this.valid = valid;
            this.value = value;
            this.errorMessage = errorMessage;
        }

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
