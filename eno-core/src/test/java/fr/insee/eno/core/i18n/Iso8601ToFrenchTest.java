package fr.insee.eno.core.i18n;

import fr.insee.eno.core.i18n.date.DateFormatter;
import fr.insee.eno.core.i18n.date.Iso8601ToFrench;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Iso8601ToFrenchTest {

    @Test
    void testConvertYearDate_ValidYear() {
        DateFormatter.Result result = new Iso8601ToFrench().convertYearDate("2023");
        assertTrue(result.isValid());
        assertEquals("2023", result.value());
    }

    @Test
    void testConvertYearDate_InvalidYearLength() {
        DateFormatter.Result result = new Iso8601ToFrench().convertYearDate("23");
        assertFalse(result.isValid());
        assertEquals("Date '23' is invalid or doesn't match format 'YYYY'.", result.errorMessage());
    }

    @Test
    void testConvertYearDate_InvalidCharacters() {
        DateFormatter.Result result =  new Iso8601ToFrench().convertYearDate("abcd");
        assertFalse(result.isValid());
        assertEquals("Date 'abcd' is invalid or doesn't match format 'YYYY'.", result.errorMessage());
    }

    @Test
    void testConvertYearMonthDate_ValidInput() {
        DateFormatter.Result result = new Iso8601ToFrench().convertYearMontDate("2023-12");
        assertTrue(result.isValid());
        assertEquals("12/2023", result.value());
    }

    @Test
    void testConvertYearMonthDate_InvalidFormat() {
        DateFormatter.Result result = new Iso8601ToFrench().convertYearMontDate("2023/12");
        assertFalse(result.isValid());
        assertEquals("Date '2023/12' is invalid or doesn't match format 'YYYY-MM'.", result.errorMessage());
    }

    @Test
    void testConvertYearMonthDate_ExtraCharacters() {
        DateFormatter.Result result = new Iso8601ToFrench().convertYearMontDate("2023-12-01");
        assertFalse(result.isValid());
        assertEquals("Date '2023-12-01' is invalid or doesn't match format 'YYYY-MM'.", result.errorMessage());
    }

    @Test
    void testConvertYearMonthDayDate_ValidInput() {
        DateFormatter.Result result = new Iso8601ToFrench().convertYearMontDayDate("2023-12-16");
        assertTrue(result.isValid());
        assertEquals("16/12/2023", result.value());
    }

    @Test
    void testConvertYearMonthDayDate_InvalidFormat() {
        DateFormatter.Result result = new Iso8601ToFrench().convertYearMontDayDate("2023/12/16");
        assertFalse(result.isValid());
        assertEquals("Date '2023/12/16' is invalid or doesn't match format 'YYYY-MM-DD'.", result.errorMessage());
    }

    @Test
    void testConvertYearMonthDayDate_InvalidDate() {
        DateFormatter.Result result = new Iso8601ToFrench().convertYearMontDayDate("2023-02-30");
        assertFalse(result.isValid());
        assertEquals("Date '2023-02-30' is invalid or doesn't match format 'YYYY-MM-DD'.", result.errorMessage());
    }

}
