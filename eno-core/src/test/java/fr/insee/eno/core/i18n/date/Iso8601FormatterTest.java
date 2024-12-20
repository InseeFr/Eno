package fr.insee.eno.core.i18n.date;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Iso8601FormatterTest {

    private final Iso8601Formatter formatter = new Iso8601Formatter();

    @Test
    void convertYearMontDate_validDate_shouldReturnFormattedDate() {
        // Arrange
        String inputDate = "2024-12";

        // Act
        DateFormatter.Result result = formatter.convertYearMontDate(inputDate);

        // Assert
        assertTrue(result.isValid());
        assertEquals("2024-12", result.value());
    }

    @Test
    void convertYearMontDate_invalidDate_shouldReturnFailure() {
        // Arrange
        String inputDate = "2024/12";

        // Act
        DateFormatter.Result result = formatter.convertYearMontDate(inputDate);

        // Assert
        assertFalse(result.isValid());
        assertTrue(result.errorMessage().contains("YYYY-MM"));
    }

    @Test
    void convertYearMontDate_emptyString_shouldReturnFailure() {
        // Arrange
        String inputDate = "";

        // Act
        DateFormatter.Result result = formatter.convertYearMontDate(inputDate);

        // Assert
        assertFalse(result.isValid());
        assertTrue(result.errorMessage().contains("YYYY-MM"));
    }

    @Test
    void convertYearMontDayDate_validDate_shouldReturnFormattedDate() {
        // Arrange
        String inputDate = "2024-12-17";

        // Act
        DateFormatter.Result result = formatter.convertYearMontDayDate(inputDate);

        // Assert
        assertTrue(result.isValid());
        assertEquals("2024-12-17", result.value());
    }

    @Test
    void convertYearMontDayDate_invalidDate_shouldReturnFailure() {
        // Arrange
        String inputDate = "2024/12/17";

        // Act
        DateFormatter.Result result = formatter.convertYearMontDayDate(inputDate);

        // Assert
        assertFalse(result.isValid());
        assertTrue(result.errorMessage().contains("YYYY-MM-DD"));
    }

    @Test
    void convertYearMontDayDate_emptyString_shouldReturnFailure() {
        // Arrange
        String inputDate = "";

        // Act
        DateFormatter.Result result = formatter.convertYearMontDayDate(inputDate);

        // Assert
        assertFalse(result.isValid());
        assertTrue(result.errorMessage().contains("YYYY-MM-DD"));
    }

}
