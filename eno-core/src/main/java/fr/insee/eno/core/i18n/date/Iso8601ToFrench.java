package fr.insee.eno.core.i18n.date;

import lombok.NonNull;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class Iso8601ToFrench implements DateFormatter {

    public Result convertYearMontDate(@NonNull String date) {
        try {
            return Result.success(
                    YearMonth.parse(date).format(DateTimeFormatter.ofPattern("MM/yyyy")));
        } catch (Exception e) {
            return Result.failure(errorMessage(date, "YYYY-MM"));
        }
    }

    public Result convertYearMontDayDate(@NonNull String date) {
        try {
            return Result.success(
                    LocalDate.parse(date).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } catch (Exception e) {
            return Result.failure(errorMessage(date, "YYYY-MM-DD"));
        }
    }

}
